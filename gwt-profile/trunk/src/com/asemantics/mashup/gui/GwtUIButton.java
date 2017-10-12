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

import com.asemantics.mashup.common.Utils;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Button;

/**
 * GWT button implementation.
 */
public class GwtUIButton extends GwtUIComponent<Button> implements UIButton<Widget> {

    /**
     * Internal GWT button instance.
     */
    private Button button;

    /**
     * Constructor.
     */
    GwtUIButton() {
        button = new Button();
        setNativeComponent(button);
    }

    public void setCaption(String caption) {
        Utils.TextBoxSize tbs = Utils.getTextBoxSize( caption );
        setWidth ( tbs.getWidthPx()  );
        setHeight( tbs.getHeightPx() );
        button.setText(caption);
    }

    public String getCaption() {
        return button.getText();
    }

    public String getComponentName() {
        return UIButton.NAME;
    }

}