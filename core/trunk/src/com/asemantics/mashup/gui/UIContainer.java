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

/**
 * Defines an abstract <i>User Interface</i> container, able to contain
 * other compoenents.
 *
 * @see com.asemantics.mashup.gui.UIComponent
 */
public interface UIContainer<T> extends UIComponent<T> {

    public static final String NAME = "UIContainer";

    /**
     * Panel internal component orientation.
     */
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    /**
     *
     * @return orientation.
     */
    public Orientation getOrientation();

    /**
     * Sets orientation.
     *
     * @param o
     */
    public void setOrientation(Orientation o);

    /**
     *
     * @return returns the scrollable flag
     */
    public boolean isScrollable();

    /**
     * Sets the scrollable flag.
     * 
     * @param s
     */
    public void setScrollable(boolean s);

    /**
     * Adds a component to the container.
     *
     * @param component
     */
    public void addComponent(UIComponent<T> component);

    /**
     * Removes a component from the container.
     *
     * @param component
     */
    public void removeComponent(UIComponent<T> component);

    /**
     * Returns the list of components.
     *
     * @return list of included components.
     */
    public UIComponent[] getComponents();

}
