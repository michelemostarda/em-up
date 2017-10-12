/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.mashup.console;

import com.asemantics.mashup.console.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a {@link com.asemantics.mashup.console.CommandModel} based
 * on the introspection of an object instance.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: IntrospectionCommandModel.java 425 2009-05-27 22:45:12Z michelemostarda $
 */
public class IntrospectionCommandModel implements CommandModel {

    /**
     * Buffer used to parse commands.
     */
    private final List<String> commandsBuffer = new ArrayList<String>();

    /**
     * The target instance.
     */
    private Object instance;

    /**
     * The setup method.
     */
    private Method setUpMethod;

    /**
     * The tear down method.
     */
    private Method tearDownMethod;

    /**
     * The hello method.
     */
    private Method helloMethod;

    /**
     * The prompt method.
     */
    private Method promptMethod;

    /**
     * The to be saved flag method.
     */
    private Method toBeSavedMethod;

    /**
     * The save method.
     */
    private Method saveModelMethod;

    /**
     * The command preprocessor method.
     */
    private Method commandPreprocessor;

    /**
     * Command handler method.
     */
    private Method commandHandler;

    /**
     * Map of commands.
     */
    private final Map<String,Command> commandsMap;

    /**
     * List of commands.
     */
    private final Command[] commands;

    /**
     * Constructor.
     *
     * @param instance object used to extract the applicable methods.
     */
    public IntrospectionCommandModel(Object instance) {
        if(instance == null) {
            throw new IllegalArgumentException("Expected object instance.");
        }
        this.instance = instance;

        Method[] methods = instance.getClass().getMethods();

        // TODO: optimize it.
        // Extract the init methods.
        for(Method method : methods) {
            if( method.getAnnotation(HelloMessage.class) != null ) {
                checkNoArgumentsMethod(method);
                helloMethod = method;
            } else if(method.getAnnotation(Prompt.class) != null) {
                checkNoArgumentsMethod(method);
                promptMethod = method;
            } else if( method.getAnnotation(SetUpModel.class) != null ) {
                checkNoArgumentsMethod(method);
                setUpMethod = method;
            } else if( method.getAnnotation(TearDownModel.class) != null ) {
                checkNoArgumentsMethod(method);
                tearDownMethod = method;
            } else if( method.getAnnotation(ToBeSaved.class) != null ) {
                checkToBeSavedMethod(method);
                toBeSavedMethod = method;
            } else if( method.getAnnotation(SaveModel.class) != null ) {
                checkSaveModelMethod(method);
                saveModelMethod = method;
            } else if( method.getAnnotation(CommandPreprocessor.class) != null ) {
                checkCommandPreprocessorMethod(method);
                commandPreprocessor = method;
            } else if( method.getAnnotation(CommandHandler.class) != null ) {
                checkCommandHandlerMethod(method);
                commandHandler = method;
            }
        }

        // Extract the command map.
        commandsMap = new HashMap<String,Command>();
        for(Method method : methods) {
            CommandMethod annotation = method.getAnnotation( CommandMethod.class );
            if( annotation != null ) {
                checkCommandMethodSignature(method);
                String methodName = extractName(method, annotation);
                commandsMap.put(
                        methodName,
                        new IntrospectionCommand(
                                instance,
                                method,
                                methodName,
                                annotation.shortDescription(),
                                annotation.longDescription()
                        )
                );
            }
        }
        commands = commandsMap.values().toArray( new Command[ commandsMap.values().size() ] );
    }

    public Object getInstance() {
        return instance;
    }

    public void setUp() {
        if( setUpMethod == null ) {
            return;
        }

        try {
            setUpMethod.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while invoking setup method.", e);
        }
    }

    public void tearDown() {
        if( tearDownMethod == null ) {
            return;
        }

        try {
            tearDownMethod.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while invoking tear down method.", e);
        }
    }

    public String getModelDescription() {
        return instance.getClass().getSimpleName();
    }

    public String getHelloMessage() {
        if(helloMethod == null) {
            return "(no hello message)";
        }
        try {
            return helloMethod.invoke(instance).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while invoking hello message.", e);
        }
    }

    public String getPrompt() {
        if(promptMethod == null) {
            return "()";
        }
        try {
            return promptMethod.invoke(instance).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieveving prompt message.", e);
        }
    }

    public String preprocessCommand(CommandContext context, String commandStr)
    throws PreprocessorException {
        if(commandPreprocessor == null) {
            return commandStr;
        }
        try {
            return (String) commandPreprocessor.invoke(instance, context, commandStr);
        } catch (Exception e) {
            throw new PreprocessorException("Error while preprocessing command.", e);
        }
    }

    public void handleCommand(CommandContext context, String commandStr) {
        boolean commandConsumed = false;
        if( commandHandler != null ) {
            try {
                commandConsumed = (Boolean) commandHandler.invoke(instance, context, commandStr);
            } catch (Exception e) {
                handleException(context, new CommandException("Error while invoking handler.", e), commandStr);
            }
        }
        if( ! commandConsumed){
            String[] args = extractArgs(commandStr);
            processCommand(context, args);
        }
    }

    public boolean containCommand(String name) {
        return commandsMap.containsKey(name);
    }

    public Command[] getCommands() {
        return commands;
    }

    public String[] getCommandNames() {
        String[] commandNames = new String[ commands.length ];
        int i = 0;
        for(Command command : commands) {
            commandNames[i++] = command.getName();
        }
        return commandNames;
    }

    public Command getCommand(String name) {
        return commandsMap.get(name);
    }

    public String getShortCommandDescription(String name) {
        Command command = commandsMap.get(name);
        return command == null ? null : command.getShortDescription();
    }

    public String getLongCommandDescription(String name) {
        Command command = commandsMap.get(name);
        return command == null ? null : command.getLongDescription();

    }

    public boolean toBeSaved() {
        if( toBeSavedMethod == null ) {
            return false;
        }
        try {
            return (Boolean) toBeSavedMethod.invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Error while invoking toBeSaved method.", e);
        }
    }

    public void saveModel(CommandLine commandLine) {
        if(saveModelMethod == null) {
            return;
        }
        try {
            saveModelMethod.invoke(instance, commandLine);
        } catch (Exception e) {
            throw new RuntimeException("Error while inviking saveModel method.", e);
        }
    }

    /**
     * Checks that the method signature has no arguments.
     * 
     * @param method
     */
    private void checkNoArgumentsMethod(Method method) {
        if( method.getParameterTypes().length > 0 )  {
            throw new IllegalArgumentException("Expected no arguments for method " + method.getName() );
        }
    }

    private void checkToBeSavedMethod(Method method) {
        checkNoArgumentsMethod(method);
        if( ! valueType(method.getReturnType() ).equals( valueType(Boolean.class) ) ) {
            throw new IllegalArgumentException("Expected a boolean return value for method: " + method.getName() );
        }
    }

    private void checkSaveModelMethod(Method method) {
        if( ! ( method.getParameterTypes().length == 1 && CommandLine.class.equals(method.getParameterTypes()[0]) ) ) {
            throw new IllegalArgumentException("Unexpected signature for save method");
        }
    }

    private void checkCommandPreprocessorMethod(Method method) {
        final String errorMsg = "Unexpected signature for preprocessor method";
        Class[] paramTypes = method.getParameterTypes();
        if(
                paramTypes.length != 2
                        ||
                ! CommandContext.class.equals(paramTypes[0])
                        ||
                ! String.class.equals( paramTypes[1])
        ) {
             throw new IllegalArgumentException( errorMsg );
        }
        if( ! String.class.equals( method.getReturnType() ) ) {
            throw new IllegalArgumentException( errorMsg );
        }
    }

    private void checkCommandHandlerMethod(Method method) {
        Class[] paramTypes = method.getParameterTypes();
        if(
                paramTypes.length != 2
                        ||
                ! CommandContext.class.equals(paramTypes[0])
                        ||
                ! String.class.equals( paramTypes[1])
        ) {
             throw new IllegalArgumentException("Unexpected signature for handler method");
        }
    }

    /**
     * Returns the class value type of arg.
     *
     * @param arg
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Class<?> valueType(Object arg) {
        try {
            Class<?> type = arg.getClass();
            try {
                Field primitiveType = type.getField("TYPE");
                return (Class<?>)primitiveType.get(null);
            } catch (NoSuchFieldException e) {
                return type;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while extracting value type.", e);
        }
    }


    /**
     * Checks the method signature, expected to be:
     * <pre>CommandContext commandContext, String[] args</pre>
     *
     * @param method
     */
    private void checkCommandMethodSignature(Method method) {
        Class[] paramTypes = method.getParameterTypes();
        if( paramTypes.length != 2 ) {
            throw new IllegalArgumentException("Invalid number of arguments for method: " + method.getName());
        }
        if( ! paramTypes[0].equals(CommandContext.class) )  {
            throw new IllegalArgumentException(
                    "Invalid argument 0, expected " + CommandContext.class + ", found " + paramTypes[0]
            );
        }
        if( ! ( paramTypes[1].isArray() && paramTypes[1].getComponentType(). equals(String.class) ) ) {
            throw new IllegalArgumentException(
                    "Invalid argument 1, expected " + String.class + " array, found " + paramTypes[1]
            );
        }
    }

    private String extractName(Method m, CommandMethod annotation) {
        if(annotation.name() == null || annotation.name().trim().length() == 0 ) {
            return m.getName();
        }
        return annotation.name();
    }

    /**
     * Processes a command line input.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    private boolean processCommand(CommandContext context, String[] args) {
        if(args.length == 0) {
            return true;
        }

        // Create commandName arguments.
        String commandName = args[0];
        String[] commandArgs = new String[args.length -1];
        int i;
        for(i = 1; i < args.length; i++) {
            commandArgs[i - 1] = args[i];
        }

        try {

            // Find the candidate command.
            Command command = getCommand(commandName);
            if (command == null) {
                throw new CommandException("unknown command: " + commandName);
            }

            // Execute command.
            command.execute(context, commandArgs);
            return true;

        } catch (CommandException ce) {
            handleException(context, ce, commandName);
            return false;
        }
    }

    /**
     * Handles an {@link Exception} raised by a command transforming it
     * in a error message.
     *
     * @param ce
     * @param cmd
     */
    private void handleException(CommandContext context, CommandException ce, String cmd) {
        ErrorReporter.handleException(ErrorReporter.ErrorLevel.ERROR, context, ce, cmd);
        if(cmd == null || getCommand(cmd) == null) {
            context.printUsage();
        } else {
            context.getSysErr().println( getLongCommandDescription(cmd) );
        }
    }

    /**
     * Extracts arguments from a command line input.
     *
     * @param cl input command string.
     * @return splitted commands.
     */
    private String[] extractArgs(String cl) {
        commandsBuffer.clear();
        boolean insideQuotes = false;
        int begin = 0;
        for(int c = 0; c < cl.length(); c++) {
            if( cl.charAt(c) == '"' ) {
                if(insideQuotes) {
                    insideQuotes = false;
                    if(c - begin > 0) {
                        commandsBuffer.add(cl.substring(begin, c));
                    }
                } else {
                    insideQuotes = true;
                }
                begin = c + 1;
            }
            if(insideQuotes) {
                if(c == cl.length() - 1) {
                    throw new IllegalArgumentException("not found quotes closure.");
                }
                continue;
            }
            if( cl.charAt(c) == ' ' ) {
                if(c - begin > 0) {
                    commandsBuffer.add( cl.substring(begin, c) );
                }
                begin = c + 1;
            } else if(c == cl.length() - 1 && c - begin >= 0) {                
                commandsBuffer.add( cl.substring(begin, c + 1) );
            }
        }
        return commandsBuffer.toArray(new String[commandsBuffer.size()]);
    }

}
