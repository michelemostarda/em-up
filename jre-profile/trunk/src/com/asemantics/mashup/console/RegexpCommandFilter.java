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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filters commands matching a regular expression.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: RegexpCommandFilter.java 384 2009-04-16 20:51:17Z michelemostarda $
 */
public class RegexpCommandFilter implements CommandFilter {

    /**
     * Pattern to be matched.
     */
    private Pattern pattern;

    /**
     * Index of group containing the command name.
     */
    private int commandNameGroup;

    /**
     * Index of group containing the command string.
     */
    private int commandBodyGroup;

    /**
     * Constructor.
     *
     * @param regexp regular expression identifying the command string.
     * @param commandNameGroup index of group corresponding to the filtered part.
     */
    public RegexpCommandFilter(String regexp, int commandNameGroup, int commandBodyGroup) {
        pattern = Pattern.compile(regexp);
        this.commandNameGroup = commandNameGroup;
        this.commandBodyGroup = commandBodyGroup;
    }

    /**
     * This constructor assumes the group index as <code>1</code>.
     *
     * @param regexp
     */
    public RegexpCommandFilter(String regexp) {
        this(regexp, 2, 1);
    }

    public boolean acceptCommand(String commandStr) {
        return pattern.matcher(commandStr).matches();
    }

    public String filteredCommandName(String commandStr) {
        Matcher matcher = pattern.matcher(commandStr);
        matcher.find();
        return matcher.group(commandNameGroup);
    }

    public String filteredCommandBody(String commandStr) {
        Matcher matcher = pattern.matcher(commandStr);
        matcher.find();
        return matcher.group(commandBodyGroup);
    }

    public String getDescription() {
        return pattern.toString();
    }

}
