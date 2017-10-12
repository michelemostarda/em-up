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

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwingUIList extends SwingUIComponent<JList> implements UIList<Container> {

    class InternalListModel implements ListModel {

        private List<String> elems = new ArrayList<String>();

        private List<ListDataListener> listeners = new ArrayList<ListDataListener>();

        void addElem(String e) {
            elems.add(e);
            notifyListeners();
        }

        void removeElem(String e) {
            elems.remove(e);
            notifyListeners();
        }

        void removeElem(int i) {
            elems.remove(i);
            notifyListeners();
        }

        void clear() {
            elems.clear();
            notifyListeners();
        }

        public int getSize() {
            return elems.size();
        }

        public Object getElementAt(int index) {
            return elems.get(index);
        }

        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        void notifyListeners() {
            ListDataEvent lde = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, elems.size() - 1 );
            for(ListDataListener listener : listeners) {
                listener.contentsChanged(lde);
            }
        }

        String printList() {
            StringBuilder sb = new StringBuilder();
            for(String e : elems) {
                sb.append(e).append(" ");
            }
            return sb.toString();
        }
    }

    private JList list;

    private InternalListModel listModel = new InternalListModel();

    SwingUIList() {
        list = new JList();
        list.setModel(listModel);
        setComponent(list);
    }

    @Override
    public String getComponentName() {
        return UIList.NAME;
    }

    public void addEntry(String entry) {
        listModel.addElem(entry);
    }

    public void removeEntry(String entry) {
        listModel.removeElem(entry);
    }

    public void removeEntry(int i) {
        listModel.removeElem(i);
    }

    public void removeAll() {
        listModel.clear();
    }

    public JsonArray getJSONList() {
        JsonArray jsonArray = JsonFactory.newJsonArray();
        for( String elem : listModel.elems ) {
            jsonArray.add(elem);
        }
        return jsonArray;
    }
}
