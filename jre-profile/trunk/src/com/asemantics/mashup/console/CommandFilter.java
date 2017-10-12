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
 * Defines a filter for a command pattern.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: CommandFilter.java 384 2009-04-16 20:51:17Z michelemostarda $
 */
public interface CommandFilter {

    /**
     * @return returns a human readable description of the filter.
     */
    public String getDescription();

    /**
     * Decides if this command filter accepts the command string or not.
     * 
     * @param commandStr
     * @return
     */
    public boolean acceptCommand(String commandStr);

    /**
     * Returns the filtered command string starting from the input command string.
     * 
     * @param commandStr
     * @return
     */
    public String filteredCommandName(String commandStr);

    /**
     * Returns the filtered command body.
     * 
     * @param commandStr
     * @return
     */
    public String filteredCommandBody(String commandStr);

}
