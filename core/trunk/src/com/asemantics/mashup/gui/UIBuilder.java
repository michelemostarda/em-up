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
 * This interface define a builder used to define a <i>User Interface</i>.
 */
public interface UIBuilder<T> {

    /**
     * Creates a button.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createButton();

    /**
     * Creates a label widget.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createLabel();

    /**
     * Creates a text area widget.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createTextArea();

    /**
     * Creates a list widget.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createList();

    /**
     * Creates a container panel.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createPanel();

    /**
     * Creates a window.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder createWindow();

    /**
     * Closes the top container in the component's stack.
     *
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder pop();

    /**
     * Sets the <i>width</i> attribute.
     *
     * @param w width attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setWidth(int w);

    /**
     * Sets the <i>height</i> attribute.
     *
     * @param h height attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setHeight(int h);

    /**
     * Sets the <i>visibility</i> attribute.
     *
     * @param v visibility attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setVisibility(boolean v);

    /**
     * Sets the <i>scrollable</i> attribute.
     *
     * @param s scrollable attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setScrollable(boolean s);

    /**
     * Sets the <i>title</i> attribute.
     *
     * @param t title attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setTitle(String t);

    /**
     * Sets the <i>modal</i> attribute.
     *
     * @param m modal attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setModal(boolean m);

    /**
     * Sets the <i>orientation</i> attribute.
     *
     * @param o orientation attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setOrientation(UIContainer.Orientation o);

    /**
     * Sets the <i>caption</i> attribute.
     *
     * @param caption caption attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setCaption(String caption);

    /**
     * Sets the <i>text</i> attribute.
     *
     * @param text text attribute.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setText(String text);

    /**
     * Sets the <i>entries</i> attribute.
     *
     * @param entries list of entries.
     * @return this <i>UIBuilder</i> instance.
     */
    UIBuilder setList(String[] entries);

    /**
     * Returns the built <i>User Interface</i>.
     *
     * @return the generated <i>User Interface</i>.
     */
    T result();

    /**
     * Resets the builder internal status.
     */
    void reset();
}
