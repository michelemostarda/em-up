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
 * Defines an abstract <i>User Interface</i> window living inside browser space.
 */
public interface UIWindow<T> extends UIContainer<T> {

    public static final String NAME = "UIWindow";

    /**
     * Sets the window title.
     *
     * @param title window title.
     */
    public void setTitle(String title);

    /**
     * @return the window title.
     */
    public String getTitle();

    /**
     * Sets if popup is modal.
     * @param m
     */
    public void setModal(boolean m);

    /**
     *
     * @return popup is modal.
     */
    public boolean isModal();

    /**
     * Shows this dialog.
     */
    public void show();

    /**
     * Hides this dialog.
     */
    public void hide();

}
