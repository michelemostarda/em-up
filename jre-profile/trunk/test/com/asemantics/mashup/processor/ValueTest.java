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


package com.asemantics.mashup.processor;

import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonObject;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class contains a bunch of tests to verify the behavior of
 * the extensions of {@link com.asemantics.mashup.processor.Value} class.
 */
public class ValueTest extends TestCase {

    /**
     * Boolean equality test.
     */
    public void testBooleanalueEquality() {
        BooleanValue bv1 = new BooleanValue(true);
        BooleanValue bv2 = new BooleanValue(true);
        assertEquals("Expected to be equal.", bv1, bv2);
    }

    /**
     * Numeric equality test.
     */
    public void testNumericValueEquality() {
        NumericValue nv1 = new NumericValue(1);
        NumericValue nv2 = new NumericValue(1);
        assertEquals("Expected to be equal.", nv1, nv2);
    }

    /**
     * String equality test.
     */
    public void testStringValueEquality() {
        StringValue sv1 = new StringValue("some text");
        StringValue sv2 = new StringValue("some text");
        assertEquals("Expected to be equal", sv1, sv2);
    }

    /**
     * JSON equality test.
     */
    public void testJsonValueEquality() {
        StringValue nv1 = new StringValue("1");
        JsonValue jv1 = new JsonValue(nv1);
        StringValue nv2 = new StringValue("1");
        JsonValue jv2 = new JsonValue(nv2);
        assertEquals("Expected to be equal.", jv1, jv2);

        List<JsonValue> jvs = new ArrayList<JsonValue>();
        jvs.add(jv1);
        jvs.add(jv2);

        StringValue nv3 = new StringValue("1");
        JsonValue jv3   = new JsonValue(nv3);
        assertTrue("Expected element.", jvs.contains(jv3));
    }

    /**
     * List equality test.
     */
    public void testListValueEquality() {
        ListValue lv1 = new ListValue( new LinkedList<Value>(
            Arrays.<Value>asList(new BooleanValue(true), new StringValue("str"), new NumericValue(12.3))
        ) );

        ListValue lv2 = new ListValue( new LinkedList<Value>(
            Arrays.<Value>asList(new BooleanValue(true), new StringValue("str"), new NumericValue(12.3))
        ) );

        assertEquals("Expected to be equal.", lv1, lv2);
    }

    /**
     * Map equality test.
     */
    public void testMapValueEquality() {
        MapValue mv1 = new MapValue(
                Arrays.<Value>asList(
                        new BooleanValue(true), new StringValue("str"), new NumericValue(12.3), NullValue.getInstance()
                )
        );
        MapValue mv2 = new MapValue(
                Arrays.<Value>asList(
                        new BooleanValue(true), new StringValue("str"), new NumericValue(12.3), NullValue.getInstance()
                )
        );

        assertEquals("Expected to be equal.", mv1, mv2);
    }

    /**
     * Tests the equality in cloned objects.
     */
    public void testClone() {
        final StringValue[] keys = new StringValue[]{
                new StringValue("boolean"),
                new StringValue("numeric"),
                new StringValue("string"),
                new StringValue("list"),
                new StringValue("map"),
                new StringValue("graph"),
        };

        ListValue listValue = new ListValue();
        listValue.add("lv1");
        listValue.add("lv2");

        MapValue mapValue = new MapValue();

        GraphValue graphValue = new GraphValue();
        graphValue.addArc( new StringValue("from1"), new StringValue("to1"), new StringValue("label1") );
        graphValue.addArc( new StringValue("from2"), new StringValue("to2"), new StringValue("label2") );

        JsonObject object = JsonFactory.newJsonObject();
        object.put("k1", 1);
        object.put("k2", 2);
        object.put("k3", 3);

        final MapValue original = new MapValue();
        original.put( keys[0], new BooleanValue(true) );
        original.put( keys[1], new NumericValue(12.5) );
        original.put( keys[2], new StringValue("this is a string") );
        original.put( keys[3], listValue );
        original.put( keys[4], mapValue  );
        original.put( keys[5], graphValue );

        final MapValue cloned = (MapValue) original.cloneValue();

        for( Value key : keys ) {
            assertTrue("Expected equality for keys.  ", original.get(key).equalsTo( cloned.get(key) ).getNativeValue() );
            assertTrue("Expected different instances for key: " + key + ".", original.get(key) != cloned.get(key) );
        }
    }

    /**
     * Tests the {@link com.asemantics.mashup.processor.Value#equalsTo} method.
     */
    public void testWeakEquality1() {
        // Numeric int.
        StringValue  sv1 = new StringValue("100");
        NumericValue iv = new NumericValue(100);
        assertTrue("Expected to be equal", sv1.equalsTo(iv).boolValue() );
        assertTrue("Expected to be equal", iv.equalsTo(sv1).boolValue() );

        // Numeric float.
        StringValue sv2 = new StringValue("3.1415");
        NumericValue fv = new NumericValue(3.1415);
        assertTrue("Expected to be equal.", sv2.equalsTo(fv).boolValue() );
        assertTrue("Expected to be equal.", fv.equalsTo(sv2).boolValue() );

        // Boolean.
        StringValue sv3 = new StringValue("true");
        assertTrue( "Expected to be equal.", sv3.equalsTo(BooleanValue.getTrueValue()).boolValue() );
        assertTrue( "Expected to be equal.", BooleanValue.getTrueValue().equalsTo(sv3).boolValue() );

        // List.
        StringValue sv4 = new StringValue("[1,\"a\",true]");
        ListValue lv = new ListValue();
        lv.add(1);
        lv.add("a");
        lv.add(true);
        assertTrue("Expected to be equal.", sv4.equalsTo(lv).boolValue());
        assertTrue("Expected to be equal.", lv.equalsTo(sv4).boolValue());

        // Map.
        StringValue sv5 = new StringValue("{\"a\":1}");
        MapValue jv = new MapValue();
        jv.put("a", 1);
        assertTrue( "Expected to be equal.", sv5.equalsTo(jv).boolValue() );
        assertTrue( "Expected to be equal.", jv.equalsTo(sv5).boolValue() );

        // Graph.
        GraphValue gv = new GraphValue();
        StringValue node1 = new StringValue("n1");
        StringValue node2 = new StringValue("n2");
        gv.addNode( node1 );
        gv.addNode( node2 );
        gv.addArc(node1, node2, new StringValue("a1"));
        StringValue sv6 = new StringValue( gv.toString() );
        assertTrue("Expected to be equal.", sv6.equalsTo(gv).boolValue() );
        assertTrue("Expected to be equal.", gv.equalsTo(sv6).boolValue() );
    }

    /**
     * Tests the weak equality on complex structures based on
     * {@link com.asemantics.mashup.processor.Value#equals} override.
     */
    public void testWeakEquality2() {
        // List weak equality.
        List<StringValue> l = new ArrayList<StringValue>(
                Arrays.asList(
                        new StringValue("1"),
                        new StringValue("1.2"),
                        new StringValue("true"),
                        new StringValue("Bla"),
                        new StringValue("[1,2,3]")
                )
        );
        assertTrue("It must contains an object that is weak equals to 1.", l.contains( new NumericValue(1) ));
        assertTrue("It must contains an object that is weak equals to 1.2", l.contains( new NumericValue(1.2) ));
        assertTrue("It must contains an object that is weak equals to true", l.contains(BooleanValue.TRUE_VALUE));
        assertTrue("It must contains an object that is weak equals to Bla", l.contains( new StringValue("Bla") ));
        ListValue lv = new ListValue();
        lv.add(1);
        lv.add(2);
        lv.add(3);
        assertTrue("It must contains an object that is weak equals to [1,2,3]", l.contains(lv));

        // Map weak equality.
        Map<Value,Value> valueMap = new HashMap<Value,Value>();
        valueMap.put( new NumericValue(1), new NumericValue(1) );
        StringValue target = new StringValue("1");
        assertTrue("", valueMap.containsKey(target));

    }

}
