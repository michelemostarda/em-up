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

import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import junit.framework.TestCase;

/**
 * Tests for class {@link com.asemantics.mashup.interpreter.Utils}.
 */
public class UtilsTest extends TestCase {

    /**
     * Tests #expandsString with less params than markers, waiting for exception.
     */
    public void testExpandLessParametersThanMarkers() {
        try {
            String result = Utils.expandsString("this %s a test %s", new String[]{} );
            fail("Expected exception here.");
        } catch (RuntimeException re) {
            // OK.
        }
    }

    /**
     * Tests #expandsString with more parameters than markers, no issues.
     */
    public void testExpandMoreParametersThanMarkers() {
        String result = Utils.expandsString("this %S a very %S test %S I guess", new String[]{"is", "complex", "string", "and more..."} );
        assertEquals("Unespected expansion.", "this is a very complex test string I guess", result);
    }

    /**
     * Tests no markers.
     */
    public void testExpandNoMarkers() {
        String result = Utils.expandsString("this is a test", new String[]{} );
        assertEquals("Unespected expansion.", "this is a test", result);
    }

    /**
     * Tests no markers but paramters.
     */
    public void testExpandNoMarkersButParameters() {
        String result = Utils.expandsString("this is a test", new String[]{"useless"} );
        assertEquals("Unespected expansion.", "this is a test", result);
    }

    /**
     * Tests many parameters as markers.
     */
    public void testExpandParameters() {
        String result = Utils.expandsString("this %S a very %S test %S", new String[]{"is", "complex", "string"} );
        assertEquals("Unespected expansion.", "this is a very complex test string", result);
    }

    /**
     * Tests #expandsString(String,Value[]) with less params than markers, waiting for exception.
     */
    public void testExpandLessParametersThanMarkersV() {
        try {
            String result = Utils.expandsString("this %S a test %S", new Value[]{} );
            fail("Expected exception here.");
        } catch (RuntimeException re) {
            // OK.
        }
    }

    /**
     * Tests #expandsString(String,Value[]) with more parameters than markers, no issues.
     */
    public void testExpandMoreParametersThanMarkersV() {
        String result = Utils.expandsString(
                "this %s a really %s test %s I guess",
                new Value[]{
                        new StringValue("is"),
                        new StringValue("complex"),
                        new StringValue("string"),
                        new StringValue("and more...")
                }
        );
        assertEquals("Unespected expansion.", "this \"is\" a really \"complex\" test \"string\" I guess", result);
    }

    /**
     * Tests no markers.
     */
    public void testExpandNoMarkersV() {
        String result = Utils.expandsString("this is a test", new Value[]{} );
        assertEquals("Unespected expansion.", "this is a test", result);
    }

    /**
     * Tests no markers but paramters.
     */
    public void testExpandNoMarkersButParametersV() {
        String result = Utils.expandsString("this is a test", new Value[]{ new StringValue("useless")} );
        assertEquals("Unespected expansion.", "this is a test", result);
    }

    /**
     * Tests many parameters as markers.
     */
    public void testExpandParametersV() {
        MapValue mapValue = new MapValue();
        mapValue.put("k1", "v1");
        mapValue.put("k2", "v2");
        String result = Utils.expandsString(
                "This is a string: %s, this is a number: %n, this is an object %o",
                new Value[] {
                        new StringValue("\"This is some quoted text.\""),
                        new NumericValue(10),
                        mapValue
                }
        );
        System.out.println("Result: " + result);
        assertEquals(
                "Unespected result.",
                "This is a string: \"&#34;This is some quoted text.&#34;\", this is a number: 10, this is an object {\"k1\":\"v1\",\"k2\":\"v2\"}",
                result
        );
    }

    /**
     * Tests the #expandsString with all markers.
     */
    public void testExpandStringFull() {
        String result = Utils.expandsString(
                "aaa %b bbb %s ccc %n ddd %a eee %o fff %j ggg %u hhh",
                new Value[]{
                        new StringValue("bla"),
                        new BooleanValue(true),
                        new StringValue("10"),
                        new StringValue("array"),
                        new StringValue("[1,2,3]"),
                        new StringValue("[1,2,3]"),
                        new NumericValue(20)
                }
        );
        System.out.println("Result: " + result);
        assertEquals(
                "Unespected value.",
                "aaa false bbb \"true\" ccc 10 ddd [\"a\",\"r\",\"r\",\"a\",\"y\"] eee {\"[1,2,3]\":null} fff [1,2,3] ggg 20 hhh",
                result
                );
    }

}
