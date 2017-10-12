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

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Defines a wrapper test class for {@link com.asemantics.mashup.gui.UIFactoryTest}
 * able to run in a GWT context.
 */
public class GWTUIGlobalTest extends GWTTestCase {

    /**
     * Wrapped test.
     */
    private UIFactoryTest wrapped;

    public GWTUIGlobalTest() {
        wrapped = new UIFactoryTest();
    }

    public String getModuleName() {
        return "com.asemantics.MashUp";
    }

    protected void gwtSetUp() throws Exception {
        wrapped.setUp();
    }

    protected void gwtTearDown() throws Exception {
        wrapped.tearDown();
    }

    /**
     * Invokes the GUI creation test.
     */
    public void testCreateGUI() {
        wrapped.testCreateGUI();
    }


}