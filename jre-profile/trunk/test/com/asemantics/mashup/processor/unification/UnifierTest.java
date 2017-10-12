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


package com.asemantics.mashup.processor.unification;

import com.asemantics.mashup.processor.*;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.parser.unification.UNParserException;
import com.asemantics.mashup.parser.ValidationException;
import junit.framework.TestCase;

import java.util.HashSet;

/**
 * Test case for {@link com.asemantics.mashup.processor.unification.Unifier} class.
 */
public class UnifierTest extends TestCase {

    private Unifier unifier;

    protected void setUp() throws Exception {
        unifier = new Unifier();
    }

    protected void tearDown() throws Exception {
        unifier = null;
    }

    /**
     * Tests basic variable unification: var =:= "value"
     */
    public void testVariableUnification() {
        final String VAR_NAME  = "var";
        final String VAR_VALUE = "value";
        VariableModelElement variableModelElement = new VariableModelElement(VAR_NAME);
        StringValue value = new StringValue(VAR_VALUE);
        UnificationResult unificationResult = unifier.unify(variableModelElement, value.asJsonValue() );
        System.out.println(unificationResult);
        assertFalse("Unification expected here.", unificationResult.isFailed());
        assertEquals("Wrong unification result.", "\"" + VAR_VALUE + "\"",  unificationResult.getValue(VAR_NAME).asJSON() );
    }

    /**
     * Tests empty list unification.
     *
     * [] =:= []
     */
    public void testUnificationWithEmptyList() {
        // Data.
        JsonArray jsonArray = new ListValue();

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());
    }

    /**
     *  Tests rest with empty list unification.
     *
     * [head|rest] =:= [value] => head = value, rest = []
     */
    public void testRestWithEmptyListUnification() {
        // Data.
        JsonArray jsonArray = new ListValue();
        Value d1 = new StringValue("a");
        jsonArray.add( d1 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("head");
        jsonArrayModel.setRemainingVariable("rest");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("head"));
        assertEquals("Unespected unification.", new ListValue(), unificationResult.getValue("rest"));
    }


    /**
     * [v1,v2] =!= [1, "2", null]
     */
    public void testMissingUnificationOverArray() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");
        Value d3 = NullValue.getInstance();

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );
        jsonArray.add( d3 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addElement("v2");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertTrue("Unification must fail.", unificationResult.isFailed());
    }

    /**
     * [v1,v2,v3] =:= [1, "2", null]
     */
    public void testBasicUnificationOverArray() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");
        Value d3 = NullValue.getInstance();

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );
        jsonArray.add( d3 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addElement("v2");
        jsonArrayModel.addElement("v3");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));
        assertEquals("Unespected unification.", d3.asJsonValue().getJsonBase(), unificationResult.getValue("v3"));
    }

    /**
     * [v1,v2,v3|tail] =:= ["a", "b", "c", "d", "e"]
     */
    public void testBasicUnificationOverArrayWithTail() {

        Value d1 = new StringValue("a");
        Value d2 = new StringValue("b");
        Value d3 = new StringValue("c");
        Value d4 = new StringValue("d");
        Value d5 = new StringValue("e");

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );
        jsonArray.add( d3 );
        jsonArray.add( d4 );
        jsonArray.add( d5 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addElement("v2");
        jsonArrayModel.addElement("v3");
        jsonArrayModel.setRemainingVariable("tail");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));
        assertEquals("Unespected unification.", d3.asJsonValue().getJsonBase(), unificationResult.getValue("v3"));

        assertTrue("Unespected tail type.", unificationResult.getValue("tail") instanceof JsonArray);
        JsonArray tail = (JsonArray)  unificationResult.getValue("tail");
        assertEquals("Unespected tail size.", 2, tail.size() );
        assertEquals("Unespected tail content.", d4, tail.get(0));
        assertEquals("Unespected tail content.", d5, tail.get(1));
    }

    /**
     * [v1,v2,[v3,v4], v5] =:= [1, "a", ["b","c"], {"k": "v"}]
     */
    public void testNestedUnificationOverArray() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");
        Value d3 = new StringValue("b");
        Value d4 = new StringValue("c");
        MapValue d5 = new MapValue();
        d5.put("k","v");

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );
        JsonArray innerJsonArray = new ListValue();
        innerJsonArray.add( d3 );
        innerJsonArray.add( d4 );
        jsonArray.add( innerJsonArray );
        jsonArray.add( d5 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addElement("v2");
        JsonArrayModel innerJsonArrayModel = new JsonArrayModel();
        innerJsonArrayModel.addElement("v3");
        innerJsonArrayModel.addElement("v4");
        jsonArrayModel.addElement(innerJsonArrayModel);
        jsonArrayModel.addElement("v5");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));
        assertEquals("Unespected unification.", d3.asJsonValue().getJsonBase(), unificationResult.getValue("v3"));
        assertEquals("Unespected unification.", d4.asJsonValue().getJsonBase(), unificationResult.getValue("v4"));
        assertEquals("Unespected unification.", d5.asJsonValue().getJsonBase(), unificationResult.getValue("v5"));
    }

    /**
     * { "k1" : v1, "k2" : v2, "k3" : v3 } =:= { "k1" : 1, "k2" : "2", "k3" : null }
     */
    public void testBasicUnificationOverObject() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");
        Value d3 = NullValue.getInstance();

        // Data.
        JsonObject jsonObject = new MapValue();
        jsonObject.put("k1", d1);
        jsonObject.put("k2", d2);
        jsonObject.put("k3", d3);

        // Model.
        JsonObjectModel jsonObjectModel = new JsonObjectModel();
        jsonObjectModel.putElement("k1", "v1");
        jsonObjectModel.putElement("k2", "v2");
        jsonObjectModel.putElement("k3", "v3");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonObjectModel, new JsonValue(jsonObject) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));
        assertEquals("Unespected unification.", d3.asJsonValue().getJsonBase(), unificationResult.getValue("v3"));
    }

    /**
     * { "k1" : v1, "k2" : v2 | tail } =:= { "k1" : "a", "k2" : "b", "k3" : "c", "k4" : "d" }
     */
    public void testBasicUnificationOverObjectWithTail() {

        Value d1 = new StringValue("a");
        Value d2 = new StringValue("b");
        Value d3 = new StringValue("c");
        Value d4 = new StringValue("d");

        // Data.
        JsonObject jsonObject = new MapValue();
        jsonObject.put("k1", d1);
        jsonObject.put("k2", d2);
        jsonObject.put("k3", d3);
        jsonObject.put("k4", d4);

        // Model.
        JsonObjectModel jsonObjectModel = new JsonObjectModel();
        jsonObjectModel.putElement("k1", "v1");
        jsonObjectModel.putElement("k2", "v2");
        jsonObjectModel.setRemainingVariable("tail");

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonObjectModel, new JsonValue(jsonObject) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));

        assertTrue("Unespected tail type.", unificationResult.getValue("tail") instanceof JsonObject);
        JsonObject jsonObjectTail = (JsonObject) unificationResult.getValue("tail");
        assertEquals("Unespected tail size.", 2, jsonObjectTail.size());
        assertEquals("Unespected content.", d3.asJsonValue().getJsonBase(), jsonObjectTail.get("k3") );
        assertEquals("Unespected content.", d4.asJsonValue().getJsonBase(), jsonObjectTail.get("k4") );
    }

    /**
     * Test string model usage with array.
     */
    public void testModelStringWithArray() {
        Value d1 = new StringValue("a");
        Value d2 = new StringValue("b");
        Value d3 = new StringValue("c");
        Value d4 = new StringValue("d");
        Value d5 = new StringValue("e");

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );
        jsonArray.add( d3 );
        jsonArray.add( d4 );
        jsonArray.add( d5 );

        UnificationResult unificationResult = unifier.unify("[v1, v2, v3 | tail ]", new JsonValue(jsonArray) );
        System.out.println("Result: " + unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));
        assertEquals("Unespected unification.", d3.asJsonValue().getJsonBase(), unificationResult.getValue("v3"));

        assertTrue("Unespected tail type.", unificationResult.getValue("tail") instanceof JsonArray);
        JsonArray tail = (JsonArray)  unificationResult.getValue("tail");
        assertEquals("Unespected tail size.", 2, tail.size() );
        assertEquals("Unespected tail content.", d4, tail.get(0));
        assertEquals("Unespected tail content.", d5, tail.get(1));
    }

    /**
     * Test string model usage with object.
     */
    public void testModelStringWithObject() {
        Value d1 = new StringValue("a");
        Value d2 = new StringValue("b");
        Value d3 = new StringValue("c");
        Value d4 = new StringValue("d");
        Value d5 = new StringValue("e");

        // Data.
        JsonObject jsonObject = new MapValue();
        jsonObject.put( "k1", d1 );
        jsonObject.put( "k2", d2 );
        jsonObject.put( "k3", d3 );
        jsonObject.put( "k4", d4 );
        jsonObject.put( "k5", d5 );

        UnificationResult unificationResult = unifier.unify("{ \"k1\" : v1, \"k2\" : v2 | tail } ", new JsonValue(jsonObject) );
        System.out.println("Result: " + unificationResult);
        assertFalse( "Unification failed.", unificationResult.isFailed() );

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));
        assertEquals("Unespected unification.", d2.asJsonValue().getJsonBase(), unificationResult.getValue("v2"));

        assertTrue("Unespected tail type.", unificationResult.getValue("tail") instanceof JsonObject);
        JsonObject jsonObjectTail = (JsonObject) unificationResult.getValue("tail");
        assertEquals("Unespected tail size.", 3, jsonObjectTail.size());
        assertEquals("Unespected content.", d3.asJsonValue().getJsonBase(), jsonObjectTail.get("k3") );
        assertEquals("Unespected content.", d4.asJsonValue().getJsonBase(), jsonObjectTail.get("k4") );
        assertEquals("Unespected content.", d5.asJsonValue().getJsonBase(), jsonObjectTail.get("k5") );
    }

    /**
     * Tests positive unification with constant terms.
     *
     * [v1, "a"] =:= [1,"a"]
     *
     */
    public void testPositiveUnificationWithConstants() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addConstantElement( new StringValue("a") );

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertFalse("Unification failed.", unificationResult.isFailed());

        assertEquals("Unespected unification.", d1.asJsonValue().getJsonBase(), unificationResult.getValue("v1"));

    }

    /**
     * Tests negative unification with constant terms.
     *
     * [v1, "a"] =!= [1,"b"]
     *
     */
    public void testNegativeUnificationWithConstants() {

        Value d1 = new NumericValue(1);
        Value d2 = new StringValue("a");

        // Data.
        JsonArray jsonArray = new ListValue();
        jsonArray.add( d1 );
        jsonArray.add( d2 );

        // Model.
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        jsonArrayModel.addElement("v1");
        jsonArrayModel.addConstantElement( new StringValue("b") );

        // Unification.
        UnificationResult unificationResult = unifier.unify(jsonArrayModel, new JsonValue(jsonArray) );
        System.out.println(unificationResult);
        assertTrue("Unification must fail.", unificationResult.isFailed());

    }

    /**
     * Tests positive validation feature.
     *
     * @throws com.asemantics.mashup.parser.unification.UNParserException
     */
    public void testPositiveValidation() throws UNParserException, ValidationException {
        JsonModel model1 = unifier.compileModel("[a, b, c | d]");
        try {
            model1.validate( new HashSet<String>() );
        } catch (JsonModelException jmve) {
            fail("Expected validation here.");
        }

        JsonModel model2 = unifier.compileModel("{\"k1\" : v1, \"k2\" : v2 | d}");
        try {
            model2.validate( new HashSet<String>() );
        } catch (JsonModelException jmve) {
            fail("Expected validation here.");
        }

    }

    /**
     * Tests negative validation feature.
     *
     * @throws com.asemantics.mashup.parser.unification.UNParserException
     */
    public void testNegativeValidation() throws UNParserException, ValidationException {
        JsonModel model1 = unifier.compileModel("[a, b, c | a]");
        try {
            model1.validate( new HashSet<String>() );
            fail("Expected validation failure here.");
        } catch (ValidationException ve) {
            assertTrue("Unespected cause.", ve.getCause() instanceof JsonModelException);
        }

        JsonModel model2 = unifier.compileModel("{\"k1\" : v1, \"k2\" : v2 | k1}");
        try {
            model2.validate( new HashSet<String>() );
            fail("Expected validation failure here.");
        } catch (ValidationException ve) {
            assertTrue("Unespected cause.", ve.getCause() instanceof JsonModelException);
        }

        JsonModel model3 = unifier.compileModel("{\"k1\" : v1, \"k2\" : v2 | v1}");
        try {
            model3.validate( new HashSet<String>() );
            fail("Expected validation failure here.");
        } catch (ValidationException ve) {
            assertTrue("Unespected cause.", ve.getCause() instanceof JsonModelException);
        }

    }
}
