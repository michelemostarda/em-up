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

/**
 * Provides a command set.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: CommandModel.java 395 2009-04-29 13:08:49Z michelemostarda $
 */
public interface CommandModel {

    /**
     * Sets up the model.
     */
    public void setUp();

    /**
     * Tears down the model.
     */
    public void tearDown();

    /**
     * @return a drescriptive string for the model. 
     */
    public String getModelDescription();

    /**
     * @return the hello message printed out by the console at startup.
     */
    public String getHelloMessage();

    /**
     * @return the prompt to be presented to the user.
     */
    public String getPrompt();

    /**
     * Preprocesses the given command.
     *
     * @return the result of preprocessing.
     */
    public String preprocessCommand(CommandContext context, String commandStr) throws PreprocessorException;

    /**
     * Handles the command string in the command model.
     * 
     * @param commandStr
     */
    public void handleCommand(CommandContext context, String commandStr);

    /**
     * @return <code>true</code> if the model contains the given command name,
     *         <code>false</code> otherwise. 
     */
    public boolean containCommand(String name);

    /**
     * Returns the list of all commands exposed by the model.
     * 
     * @return list of all commands.
     */
    public Command[] getCommands();

    /**
     * Returns the list of all command names.
     *
     * @return list of command names.
     */
    public String[] getCommandNames();

    /**
     * Returns a command with a specific name.
     *
     * @param name name of the command.
     * @return the command if found, <code>null</code> otherwise.
     */
    public Command getCommand(String name);

    /**
     * Returns the short description of the command with given name.
     *
     * @param name name of the command sought.
     * @return the short description if found, <code>null</code> otherwise.
     */
    public String getShortCommandDescription(String name);

    /**
     * Returns the long description of the command with given name.
     *
     * @param name name of the command sought.
     * @return the long description if found, <code>null</code> otherwise.
     */
    public String getLongCommandDescription(String name);

    /**
     * @return <code>true</code> if the model should be queried to be saved,
     *         <code>false</code> otherwise.
     */
    public boolean toBeSaved();

    /**
     * Saves the model data.
     */
    public void saveModel(CommandLine commandLine);

}
