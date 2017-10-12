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

import com.asemantics.mashup.interpreter.Interpreter;
import com.asemantics.mashup.interpreter.InterpreterException;
import com.asemantics.mashup.console.annotations.CommandMethod;

import java.io.File;
import java.io.IOException;

/**
 * Defines the specific <i>MU</i> commands.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MUCommands.java 413 2009-05-26 21:43:57Z michelemostarda $
 */
public class MUCommands extends DefaultCommands {

    public static final String CONTEXT_EXT = ".mu";

    /**
     * Interpreter instance.
     */
    private Interpreter interpreter;

    /**
     * Constructor.
     */
    public MUCommands(File initialLocation, Interpreter interpreter) {
        super(initialLocation);

        if( interpreter == null ) {
            throw new NullPointerException("interpreter cannot be null.");
        }
        this.interpreter = interpreter;
    }

    /**
     * Returns the internal interpreter.
     * 
     * @return
     */
    public Interpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Stores the programmative context in a file.
     */
    @CommandMethod(
        name = "storecontext",
        shortDescription = "Store the programmative context",
        longDescription = "Store the programmative context\n\t syntax: storepcontext <filename>"
    )
    public void commandStorePContext(CommandContext commandContext, String[] args)
    throws IOException {
        if( args.length != 1 ) {
            throw new IllegalArgumentException("Invalid argument, expected file name.");
        }
        String fn = args[0];
        String programmativeContext = interpreter.getProgrammativeContext();
        storeDataFile(programmativeContext, fn + CONTEXT_EXT);
    }

    /**
     * Loads the programmative context from a file.
     */
    @CommandMethod(
        name = "loadcontext",
        shortDescription = "Load the programmative context",
        longDescription = "Load the programmative context\n\t syntax: loadpcontext <filename>"
    )
    public void commandLoadPContext(CommandContext commandContext, String[] args)
    throws IOException, InterpreterException {
        if( args.length != 1 ) {
            throw new IllegalArgumentException("Invalid argument, expected file name.");
        }
        String fn = args[0];
        String pContext = loadDataFile( fn + CONTEXT_EXT );
        interpreter.process( pContext );
    }
}
