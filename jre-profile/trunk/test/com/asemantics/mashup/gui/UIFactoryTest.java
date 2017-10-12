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

import com.asemantics.mashup.nativepkg.NativeImpl;
import junit.framework.TestCase;

/**
 * Test for the {@link com.asemantics.mashup.gui.UIFactory}
 * class.
 */
public class UIFactoryTest extends TestCase {

    /**
     * Widget factory.
     */
    private UIFactory factory;

    public void setUp() throws Exception {
        factory = NativeImpl.getInstance().getUIFactory();
    }

    public void tearDown() throws Exception {
        factory = null;
    }

    /**
     * Tests the creation of a GUI.
     */
    @SuppressWarnings("unchecked")
    public synchronized void testCreateGUI() {
        UIWindow window = factory.createWindow();
        UIPanel  panel  = factory.createPanel();
        UIList   list   = factory.createList();
        UIButton button = factory.createButton();

        window.setTitle("Test title");
        window.setHeight(100);
        window.setWidth (200);
        button.setCaption("OK");

        panel.setOrientation(UIPanel.Orientation.VERTICAL);

        UILabel label;
        for( int i = 0; i < 5; i++ ) {
            label = factory.createLabel();
            label.setText("Label " + i);
            panel.addComponent( label );
        }

        for(int i = 0; i < 5; i++) {
            list.addEntry("Entry " + i);
        }

        panel.addComponent( list  );
        panel.addComponent(button );

        window.addComponent( panel  );

        String jsonScheme = window.getDescriptionScheme().asJSON();
        System.out.println("Description scheme: " + jsonScheme);

        assertEquals(
                "{\"UIWindow\":" +
                        "{\"components\":[" +
                            "{\"UIContainer\":{\"components\":[" +
                                "{\"UILabel\":{\"text\":\"Label 0\",\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UILabel\":{\"text\":\"Label 1\",\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UILabel\":{\"text\":\"Label 2\",\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UILabel\":{\"text\":\"Label 3\",\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UILabel\":{\"text\":\"Label 4\",\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UIList\":{\"list\":[\"Entry 0\",\"Entry 1\",\"Entry 2\",\"Entry 3\",\"Entry 4\"]," +
                                "\"width\":0,\"height\":0,\"visible\":true}}," +
                                "{\"UIButton\":{\"width\":0,\"height\":0,\"visible\":true,\"caption\":\"OK\"}}]," +
                        "\"width\":0,\"height\":0,\"title\":\"\",\"scrollable\":false,\"orientation\":\"VERTICAL\",\"visible\":true}}]," +
                        "\"width\":200,\"height\":100,\"title\":\"Test title\",\"scrollable\":false,\"orientation\":\"HORIZONTAL\",\"visible\":false,\"modal\":false}}",
                jsonScheme 
        );

        /*
        window.setVisible(true);
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

}
