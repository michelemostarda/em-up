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

import com.asemantics.mashup.processor.json.JsonObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * GWT abstract component implementation.
 */
public abstract class GwtUIComponent<T extends Widget> implements UIComponent<Widget> {

    /**
     * Internal GWT component.
     */
    private T component;

    /**
     * Component width.
     */
    private int width;

    /**
     * Component height.
     */
    private int height;

    /**
     * Constructor.
     */
    GwtUIComponent() {
        // Empty.
    }

    /**
     * Constructor with argument wrapped component.
     *
     * @param c
     */
    GwtUIComponent(T c) {
        setNativeComponent(c);
    }

    /**
     * Sets the wrapped component.
     *
     * @param c
     */
    protected void setNativeComponent(T c) {
        if(c == null) {
            throw new NullPointerException();
        }
        component = c;
    }

    public String getComponentName() {
        return UIComponent.NAME;
    }

    public JsonObject getDescriptionScheme() {
        try {
            return JsonTranslator.convertToJson(this);
        } catch (JsonTranslationException jte) {
            throw new RuntimeException("Error while generating description scheme.", jte);
        }
    }

    public T getNativeComponent() {
        return component;
    }

    public void setWidth(int w) {
        width = w;
        component.setWidth( w + "px" );
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int h) {
        height = h;
        component.setHeight( h + "px" );
    }

    public int getHeight() {
        return height;
    }

    public void setVisible(boolean v) {
        component.setVisible(v);
    }

    public boolean isVisible() {
        return component.isVisible();
    }

    public Size getSize() {
        Size size = new Size( width, height );
        return size;
    }
}
