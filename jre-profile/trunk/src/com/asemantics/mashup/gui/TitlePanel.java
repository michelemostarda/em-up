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

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;

/**
 * Defines a <i>JPanel</i> with a title section.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: TitlePanel.java 438 2009-06-01 14:39:38Z michelemostarda $
 */
public class TitlePanel extends JPanel {

    private JLabel title;

    private JPanel centerPanel;

    public TitlePanel() {
        setLayout( new BorderLayout() );
        title = new JLabel();
        centerPanel = new JPanel();
        super.add(title, BorderLayout.NORTH);
        super.add(centerPanel, BorderLayout.CENTER);
    }

    public void setTitle(String str) {
        title.setText(str);
    }

    public String getTitle() {
        return title.getText();
    }

    @Override
    public Component add(Component component) {
        return centerPanel.add(component);
    }

    @Override
    public Component add(String s, Component component) {
        return centerPanel.add(s, component);
    }

    @Override
    public Component add(Component component, int i) {
        return centerPanel.add(component, i);
    }

    @Override
    public void add(Component component, Object o) {
        centerPanel.add(component, o);
    }

    @Override
    public void add(Component component, Object o, int i) {
        centerPanel.add(component, o, i);
    }

    @Override
    public Component getComponentAt(int i, int i1) {
        return centerPanel.getComponentAt(i, i1);
    }

    @Override
    public Component getComponentAt(Point point) {
        return centerPanel.getComponentAt(point);
    }

    @Override
    public Component findComponentAt(int i, int i1) {
        return centerPanel.findComponentAt(i, i1);
    }

    @Override
    public Component findComponentAt(Point point) {
        return centerPanel.findComponentAt(point);
    }

}
