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

import java.awt.Container;

/**
 * Defines a Java Swing abstract component.
 */
public abstract class SwingUIComponent<T extends Container> implements UIComponent<Container> {

    private T component;

    SwingUIComponent() {
    }

    SwingUIComponent(T c) {
        setComponent(c);
    }

    protected void setComponent(T c) {
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
        } catch (JsonTranslationException te) {
            throw new RuntimeException("Error while retrieving the description scheme.", te);
        }
    }

    public T getNativeComponent() {
        return component;
    }

    public void setWidth(int w) {
        component.setSize( w, component.getHeight() );
    }

    public int getWidth() {
        return component.getWidth();
    }

    public void setHeight(int h) {
        component.setSize( component.getWidth(), h );
    }

    public int getHeight() {
        return component.getHeight();
    }

    public void setVisible(boolean v) {
        component.setVisible(v);
    }

    public boolean isVisible() {
        return component.isVisible();
    }

    public Size getSize() {
        return new Size( getWidth(), getHeight() );
    }
}
