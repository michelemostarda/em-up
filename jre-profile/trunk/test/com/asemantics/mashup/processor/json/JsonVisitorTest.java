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
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.ListValue;

/**
 * Test case of {@link com.asemantics.mashup.processor.json.JsonVisitor}.
 */
public class JsonVisitorTest extends TestCase {

    /**
     * Target.
     */
    private JsonVisitor jsonVisitor;

    @Override
    protected void setUp() throws Exception {
        jsonVisitor = new JsonVisitor( createJsonData() );
    }

    @Override
    protected void tearDown() throws Exception {
        jsonVisitor = null;
    }

    /**
     * Tests the iterability of the visitor and the expected size.
     */
    public void testIteration() {
        assertTrue("Elements not found.", jsonVisitor.hasNext());
        int counter = 0;
        for(JsonBase base : jsonVisitor) {
            System.out.println("base: " + base.asJSON() );
            counter++;
        }
        assertFalse("Elements still found.", jsonVisitor.hasNext());
        assertEquals("Unespected number of elements.", 7, counter);
    }

    /**
     *  Tests the content of the visit, with the right progression.
     */
    public void testVisit() {
        assertTrue("Expected element.", jsonVisitor.hasNext());
        assertEquals(
                "Unespected element.", "{\"k3\":[\"a1\",true,1],\"k1\":\"v1\",\"k2\":\"v2\"}",
                jsonVisitor.next().asJSON()
        );
        assertEquals("Unespected element.", "[\"a1\",true,1]", jsonVisitor.next().asJSON());
        assertEquals("Unespected element.", "\"v1\"", jsonVisitor.next().asJSON());
        assertEquals("Unespected element.", "\"v2\"", jsonVisitor.next().asJSON());
        assertEquals("Unespected element.", "\"a1\"", jsonVisitor.next().asJSON());
        assertEquals("Unespected element.", "true", jsonVisitor.next().asJSON());
        assertEquals("Unespected element.", "1", jsonVisitor.next().asJSON());
        assertFalse("Expected end of elements.", jsonVisitor.hasNext());

    }

    private JsonBase createJsonData() {
        JsonArray jsonArray = new ListValue();
        jsonArray.add("a1");
        jsonArray.add(true);
        jsonArray.add(1);

        JsonObject jsonObject = new MapValue();
        jsonObject.put("k1", "v1");
        jsonObject.put("k2", "v2");
        jsonObject.put("k3", jsonArray);

        return jsonObject;
    }
}
