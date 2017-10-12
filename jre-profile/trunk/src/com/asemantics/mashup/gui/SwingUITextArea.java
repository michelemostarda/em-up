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
 * Defines a Java Swing text area.
 */
public class SwingUITextArea extends SwingUIComponent<JTextArea> implements UITextArea<Container>  {

    private JTextArea textArea;

    SwingUITextArea() {
        textArea = new JTextArea();
        setComponent(textArea);
    }

    public void setText(String txt) {
        textArea.setText(txt);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setEditable(boolean e) {
        textArea.setEditable(e);
    }

    public boolean isEditable() {
        return textArea.isEditable();
    }

    @Override
    public String getComponentName() {
        return UITextArea.NAME;
    }

}
