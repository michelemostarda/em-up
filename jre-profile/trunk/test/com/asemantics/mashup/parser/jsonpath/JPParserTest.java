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


package com.asemantics.mashup.parser.jsonpath;

import com.asemantics.mashup.parser.AbstractParserTest;
import com.asemantics.lightparser.ParseTree;

/**
 * Tests the <i>JSONPath</i> grammar defined in
 * {@link com.asemantics.mashup.parser.jsonpath.JPGrammarFactory}.
 */
public class JPParserTest extends AbstractParserTest {

    /**
     * Parser instance to be tested.
     */
    private JPParser parser;

    protected void setUp() throws Exception {
        parser = new JPParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    /**
     * Tests the array accessor non terminal.
     * 
     * @throws JPParserException
     */
    public void testArrayAccessor() throws JPParserException {
        ParseTree pt = parser.parse("[100]");
        System.out.println("Parse tree: " + pt);
        checkParsing(pt, "[", "numeric", "]");
    }

    /**
     * Tests the star accessor.
     */
    public void testStarAccessor() throws JPParserException {
        ParseTree pt = parser.parse("[*]");
        System.out.println("Parse tree: " + pt);
        checkParsing(pt, "[", "*", "]");
    }

    /**
     * Tests the range accessor non terminal.
     *
     * @throws JPParserException
     */
    public void testRangeAccessor() throws JPParserException {
        ParseTree pt1 = parser.parse("[1:2]");
        System.out.println("Parse tree: " + pt1);
        checkParsing(pt1, "[", "numeric", ":", "numeric","]");

        ParseTree pt2 = parser.parse("[1:2:3]");
        System.out.println("Parse tree: " + pt2);
        checkParsing(pt2, "[", "numeric", ":", "numeric", ":", "numeric", "]");

        ParseTree pt3 = parser.parse("[-1:]");
        System.out.println("Parse tree: " + pt3);
        checkParsing(pt3, "[", "-", "numeric", ":", "]");

        ParseTree pt4 = parser.parse("[:3]");
        System.out.println("Parse tree: " + pt4);
        checkParsing(pt4, "[", ":","numeric", "]");
    }

    /**
     * Tests the array path non terminal.
     *
     * @throws JPParserException
     */
    public void testArrayPath() throws JPParserException {
        ParseTree pt  = parser.parse("[1][2][3]");
        System.out.println("Parse tree: " + pt);
        checkParsing(pt, "[", "numeric", "]", "[", "numeric", "]", "[", "numeric", "]");
    }

    /**
     * Tests the object accessor non terminal.
     *
     * @throws JPParserException
     */
    public void testObjectAccessorsPath() throws JPParserException {
        ParseTree pt  = parser.parse(".aaa.bbb.ccc");
        System.out.println("Parse tree: " + pt);
        checkParsing(pt, ".", "variable", ".", "variable", ".", "variable");
    }

    /**
     * Tests the complex path non terminal.
     *
     * @throws JPParserException
     */
    public void testComplexPath() throws JPParserException {
       ParseTree pt2  = parser.parse(".a.b[1][2].c.d[3].e");
       System.out.println("Parse tree: " + pt2);
       checkParsing(pt2,
               ".", "variable",
               ".", "variable",
               "[", "numeric", "]",
               "[", "numeric", "]",
               ".", "variable",
               ".", "variable",
               "[", "numeric", "]",
               ".", "variable");
    }

}
