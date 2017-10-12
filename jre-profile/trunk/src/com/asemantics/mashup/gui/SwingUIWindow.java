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

import javax.swing.*;
import java.awt.*;


/**
 * Defines a Java Swing Window.
 */
public class SwingUIWindow extends SwingUIContainer implements UIWindow<Container> {

    private JFrame frame;

    SwingUIWindow() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        setComponent(frame);
    }

    @Override
    public void addComponent(UIComponent<Container> component) {
        components.add( component );
        frame.getContentPane().add( component.getNativeComponent(), BorderLayout.CENTER);
    }

    @Override
    public void removeComponent(UIComponent<Container> component) {
        frame.getContentPane().remove( component.getNativeComponent() );
        components.remove( component );
    }

    public void setTitle(String t) {
        frame.setTitle(t);
    }

    public void setModal(boolean m) {
        // Empty.
    }

    public boolean isModal() {
        // Empty.
        return false;
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public String getTitle() {
        return frame.getTitle();
    }

    @Override
    public String getComponentName() {
        return UIWindow.NAME;
    }

    public void setVisible(boolean f) {
        frame.setVisible(f);
    }

    public boolean isVisible() {
        return frame.isVisible();
    }
}
