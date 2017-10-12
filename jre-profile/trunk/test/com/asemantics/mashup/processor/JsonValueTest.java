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

import junit.framework.TestCase;

/**
 * Test case used to verify the behavior of {@link com.asemantics.mashup.processor.JsonValue}
 * and related classes.
 */
public class JsonValueTest extends TestCase {



    @Override
    protected void setUp() throws Exception {
        // Empty.
    }

    @Override
    protected void tearDown() throws Exception {
        // Empty.
    }

    /**
     * Tests the NullValue --> JsonValue --> NullValue round trip.
     */
    public void testNullRoundTrip() {
       final NullValue original  = NullValue.getInstance();
       final JsonValue converted = original.asJsonValue();
       final NullValue reverted  = converted.asNull();
       assertEquals("Inverted should be equals to original.", original, reverted);
    }

    /**
     * Tests the BooleanValue --> JsonValue --> BooleanValue round trip.
     */
    public void testBooleanRoundTrip() {
        // FALSE.
        final JsonValue converted1 = BooleanValue.FALSE_VALUE.asJsonValue();
        final BooleanValue reverted1 = converted1.asBoolean();
        assertEquals("Inverted should be equals to original.", BooleanValue.FALSE_VALUE, reverted1);

        // TRUE.
        final JsonValue converted2 = BooleanValue.TRUE_VALUE.asJsonValue();
        final BooleanValue reverted2 = converted2.asBoolean();
        assertEquals("Inverted should be equals to original.", BooleanValue.TRUE_VALUE, reverted2);
    }

    /**
     * Tests the NumericValue --> JsonValue --> NumericValue round trip.
     */
    public void testNumericRoundTrip() {
        // Integer.
        final NumericValue original1 = new NumericValue(10);
        final JsonValue converted1 = original1.asJsonValue();
        final NumericValue reverted1 = converted1.asNumeric();
        assertEquals("Inverted should be equals to original.", original1, reverted1);

        // Float.
        final NumericValue original2 = new NumericValue(10.7);
        final JsonValue converted2 = original2.asJsonValue();
        final NumericValue reverted2 = converted2.asNumeric();
        assertEquals("Inverted should be equals to original.", original2,  reverted2);
    }

    /**
     * Tests the StringValue --> JsonValue --> StringValue round trip.
     */
    public void testStringRoundTrip() {
        final StringValue original = new StringValue("this is a string.");
        final JsonValue converted = original.asJsonValue();
        final StringValue reverted = converted.asString();
        assertEquals("Inverted should be equals to original.", original.getNativeValue(), reverted.getNativeValue());
    }

    /**
     * Tests the ListValue --> JsonValue --> ListValue round trip.
     */
    public void testListRoundTrip() {
        final ListValue original = new ListValue();
        original.add( new BooleanValue(true)   );
        original.add( new StringValue("false") );
        original.add( new NumericValue(10.4)   );
        final JsonValue converted = original.asJsonValue();
        final ListValue reverted = converted.asList();
        assertEquals("Inverted should be equals to original.", original, reverted);
    }

    /**
     * Tests the MapValue --> JsonValue --> MapValue round trip.
     */
    public void testMapRoundTrip() {
        final MapValue original = new MapValue();
        original.put( new BooleanValue(false) , new BooleanValue(true)  );
        original.put( new StringValue("false"), new StringValue("true") );
        original.put( new NumericValue(10.4) , new NumericValue(3)      );
        JsonValue converted = original.asJsonValue();
        MapValue reverted   = converted.asMap();
        assertEquals("Inverted should be equals to original.", original, reverted);
    }

    /**
     * Tests the JsonValue --> JsonValue --> JsonValue round trip. (identity)
     */
    public void testJsonRoundTrip() {
        final MapValue mapValue = new MapValue();
        mapValue.put( new BooleanValue(false) , new BooleanValue(true)  );
        mapValue.put( new StringValue("false"), new StringValue("true") );
        mapValue.put( new NumericValue(10.4) , new NumericValue(3)      );
        final JsonValue original = new JsonValue(mapValue);
        JsonValue converted = original.asJsonValue();
        JsonValue reverted  = converted.asJsonValue();
        assertEquals("Inverted should be equals to original.", original, reverted);
    }

    /**
     * Tests the GraphValue --> JsonValue --> GraphValue round trip.
     */
    public void testGraphRoundTrip() {
        final GraphValue original = new GraphValue();
        original.addArc( new BooleanValue(true), new StringValue("label"), new StringValue("false") );
        original.addArc( new BooleanValue(true), new NumericValue(12.3)  , new StringValue("toNode") );
        final MapValue toNode = new MapValue();
        toNode.put("key", "value");
        original.addArc( new BooleanValue(true), new NumericValue(12.3)  , toNode);

        final JsonValue converted = original.asJsonValue();
        final GraphValue reverted = converted.asGraph();
        assertEquals("Inverted should be equals to original.", original, reverted);
    }


}
