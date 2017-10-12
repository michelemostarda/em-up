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


package com.asemantics.mashup.processor.json;

import junit.framework.TestCase;
import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.NullValue;
import com.asemantics.mashup.processor.MapValue;

import java.util.List;

/**
 * Test class for {com.asemantics.mashup.processor.json.JsonUtil}.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: JsonUtilTest.java 434 2009-06-01 13:45:43Z michelemostarda $
 */
public class JsonUtilTest extends TestCase {

    /**
     * Test difference between null values.
     */
    public void testJsonDiffNull() {
        JsonDiff diff;

        diff = JsonUtil.difference( NullValue.getInstance(), NullValue.getInstance() );
        System.out.println("Difference: " + diff);
        assertTrue("Expected no difference.", diff.noDifference() );

        diff = JsonUtil.difference( NullValue.getInstance(), new StringValue() );
        System.out.println("Difference: " + diff);
        assertFalse("Expected difference.", diff.noDifference() );
    }

    /**
     * Test difference between boolean values.
     */
    public void testJsonDiffBoolean() {
        JsonBoolean b1;
        JsonBoolean b2;
        JsonDiff diff;

        b1 = new BooleanValue(true);
        b2 = new BooleanValue(false);
        diff = JsonUtil.difference(b1, b2);
        System.out.println("Difference: " + diff);
        assertFalse("Expected difference.", diff.noDifference() );

        b1 = new BooleanValue(false);
        b2 = new BooleanValue(false);
        diff = JsonUtil.difference(b1, b2);
        System.out.println("Difference: " + diff);
        assertTrue("Expected difference.", diff.noDifference() );
    }

    /**
     * Test difference between numeric values.
     */
    public void testJsonDiffNumeric() {
        JsonNumber n1;
        JsonNumber n2;
        JsonDiff diff;

        n1 = new NumericValue(10.1);
        n2 = new NumericValue(10.2);
        diff = JsonUtil.difference(n1, n2);
        System.out.println("Difference: " + diff);
        assertFalse("Expected difference.", diff.noDifference() );

        n1 = new NumericValue(1.12);
        n2 = new NumericValue(1.12);
        diff = JsonUtil.difference(n1, n2);
        System.out.println("Difference: " + diff);
        assertTrue("Expected difference.", diff.noDifference() );
    }

    /**
     * Test difference between string values.
     */
    public void testJsonDiffString() {
        JsonString n1;
        JsonString n2;
        JsonDiff diff;

        n1 = new StringValue("n1");
        n2 = new StringValue("n2");
        diff = JsonUtil.difference(n1, n2);
        System.out.println("Difference: " + diff);
        assertFalse("Expected difference.", diff.noDifference() );

        n1 = new StringValue("n");
        n2 = new StringValue("n");
        diff = JsonUtil.difference(n1, n2);
        System.out.println("Difference: " + diff);
        assertTrue("Expected difference.", diff.noDifference() );
    }

    /**
     * Test difference between array values.
     */
    public void testJsonDiffArray() {
        JsonArray a1;
        JsonArray a2;
        JsonDiff diff;
        List<JsonDiff.Issue> issues;

        // Differ in size.
        a1 = new ListValue();
        for(int i = 0; i < 10; i++) {
            a1.add( new StringValue("value" + i) );
        }
        a2 = new ListValue();
        for(int i = 0; i < 7; i++) {
            a2.add( new StringValue("value" + i) );
        }
        diff = JsonUtil.difference(a1, a2);
        System.out.println("Difference: " + diff);
        issues = diff.getIssues();
        assertEquals("Unespected issue size.", 1, issues.size());
        assertTrue("usespected issue", issues.get(0).getDescription().indexOf("differ in size") != -1 );

        // Differ in size and content.
        a1 = new ListValue();
        for(int i = 0; i < 10; i++) {
            a1.add( new StringValue("value" + i) );
        }
        a2 = new ListValue();
        for(int i = 0; i < 7; i++) {
            a2.add( new StringValue("valueX") );
        }
        diff = JsonUtil.difference(a1, a2);
        System.out.println("Difference: " + diff);
        issues = diff.getIssues();
        assertEquals("Unespected issue size.", 8, issues.size());
        assertTrue("usespected issue", issues.get(0).getDescription().indexOf("differ in size") != -1 );
    }

    /**
     * Test difference between object values.
     */
    public void testJsonDiffObject() {
        JsonObject o1;
        JsonObject o2;
        JsonDiff diff;
        List<JsonDiff.Issue> issues;

        // Differ in size.
        o1 = new MapValue();
        for(int i = 0; i < 10; i++) {
            o1.put("key" + i, "value" + i);
        }
        o2 = new MapValue();
        for(int i = 0; i < 7; i++) {
            o2.put("key" + i, "value" + i);
        }
        diff = JsonUtil.difference(o1, o2);
        System.out.println("Difference: " + diff);
        issues = diff.getIssues();
        assertEquals("Unsespected number of issues.", 4, issues.size());

        // Differ in size and content.
        o1 = new MapValue();
        for(int i = 0; i < 10; i++) {
            o1.put("key" + i, "value" + i);
        }
        o2 = new MapValue();
        for(int i = 0; i < 7; i++) {
            o2.put("key" + i, "valueX");
        }
        diff = JsonUtil.difference(o1, o2);
        System.out.println("Difference: " + diff);
        issues = diff.getIssues();
        assertEquals("Unsespected number of issues.", 11, issues.size());

    }

}
