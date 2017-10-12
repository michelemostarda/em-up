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


package com.asemantics.mashup.parser;

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.Terminal;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Defines the base class for defining tests on {@link com.asemantics.lightparser.Parser}
 * derived classes.
 */
public abstract class AbstractParserTest extends TestCase {

    /**
     * Checks the given parse tree on the specified terminal sequence.
     *
     * @param pt parse tree to be checked.
     * @param terminalSequence list od expected terminals from left to right.
     */
    protected void checkParsing(ParseTree pt, String... terminalSequence) {

        Terminal[] terminals = pt.getTerminalNodes();

        System.out.println("Terminal sequence found:" + Arrays.toString(terminals) );

        assertEquals("Espected terminal sequence differs from parse tree terminals.", terminalSequence.length, terminals.length);
        for( int i = 0; i < terminalSequence.length; i++ ) {
            assertEquals("Unespected terminal value.", terminalSequence[i], terminals[i].getContent());
        }
    }

}

