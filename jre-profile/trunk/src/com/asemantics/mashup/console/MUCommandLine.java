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

import com.asemantics.mashup.MU;
import com.asemantics.mashup.common.ExpansionException;
import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.interpreter.Interpreter;
import com.asemantics.mashup.interpreter.InterpreterException;
import com.asemantics.mashup.console.annotations.CommandHandler;
import com.asemantics.mashup.console.annotations.CommandPreprocessor;
import com.asemantics.mashup.console.annotations.HelloMessage;
import com.asemantics.mashup.console.annotations.SaveModel;
import com.asemantics.mashup.console.annotations.SetUpModel;
import com.asemantics.mashup.console.annotations.TearDownModel;
import com.asemantics.mashup.console.annotations.ToBeSaved;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Defines the <i>MU</i> command line.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MUCommandLine.java 426 2009-05-27 23:20:09Z michelemostarda $
 */
public class MUCommandLine {

    /**
     * Defines the initial char of a command.
     */
    public static final char COMMAND_DISCRIMINATOR = ':';

    /**
     * History file.
     */
    public static final File HISTORY_FILE = new File( new File(System.getProperty("user.home") ), ".mup");

    /**
     * Language name.
     */
    public static final String LANGUAGE_NAME  = MU.instance().getLanguageName();

    /**
     * Major version of CommandLine console.
     */
    public static final int VERSION_MAJOR  = MU.instance().getMajorVersion();

    /**
     * Minor version of CommandLine console.
     */
    public static final int VERSION_MINOR  = MU.instance().getMinorVersion();

    /**
     * Size of result abtract.
     */
    private static final int RESULT_ABSTRACT_SIZE = 60;

    /**
     * Kilobyte.
     */
    private static final int KILOBYTE = 1024;


    /**
     * <i>MU</i> language interpreter.
     */
    private final Interpreter interpreter;

    /**
     * Internal instance of default commands.
     */
    MUCommands muCommands;

    /**
     * Empty constructor.
     */
    public MUCommandLine() {
        // MU language interpreter.
        interpreter = new Interpreter();
        // Default command handler.
        muCommands = new MUCommands( new File("."), interpreter );
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
    public String getHelloMessage() {
        return  LANGUAGE_NAME + "command line console [version " + VERSION_MAJOR + "." + VERSION_MINOR + "]";
    }

    @ToBeSaved
    public boolean toBeSaved() {
        return false;
    }

    @SaveModel
    public void saveModel(CommandLine cl) {
        // Empty.
    }

    @CommandPreprocessor
    public String expandCommandStr(CommandContext context, String commandStr)
    throws ExpansionException {
        return Utils.expandString(muCommands, commandStr);
    }

    @CommandHandler
    public boolean commandHandler(CommandContext context, String commandStr)
    throws InterpreterException {
        if( commandStr.indexOf( COMMAND_DISCRIMINATOR ) != 0 ) {
            // Process command string as MU program.
            final Value response = interpreter.process( commandStr );
            muCommands.addResult( new InMemoryResult<Value>(commandStr, response) );
            context.getSysOut().println(
                    String.format(
                            "\nresult[%d] >> %s",
                            muCommands.getLastResultIndex(),
                            getResultAbstract( response )
                    )
            );
            return true;
        }
        return false;
    }

    /**
     * Returns a result abstract to be printed out.
     *
     * @param result a string representing result abstract.
     * @return
     */
    private String getResultAbstract(Value result) {
        final String resultString = result.asString().getNativeValue();
        final int abstractSize = resultString.length() <= RESULT_ABSTRACT_SIZE ? resultString.length() : RESULT_ABSTRACT_SIZE;
        final String resultAbstract = resultString.substring(0, abstractSize);
        if( resultString.length() > RESULT_ABSTRACT_SIZE ) {
            return resultAbstract + String.format(" ...[%s]", asKiloByte(resultString.length() - RESULT_ABSTRACT_SIZE) );
        } else {
            return resultAbstract;
        }
    }

    /**
     * Converts the integer to a <i>kilobyte</i> size.
     * 
     * @param i
     * @return
     */
    private String asKiloByte(int i) {
        if(i < KILOBYTE) {
            return Integer.toString(i);
        }  else {
            return (i / KILOBYTE) + "kB"; 
        }
    }

    /**
      * Access point.
      *
      * @throws java.io.IOException
      * @throws IllegalAccessException
      * @throws java.lang.reflect.InvocationTargetException
      */
     public static CommandLine createCommandLine(PrintStream out, PrintStream err)
     throws IOException, IllegalAccessException, InvocationTargetException {

        // MU command line.
        final MUCommandLine muCommandLine = new MUCommandLine();

        // Dispatcher.
        CommandDispatcherModel commandDispatcherModel = new CommandDispatcherModel();

        // Deafault commands model.
        IntrospectionCommandModel defaultCommandsModel = new IntrospectionCommandModel(muCommandLine.muCommands);

        // Default command handler.
        commandDispatcherModel.addCommandHandler(
                new ModelBasedCommandFilter(
                        defaultCommandsModel,
                        "\\s*\\"+ COMMAND_DISCRIMINATOR + "((\\w*)\\s*(.*))"
                ),
                defaultCommandsModel
        );

        // MU command handler.
        IntrospectionCommandModel muCommandModel = new IntrospectionCommandModel(muCommandLine);
        commandDispatcherModel.addCommandHandler(
                new AcceptAllCommandFilter() {
                    @Override
                    public String filteredCommandName(String commandStr) {
                        if( commandStr.indexOf(COMMAND_DISCRIMINATOR) == 0 ) {
                            return commandStr.substring(1);
                        } else {
                            return commandStr;
                        }
                    }
                },
                muCommandModel
        );

        // Command line instance startup.
        CommandLine commandLine = new CommandLine(
            HISTORY_FILE,
            commandDispatcherModel,
            out,
            err
        );

        return commandLine;
     }

     public static CommandLine createCommandLine()
     throws IllegalAccessException, IOException, InvocationTargetException {
         return createCommandLine(System.out, System.err);
     }

     public static void main(String[] args)
     throws IllegalAccessException, IOException, InvocationTargetException {
         createCommandLine().mainCycle();
     }

}
