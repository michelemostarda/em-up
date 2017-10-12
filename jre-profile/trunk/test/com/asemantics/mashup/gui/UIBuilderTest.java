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

import junit.framework.TestCase;
import com.asemantics.mashup.processor.json.JsonObject;

/**
 * This class is a comparative test of the {@link com.asemantics.mashup.gui.ConcreteUIBuilder}
 * and {@link com.asemantics.mashup.gui.JsonUIBuilder} implementation classes of
 * {@link com.asemantics.mashup.gui.UIBuilder}.
 */
public class UIBuilderTest extends TestCase {

    /**
     * Expected result structure.
     */
    static final String EXPECTED_RESULT = "{" +
        "\"UIWindow\":" +
            "{\"components\":[" +
            "{\"UIContainer\":{" +
                "\"components\":[" +
                    "{\"UILabel\":{\"text\":\"label content\",\"width\":0,\"height\":0,\"visible\":true}}," +
                    "{\"UIButton\":{\"width\":0,\"height\":0,\"visible\":true,\"caption\":\"button caption\"}}" +
                "]," +
                    "\"width\":10,\"height\":20,\"title\":\"\",\"scrollable\":false,\"orientation\":\"HORIZONTAL\",\"visible\":true}" +
                    "}," +
            "{\"UIContainer\":{" +
                "\"components\":[" +
                    "{\"UITextArea\":{\"text\":\"text area content\",\"width\":30,\"height\":0,\"visible\":true}}," +
                    "{\"UIList\":{\"list\":[\"list content 1\",\"list content 2\"],\"width\":0,\"height\":0,\"visible\":true}}" +
                "]," +
                    "\"width\":0,\"height\":0,\"title\":\"\",\"scrollable\":false,\"orientation\":\"VERTICAL\",\"visible\":true}" +
                "}" +
                "]," +
                    "\"width\":200,\"height\":300,\"title\":\"\",\"scrollable\":false,\"orientation\":\"HORIZONTAL\",\"visible\":false,\"modal\":false}" +
        "}";

    /**
     * Concrete Builder implementation.
     */
    private UIBuilder<UIComponent> concreteUIBuilder;

    /**
     * JSON Builder implementation.
     */
    private UIBuilder<JsonObject> jsonUIBuilder;

    @Override
    protected void setUp() throws Exception {
        concreteUIBuilder = new ConcreteUIBuilder();
        jsonUIBuilder     = new JsonUIBuilder();
    }

    @Override
    protected void tearDown() throws Exception {
        concreteUIBuilder = null;
        jsonUIBuilder     = null;
    }

    /**
     * Tests the creation of a window structure with the concrete builder.
     */
    public void testConcreteUICreation() {
        UIComponent root = createWindow(concreteUIBuilder);
        final String jsonScheme = root.getDescriptionScheme().asJSON();
        System.out.println("root scheme:" + jsonScheme );
        checkResult(jsonScheme);
    }

    /**
     * Tests the creation of a window with the JSON builder.
     */
    public void testJsonUICreation() {
        JsonObject root = createWindow(jsonUIBuilder);
        final String jsonScheme = root.asJSON();
        System.out.println("root scheme:" + jsonScheme );
        checkResult(jsonScheme);
    }

    /**
     * Creates a test window structure with the given builder.
     * 
     * @param uiBuilder
     * @return
     */
    private <T> T createWindow(UIBuilder<T> uiBuilder) {
        uiBuilder
                .createWindow()
                    .setVisibility(false)
                    .setWidth(200)
                    .setHeight(300)
                .createPanel()
                    .setWidth(10)
                    .setHeight(20)
                    .createLabel().setText("label content")
                    .createButton().setCaption("button caption")
                .pop()
                .createPanel()
                    .setOrientation(UIContainer.Orientation.VERTICAL)
                    .createTextArea()
                        .setWidth(30)
                        .setText("text area content")
                    .createList().setList( new String[] {"list content 1", "list content 2"} )
                .pop();


        return uiBuilder.result();
    }

    /**
     * Checks the result on the expected structure.
     * 
     * @param jsonScheme
     */
    private void checkResult(String jsonScheme) {
        assertEquals("Unespected result.", EXPECTED_RESULT, jsonScheme);
    }
}
