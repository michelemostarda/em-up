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

/**
 * Defines the base class for the abstract <i>User Interface</i> component.
 *
 * @see com.asemantics.mashup.gui.UIContainer
 */
public interface UIComponent<T> extends UISizeable {

    public static final String NAME = "UIComponent";

    /**
     * Returns the qualifying component name.
     * 
     * @return the component name.
     */
    public String getComponentName();

    /**
     * Returns a string with the descriptor scheme.
     * 
     * @return the description scheme.
     */
    public JsonObject getDescriptionScheme();

    /**
     * Returns the native conponent.
     *
     * @return internal native component.
     */
    public T getNativeComponent();

    /**
     * Sets the component witdh.
     *
     * @param w
     */
    public void setWidth(int w);

    /**
     * Returns the component width.
     *
     * @return  component width.
     */
    public int getWidth();

    /**
     * Sets the component height.
     *
     * @param h component height.
     */
    public void setHeight(int h);

    /**
     * Returns the component height.
     * 
     * @return component height.
     */
    public int getHeight();

    /**
     * Sets the component visibility.
     *
     * @param v visibility flag.
     */
    public void setVisible(boolean v);

    /**
     *
     * @return component visibility.
     */
    public boolean isVisible();

}
