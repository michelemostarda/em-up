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


package com.asemantics.mashup.processor.jsonpath;

import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultContextImpl implements Context {

    /**
     * List of elements.
     */
    private List<JsonBase> elements;

    public DefaultContextImpl() {
        elements = new ArrayList<JsonBase>();
    }

    public void addElement(JsonBase jb) {
        elements.add(jb);
    }

    public void addAllElements(JsonArray array) {
        for(JsonBase base: array) {
            elements.add(base);
        }
    }

    public Value asValue() {
        if( elements.size() == 1) {
            return (Value) elements.get(0);
        }
        ListValue list = new ListValue();
        for(JsonBase element : elements) {
            list.add(element);
        }
        return list;
    }

    public int getContextSize() {
        return elements.size();
    }

    public JsonBase getElement(int i) {
        return elements.get(i);
    }

    public Iterator iterator() {
        return elements.iterator();
    }

}
