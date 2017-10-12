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
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: Command.java 384 2009-04-16 20:51:17Z michelemostarda $
 */

/**
 * Defines a command that can be exposed by the {@link CommandLine}.
 */
public interface Command {

    /**
     * Command name.
     *
     * @return string name.
     */
    public String getName();

    /**
     * Command short description.
     *
     * @return description string.
     */
    public String getShortDescription();

    /**
     * command long description.
     *
     * @return description string.
     */
    public String getLongDescription();

    /**
     * Executes the current command. 
     */
    public void execute(CommandContext commandContext, String[] args)
    throws CommandException;

}
