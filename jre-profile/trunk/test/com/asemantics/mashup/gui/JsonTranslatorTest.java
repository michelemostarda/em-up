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
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.json.JsonObject;

/**
 * Test for {@link com.asemantics.mashup.gui.JsonTranslator}.
 */
public class JsonTranslatorTest extends TestCase {

    /**
     * Internal unser interface factory.
     */
    private static final UIFactory UIFACTORY;

    static {
        UIFACTORY = NativeImpl.getInstance().getUIFactory();
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    /**
     * Tests the UI -> JSON UI -> UI cycle for a component.
     */
    public void testComponentRoundTrip() throws JsonTranslationException {
        UIComponent original = createTestPanel();
        JsonObject jsonOriginal = JsonTranslator.convertToJson(original);
        System.out.println("JSON original: " + jsonOriginal.asPrettyJSON() );

        UIComponent reversed = JsonTranslator.convertToUI(jsonOriginal);
        JsonObject jsonReversed = JsonTranslator.convertToJson(reversed);
        System.out.println("JSON reversed: " + jsonReversed.asPrettyJSON() );

        assertTrue("Original and reversed must be equivalent.", jsonOriginal.equals(jsonReversed) );
    }

    /**
     * Tests the UI -> JSON -> UI cycle for a window.
     */
    public void testWindowRoundTrip() throws JsonTranslationException {
        UIComponent original = createTestWindow();
        JsonObject jsonOriginal = JsonTranslator.convertToJson(original);
        System.out.println("JSON original: " + jsonOriginal.asPrettyJSON() );

        UIComponent reversed = JsonTranslator.convertToUI(jsonOriginal);
        JsonObject jsonReversed = JsonTranslator.convertToJson(reversed);
        System.out.println("JSON reversed: " + jsonReversed.asPrettyJSON() );

        assertTrue("Original and reversed must be equivalent.", jsonOriginal.equals(jsonReversed) );
    }

    private UIComponent createTestPanel() {
        UIPanel panel1  = UIFACTORY.createPanel();
        UIPanel panel2  = UIFACTORY.createPanel();
        UIPanel panel3  = UIFACTORY.createPanel();

        UITextArea textArea = UIFACTORY.createTextArea();
        UILabel    label    = UIFACTORY.createLabel();
        UIButton   button   = UIFACTORY.createButton();

        panel1.addComponent(panel2);
        panel2.addComponent(panel3);
        panel3.addComponent(textArea);
        panel3.addComponent(label);
        panel3.addComponent(button);

        return panel1;
    }

    private UIComponent createTestWindow() {
        UIWindow window = UIFACTORY.createWindow();
        UIPanel panel1  = UIFACTORY.createPanel();
        UIPanel panel2  = UIFACTORY.createPanel();
        UIPanel panel3  = UIFACTORY.createPanel();

        UITextArea textArea = UIFACTORY.createTextArea();
        UILabel    label    = UIFACTORY.createLabel();
        UIButton   button   = UIFACTORY.createButton();

        panel1.addComponent(textArea);
        panel2.addComponent(label);
        panel3.addComponent(button);

        window.addComponent(panel1);
        window.addComponent(panel2);
        window.addComponent(panel3);

        return window;
    }

}
