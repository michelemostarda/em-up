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


package com.asemantics.lightparser;

import junit.framework.TestCase;

/**
 * Defines a test case for the {@link com.asemantics.lightparser.Body} class.
 */
public class BodyTest extends TestCase {

    /**
     * Target class.
     */
    Body body;

    protected void setUp() throws Exception {
        body = new Body();
    }

    protected void tearDown() throws Exception {
        body = null;
    }

    /**
     * Tests the body by populating it and verifying consistency.
     */
    public void testPopulate() {
        final int SIZE = 100;

        // Ropulates body content.
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if( j % 2 == 0 ) {
                    body.addNonTerminal( "non_terminal_" + j );
                } else {
                    body.addTerminal( "non_terminal_" + j );
                }
            }
            body.addAlternative();
        }

        // Reads body content.
        for( int i = 0; i < SIZE; i++ ) {
            Term[] terms = body.getTerms()[i];
            assertEquals("Wrong number of terminals.", SIZE, terms.length);
            int terminals = 0, nonterminals = 0;
            for( int j = 0; j < terms.length; j++ ) {
                if( terms[j] instanceof Terminal ) {
                    terminals++;
                } else if( terms[j] instanceof NonTerminal ) {
                    nonterminals++;
                }
            }
            // Check consistency.
            assertEquals("Wrong number of terminals."    , SIZE / 2, terminals   );
            assertEquals("Wrong number of non terminals.", SIZE / 2, nonterminals);
        }
    }
}
