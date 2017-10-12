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
 * Defines a factory for <i>User Iterface</i> widgets.
 */
public interface UIFactory {

    public static final String NAME = "UIFactory";

    /**
     *
     * @return a button.
     */
    UIButton createButton();

    /**
     *
     * @return a label.
     */
    UILabel createLabel();

    /**
     *
     * @return a text area.
     */
    UITextArea createTextArea();

    /**
     *
     * @return a list.
     */
    UIList createList();

    /**
     *
     * @return a panel.
     */
    UIPanel createPanel();

    /**
     *
     * @return a window.
     */
    UIWindow createWindow();

}
