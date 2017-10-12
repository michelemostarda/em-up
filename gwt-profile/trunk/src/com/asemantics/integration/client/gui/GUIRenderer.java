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

package com.asemantics.integration.client.gui;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.core.RegionPosition;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.gui.Renderer;
import com.asemantics.mashup.gui.UIComponent;
import com.asemantics.mashup.gui.ConcreteUIBuilder;
import com.google.gwt.user.client.ui.*;


/**
 * Defines a GUI renderer based on JSON models.
 */
public class GUIRenderer extends Window {

    /**
     * Renderer utility.
     */
    private Renderer renderer;

    /**
     * Constructor.
     *
     * @param jsonBase model.
     */
    public GUIRenderer(JsonBase jsonBase) {

        // Init renderer.
        renderer = new Renderer( new ConcreteUIBuilder() );

        // Generarates renderization.
        UIComponent<Widget> component = (UIComponent<Widget>) renderer.renderizeAsPanel(jsonBase);

        // Window configuration.
        setTitle("JSON Renderer");
        setClosable(true);
        setPlain(true);
        setLayout(new BorderLayout());
        BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
        centerData.setMargins(0, 0, 0, 0);
        setCloseAction(Window.CLOSE);

        ScrollPanel scrollPanel = new ScrollPanel( component.getNativeComponent() );
        add(scrollPanel, centerData);
    }

    @Override
    public void show() {
        setWidth(450);
        setHeight(510);
        super.show();
    }

}
