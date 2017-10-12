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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Dispatches a command line to a command handler.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: CommandDispatcherModel.java 427 2009-06-01 13:20:28Z michelemostarda $
 */
public class CommandDispatcherModel implements CommandModel {

    /**
     * Separator of handler prompts.
     */
    public static final String PROMPT_SEPARATOR = "|";

    /**
     * String builder used to build composed prompt.
     */
    private StringBuilder promptBuilder = new StringBuilder();

    /**
     * List of handlers aggregated by the dispatcher.
     */
    private List<CommandHandler> handlers = new ArrayList<CommandHandler>();

    /**
     * Constructor.
     */
    public CommandDispatcherModel() {}

    /**
     * Adds a command handler.
     *
     * @param filter
     * @param model
     */
    public void addCommandHandler(CommandFilter filter, CommandModel model) {
        handlers.add( new CommandHandler(filter, model) );
    }

    /**
     * Adds a command handler.
     *
     * @param commandModel
     */
    public void addCommandHandler(CommandModel commandModel) {
        addCommandHandler( AcceptAllCommandFilter.INSTANCE, commandModel );
    }

    /**
     * Returns the list of command handlers.
     *
     * @return
     */
    public List<CommandHandler> getCommandHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    /**
     * Removes all the handlers from the dispatcher.
     *
     */
    public void removeHandlers() {
        tearDown();
        handlers.clear();
    }

    /**
     * Preprocesses a command.
     *
     * @param context
     * @param commandStr
     * @return
     * @throws PreprocessorException
     */
    public String preprocessCommand(final CommandContext context, final String commandStr)
     throws PreprocessorException {
        String current = commandStr;
        for(CommandHandler handler : handlers) {
            current = handler.model.preprocessCommand(context, current);
        }
        return current;
    }

    /**
     * Distatches a command.
     *
     * @param commandContext
     * @param commandStr
     * @throws DispatcherException
     */
    public void dispatchCommand(CommandContext commandContext, String commandStr)
    throws DispatcherException {
        for(CommandHandler handler : handlers) {
            if( handler.filter.acceptCommand(commandStr) ) {
                String filteredCommandBody = handler.filter.filteredCommandBody(commandStr);
                handler.model.handleCommand(commandContext, filteredCommandBody);
                return;
            }
        }
        throw new DispatcherException("Cannot find a model for command '" + commandStr + "'");
    }


    public void setUp() {
        for(CommandHandler entry : handlers) {
            entry.model.setUp();
        }
    }

    public void tearDown() {
        for(CommandHandler entry : handlers) {
            entry.model.tearDown();
        }
    }

    public String getModelDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("List  of available handlers {\n");
        int i = 0;
        for(CommandHandler handler : handlers) {
            sb.append("[").append( handler.filter.getDescription() ).append("]\t");
            sb.append( handler.model.getModelDescription() ).append(": ");
            sb.append( handler.model.getHelloMessage() );
            sb.append( "\n" );
            if( i < handlers.size() - 2) {
                sb.append( "\n" );
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    public String getHelloMessage() {
        return getModelDescription();
    }

    public String getPrompt() {
        if(promptBuilder.length() > 0) {
            promptBuilder.delete(0, promptBuilder.length() );
        }

        final int separatorGuard = handlers.size() - 1;
        for(int i = 0; i < handlers.size(); i++ ) {
            promptBuilder.append( handlers.get(i).model.getPrompt() );
            if( i < separatorGuard ) {
                promptBuilder.append(PROMPT_SEPARATOR);
            }
        }

        return promptBuilder.toString();
    }

    public void handleCommand(CommandContext context, String commandStr) {
        try {
            dispatchCommand(context, commandStr);
        } catch (DispatcherException de) {
            throw new RuntimeException("Error while handling command.", de);
        }
    }

    public boolean containCommand(String name) {
        for(CommandHandler handler : handlers) {
            if( handler.model.containCommand(name) ) {
                return true;
            }
        }
        return false;
    }

    public Command[] getCommands() {
        // TODO: improve it.
        List<Command> commands = new ArrayList<Command>();
        for(CommandHandler entry  : handlers) {
            commands.addAll( Arrays.<Command>asList(entry.model.getCommands()) );
        }
        return commands.toArray( new Command[commands.size()] );
    }

    public String[] getCommandNames() {
        Command[] commands = getCommands();
        String[] commandNames = new String[ commands.length ];
        int i = 0;
        for(Command command : commands) {
            commandNames[i++] = command.getName();
        }
        return commandNames;
    }

    public Command getCommand(String name) {
        Command found;
        for(CommandHandler handler : handlers) {
            found = handler.model.getCommand(name);
            if(found != null) {
                return found;
            }
        }
        return null;
    }

    public String getShortCommandDescription(String name) {
        String found;
        for(CommandHandler handler : handlers) {
            found = handler.model.getShortCommandDescription(name);
            if(found != null) {
                return found;
            }
        }
        return null;

    }

    public String getLongCommandDescription(String name) {
        String found;
        for(CommandHandler handler : handlers) {
            found = handler.model.getLongCommandDescription(name);
            if(found != null) {
                return found;
            }
        }
        return null;

    }

    public boolean toBeSaved() {
        for(CommandHandler entry : handlers) {
            if( entry.model.toBeSaved() ) {
                return true;
            }
        }
        return false;
    }

    public void saveModel(CommandLine commandLine) {
        for(CommandHandler entry : handlers) {
            if( entry.model.toBeSaved() ) {
                entry.model.saveModel(commandLine);
            }
        }
    }

    /**
     * An entry of the command dispatcher.
     */
    public class CommandHandler {

        protected CommandFilter filter;
        protected CommandModel model;

        protected CommandHandler(CommandFilter filter, CommandModel model) {
            this.filter = filter;
            this.model  = model;
        }

        public CommandFilter getFilter() {
            return filter;
        }

        public CommandModel getModel() {
            return model;
        }
    }
}
