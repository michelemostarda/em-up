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
 * This class wants to test the <i>Modelize</i> to <i>Concretize</i>
 * cycle. The test is actuated by creating an initial <i>JSON Object</i>,
 * generating for it the <i>JSON Model</i>, and finally concretizing
 * this model. The result is compared with the same initial <i>JSON Object</i>
 * that is rendered directly.
 * <pre>
 * JSON Object -- modelize  --> JSON Model -- concretize --> Native UI 1 -- descriptionScheme --> JSON UI 1
 * JSON Object -- renderize --> JSON UI 2
 * Expected: JSON UI 1 == JSON UI 2
 * </pre>
 */
public class ModelizeConcretizeCycleTest extends TestCase {

    /**
     * Renderer, is responsible for the modelization of the initial object.
     */
    private Renderer<JsonObject> jsonRenderer;

    /**
     * Renderer, is responsible for the generation of the native UI from the initial object.
     */
    private Renderer<UIComponent> nativeRenderer;

    @Override
    protected void setUp() throws Exception {
        jsonRenderer   = new Renderer<JsonObject> ( new JsonUIBuilder()     );
        nativeRenderer = new Renderer<UIComponent>( new ConcreteUIBuilder() );
    }

    @Override
    protected void tearDown() throws Exception {
        jsonRenderer   = null;
        nativeRenderer = null;
    }

    /**
     * Tests the generation cycle.
     * 
     * @throws JsonTranslationException
     */
    public void testCycle() throws JsonTranslationException {

        final int SIZE = 20;

        // Initial object.
        JsonObject initial = RendererTest.createObject(SIZE);

        // JSON UI model.
        JsonObject uiModel = jsonRenderer.renderizeAsPanel(initial);

        // User interface.
        UIComponent component = JsonTranslator.convertToUI(uiModel);

        // Description scheme associated to the user interface.
        String descriptionScheme1 = component.getDescriptionScheme().asJSON();
        System.out.println("Description Scheme (JSON UI 1):" + descriptionScheme1);

        // Description scheme associated to the user interface.
        UIComponent uiComponent   = nativeRenderer.renderizeAsPanel(initial);
        String descriptionScheme2 = uiComponent.getDescriptionScheme().asJSON();
        System.out.println("Description Scheme (JSON UI 2):" + descriptionScheme2);

        // Checks equivalence.
        assertEquals(
                "Expected that JSON UI schema 1 and JSON UI schema 2 were equal.",
                descriptionScheme1,
                descriptionScheme2
        );
    }
}
