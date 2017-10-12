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

import com.asemantics.mashup.interpreter.Utils;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.form.TextArea;

/**
 * GWT text area implementation.
 */
public class GwtUITextArea extends GwtUIComponent<TextArea> implements UITextArea<Widget>  {

    /**
     * Internal text area.
     */
    private TextArea textArea;

    /**
     * Constructor.
     */
    GwtUITextArea() {
        textArea = new TextArea();
        setNativeComponent(textArea);
    }

    @Override
    public String getComponentName() {
        return UITextArea.NAME;
    }

    public void setText(String txt) {
        textArea.setValue(txt);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setEditable(boolean e) {
        if(e) {
            textArea.enable();
        } else {
            textArea.disable();
        }
    }

    public boolean isEditable() {
        return ! textArea.isDisabled();
    }

}
