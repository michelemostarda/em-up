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


package com.asemantics.mashup.digester.syntax;

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.ParseTreeVisitor;
import com.asemantics.mashup.parser.AbstractParserTest;

public class XPParserTest extends AbstractParserTest {

    private XPParser parser;

    protected void setUp() throws Exception {
        parser = new XPParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    public void testIdentifierNode() throws XPParserException {
        ParseTree pt = parser.parse("identifier", "node1");
        checkParsing( pt, new String[] {"node"} );
    }

    public void testIdentifierAttribute() throws XPParserException {
        ParseTree pt = parser.parse("identifier", "@attribute1");
        checkParsing( pt, new String[] {"@", "attribute"} );
    }

     public void testConditionNode() throws XPParserException {
        ParseTree pt = parser.parse("condition", "node1>value1");
        checkParsing( pt, new String[] {"node", ">", "value"} );
    }

    public void testConditionValue() throws XPParserException {
        ParseTree pt = parser.parse("condition", "@attibute=value1");
        checkParsing( pt, new String[] {"@", "attribute", "=", "value"} );
    }

    public void testConditionIndex() throws XPParserException {
        ParseTree pt = parser.parse("condition", "1");
        checkParsing( pt, new String[] {"index"} );
    }

    public void testPathElemFilteredNodeComparison() throws XPParserException {
        ParseTree pt = parser.parse("path_elem", "node1[@attibute=value1]");
        checkParsing( pt, new String[] {"node", "[", "@", "attribute", "=", "value", "]"} );
    }

    public void testPathElemFilteredNodeIndex() throws XPParserException {
        ParseTree pt = parser.parse("path_elem", "node1[3]");
        checkParsing( pt, new String[] {"node", "[", "index", "]"} );
    }

    public void testPathElemNode() throws XPParserException {
        ParseTree pt = parser.parse("path_elem", "node1");
        checkParsing( pt, new String[] {"node"} );
    }

    public void testAbsolutePath() throws XPParserException {
        ParseTree pt = parser.parse("/a/b/c");
        checkParsing( pt, new String[] {"/", "node", "/", "node", "/", "node"} );
    }

    public void testRelativePath() throws XPParserException {
        ParseTree pt = parser.parse("a/b/c");
        checkParsing( pt, new String[] {"node", "/", "node", "/", "node"} );
    }

    public void testComplexPath() throws XPParserException {
        ParseTree pt = parser.parse("/a[@attr1!=av1]/b[node1<nv1]/c/d[node3>=nv3]/e");
        checkParsing( pt, new String[] {
                "/", "node", "[", "@", "attribute", "!", "=", "value", "]", "/", "node", "[", "node", "<", "value", "]", "/", "node", "/", "node", "[", "node", ">", "=", "value","]", "/", "node"} );
    }

    public void testCompileSimple() throws XPParserException {
        ParseTree pt = parser.parse("/a/b/c[1]");
        ParseTreeVisitor ptv = new ParseTreeVisitor(pt);
        ptv.compile();
    }

    public void testCompileComplex() throws XPParserException {
        ParseTree pt = parser.parse("/a[@attr1!=av1]/b[node1<nv1]/c/d[node3>=nv3]/e");
        ParseTreeVisitor ptv = new ParseTreeVisitor(pt);
        ptv.compile();
    }


}
