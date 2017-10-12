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


package com.asemantics.mashup.parser.unification;

import com.asemantics.lightparser.ParseTree;
import com.asemantics.mashup.parser.AbstractParserTest;

/**
 * Test class of {@link com.asemantics.mashup.parser.unification.UNParser}.
 */
public class UNParserTest extends AbstractParserTest {

    /**
     * Parser instance to be tested.
     */
    private UNParser parser;

    protected void setUp() throws Exception {
        parser = new UNParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    /**
     * Parses a basic unification model done with a variable.
     * 
     * @throws UNParserException
     */
    public void testRootVariable() throws UNParserException {
        ParseTree pt = parser.parse("un_root", "var");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable");
    }

   /**
     * Parses a basic unification model done with a string.
     *
     * @throws UNParserException
     */
    public void testRootString() throws UNParserException {
        ParseTree pt = parser.parse("un_root", "'abc def'");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "const");
    }

   /**
     * Parses a basic unification model done with a number.
     *
     * @throws UNParserException
     */
    public void testRootNumber() throws UNParserException {
        ParseTree pt = parser.parse("un_root", "123456");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "numeric");
    }

    /**
     * Parses a declaration of a model with empty array.
     *
     * @throws UNParserException
     */
    public void testParseModelEmptyArray() throws UNParserException {
       ParseTree pt = parser.parse("m_array", "[]");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "[", "]");
    }

    /**
     * Parses a declaration of a model array.
     *
     * @throws UNParserException
     */
    public void testParseModelArray() throws UNParserException {
        ParseTree pt = parser.parse("m_array", "[ v1, v2, \"const value\"]");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "[", "variable", "variable", "const", "]");
    }

    /**
     * Parses a declaration of a model array with tail.
     *
     * @throws UNParserException
     */
    public void testParseModelArrayWithTail() throws UNParserException {
        ParseTree pt = parser.parse("m_array", "[ v1, \"const value\", v3| tail ]");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "[", "variable", "const", "variable", "|", "variable", "]");
    }

   /**
     * Parses a declaration of a model object.
     *
     * @throws UNParserException
     */
    public void testParseModelObject() throws UNParserException {
        ParseTree pt = parser.parse("m_object", "{ \"k1\" : v1, \"k2\" : 10 }");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "{", "const", ":", "variable", "const", ":", "numeric", "}");
    }

    /**
     * Parses a declaration of a model array with tail.
     *
     * @throws UNParserException
     */
    public void testParseModelObjectWithTail() throws UNParserException {
        ParseTree pt = parser.parse("m_object", "{ \"k1\" : v1, \"k2\" : v2 | tail}");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "{", "const", ":", "variable", "const", ":", "variable", "|", "variable", "}");
    }

    public void testNestedModels() throws UNParserException {
        ParseTree pt = parser.parse("m_object", "{ \"k1\" : [v1, v2, v3], \"k2\" : { \"k3\" : v4 | tail1} | tail2}");
        System.out.println("parse tree: " + pt);
        checkParsing( pt,
                "{", "const", ":", "[", "variable", "variable", "variable", "]", "const", ":", "{", "const", ":", "variable", "|", "variable","}", "|", "variable", "}");
    }


}
