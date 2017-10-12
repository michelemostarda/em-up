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

/**
 * Tests the {@link com.asemantics.mashup.parser.MUParser}
 */
public class MUParserTest extends AbstractParserTest {

    /**
     * Target of test.
     */
    private MUParser parser;

    public MUParserTest() {}

    protected void setUp() throws Exception {
        parser = new MUParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    /**
     * Tests parsing of a variable term.
     *
     * @throws MUParserException
     */
    public void testParseTermVariable() throws MUParserException {
        ParseTree pt = parser.parse("term", "variable123");
        checkParsing(pt, "variable");
    }

    /**
     * Tests parsing of a constant term delimited with single quote.
     *
     * @throws MUParserException
     */
    public void testParseTermConstSingleQuote() throws MUParserException {
        ParseTree pt = parser.parse("term", "'this is a const value'");
        checkParsing(pt, "const");
    }

    /**
     * Tests parsing of a constant term delimited with double quote.
     *
     * @throws MUParserException
     */
    public void testParseTermConstDoubleQuote() throws MUParserException {
        ParseTree pt = parser.parse("term", "\"this is a const value'\"");
        checkParsing(pt, "const");
    }


    /**
     * Tests parsing of a numeric integer term.
     * @throws MUParserException
     */
    public void testParseTermNumberInteger() throws MUParserException {
        ParseTree pt = parser.parse("term", "18");
        checkParsing(pt, "numeric");
    }

    /**
     * Tests parsing of a numeric float term.
     *
     * @throws MUParserException
     */
    public void testParseTermNumberFloat() throws MUParserException {
        ParseTree pt = parser.parse("term", "0.1235");
        System.out.println("parse tree:" + pt);
        checkParsing(pt, "numeric");
    }

    /**
     * Tests parsing of positive numeric.
     *
     * @throws MUParserException
     */
    public void testPositiveNumeric() throws MUParserException {
        ParseTree pt = parser.parse("term", "+0.1235");
        System.out.println("parse tree:" + pt);
        checkParsing(pt, "+", "numeric");
    }

    /**
     * Tests parsing of negative numeric.
     *
     * @throws MUParserException
     */
    public void testNegativeNumeric() throws MUParserException {
        ParseTree pt = parser.parse("term", "-0.1235");
        System.out.println("parse tree:" + pt);
        checkParsing(pt, "-", "numeric");
    }

    /**
     * Tests parsing of boolean <i>true</i> value.
     *
     * @throws MUParserException
     */
    public void testParseTermTrue() throws MUParserException {
        ParseTree pt = parser.parse("term", "true");
        checkParsing(pt, "true");
    }

    /**
     * Tests parsing of boolean <i>false</i> value.
     *
     * @throws MUParserException
     */
    public void testParseTermFalse() throws MUParserException {
        ParseTree pt = parser.parse("term", "false");
        checkParsing(pt, "false");
    }

    /**
     * Tests parsing of vararg value.
     *
     * @throws MUParserException
     */
    public void testParseTermVararg() throws MUParserException {
        ParseTree pt = parser.parse("term", "_1");
        checkParsing(pt, "varargs", "numeric");
    }

    /**
     * Tests parsing of varargs value.
     *
     * @throws MUParserException
     */
    public void testParseTermVarargs() throws MUParserException {
        ParseTree pt = parser.parse("term", "_");
        checkParsing(pt, "varargs");
    }

    /**
     * Test parsing of a list of variables.
     *
     * @throws MUParserException
     */
    public void testParseListOfVars() throws MUParserException {
        ParseTree pt = parser.parse("list_of_models", "var1, var2, var3, var4");
        System.out.println("parse tree:" + pt);
        checkParsing(pt, "variable", "variable", "variable", "variable");
    }

    /**
     * Tests parsing of a predicate term.
     *
     * @throws MUParserException
     */
    public void testParseTermPredicate() throws MUParserException {
        ParseTree pt = parser.parse("term", "PredicateName ( 1, var, 3, OtherPredicate ( a, b )  )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", "numeric", "variable", "numeric", "predicate_name", "(", "variable", "variable", ")", ")");
    }

    /**
     * Tests parsing of a nested predicate term.
     *
     * @throws MUParserException
     */
    public void testParseTermNamedPredicate() throws MUParserException {
        ParseTree pt = parser.parse("term", "result = PredicateName ( 12, v2, v3 )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=", "predicate_name", "(", "numeric", "variable", "variable", ")");
    }

    /**
     * Tests parsing of a predicate with no arguments.
     *
     * @throws MUParserException
     */
    public void testParsePredicateNoArguments() throws MUParserException {
        ParseTree pt = parser.parse("predicate", "PredicateName ()");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", ")");
    }

    /**
     * Tests parsing of a predicate.
     *
     * @throws MUParserException
     */
    public void testParsePredicate() throws MUParserException {
        ParseTree pt = parser.parse("predicate", "PredicateName ( 1, var, 3 )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", "numeric", "variable", "numeric", ")");
    }

    /**
     * Tests parsing of a predicate declaration with no arguments.
     *
     * @throws MUParserException
     */
    public void testParsePredicateDeclarationWithNoArguments() throws MUParserException {
        ParseTree pt = parser.parse("predicate_declaration", "PredicateName ( )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(",")");
    }

    /**
     * Tests parsing of a predicate declaration with varargs.
     *
     * @throws MUParserException
     */
    public void testParsePredicateDeclarationWithVarargs() throws MUParserException {
        ParseTree pt = parser.parse("predicate_declaration", "PredicateName ( _ )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", "varargs", ")");
    }

    /**
     * Tests parsing of a predicate declaration with fixed arguments.
     *
     * @throws MUParserException
     */
    public void testParsePredicateDeclarationFixedArguments() throws MUParserException {
        ParseTree pt = parser.parse("predicate_declaration", "PredicateName ( var1, var2, var3, var4 )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", "variable", "variable", "variable", "variable", ")");
    }

    /**
     * Tests parsing of a predicate declaration with fixed and variable arguments.
     *
     * @throws MUParserException
     */
    public void testParsePredicateDeclarationWithFixedAndVargs() throws MUParserException {
        ParseTree pt = parser.parse("predicate_declaration", "PredicateName ( var1, var2, var3, _ )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(", "variable", "variable", "variable", "varargs", ")");
    }

    /**
     * Tests parsing of a preposition body.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionBody() throws MUParserException {
        ParseTree pt = parser.parse("preposition_body", "PredicateName1 ( var11, var12, var13 ), PredicateName2 ( var21, var22 ) ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", "variable", "variable", "variable", ")",
            "predicate_name", "(", "variable", "variable", ")"
        );
    }

    /**
     * Tests parsing of a preposition element in preposition tree.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPrepositionElement() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateName1 ( var11, var12, var13 ), if( var1, P1(var2), P2(var3) ) ;");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", "variable", "variable", "variable", ")",
            "if", "(", "variable", "predicate_name", "(", "variable", ")", "predicate_name", "(", "variable", ")", ")",
            ";"
        );
    }

    /**
     * Tests parsing of a preposition.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPrepositionBody() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateName1 ( var11, var12, var13 ), PredicateName2 ( var21, var22 ); ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", "variable", "variable", "variable", ")",
            "predicate_name", "(", "variable", "variable", ")",
            ";"
        );
    }

    /**
     * Tests parsing of a predicate declatation.
     * 
     * @throws MUParserException
     */
    public void testParsePrepositionPredicateDeclaration() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateDeclaration( varin1, varin2 ) : PredicateName1 ( var11, var12, var13 ), PredicateName2 ( var21, var22 ); ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", "variable", "variable", ")",
            ":",
            "predicate_name", "(", "variable", "variable", "variable", ")",
            "predicate_name", "(", "variable", "variable", ")",
            ";"
        );
    }

    /**
     * Tests parsing of a predicate declaration.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPredicateDeclarationNoArguments() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateDeclaration( ) : PredicateName1 ( var11, var12, var13 ), PredicateName2 ( var21, var22 ); ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", ")",
             ":",
            "predicate_name", "(", "variable", "variable", "variable", ")",
            "predicate_name", "(", "variable", "variable", ")",
            ";"
        );
    }

    /**
     * Tests parsing of predicate number body declaration.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPredicateConstBodyDeclarationNumber() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateDeclaration( ) : 12 ; ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", ")",
            ":",
            "numeric",
            ";"
        );
    }

    /**
     * Tests parsing of predicate const body declaration.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPredicateConstBodyDeclarationString() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "PredicateDeclaration( ) : \"Const String\" ; ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", ")",
            ":",
            "const",
            ";"
        );
    }

    /**
     * Tests parsing of predicate variable body declaration.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionPredicateConstBodyDeclarationVariable() throws MUParserException {
        ParseTree pt = parser.parse("preposition", "Identity(x) : x ; ");
        System.out.println("parse tree: " + pt);
        checkParsing(
            pt,
            "predicate_name", "(", "variable", ")",
            ":",
            "variable",
            ";"
        );
    }

    /**
     * Parses a declaration on a variable.
     *
     * @throws MUParserException
     */
    public void testParseDeclarationVariable() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "var1=var2");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=", "variable");
    }

    /**
     * Parses a declaration on a const value.
     *
     * @throws MUParserException
     */

    public void testParseDeclarationConst() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "var='ConstValue'");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=", "const");
    }

    /**
     * Parses a declaration on a numeric value.
     *
     * @throws MUParserException
     */
    public void testParseDeclarationNumber() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "var=12.3");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=", "numeric");
    }

    /**
     * Parses a declaration on a predicate result.
     *
     * @throws MUParserException
     */
    public void testParseDeclarationPredicate() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "var=Predicate(a,b,c)");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=", "predicate_name", "(", "variable", "variable", "variable", ")");
    }

    /**
     * Parses <i>if</i> control in binary version.
     *
     * @throws MUParserException
     */
    public void testParseControlIfBinary() throws MUParserException {
        ParseTree pt = parser.parse("control", "if( condition, IFPredicate(v1) )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "if", "(", "variable", "predicate_name", "(", "variable", ")", ")");
    }

    /**
     * Parses <i>if</i> control in binary version with variable argument.
     *
     * @throws MUParserException
     */
    public void testParseControlIfBinaryVariableArgument() throws MUParserException {
        ParseTree pt = parser.parse("control", "if( condition, var )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "if", "(", "variable", "variable", ")");
    }

    /**
     * Parses <i>if</i> control in ternary version.
     *
     * @throws MUParserException
     */
    public void testParseControlIfTernary() throws MUParserException {
        ParseTree pt = parser.parse("control", "if( condition, IFPredicate(v1), ELSEPredicate(v2) )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "if", "(", "variable", "predicate_name", "(", "variable", ")", "predicate_name", "(", "variable", ")", ")");
    }

    /**
     * Parses <i>if</i> control in ternary version with variable arguments.
     *
     * @throws MUParserException
     */
    public void testParseControlIfTernaryVariableArguments() throws MUParserException {
        ParseTree pt = parser.parse("control", "if( condition, var1, var2 )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "if", "(", "variable", "variable", "variable", ")");
    }

   /**
     * Parses <i>for</i> control.
     *
     * @throws MUParserException
     */
    public void testParseControlFor() throws MUParserException {
        ParseTree pt = parser.parse("control", "for( l, v, Predicate(v) )");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "for", "(", "variable", "variable", "predicate_name", "(", "variable", ")", ")");
    }

    /**
     * Parses a declaration on a named predicate.
     *
     * @throws MUParserException
     */
    public void testParseDeclarationNamedPredicate() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "var=x=Predicate(a)");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=","variable", "=", "predicate_name", "(", "variable", ")");
    }

    /**
     * Tests the last value usage.
     *
     * @throws MUParserException
     */
    public void testLastValue() throws MUParserException {
        ParseTree pt = parser.parse("x=1, P1(x), P2($);");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "variable", "=","numeric", "predicate_name", "(", "variable", ")", "predicate_name", "(", "last_value", ")", ";");
    }

    /**
     * Tests the support of model arguments in predicate declaration.
     *
     * @throws MUParserException
     */
    public void testPredicateWithModelArgument() throws MUParserException {
        ParseTree pt = parser.parse("P({\"k1\" : [v1,v2]}): B();");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "predicate_name", "(","{", "const", ":", "[", "variable", "variable", "]", "}", ")", ":", "predicate_name", "(", ")", ";");
    }

    /**
     * Tests the support of model assignment.
     *
     * @throws MUParserException
     */
    public void testModelAssignmentFromPredicate() throws MUParserException {
        ParseTree pt = parser.parse("named_predicate", "[a,b,c]=P()");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "[", "variable", "variable", "variable", "]", "=", "predicate_name", "(", ")");
    }

    /**
     * Tests the support of model assignment.
     *
     * @throws MUParserException
     */
    public void testModelAssignmentFromTerm() throws MUParserException {
        ParseTree pt = parser.parse("declaration", "[a,b,c]=v");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "[", "variable", "variable", "variable", "]", "=", "variable");
    }

    /**
     * Tests the <i>triple</i> non terminal parsing.
     *
     * @throws MUParserException
     */
    public void testParseTriple() throws MUParserException {
         ParseTree pt = parser.parse("triple", "'some text', true, aVar");
        System.out.println("parse tree: " + pt);
        checkParsing(pt, "const", "true", "variable");
    }

    /**
     * Tests the <i>list_of_triples</i> non terminal parsing.
     *
     * @throws MUParserException
     */
    public void testParseListOfTriples() throws MUParserException {
         ParseTree pt = parser.parse("list_of_triples", "true, var, 'some text'; 12, $, P1(); x = P2(), false, 'end'");
        System.out.println("parse tree: " + pt);
        checkParsing(
                pt,
                "true", "variable", "const", ";",
                "numeric", "last_value", "predicate_name", "(", ")", ";",
                "variable", "=", "predicate_name", "(", ")", "false", "const"
        );
    }

    /**
     * Tests <i>graph</i> non terminal parsing.
     *
     * @throws MUParserException
     */
    public void testParseGraph() throws MUParserException {
         ParseTree pt = parser.parse("graph", "<true, var, 'some text'; 12, $, P1(); x = P2(), false, 'end'>");
        System.out.println("parse tree: " + pt);
        checkParsing(
                pt,
                "<",
                "true", "variable", "const", ";",
                "numeric", "last_value", "predicate_name", "(", ")", ";",
                "variable", "=", "predicate_name", "(", ")", "false", "const",
                ">"
        );
    }

      /**
     * Tests the <i>graph</i> non terminal as preposition body parsing.
     *
     * @throws MUParserException
     */
    public void testParsePrepositionBodyAsGraph() throws MUParserException {
         ParseTree pt = parser.parse("preposition", "Pred(a,b) : <1, '2', <false, 4, var> >;");
        System.out.println("parse tree: " + pt);
        checkParsing(
                pt,
                "predicate_name", "(", "variable", "variable", ")", ":",
                "<", "numeric", "const", "<", "false", "numeric", "variable", ">", ">", ";"
        );
    }

    /**
     * Tests the root grammar node.
     *
     * @throws MUParserException
     */
    public void testProgram() throws MUParserException {
        ParseTree pt = parser.parse("program", "P1() : S1(a), S2(b); P2(); P3(c);");
        System.out.println("Parse tree: " + pt);
        checkParsing(
                pt,
                "predicate_name", "(", ")" , ":",
                "predicate_name", "(", "variable", ")", 
                "predicate_name", "(", "variable", ")", ";",
                "predicate_name", "(", ")", ";",
                "predicate_name", "(", "variable", ")", ";"
        );
    }

    /**
     * Tests a complex parsing expression.
     *
     * @throws MUParserException
     */
    public void testComplexParsing() throws MUParserException {
        ParseTree pt = parser.parse("D(x,y) : a=1, b=2, r=P1(x,a), P2(y,r,b);");
        System.out.println("pt: " + pt);
    }

}
