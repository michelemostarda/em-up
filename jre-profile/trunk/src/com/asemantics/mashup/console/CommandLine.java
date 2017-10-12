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

import jline.ArgumentCompletor;
import jline.CandidateListCompletionHandler;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.History;
import jline.SimpleCompletor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Defines a generic command line class.
 */
public class CommandLine implements CommandContext {

    /**
     * Prompt delimiter.
     */
    public static final String PROMPT_DELIMITER = "> ";

    /**
     * Common comparator instance.
     */
    private static final CommandComparator COMMAND_COMPARATOR = new CommandComparator();

    /**
     * Command shell history file.
     */
    protected final File historyFile;

    /**
     * Console command group.
     */
    private final CommandModel commandModel;

    /**
     * Command line console reader.
     */
    private final ConsoleReader consoleReader;

    /**
     * Out stream.
     */
    private final PrintStream out;

    /**
     * Error stream.
     */
    private final PrintStream err;

    /**
     * Console history.
     */
    private final History history;

    /**
     * Debug mode flag.
     */
    private boolean debug = false;

    /**
     * The exit flag.
     */
    private boolean exit = false;

    /**
     * Constructor.

     * @throws java.io.IOException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public CommandLine(File historyFile, CommandModel cm, PrintStream out, PrintStream err)
    throws IOException, IllegalAccessException, InvocationTargetException {

        // Init history file.
        if(historyFile == null) {
            throw new IllegalArgumentException("History file cannot be null");
        }
        this.historyFile = historyFile;

        // Init command model.
        if(cm == null) {
            throw new IllegalArgumentException("Command group cannot be null.");
        }
        commandModel = cm;

        // Init out stream.
        if(out == null) {
            throw new IllegalArgumentException("out cannot be null");
        }
        this.out = out;

        // Init err stream.
        if(err == null) {
            throw new IllegalArgumentException("err cannot be null");
        }
        this.err = err;

        // Init of command history.
        consoleReader = new ConsoleReader();
        if(!historyFile.exists()) {
            historyFile.createNewFile();
        }
        history = new History(historyFile);
        history.setMaxSize(1000);
        consoleReader.setHistory(history);
        consoleReader.setUseHistory(true);

        // Initialization of completition handler.
        CandidateListCompletionHandler completionHandler = new CandidateListCompletionHandler();
        consoleReader.setCompletionHandler(completionHandler);
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new SimpleCompletor( getAvailableCommandNames() )
                )
        );
        consoleReader.addCompletor (
                new ArgumentCompletor (
                    new FileNameCompletor ()
                )
        );
    }

    /**
     * onstructor.
     *
     * @param historyFile
     * @param cm
     */
    public CommandLine(File historyFile, CommandModel cm)
    throws IllegalAccessException, IOException, InvocationTargetException {
        this(historyFile, cm, System.out, System.err);
    }

    /**
     * Main cycle of the command line console.
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    public void mainCycle() throws IOException {

        commandModel.setUp();

        printHello();

        while (true) {
            String commandStr = readInput( getPrompt() );
            if(commandStr.trim().length() == 0) {
                continue;
            }
            try {
                processCommand(commandStr);
                if ( isExit() && confirmExit() ) {
                    getSysOut().println("Bye");
                    break;
                }
            } catch (PreprocessorException pe) {
                 ErrorReporter.handleException(ErrorReporter.ErrorLevel.ERROR, this, pe, commandStr);
            } catch (Throwable t) {
                ErrorReporter.handleException(ErrorReporter.ErrorLevel.SEVERE_ERROR, this, t, commandStr);
            }
        }

        commandModel.tearDown();

        System.exit(0);
    }

    public CommandModel getCommandModel() {
        return commandModel;
    }

    public Command[] getAvailableCommands() {
        return commandModel.getCommands();
    }

    /**
     * @return the debug flag.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug flag.
     *
     * @param b new value.
     */
    public void setDebug(boolean b) {
        debug = b;
    }

    /**
     * Returns the short command description.
     *
     * @param commandName
     * @return
     */
    public String getShortCommandDescription(String commandName) {
        return commandModel.getShortCommandDescription(commandName);
    }

    /**
     * Returns the long command description.
     *
     * @param commandName
     * @return
     */
    public String getLongCommandDescription(String commandName) {
        return commandModel.getLongCommandDescription(commandName);
    }

    /**
     * List of command names.
     */
    private final List<String> commandNames = new ArrayList();

    /**
     * List of short command descriptions.
     */
    private final List<String> commandDescriptions = new ArrayList();

    /**
     * Prints the usage of the CommandLine interface.
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void printUsage() {
        PrintStream ps = getSysOut();
        ps.println("Usage: <command> <parameters>");
        ps.println();
        ps.println("\tavailable commands:");
        Command[] availableCommands = getAvailableCommands();
        Arrays.sort(availableCommands, COMMAND_COMPARATOR);
        commandNames.clear();
        commandDescriptions.clear();
        for (Command availableCommand : availableCommands) {
            commandNames.add( availableCommand.getName() );
            commandDescriptions.add( availableCommand.getShortDescription() );
        }
        ps.println( formatTable(commandNames, commandDescriptions) );
        ps.println();
    }

    public PrintStream getSysOut() {
        return out;
    }

    public PrintStream getSysErr() {
        return err;
    }

    /**
     * Sets the exit flag.
     *
     * @param f
     */
    public void setExit(boolean f) {
        exit = f;
    }

    /**
     * Returns <code>true</code> if <i>EXIT_COMMAND</i> has been specified.
     * @return
     */
    public boolean isExit() {
        return exit;
    }

    /**
     * Processes the given command.
     * 
     * @param commandStr
     */
    protected void processCommand(String commandStr) {
        final String preprocessedCommandStr = commandModel.preprocessCommand(this, commandStr);
        commandModel.handleCommand( this, preprocessedCommandStr );
    }

    /**
     * Requires a confirmation exit.
     *
     * @return
     * @throws IOException
     */
    private boolean confirmExit() throws IOException {
        if ( commandModel.toBeSaved() ) {
            while(true) {
                String response = readInput("Are you sure do you want to exit? [Yes/No]");
                if (response.toLowerCase().equals("y") || response.toLowerCase().equals("yes")) {
                    commandModel.saveModel( this );
                    return true;
                } else if (response.toLowerCase().equals("n") || response.toLowerCase().equals("no")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns an array of the available command names.
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    // TODO: optimize it.
    private String[] getAvailableCommandNames()
    throws IllegalAccessException, InvocationTargetException {
        Command[] commands = getAvailableCommands();
        String[] commandNames = new String[commands.length];
        for(int i = 0; i < commands.length; i++) {
            commandNames[i] = commands[i].getName();
        }
        return commandNames;
    }

    /**
     * Prints a <i>Hello</i> message.
     */
    private void printHello() {
        getSysOut().println( commandModel.getHelloMessage() );
        getSysOut().println();
    }

    /**
     * Reads an input line after having printed out the prompt.
     * 
     * @param prompt
     * @return
     * @throws IOException
     */
    private String readInput(String prompt) throws IOException {
        getSysOut().print(prompt);
        String ret = consoleReader.readLine();
        return ret;
    }

    /**
     * Generates the prompt string.
     *
     * @return
     */
    private String getPrompt() {
        return commandModel.getPrompt() + PROMPT_DELIMITER;
    }

    /**
     * Columns format buffer.
     */
    private final StringBuilder tableBuffer = new StringBuilder();

    /**
     * Formats a two columns table.
     *
     * @param col1
     * @param col2
     * @return
     */
    private String formatTable(List<String> col1, List<String> col2) {
        int maxLen = 0;
        for(String str : col1) {
            if(str.length() > maxLen) {
                maxLen = str.length();
            }
        }
        final int limit = maxLen + 4;
        String str1;
        if( tableBuffer.length() > 0 ) {
            tableBuffer.delete(0, tableBuffer.length() - 1);
        }
        for(int i = 0; i < col1.size(); i++) {
            str1 = col1.get(i);
            tableBuffer.append(str1);
            tableBuffer.append( spaces(limit - str1.length()) );
            tableBuffer.append( col2.get(i) );
            tableBuffer.append('\n');
        }
        String result = tableBuffer.toString();
        tableBuffer.delete(0, tableBuffer.length() - 1);
        return result;
    }

    /**
     * Spaces string builder.
     */
    private final StringBuilder spacesBuffer = new StringBuilder();

    /**
     * Creates a string of spaces with given length.
     * @param count
     * @return
     */
    private String spaces(int count) {
        if(spacesBuffer.length() > 0) {
            spacesBuffer.delete(0, spacesBuffer.length() - 1);
        }
        for(int i = 0; i < count; i++ ) {
            spacesBuffer.append(' ');
        }
        return spacesBuffer.toString();
    }

    /**
     * Orders commands by command names.
     */
    static class CommandComparator implements Comparator<Command> {
        public int compare(Command c1, Command c2) {
            return c1.getName().compareTo( c2.getName() );
        }
    }

}
