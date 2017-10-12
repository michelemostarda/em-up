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
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * GWT list implementation.
 */
public class GwtUIList  extends GwtUIComponent<ListBox> implements UIList<Widget> {

    private ListBox listBox;

    private int maxItemLengthPx = 0;

    GwtUIList() {
        listBox = new ListBox();
        setNativeComponent(listBox);
    }

    @Override
    public String getComponentName() {
        return UIList.NAME;
    }

    public void addEntry(String entry) {
        int entryLength = Utils.getTextBoxSize(entry).getWidthPx();
        if( maxItemLengthPx < entryLength ) {
            maxItemLengthPx = entryLength;
            setWidth( maxItemLengthPx );
        }
        setHeight( Utils.getLinesHeight( listBox.getItemCount() ) );
        listBox.addItem(entry);
    }

    public void removeEntry(String entry) {
        int entryIndex = findEntryIndex(entry);
        if(entryIndex != -1 ) {
            listBox.removeItem(entryIndex);
        }
    }

    public void removeEntry(int i) {
        listBox.removeItem(i);
    }

    public void removeAll() {
        listBox.clear();
    }

    public String printList() {
        StringBuilder sb = new StringBuilder();
        int lastCommaIndex = listBox.getItemCount() - 2;
        for(int i = 0; i < listBox.getItemCount(); i++) {
            sb.append(listBox.getItemText(i));
            if(lastCommaIndex <= i) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public JsonArray getJSONList() {
        JsonArray jsonArray = JsonFactory.newJsonArray();
        for(int i = 0; i < listBox.getItemCount(); i++) {
            jsonArray.add( JsonFactory.newJsonString( listBox.getItemText(i) ) );
        }
        return jsonArray;
    }

    private int findEntryIndex(String e) {
        if(e == null ) {
            return -1;
        }
        for(int i = 0; i < listBox.getItemCount(); i++) {
            if( e.equals(listBox.getItemText(i) ) ) {
                return i;
            }
        }
        return -1;
    }

}
