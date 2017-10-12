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

import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;


/**
 * GWT dialog panel implementation.
 */
public class GwtUIWindow extends GwtUIContainer implements UIWindow<Widget> {

    /**
     * Internal dialog box implementation.
     */
    private Window window;

    private Panel windowPanel;

    /**
     * Constructor.
     */
    GwtUIWindow() {
        window = new Window("Dialog", false, true);
        window.setClosable(true);
        window.setAutoScroll(true);

        windowPanel = new Panel();
        windowPanel.setLayout( new HorizontalLayout(2) );

        window.setLayout( new BorderLayout());
        window.add( windowPanel, new BorderLayoutData(RegionPosition.CENTER) );

        setNativeComponent(windowPanel);
    }

    @Override
    public String getComponentName() {
        return UIWindow.NAME;
    }

    public void addComponent(UIComponent<Widget> component) {
        window.add( component.getNativeComponent() );
    }

    public void setTitle(String title) {
        window.setTitle(title);
    }

    public String getTitle() {
        return window.getTitle();
    }

    /**
     * Provides set modal support.
     * 
     * @param m
     */
    public void setModal(boolean m) {
        window.setModal(m);
    }

    public boolean isModal() {
        return window.isModal();
    }

    public void show() {
        // Show window.
        window.setWidth(300);
        window.setHeight(250);
        window.show();
    }

    public void hide() {
        window.hide();
    }

    /**
     * Modification of visibility for a dialog means showing or hiding it.
     * @param v
     */
    @Override
    public void setVisible(boolean v) {
        super.setVisible(v);
        if( v ) {
            show();
        } else {
            hide();
        }
    }


}
