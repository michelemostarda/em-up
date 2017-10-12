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

import java.io.File;
import java.io.PrintStream;

/**
 * Defines the command context used to perform system out.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: CommandContext.java 384 2009-04-16 20:51:17Z michelemostarda $
 */
public interface CommandContext {

    /**
     * @return the debug flag.
     */
    public boolean isDebug();

    /**
     * @return the exit flag.
     */
    public boolean isExit();

    /**
     * Sets the exit flag.
     *
     * @param b if <code>true</code> the shell is required to exit.
     */
    public void setExit(boolean b);

    /**
     * Sets the debug flag.
     *
     * @param b new value.
     */
    public void setDebug(boolean b);

    /**
     * Returns the list of available commands.
     *
     * @return
     */
    public Command[] getAvailableCommands();

    /**
     * Returns the short command description.
     *
     * @param commandName
     * @return
     */
    public String getShortCommandDescription(String commandName);

    /**
     * Returns the long command description.
     *
     * @param commandName
     * @return
     */
    public String getLongCommandDescription(String commandName);

    /**
     * Prints the usage of the CommandLine interface.
     */
    public void printUsage();

    /**
     * Returns the system out print stream.
     * @return
     */
    public PrintStream getSysOut();

    /**
     * Returns the system error print stream.
     * @return
     */
    public PrintStream getSysErr();

}
