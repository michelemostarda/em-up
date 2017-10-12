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

import com.asemantics.mashup.common.VariableMapper;
import com.asemantics.mashup.console.annotations.CommandMethod;
import com.asemantics.mashup.console.annotations.HelloMessage;
import com.asemantics.mashup.console.annotations.Prompt;
import com.asemantics.mashup.console.annotations.SaveModel;
import com.asemantics.mashup.console.annotations.SetUpModel;
import com.asemantics.mashup.console.annotations.TearDownModel;
import com.asemantics.mashup.console.annotations.ToBeSaved;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a generic set of commands.
 *
 * @see com.asemantics.mashup.console.IntrospectionCommandModel
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: DefaultCommands.java 426 2009-05-27 23:20:09Z michelemostarda $
 */
public class DefaultCommands implements VariableMapper {

    /**
     * Extension used to result files.
     */
    public static final String RESULT_EXT = ".result";

    /**
     * Exit command.
     */
    protected static final String EXIT_COMMAND = "exit";

    /**
     * <i>shell</i> command buffer size.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * <i>shell</i> command buffer.
     */
    private final byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * The current console directory.
     */
    private File currentLocation;

    /**
     * Stores the list of interpreter results.
     */
    private List<Result> results;

    /**
     * Map of variables.
     */
    private Map<String,String> variables;

    /**
     * Empty constructor.
     */
    public DefaultCommands(File initialLocation) {
        // init initial location.
        if(initialLocation == null) {
            throw new IllegalArgumentException("Initial location cannot be null");
        }
        currentLocation = initialLocation;
        results = new ArrayList<Result>();
    }

    /**
     * Adds a result.
     *
     * @param result resutl to add.
     */
    public void addResult(Result result) {
        results.add(result);
    }

    /**
     * Returns the index of the last result.
     *
     * @return positive index.
     */
    public int getLastResultIndex() {
        return results == null ? - 1 : results.size() - 1;
    }

    /**
     * Returns the collected results.
     *
     * @return a unmodifiable list of results.
     */
    public List<Result> getResults() {
        return Collections.unmodifiableList(results);
    }

    /**
     * Clears the available results.
     */
    public void clearResults() {
        results.clear();
    }

    @SetUpModel
    public void setUp() {
        // Empty.
    }

    @TearDownModel
    public void tearDown() {
        // Empty.
    }

    @HelloMessage
    public String helloMessage() {
        return "Basic shell command interpreter.";
    }

    @Prompt
    public String getPrompt() {
        return currentLocation.toString();
    }

    @ToBeSaved
    public boolean toBeSaved() {
        return false;
    }

    @SaveModel
    public void saveModel(CommandLine cl) {
        // Empty.
    }

    /**
     * Command to obtain help on other command.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    @CommandMethod(
        name = "help",
        shortDescription = "print this help",
        longDescription  = "print the help for a specific command.\n\tsyntax: help <command>"
    )
    public void commandHelp(CommandContext commandContext, String[] args)
    throws IllegalAccessException, InvocationTargetException {
        if(args.length == 0) {
            commandContext.printUsage();
            return;
        }
        final PrintStream out = commandContext.getSysOut();
        if(args.length == 1) {
            String longHelp = commandContext.getLongCommandDescription( args[0] );
            if(longHelp == null) {
                out.println("cannot find help for command '" + args[0] + "'");
            } else {
                out.println(longHelp);
            }
            return;
        }
        out.println("invalid command");
        out.println(
                "print this help\n\tTo obtain more informations about a specific command type: help <command>"
        );
    }

    /**
     * Exits the shell.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
            name = EXIT_COMMAND,
            shortDescription = "exit the shell",
            longDescription  = "exit the shell"
    )
    public void commandExit(CommandContext commandContext, String[] args) {
        commandContext.setExit(true);
    }

    /**
     * Command to enable / disable the debug flag.
     */
    @CommandMethod(
        name = "debug",
        shortDescription = "show/set the debug flag",
        longDescription  = "show/set the debug flag\n\tsyntax: debug[{true|false}]"
    )
    public void commandDebug(CommandContext commandContext, String[] args) {
        if( args.length == 0 ) {
            commandContext.getSysOut().println( "debug: " + commandContext.isDebug() );
        } else if(args.length == 1 && "true".equals(args[0]) ) {
            commandContext.setDebug(true);
        }  else if(args.length == 1 && "false".equals(args[0]) ) {
            commandContext.setDebug(false);
        } else {
            throw new IllegalArgumentException("invalid argument");
        }
    }

    /**
     * Prints the current directory.
     */
    @CommandMethod(
        name = "pwd",
        shortDescription = "print the current directory",
        longDescription  = "print the current directory\n\tsyntax: pwd"
    )
    public void commandPwd(CommandContext commandContext, String[] args) {
        try {
            commandContext.getSysOut().println( getCurrentLocation().getCanonicalPath() );
        } catch (IOException ioe) {
            throw new RuntimeException("Error while computing absolute file.", ioe);
        }
    }

    /**
     * Changes the current directory.
     */
    @CommandMethod(
        name = "cd",
        shortDescription = "change the current directory",
        longDescription  = "change the current directory\n\tsyntax: cd <path>"
    )
    public void commandCd(CommandContext commandContext, String[] args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("A single path must be specified");
        }
        setCurrentLocation( new File(args[0]) );
    }



    /**
     * Lists the content of the current directory.
     */
    @CommandMethod(
        name = "ls",
        shortDescription = "List the content of the current directory",
        longDescription  = "List the content of the current directory\n\tsyntax: ls"
    )
    public void commandLs(CommandContext commandContext, String[] args) {
        File[] content = getCurrentLocation().listFiles();
        FilePermission fp;
        for(File f : content) {
            fp = new FilePermission(f.getAbsolutePath(), "read,write,execute,delete");
            PrintStream out = commandContext.getSysOut();
            out.println(
                    String.format(
                        "%s\t%s\t\t\t%s\t%d\n",
                        ( f.isDirectory() ? "d" : "-"),
                        f.getName(),
                        rewriteActions( fp.getActions() ),
                        f.length()
                    )
            );
        }
    }

    /**
     * Echoes the given parameters.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "echo",
        shortDescription = "Echo the given parameters",
        longDescription  = "Echo the given parameters\n\tsyntax: echo [params]"
    )
    public void commandEcho(CommandContext commandContext, String[] args) {
        final PrintStream out = commandContext.getSysOut();
        final int last = args.length - 1;
        for(int i = 0; i <= last; i++) {
            out.print( args[i] );
            if(i < last) {
                out.print(' ');
            }
        }
        out.println();
    }

    /**
     * Allows to set a variable.
     */
    @CommandMethod(
        name = "set",
        shortDescription = "Set a variable value or list defined variables",
        longDescription = "Set a variable value or list defined variables\n\tsyntax: set [name value]"
    )
    public void commandSet(CommandContext commandContext, String[] args) {
        PrintStream out = commandContext.getSysOut();
        // List defined variables.
        if(args.length == 0) {
            if(variables != null) {
                for( Map.Entry<String,String> entry : variables.entrySet() ) {
                    out.println( entry.getKey() + "=" + entry.getValue() );
                }
                out.println(variables.size()  + " variable" + ( variables.size() > 1 ? "s" : "") + " defined");
            } else {
                out.println("no variables defined");
            }
            return;
        }
        // Set variable.
        if(args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }
        setVariable(args[0], args[1]);
        out.println(args[0] + "=" + args[1]);
    }

    /**
     * Allows to unset a variable.
     */
    @CommandMethod(
        name = "unset",
        shortDescription = "Unset a variable value",
        longDescription = "Unset a variable value\n\tsyntax: unset name"
    )
    public void commandUnset(CommandContext commandContext, String[] args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }
        unsetVariable(args[0]);
        commandContext.getSysOut().println("removed " + args[0]);
    }

    /**
     * Lists all the buffered results.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "results",
        shortDescription = "List past execution results",
        longDescription  = "List past execution results\n\tsyntax: results"
    )
    public void commandResults(CommandContext commandContext, String[] args) {
        PrintStream out = commandContext.getSysOut();
        for(int i = 0; i < results.size(); i++) {
            out.println( "[" + i + "] " + results.get(i) );
        }
        out.println( String.format("[%d result%s]",  results.size(), (results.size() != 1 ? "s" : "") ) );
    }

    /**
     * Clears the results buffer.
     */
    @CommandMethod(
            name = "resultsclear",
            shortDescription = "Clear results buffer",
            longDescription  = "Clear results buffer\n\tsyntax: resultsclear"
    )
    public void commandResultsClear(CommandContext commandContext, String[] args) {
        results.clear();
    }

    /**
     * Returns the value of a specific result.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "result",
        shortDescription = "Print out the specified result",
        longDescription  = "Print out the specified result\n\tsyntax: result N"
    )
    public void commandResult(CommandContext commandContext, String[] args) {
        final String resultIndexStr = args[0];
        final int resultIndex;
        final int maxIndex = results.size() - 1;
        final PrintStream err = commandContext.getSysErr();
        try {
            resultIndex = Integer.parseInt( resultIndexStr );
            if(resultIndex < 0 || resultIndex > maxIndex ) {
                throw new IllegalArgumentException(
                        String.format("Invalid index value, must be comprised in [0,%d]", maxIndex )
                );
            }
            final PrintStream out = commandContext.getSysOut();
            final Result result = results.get(resultIndex);
            out.println( result.asStringPreview(false) );
            out.println("//========================================");
            out.println( result.getContent() );
            out.println("========================================//");
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid index value :'" + resultIndexStr + "', expected integer value");
        }
    }

    /**
     * Stores a result on the file system.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "storeresult",
        shortDescription = "Store a result in a file",
        longDescription  = "Store a result is a file\n\tsyntax: storeresult N file"
    )
    public void commandStoreResult(CommandContext commandContext, String[] args) {
        if( args.length != 2 ) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }
        int resultIndex;
        try {
            resultIndex = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Expected number as argument index.", nfe);
        }
        final int maxIndex = results.size() - 1;
        if(resultIndex > maxIndex ) {
            throw new IllegalArgumentException( String.format("Expected result index comprised in [0,%d]", maxIndex) );
        }
        try {
            storeResultInFile(resultIndex, args[1] );
        } catch (IOException ioe) {
            throw new RuntimeException("Error while storing result.", ioe);
        }
    }

    /**
     * Loads a result from the file system.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "loadresult",
        shortDescription = "Load a result from file",
        longDescription  = "Load a result from file\n\tsyntax: loadresult file"
    )
    public void commandLoadResult(CommandContext commandContext, String[] args) {
        if( args.length != 1 ) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }
        try {
            loadFileinResults( args[0] );
        } catch (IOException ioe) {
            throw new RuntimeException("Error while loading result.", ioe);
        }
    }

    /**
     * Shell command.
     *
     * @param commandContext
     * @param args
     */
    @CommandMethod(
        name = "shell",
        shortDescription = "Execute native shell command",
        longDescription  = "Execute native shell command\n\tsyntax: shell command [args]"
    )
    public void commandShell(CommandContext commandContext, String[] args) {
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory( getCurrentLocation() );
        Process process;
        final PrintStream out = commandContext.getSysOut();
        final PrintStream err = commandContext.getSysErr();
        try {

            process = pb.start();

            out.println("[out]");
            forwardStream( process.getInputStream(), out );

        } catch (IOException ioe) {
            throw new RuntimeException("Error while starting shell process.", ioe);
        }
        synchronized (process) {
            try {

                process.waitFor();

                InputStream processErrorStream = process.getErrorStream();
                if( processErrorStream.available() > 0 ) {
                    err.println("[err]");
                    forwardStream( processErrorStream, err );
                }
                out.println("[" + process.exitValue() + "]");

            } catch (Exception e) {
                throw new RuntimeException("Error while executing shell process.", e);
            } finally {
                process.destroy();                
            }

        }
    }

    /**
     * @inheritdoc
     */
    public File getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets a new current location.
     *
     * @param newLocation the new location.
     */
    public void setCurrentLocation(File newLocation) {
        if(newLocation.exists()) {
            try {
                currentLocation = toAbsolutePath( newLocation ).getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Error while computing canonical path:'" + newLocation.getPath() + "'");
            }
        } else {
            throw new IllegalArgumentException("Cannot change directory to unexisting path:'" + newLocation.getAbsolutePath() + "'");
        }
    }

    public void setVariable(String name, String value) {
        if( isInteger(name) ) {
            throw new IllegalArgumentException("Invalid variable name '" + name + "', integer is reserved for resutls");
        }
        checkVariableName(name);
        if(variables == null) {
            variables = new HashMap<String,String>();
        }
        variables.put(name, value);
    }

    public void unsetVariable(String name) {
        if( variables == null || variables.remove(name) == null ) {
            throw new IllegalArgumentException("Variable '" + name + "' is not defined.");
        }
        if(variables.isEmpty()) {
            variables = null;
        }
    }

    public boolean defineVariable(String name) {
        try {
            int index = Integer.parseInt(name);
            return results.size() > index;
        } catch (NumberFormatException nfe) {
            // Empty.
        }
        return variables != null && variables.containsKey(name);
    }

    public String getValueOf(String name) {
        try {
            int index = Integer.parseInt(name);
            // Is result.
            return results.get(index).getContent().toString();
        } catch (NumberFormatException nfe) {
            // Empty.
        }
        // Is variable.
        return variables == null ? "null" : variables.get(name);
    }

    /**
     * Stores a data string in a file.
     *
     * @param data
     * @param file
     * @throws IOException
     */
    protected void storeDataFile(String data, File file)
    throws IOException {
        if( ! file.exists() ) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write( data );
        fileWriter.close();
    }

    /**
     * Stores a data string in a file.
     *
     * @param data
     * @param file
     * @throws IOException
     */    
    protected void storeDataFile(String data, String file)
    throws IOException {
        storeDataFile(data, new File( getCurrentLocation(), file) );
    }

    /**
     * Loads a string from a data file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    protected String loadDataFile(File file)
    throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[ 1024 * 4];
        int i;
        StringBuilder sb = new StringBuilder();
        while( (i = fis.read(buffer) ) != -1) {
            sb.append( new String(buffer, 0, i) );
        }
        return sb.toString();        
    }

    /**
     * Loads a string from a data file.
     *
     * @param file
     * @return
     * @throws IOException
     */    
    protected String loadDataFile(String file)
    throws IOException {
        return loadDataFile( new File( getCurrentLocation(), file ) );
    }

    /**
     * Forwards the content of the input stream into the output stream.
     * @param is
     * @param os
     */
    private void forwardStream(InputStream is, OutputStream os) {
        int i;
        try {
            do {
                i = is.read(buffer);
                os.write(buffer, 0, i);
            } while ( i == BUFFER_SIZE);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while reading input stream.", ioe);
        }
    }

    /**
     * Stores the result index in the given file.
     *
     * @param resultIndex
     * @param filename
     * @param fileExt
     * @throws IOException
     */
    private void storeResultInFile(int resultIndex, String filename, String fileExt)
    throws IOException {
        storeDataFile( results.get(resultIndex).getContent().toString(), filename + fileExt );
    }

    /**
     * Loads a result from a file.
     *
     * @param filename
     * @param fileExt
     * @throws IOException
     */
    private void loadFileinResults(String filename, String fileExt)
    throws IOException {
        File dataFile = new File( getCurrentLocation(), filename + fileExt );
        String data = loadDataFile(dataFile);
        addResult( new InMemoryResult<String>("<loaded from file" + dataFile.getCanonicalPath() + ">", data) );
    }

    /**
     * Stores the result index in the given file.
     *
     * @param resultIndex
     * @param filename
     * @throws IOException
     */
    private void storeResultInFile(int resultIndex, String filename)
    throws IOException {
        storeResultInFile(resultIndex, filename, RESULT_EXT);
    }

    /**
     * Loads a result from a file.
     *
     * @param filename
     * @throws IOException
     */
    private void loadFileinResults(String filename)
    throws IOException {
        loadFileinResults(filename, RESULT_EXT);
    }

    private void checkVariableName(String name) {
        if( ! Character.isJavaIdentifierStart(name.charAt(0)) ) {
            throw new IllegalArgumentException("Illegal start char for variable name '" + name + "'");
        }
    }

    private boolean isInteger(String intStr) {
        try {
            Integer.parseInt(intStr);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * If <i>in</i>parameter is an absolute file it is returned unchanged,
     * otherwise a concatenation of #currentDirectory and the relative path is
     * returned.
     *
     * @return
     */
    private File toAbsolutePath(File in) {
        if(in.isAbsolute()) {
            return in;
        }
        return new File( getCurrentLocation(), in.getPath() );
    }

    /**
     * Rewrites the file actions.
     * 
     * @param in
     * @return
     */
    private String rewriteActions(String in) {
        StringBuilder sb = new StringBuilder();
        if(in.indexOf("read")    != -1) { sb.append("r"); }
        if(in.indexOf("write")   != -1) { sb.append("w"); }
        if(in.indexOf("execute") != -1) { sb.append("e"); }
        if(in.indexOf("delete")  != -1) { sb.append("d"); }
        return sb.toString();
    }

}
