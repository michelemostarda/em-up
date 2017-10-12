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


package com.asemantics.mashup.interpreter;

import com.asemantics.mashup.parser.ValidationException;
import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.InvocableValue;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.ProcessorException;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonUtil;
import com.asemantics.mashup.processor.json.JsonDiff;

import java.util.Iterator;
import java.util.List;

/**
 * Test of {@link com.asemantics.mashup.interpreter.Interpreter} class.
 */
public class InterpreterTest extends AbstractInterpreterTest {

    /**
     * Tests process failure because sequence name is not known.
     */
    public void testEvaluationFailureNoSequenceName() {
        try {
            interpreter.setValidating(false);
            interpreter.process( "PredicateName1 ( var1, var2, var3 );" );
            fail();
        } catch (InterpreterException ie) {
            assertEquals("Unespected cause.", ProcessorException.class, ie.getCause().getClass() );
        }
    }

    /**
     * Tests Evaluate() function.
     * 
     * @throws InterpreterException
     */
    public void testEvaluate() throws InterpreterException {
        Value v = interpreter.process(" Evaluate(' Print(\"This \", \"is \", \"the \" \"result.\"); ');");
        System.out.println("Value : " + v);
        assertEquals("Unespected evaluated value.", "This is the result.", v.asString().getNativeValue() );
    }

    /**
     * Tests Context() function.
     *
     * @throws InterpreterException
     */
    public void testContextFunction() throws InterpreterException {
        Value v = interpreter.process("Context();");
        String context = v.asString().getNativeValue();
        System.out.println("Context: " + context);
        String[] functions = context.split("\n");
        assertTrue("Unespected number of functions.", functions.length > 10);
    }

    /**
     * Tests the programmative context predicate ( PContext() ).
     * 
     * @throws InterpreterException
     */
    public void testProgrammativeContext() throws InterpreterException {
        // Loading predicates.
        String s1 = "Level1() : Print('pre2 ')  , Level2();";
        interpreter.process(s1);
        String s2 = "Level2() : Print('pre3 ')  , Level3();";
        interpreter.process(s2);
        String s3 = "Level3() : Print('pre4.1') , Print(' pre4.2 '), Level4();";
        interpreter.process(s3);
        String s4 = "Level4() : Print('Finally here!!'), StackTrace();";
        interpreter.process(s4);

        String[] predicates = new String[]{s1,s2,s3,s4};

        Value v = interpreter.process("PContext();");
        String result = v.asString().getNativeValue();
        System.out.println("Result:\n" + result);
        String[] lines = result.split("\n");
        assertEquals("Unespected number of lines.", 4, lines.length);
        for(int i = 0; i < lines.length; i++) {
            assertEquals("Unespected line index.", lines[i].substring(1, 2), Integer.toString(i) );
            String predicate = lines[i].substring(5);
            assertEquals("Unespected predicate", predicates[i], predicate);
        }
    }

    /**
     * Tests StackTrace() function.
     *
     * @throws InterpreterException
     */
    public void testStackTraceFunction() throws InterpreterException {
        interpreter.process("Level1() : Print('pre2 ')  , Level2();");
        interpreter.process("Level2() : Print('pre3 ')  , Level3();");
        interpreter.process("Level3() : Print('pre4.1') , Print(' pre4.2 '), Level4();");
        interpreter.process("Level4() : Print('Finally here!!\n'), StackTrace();");
        Value v = interpreter.process("Print('main level\n'), Level1();");
        String stackTrace = v.asString().getNativeValue();
        System.out.println("Stack trace:\n" + stackTrace);
        String[] stackElements = stackTrace.split("\n");
        assertEquals("Unespected number levels in stack trace.", 6, stackElements.length );

        assertTrue("Unespected trace row.", stackElements[0].indexOf("StackTrace") != -1);
        assertTrue("Unespected trace row.", stackElements[1].indexOf("Level4") != -1);
        assertTrue("Unespected trace row.", stackElements[2].indexOf("Level3") != -1);
        assertTrue("Unespected trace row.", stackElements[3].indexOf("Level2") != -1);
        assertTrue("Unespected trace row.", stackElements[4].indexOf("Level1") != -1);
        assertTrue("Unespected trace row.", stackElements[5].indexOf("main")   != -1);
    }

    /**
     * Tests process failure because variable name is not known.
     */
    public void testEvaluationFailureNoVariableName() {
        try {
            interpreter.setValidating(false);
            interpreter.process( "Print ( var1, var2, var3 );" );
            fail();
        } catch (InterpreterException ie) {
            assertEquals("Unespected cause.", ProcessorException.class, ie.getCause().getClass() );
        }
    }

    /**
     * Tests the Print function.
     *
     * @throws InterpreterException
     */
    public void testPrint() throws InterpreterException {
        Value v  = interpreter.process( " Print('This','is', 'an'), Print('Hello', 'World', '!!' ); ");
        assertEquals("unespected result.", "HelloWorld!!", v.asString().getNativeValue() );
    }

    /**
     * Tests that nested predicates are processed in right order.
     *
     * @throws InterpreterException
     */
    public void testNestedPredicates() throws InterpreterException {
        Value v = interpreter.process( "Print ( Print ( Print('a'), 'b' ), 'c' );");
        assertEquals("Unespected result.", "abc", v.asString().getNativeValue() );
    }

    /**
     * Tests numeric conditions.
     *
     * @throws InterpreterException
     */
    public void testConditions() throws InterpreterException {
        checkResult( false, interpreter.process( "Eq (1 ,3);" ) );
        checkResult( true,  interpreter.process( "Eq (3, 3);" ) );

        checkResult( false, interpreter.process( "Neq(0 , 0);" ) );
        checkResult( true,  interpreter.process( "Neq(1 , 3);" ) );

        checkResult( false, interpreter.process( "Lt(2 , 1);" ) );
        checkResult( true,  interpreter.process( "Lt(1 , 2);" ) );

        checkResult( false, interpreter.process( "Lte(2 , 1);" ) );
        checkResult( true,  interpreter.process( "Lte(1 , 1);" ) );
        checkResult( true,  interpreter.process( "Lte(1 , 2);" ) );

        checkResult( false, interpreter.process( "Gt(1 , 2);" ) );
        checkResult( true,  interpreter.process( "Gt(2 , 1);" ) );

        checkResult( false, interpreter.process( "Gte(1 , 2);" ) );
        checkResult( true,  interpreter.process( "Gte(1 , 1);" ) );
        checkResult( true,  interpreter.process( "Gte(2 , 1);" ) );
    }

    /**
     * Tests boolean operators.
     *
     * @throws InterpreterException
     */
    public void testBooleanOperations() throws InterpreterException {
        checkResult( false, interpreter.process( "And(false , false);" ) );
        checkResult( false, interpreter.process( "And(false , true );" ) );
        checkResult( false, interpreter.process( "And(true  , false);" ) );
        checkResult( true,  interpreter.process( "And(true  , true );" ) );

        checkResult( false, interpreter.process( "Or(false , false);" ) );
        checkResult( true,  interpreter.process( "Or(false , true );" ) );
        checkResult( true,  interpreter.process( "Or(true  , false);" ) );
        checkResult( true,  interpreter.process( "Or(true  , true );" ) );

        checkResult( false, interpreter.process( "Xor(false , false);" ) );
        checkResult( true,  interpreter.process( "Xor(false , true );" ) );
        checkResult( true,  interpreter.process( "Xor(true  , false);" ) );
        checkResult( false, interpreter.process( "Xor(true  , true );" ) );

        checkResult( true,  interpreter.process( "Not(false);" ) );
        checkResult( false, interpreter.process( "Not(true );" ) );
    }

    /**
     * Tests type identifier.
     *
     * @throws InterpreterException
     */
    public void testType() throws InterpreterException {
        checkResult( "boolean" , interpreter.process( "Type(true);"          ) );
        checkResult( "numeric" , interpreter.process( "Type(1.0);"           ) );
        checkResult( "string"  , interpreter.process( "Type('Hello type');"  ) );
        checkResult( "list"    , interpreter.process( "Type( List(1,2,3) );" ) );
        checkResult( "map"     , interpreter.process( "Type( Map(1,2) );"              ) );
        checkResult( "list"    , interpreter.process( "Type( AsJSON( List(1,2,3) ) );" ) );
    }

    /**
     * Tests cast interpreter.
     *
     * @throws InterpreterException
     */
    public void testCast() throws InterpreterException {

        // Check generic casting.
        checkResult( "boolean" , interpreter.process( "Type( AsBoolean(1) );"                ) );
        checkResult( "string"  , interpreter.process( "Type( AsString(true) );"              ) );
        checkResult( "numeric" , interpreter.process( "Type( AsNumeric( List('a', 'b') ) );" ) );
        checkResult( "list"    , interpreter.process( "Type( AsList( false ) );"             ) );
        checkResult( "map"     , interpreter.process( "Type( AsMap( '123' ) );"              ) );
        checkResult( "numeric" , interpreter.process( "Type( AsJSON( '123' ) );"             ) );

    }

    /**
     * Tests the JSON support.
     *
     * @throws InterpreterException
     */
    public void testJSONSupport() throws InterpreterException {

        // boolean to JSON.
        checkResult("true"  , interpreter.process( "b=true, AsJSON(b);" ) );

        // Numeric to JSON.
        checkResult("10"  , interpreter.process( "n=10  , AsJSON(n);" ) );
        checkResult("12.5", interpreter.process( "n=12.5, AsJSON(n);" ) );


        // String to JSON.
        Value json = interpreter.process(" AsJSON('{ \"k1\" : \"v1\" , \"k2\" : \"v2\" }'); ");
        assertTrue("Espected JSON value here.", json instanceof JsonValue);
        JsonValue jv = (JsonValue) json;
        assertTrue("Unespected JSON.", jv.getJsonBase() instanceof JsonObject );
        JsonObject jsObj = (JsonObject) jv.getJsonBase();
        assertTrue("Unespected JSON structure.", jsObj.containsKey("k1") );
        assertTrue("Unespected JSON structure.", jsObj.containsKey("k2") );

        // List to JSON.
        checkResult("[1,2,3]"  , interpreter.process( "l=List(1,2  ,3), AsJSON(l);" ) );
        checkResult("[1,2.4,3]", interpreter.process( "l=List(1,2.4,3), AsJSON(l);" ) );

        // Map to JSON.
        checkResult("{\"x2\":\"a\",\"x1\":1}", interpreter.process( "m=Map( 'x1', 1, 'x2', 'a' ), AsJSON(m);" ) );
    }

    /**
     * Tests arithmetic expressions.
     *
     * @throws InterpreterException
     */
    public void testExpressions() throws InterpreterException {
        checkResult( 2  , interpreter.process("Sum(1,1);"  ) );
        checkResult( 4  , interpreter.process("Diff(1,-3);") );
        checkResult( 42 , interpreter.process("Mult(6,7);" ) );
        checkResult( 1.5, interpreter.process("Div(3,2);"  ) );
        checkResult( 1  , interpreter.process("Mod(10,3);" ) );

        checkResult( 13 , interpreter.process("Sum(3, Mult(2,5) );") );          // 3 + (2 * 5)
        checkResult( 58 , interpreter.process("Sum( Mult(6,8), Mult(2,5) );") ); // (6 * 8) + (2 * 5)
    }

    /**
     * Tests list handling.
     *
     * @throws InterpreterException
     */
    public void testList() throws InterpreterException {
        // Empty.
        checkResult( "[]"            , interpreter.process("List();") );

        // Single.
        checkResult( "single"        , interpreter.process("List('single');") );

        checkResult( "[1,2,3]", interpreter.process("List(1, 2, Sum(1,2) );") );
        checkResult( "[\"a\",\"b\"]", interpreter.process("l = List(), AddElem(l, 'a'), AddElem(l, 'b');") );

        checkResult( "[\"a\",\"x\",\"y\",\"z\"]", interpreter.process("l = List('x', 'y', 'z'), AddFirst(l, 'a');") );
        checkResult( "[\"x\",\"y\",\"z\",\"a\"]", interpreter.process("l = List('x', 'y', 'z'), AddLast (l, 'a');") );

        checkResult( "z" , interpreter.process("l = List('x', 'y', 'z'), GetElem (l, 2);") );

        checkResult( "1" , interpreter.process("l = List('x', 'y', 'z'), LIndexOf(l, 'y');") );

        checkResult( "[\"x\",\"y\",\"z\"]", interpreter.process("l = List('x', 'y', 'z'), AddElem(l, 'a'), LRemove(l, 3), Print(l);") );
        checkResult( "[\"x\",\"z\",\"a\"]", interpreter.process("l = List('x', 'y', 'z'), AddElem(l, 'a'), LRemove(l, 1), Print(l);") );

        checkResult( "[\"a\",\"b\",\"c\",\"d\"]", interpreter.process("l1 = List('a', 'b'), l2 = List('c', 'd'), l = AddAll(l1, l2), Print(l);") );
    }

    /**
     * Tests <i>Range()</i> predicate.
     */
    public void testRange() throws InterpreterException {

        // Progressive range.
        checkResult( "[0,2,4,6,8,10]", interpreter.process("Range(0, 10, 2);") );

        // Progressive range with negative step.
        assertTrue(
                "Unespected result.",
                interpreter.process("Range(0, 10, -2);").asString().getNativeValue().indexOf("Infinite loop") != -1
        );

        // Regressive range.
        checkResult( "[10,8,6,4,2,0]", interpreter.process("Range(10, 0, -2);") );

        // Regressive range with positive step.
        assertTrue(
                "Unespected result.",
                interpreter.process("Range(10, 0, 2);").asString().getNativeValue().indexOf("Infinite loop") != -1
        );

    }

    /**
     * Tests map handling.
     *
     * @throws InterpreterException
     */
    public void testMap() throws InterpreterException {

        // Empty.
        checkResult( "{}", interpreter.process("Map();") );

        // Initalization.
        checkResult( "{\"k1\":\"v1\",\"k2\":\"v2\"}", interpreter.process("Map( 'k1', 'v1', 'k2' , 'v2' );"          ) );
        checkResult( "{\"k1\":\"v1\",\"orphan\":null,\"k2\":\"v2\"}", interpreter.process("Map( 'k1', 'v1', 'k2' , 'v2', 'orphan' );") );

        checkResult("{\"3\":4,\"1\":2.5}", interpreter.process("m = Map(), MPut(m, 1, 2.5), MPut(m, 3, 4), Print(m);") );

        checkResult("{\"c\":\"d\"}", interpreter.process("m = Map(), MPut(m, 'a', 'b'), MPut(m, 'c', 'd'), MRemove(m, 'a'), Print(m);") );

        checkResult("d", interpreter.process("m = Map(), MPut(m, 'a', 'b'), MPut(m, 'c', 'd'), MPut(m, 'e', 'f'), MRemove(m, 'a'), Print( GetKey(m, 'c') );") );
    }

    /**
     * Tests the string support.
     *
     * @throws InterpreterException
     */
    public void testString() throws InterpreterException {
        checkResult( "klmno" , interpreter.process("SubString('abcdefghijklmnopqrstuvwxyz', 10, 15);") );
        checkResult( 10      , interpreter.process("SIndexOf('abcdefghijklmnopqrstuvwxyz', 'k');"    ) );
    }

    /**
     * Tests if control.
     *
     * @throws InterpreterException
     */
    public void testIf() throws InterpreterException {
        checkResult( "OK"  , interpreter.process("if( true , Print('OK'), Print('NOK') );") );
        checkResult( "NOK" , interpreter.process("if( false, Print('OK'), Print('NOK') );") );

        checkResult( "OK"  , interpreter.process("if( Eq(1,1), Print('OK') );") );
        checkResult( "null", interpreter.process("if( Eq(1,2), Print('OK') );") );

        checkResult( "OK" , interpreter.process("if( Eq(1,1), Print('OK'), Print('NOK') );") );
        checkResult( "NOK", interpreter.process("if( Eq(1,2), Print('OK'), Print('NOK') );") );

        // Test constant usage in alternatives.
        checkResult( "1"  , interpreter.process("if( Neq(1,2), 1, 2 );") );
        checkResult( "2"  , interpreter.process("if(  Eq(1,2), 1, 2 );") );

        // Test variable usage in alternatives.
        checkResult( "1"  , interpreter.process("a=1, b=2.5, if( Neq(a,b), a, b );") );
        checkResult( "2.5", interpreter.process("a=1, b=2.5, if( Eq (a,b), a, b );") );
    }

    /**
     * Tests for control.
     *
     * @throws InterpreterException
     */
    public void testFor() throws InterpreterException {
        checkResult( "[1,2,3]", interpreter.process("l=List(1, 2, 3), for( l , v, Print(v) );") );
    }

    /**
     * Tests native HTTP Get request.
     *
     * @throws InterpreterException
     */
    public void testNativeGet() throws InterpreterException {
        Value value = interpreter.process(" Get('http://www.bbc.co.uk', List() ); ");
        String content = value.asString().getNativeValue();
        System.out.println("Value: " + content);
        assertTrue("Cannot find an expected content.", content.indexOf("BBC") != -1 &&  content.indexOf("Homepage") != -1 );
    }

    /**
     * Tests native HTTP Post request.
     *
     * @throws InterpreterException
     */
    public void testNativePost() throws InterpreterException {
        Value value = interpreter.process(" Post('http://www.bbc.co.uk', List() ); ");
        String content = value.asString().getNativeValue();
        System.out.println("Value: " + content);
        assertTrue("Cannot find an expected content.", content.indexOf("BBC") != -1 &&  content.indexOf("Homepage") != -1 );
    }

    /**
     * Tests the <i>XPath</i> operator.
     */
    public void testXPath() throws InterpreterException {
        Value value = interpreter.process(" x = Get('http://www.bbc.co.uk', List() ), XPath('/html/head/title', x); ");
        System.out.println( "value: " + value.asString().getNativeValue() );
        ListValue lv  = (ListValue) value;
        List<Value> l = lv.getNativeValue();
        assertEquals("Unespected result size.", 1, l.size() );
        String result = l.get(0).asString().getNativeValue();
        assertTrue("Unespected result.", result.indexOf("BBC")      != -1 );
        assertTrue("Unespected result.", result.indexOf("Homepage") != -1 );
    }

    /**
     * Tests jsonize function.
     *
     * @throws InterpreterException
     */
    public void testJSonize() throws InterpreterException {
        Value json = interpreter.process("Jsonize('{ \"a1\" : %s, \"a2\" : %s, \"a3\" : [%s] }', List('v1', 'v2', 'v3') );");
        String content = json.asString().getNativeValue();
        System.out.println("Value: " + content);
        compareJsonObjects("Cannot find expected content.", "{\"a1\":\"v1\",\"a2\":\"v2\",\"a3\":[\"v3\"]}", content);
    }

    /**
     * Tests definition of a sequence.
     *
     * @throws InterpreterException
     */
    public void testDefinition() throws InterpreterException {
        Value v = interpreter.process( "PredicateDeclaration( varin1, varin2 ) : PredicateName1 ( varin1, varin2, 'const1' ), PredicateName2 ( 'const2', varin2 ); " );
        assertEquals( "Unespected result.", InvocableValue.class, v.getClass() );
    }

    /**
     * Tests validation in invocation with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testValidationInInvocation() {
        try {
            interpreter.process( " P1(v1) : P2(vX); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

    /**
     * Tests validation in if condition with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testValidationInIfCondition() {
        try {
            interpreter.process( " P1(v1) : if( Cond(vX), P1(v1), P2(v1) ); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

    /**
     * Tests validation in if positive statement with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testDeclarationValidationInIfPositiveStatement() {
        try {
            interpreter.process( " P1(v1) : if( Cond(v1), P1(vX), P2(v1) ); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

   /**
     * Tests validation in if negative statement with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testDeclarationValidationInIfNegativeStatement() {
        try {
            interpreter.process( " P1(v1) : if( Cond(v1), P1(v1), P2(vX) ); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

    /**
     * Tests validation in for statement with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testDeclarationValidationInForListStatement() {
        try {
            interpreter.process( " P1(v1) : for( L(vX), var, P(var) ); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

    /**
     * Tests validation in for statement with undefined variables.
     *
     * @throws InterpreterException
     */
    public void testDeclarationValidationInForStatement() {
        try {
            interpreter.process( " P1(v1) : for( L(vX), var, P(varX) ); " );
            fail("Expected failure");
        } catch (InterpreterException ie) {
            assertTrue( "Unespected cause.", ie.getCause() instanceof ValidationException );
        }
    }

    /**
     * Tests declaration of const numeric.
     *
     * @throws InterpreterException
     */
    public void testDeclarationOfConstNumeric() throws InterpreterException {
        interpreter.process(" PI() : 3.1415 ; ");
        checkResult(
                "3.1415",
                interpreter.process( "Print( PI() ) ;" )
        );
    }

    /**
     * Tests declaration of const string.
     *
     * @throws InterpreterException
     */
    public void testDeclarationOfConstString() throws InterpreterException {
        interpreter.process(" CONST() : 'important_value' ; ");
        checkResult(
                "important_value",
                interpreter.process( "Print( CONST() ) ;" )
        );
    }

    /**
     * Tests declaration of identity predicate.
     *
     * @throws InterpreterException
     */
    public void testDeclarationOfIdentity() throws InterpreterException {
        interpreter.process(" Identity(x) : x ; ");
        checkResult(
                "myself",
                interpreter.process( "Identity( 'myself' ) ;" )
        );
    }

   /**
     * Tests last value operator.
     *
     * @throws InterpreterException
     */
    public void testLastValue() throws InterpreterException {
        assertEquals("Unespected result.", 9.0, interpreter.process(" x=Sum(1,2), Mult($,$); ").asNumeric().getNativeValue() );
    }

    /**
     * Tests the recursion support.
     *
     * @throws InterpreterException
     */
    public void testRecursionA() throws InterpreterException {
        Value result = interpreter.process(" Factorial(n) : if( Eq(n,0), 1, Mult(n, Factorial( Diff(n,1) ) ) ); Factorial(6);");
        int v = result.asNumeric().integer();
        System.out.println("Result: " + v);
        assertEquals("Unespected result.", 720, v );
    }

    /**
     * Tests the recursion support.
     * 
     * @throws InterpreterException
     */
    public void testRecursionB() throws InterpreterException {
        interpreter.process("ListLenght([]) : 0; ListLenght([head|tail]) : Sum(1,ListLenght(tail));");
        Value result = interpreter.process("ListLenght( List(1,2,3,4,5,6,7,8,9,10) );");
        System.out.println("Result: " + result);
        assertEquals("Unespected Result.", 10, result.asNumeric().integer() );
    }

    /**
     * Tests the predicate logic connectors.
     *
     * @throws InterpreterException
     */
    public void testPredicateLogicConnectors() throws InterpreterException {

        // false OR ? test.
        Value result1 = interpreter.process("Eq(1,2) | Print('OK');");
        checkResult("OK", result1);

        // true OR ? test.
        Value result2 = interpreter.process("Eq(2,2) | Print('OK');");
        checkResult("true", result2);

        // true AND ? test.
        Value result3 = interpreter.process(" Print('OK');");
        checkResult( "OK", result3);

        // false AND ? test.
        Value result4 = interpreter.process("Eq(1,2) & Print('OK');");
        checkResult("false", result4);
    }

    /**
     * Tests Unification support with empty list.
     *
     * @throws InterpreterException
     */
    public void testUnificationWithEmptyList() throws InterpreterException {
        String model = " []";
        String data  = "List()";

        Value result = interpreter.process( " Unify('" + model + "'," + data + "); " );
        System.out.println("Result: " + result);
        assertTrue("Expected unification here.", result.asString().getNativeValue().indexOf("succeeded") != -1);
    }

    /**
     * Tests Unification support.
     *
     * @throws InterpreterException
     */
    public void testUnification() throws InterpreterException {
        String model = " [v1, v2, {\"k1\" : v3 | tail1} | tail2 ]";
        String data  = "List( 'a', 'b', Map('k1', 'value1', 'k2', 'value2'), 'c', 'd' )";

        Value result = interpreter.process( " Unify('" + model + "'," + data + "); " );
        System.out.println("Result: " + result);
        assertTrue("Expected unification here.", result.asString().getNativeValue().indexOf("succeeded") != -1);
    }

    /**
     * Tests Unification support with constants.
     *
     * @throws InterpreterException
     */
    public void testUnificationWithConstants() throws InterpreterException {
        String model = " [v1, v2, {\"k1\" : 'value1' | tail1} | tail2 ]";
        String data  = "List( 'a', 'b', Map('k1', 'value1', 'k2', 'value2'), 'c', 'd' )";

        Value result = interpreter.process( " Unify('" + model + "'," + data + "); " );
        System.out.println("Result: " + result);
        assertTrue("Expected unification here.", result.asString().getNativeValue().indexOf("succeeded") != -1);
    }

    /**
     * Tests <i>Content</i> operation.
     *
     * @throws InterpreterException
     */
    public void testContentHTML() throws InterpreterException {
        String program = " Get('http://www.bbc.co.uk/', List() ), Content($);";
        Value result = interpreter.process( program );
        System.out.println("Result: " + result);
        checkResult("HTML", result);
    }

    /**
     * Tests <i>Content</i> operation.
     *
     * @throws InterpreterException
     */
    public void testContentXML() throws InterpreterException {
        String program = " Get('http://www.repubblica.it/rss/homepage/rss2.0.xml', List() ), Content($);";
        Value result = interpreter.process( program );
        System.out.println("Result: " + result);
        checkResult("XML", result);
    }

    /**
     * Tests the applicability of unification models as predicate arguments.
     *
     * @throws InterpreterException
     */
    public void testDeclarationOfPredicateWithModelParameter() throws InterpreterException {
        // Declaration 1.
        String declaration1 = "ModelPredicate([x,y|a]) : Print('Hello ', x, ',', y, ' and', a, '!');";
        Value result1 = interpreter.process(declaration1);
        System.out.println("Declaration result: " + result1);

        // Invocation 1.
        String invocation2 = "ModelPredicate(List(1,2,3,4));";
        Value result2 = interpreter.process(invocation2);
        System.out.println("Invocation result: " + result2);
        checkResult("Hello 1,2 and[3,4]!", result2);

        // Declaration 2.
        String declaration3 = "ModelPredicate({\"x\" : a, \"y\" : b}) : Print('Hello ', a, ',', b, '!');";
        Value result3 = interpreter.process(declaration3);
        System.out.println("Declaration result: " + result3);

        // Invocation 1.
        String invocation4 = "ModelPredicate(Map('x','1','y','2'));";
        Value result4 = interpreter.process(invocation4);
        System.out.println("Invocation result: " + result4);
        checkResult("Hello 1,2!", result4);
    }

    /**
     * Tests the predicate overloading with discrimination based on argument unification. 
     *
     * @throws InterpreterException
     */
    public void testPredicateOverloading() throws InterpreterException {

        // Test array overload.
        String overload1 = "OL([a,b,c]  , v) : Print('overload1');";
        String overload2 = "OL([a,b,c,d], v) : Print('overload2');";

        interpreter.process(overload1);
        interpreter.process(overload2);

        String invocation1 = "OL(List(1,2,3) , 4);";
        Value result1 = interpreter.process(invocation1);
        checkResult("overload1", result1);

        String invocation2 = "OL(List(1,2,3,4), 5);";
        Value result2 = interpreter.process(invocation2);
        checkResult("overload2", result2);

        // Test object overload.
        String overload3 = "OL({\"k1\" : v1, \"k2\" : v2}  , v) : Print('overload3');";
        String overload4 = "OL({\"k1\" : v1, \"k2\" : v2, \"k3\" : v3}  , v) : Print('overload4');";

        interpreter.process(overload3);
        interpreter.process(overload4);

        String invocation3 = "OL(Map('k1',1,'k2',2) , 4);";
        Value result3 = interpreter.process(invocation3);
        checkResult("overload3", result3);

        String invocation4 = "OL(Map('k1',1,'k2',2,'k3',3), 5);";
        Value result4 = interpreter.process(invocation4);
        checkResult("overload4", result4);
    }

    /**
     * Test the model assigment capabilities with terms.
     *
     * @throws InterpreterException
     */
    public void testModelAssignmentInTerm() throws InterpreterException {
         String termAssignment = "[a,b,c]=List(1,2,3), Print(a,b,c);";
         Value result = interpreter.process(termAssignment);
         System.out.println("Result: " + result);
         checkResult("123", result);
     }

    /**
     * Test the model assigment capabilities inside invocations.
     *
     * @throws InterpreterException
     */
     public void testModelAssignmentInInvocation() throws InterpreterException {
         String termAssignment = "Print(l = List(1,2), l);";
         Value result = interpreter.process(termAssignment);
         System.out.println("Result: " + result);
         checkResult("[1,2][1,2]", result);
     }

    /**
     * Tests the predicate invocation with const unification.
     *
     * @throws InterpreterException
     */
    public void testPredicateInvocationConstUnification() throws InterpreterException {

        // Test array overload.
        String predicate = "P(3) : Print('const_predicate');";
        interpreter.process(predicate);

        // Positive case.
        String invocation1 = "P(3);";
        Value result1 = interpreter.process(invocation1);
        assertEquals("Unespected overload.", "const_predicate", result1.asString().getNativeValue());

        // Negative case.
        String invocation2 = "P(1);";
        try {
            interpreter.process(invocation2);
            fail("Expected failure here.");
        } catch (InterpreterException ie) {
            // OK.
        }
    }

    /**
     * Tests the <i>JPath</i> operation.
     *
     * @throws InterpreterException
     */
    public void testJPathOperation() throws InterpreterException {
        String predicate = "JPath( List(1,2,Map('a',10,'b',20)), '[2].b' );";
        Value result = interpreter.process(predicate);
        System.out.println("Result: " + result.asString().getNativeValue());
        checkResult( 20, result );
    }

    /**
     * Tests the <i>Apply()</i> operation.
     *
     * @throws InterpreterException
     */
    public void testApplyOperation() throws InterpreterException {
        String applied = "A(x): Print('<', x, '>');";
        String predicate = "Apply('A', Map('a', 1, 'b', true, 'c', List(1,2,'false')));";
        interpreter.process(applied);
        Value result = interpreter.process(predicate);
        Iterator<JsonBase> iterator = ((ListValue) result).iterator();
        final String ERROR_MSG = "Unespected element.";
        assertEquals(
                ERROR_MSG,
                "\"<{&#34;a&#34;:1,&#34;c&#34;:[1,2,false],&#34;b&#34;:true}>\"",
                iterator.next().asJSON()
        );
        assertEquals(ERROR_MSG, "\"<1>\"", iterator.next().asJSON());
        assertEquals(ERROR_MSG, "\"<[1,2,false]>\"", iterator.next().asJSON());
        assertEquals(ERROR_MSG, "\"<true>\"", iterator.next().asJSON());
        assertEquals(ERROR_MSG, "\"<1>\"", iterator.next().asJSON());
        assertEquals(ERROR_MSG, "\"<2>\"", iterator.next().asJSON());
        assertEquals(ERROR_MSG, "\"<false>\"", iterator.next().asJSON());
    }

    /**
     * Tests the <i>LSize(list)</i> operation.
     */
    public void testLSizeOperation() throws InterpreterException {
        String code = "LSize( List(1,'2', true, List('inner') ) );";
        Value result = interpreter.process(code);
        assertEquals("Unespected result for list size.", 4, result.asNumeric().integer());
    }

    /**
     * Tests the <i>MSize(map)</i> operation.
     */
    public void testMSizeOperation() throws InterpreterException {
        String code = "MSize( Map('k1','v1', 'k2', 'v2', 'k3', List('inner') ) );";
        Value result = interpreter.process(code);
        assertEquals("Unespected result for list size.", 3, result.asNumeric().integer());
    }

    /**
     * Tests the Graph support.
     *
     * @throws InterpreterException
     */
    public void testGraph() throws InterpreterException {
        // Empty graph.
        checkResult( "{\"arcs\":[],\"labels\":{},\"nodes\":{}}", interpreter.process("Graph();") );

        // Add node.
        checkResult(
                "{\"arcs\":[],\"labels\":{},\"nodes\":{\"S0\":\"node1\"}}",
                interpreter.process("g = Graph(), GAddNode(g,'node1');")
        );

        // Remove node.
        checkResult(
                "{\"arcs\":[],\"labels\":{},\"nodes\":{}}", 
                interpreter.process("g = Graph(), GAddNode(g,'node1'), GRemoveNode(g, 'node1');")
        );

        // Add arc.
        checkResult(
                "{\"arcs\":[{\"S1\":{\"S0\":\"S0\"}}],\"labels\":{\"S0\":\"label\"},\"nodes\":{\"S0\":\"to\",\"S1\":\"from\"}}",
                interpreter.process("g = Graph(), GAddArc(g, 'from', 'label', 'to');")
        );

        // Remove arc.
        checkResult(
                "{\"arcs\":[],\"labels\":{},\"nodes\":{\"S0\":\"to\",\"S1\":\"from\"}}",
                interpreter.process("g = Graph(),  GAddArc(g, 'from', 'label', 'to'),  GRemoveArc(g, 'from', 'label', 'to');")
        );
    }

    /**
     * Tests the varargs support.
     * 
     * @throws InterpreterException
     */
    public void testVarargsSupport() throws InterpreterException {
        // Defines the predicate with varargs.
        interpreter.process("VAPredicate(v1, v2, _) : Print('v1:', v1, ' v2:', v2), Print(' varargs:'), for(_, va, Print(' ', va)); ");

        // Case with rest.
        Value result1 = interpreter.process("VAPredicate('a','b', 'c', 'd', 'e', 'f');");
        System.out.println();
        System.out.println("Result1: " + result1);
        checkResult("[\" c\",\" d\",\" e\",\" f\"]", result1.asString());

        // Case with no rest.
        Value result2 = interpreter.process("VAPredicate('a','b');");
        System.out.println();
        System.out.println("Result2: " + result2);
        checkResult("[]", result2.asString());

        // Case with insufficied arguments.
        try {
            interpreter.process("VAPredicate('a');");
            fail("Expected failure.");
        } catch (Exception e) {
            // OK.
            assertTrue( "Unespected cause.", e.getCause().getCause() instanceof SequenceNotFoundException );
        }

        // Verifies the varargs access.
        interpreter.process("VAAccessPredicate(_) : Print(_5, _4, _3, _2, _1, _0);");
        Value result3 = interpreter.process("VAAccessPredicate('a', 'b', 'c', 'd', 'e');");
        System.out.println();
        System.out.println("Result3: " + result3);
        checkResult( "nulledcba", result3.asString() );

    }

    /**
     * Tests the <i>Modelize()</i> --> <i>Concretize()</i> flow.
     */
    public void testModelizeConcretize() throws InterpreterException {
        final String inputData = "List('this', 'is', 'a', 'list')";

        Value result1 = interpreter.process("l = " + inputData + ", m = Modelize(l), Concretize(m);");
        System.out.println( "Result1:" + result1.asString().getNativeValue() );

        Value result2 = interpreter.process("Renderize(" + inputData +");");
        System.out.println("Result2:" + result2.asString().getNativeValue()  );

        JsonDiff diff = JsonUtil.difference(
                result1.asJsonValue().getJsonBase(),
                result2.asJsonValue().getJsonBase()
        );

        System.out.println("Difference:\n" + diff);
        List<JsonDiff.Issue> issues = diff.getIssues();

        // Expected different values just for {width|height}
        final int EXPECTED_ISSUES = 12;
        assertEquals( "unespected number of issues.", EXPECTED_ISSUES, issues.size());

        // First four couples.
        for(int i = 0; i < 4; i++) {
            assertContains( "unespected issue.", "UILabel.width" , issues.get(i * 2).getLocation() );
            assertContains( "unespected issue.", "UILabel.height", issues.get(i * 2 + 1).getLocation() );
        }

        // Last 2 couples.
        for(int i = 4; i < 6; i++) {
            assertContains( "unespected issue.", "UIContainer.width" , issues.get(i * 2).getLocation() );
            assertContains( "unespected issue.", "UIContainer.height", issues.get(i * 2 + 1).getLocation() );
        }
    }

    /**
     * Tests the <i>Clone()</i> operation.
     *
     * @throws InterpreterException
     */
    public void testCloneOperation() throws InterpreterException {
        final String program =
                "g = Graph( )," +
                "GAddArc( g, 'from', 'label', 'to' )," +
                "g1 = Clone( g )," +
                "Eq( g, g1 )," +
                "GAddArc( g, 'from', 'label2', 'to' )," +
                "Neq( g, g1 );";
        BooleanValue bv = (BooleanValue) interpreter.process(program);
        assertTrue("Unespected result.", bv.getNativeValue() );
    }

    /**
     * Tests the graph support version 1.
     *
     * @throws InterpreterException
     */
    public void testGraphSupport1() throws InterpreterException {
        final String program =
                "MyGraph() : <'sub', 'pred', <'a', 'b', 'c'; 'c', 'd', 'e'> > ;" +
                "MyGraph();";
        final String expectedResult =
                "{\"arcs\":[{\"S1\":{\"G0\":\"S0\"}}],\"labels\":{\"G0\":{\"arcs\":[{\"S2\":{\"S1\":\"S0\"}},{\"S1\":{\"S0\":\"S3\"}}],\"labels\":{\"S0\":\"c\",\"S1\":\"e\"},\"nodes\":{\"S2\":\"c\",\"S0\":\"d\",\"S3\":\"b\",\"S1\":\"a\"}}},\"nodes\":{\"S0\":\"pred\",\"S1\":\"sub\"}}";
        Value result = interpreter.process(program);
        compareJsonObjects("Unespected result.", expectedResult, result.asJsonValue().toString() );
    }

    /**
     * Tests the graph support version 2.
     *
     * @throws InterpreterException
     */
    public void testGraphSupport2() throws InterpreterException {
        final String program =
                "Print( <'sub', 'pred', <'a', 'b', 'c'; 'c', 'd', 'e'> > );";
        final String expectedResult =
                "{\"arcs\":[{\"S1\":{\"G0\":\"S0\"}}],\"labels\":{\"G0\":{\"arcs\":[{\"S2\":{\"S1\":\"S0\"}},{\"S1\":{\"S0\":\"S3\"}}],\"labels\":{\"S0\":\"c\",\"S1\":\"e\"},\"nodes\":{\"S2\":\"c\",\"S0\":\"d\",\"S3\":\"b\",\"S1\":\"a\"}}},\"nodes\":{\"S0\":\"pred\",\"S1\":\"sub\"}}";
        Value result = interpreter.process(program);
        compareJsonObjects("Unespected result.", expectedResult, result.asJsonValue().toString() );
    }

    /**
     * Tests the renderization of map elements when keys are not strings.
     * 
     * @throws InterpreterException
     */
    public void testRenderizationOfNonStringKeys() throws InterpreterException {
        final String program = "Renderize( Map(1,1) );";
        Value result = interpreter.process(program);
        System.out.println("Result: " + result);
        assertEquals(
                "Unespected result.",
                "{\"UIContainer\":{\"components\":[{\"UIContainer\":{\"components\":[{\"UIContainer\":{\"components\":[{\"UILabel\":{\"text\":\"1.0\",\"width\":24,\"height\":20,\"visible\":true}}],\"width\":34,\"height\":46,\"title\":\"\",\"scrollable\":false,\"orientation\":\"HORIZONTAL\",\"visible\":true}}],\"width\":44,\"height\":56,\"title\":\"\",\"scrollable\":false,\"orientation\":\"VERTICAL\",\"visible\":true}}],\"width\":128,\"height\":66,\"title\":\"\",\"scrollable\":false,\"orientation\":\"HORIZONTAL\",\"visible\":true}}", 
                result.asString().stringValue()
        );
    }

}
