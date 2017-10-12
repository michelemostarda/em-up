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


package com.asemantics.mashup.gui;

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBoolean;
import com.asemantics.mashup.processor.json.JsonDouble;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonInteger;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonString;
import junit.framework.TestCase;

/**
 * Test class for {@link com.asemantics.mashup.gui.Renderer}.
 */
public class RendererTest extends TestCase {

    /**
     * Internal renderer.
     */
    private Renderer<UIComponent> renderer;

    protected static JsonArray createArray(int size) {
        JsonArray jsonArray = JsonFactory.newJsonArray();
        for(int i = 0; i < size; i++) {
            jsonArray.add( i % 2 == 0 ?  JsonFactory.newJsonString("item_" + i) : JsonFactory.newJsonInteger(i) );
        }
        return jsonArray;
    }

    protected static JsonObject createObject(int size) {
        JsonObject jsonObject = JsonFactory.newJsonObject();
        for(int i = 0; i < size; i++) {
            jsonObject.put("key_" + i,  i % 2 == 0 ? JsonFactory.newJsonString("item_" + i) : createArray(1) );
        }
        return jsonObject;
    }

    protected void setUp() throws Exception {
        renderer = new Renderer<UIComponent>( new ConcreteUIBuilder() );
    }

    protected void tearDown() throws Exception {
        renderer = null;
    }

    public synchronized void testSimple() {

        UIComponent component;

        // String
        JsonString jsonString = JsonFactory.newJsonString("json string");
        component = renderer.renderize(jsonString);
        containsWidgetName( "UILabel", component );
        renderer.reset();

        // Integer
        JsonInteger jsonInteger = JsonFactory.newJsonInteger(0);
        component = renderer.renderize(jsonInteger);
        containsWidgetName( "UILabel", component );
        renderer.reset();

        // Double
        JsonDouble jsonDouble = JsonFactory.newJsonDouble(0);
        component = renderer.renderize(jsonDouble);
        containsWidgetName( "UILabel", component );
        renderer.reset();

        // boolean
        JsonBoolean jsonBool = JsonFactory.newJsonBoolean(true);
        component = renderer.renderize(jsonBool);
        containsWidgetName( "UILabel", component );
        renderer.reset();

    }

    public synchronized void testComplex() {

        UIComponent component;

        final int TIMES = 10;

        // Array
        JsonArray jsonArray = createArray(TIMES);
        component = renderer.renderizeAsPanel(jsonArray);
        containsWidgetName( "UILabel", 10, component );
        renderer.reset();

        // Object
        JsonObject jsonObject = createObject(TIMES);
        component = renderer.renderizeAsPanel(jsonObject);
        containsWidgetName( "UIContainer", 17, component );
        containsWidgetName( "UILabel"    , 10, component );
        renderer.reset();
    }

    private void containsWidgetName(String expected, UIComponent component) {
        String scheme = component.getDescriptionScheme().asJSON();
        assertTrue( "Unespected widget: " + scheme, scheme.indexOf(expected) != -1 );
    }

    private void containsWidgetName(String expected, final int times, UIComponent component) {
        String scheme = component.getDescriptionScheme().asJSON();
        int timesFound = 0;
        int beginIndex = 0, nextIndex;
        while( (nextIndex = scheme.indexOf(expected, beginIndex) ) != -1 ) {
            timesFound++;
            beginIndex = nextIndex +    expected.length();
        }

        assertEquals( "Unespected widget: " + scheme, times, timesFound );
    }

}
