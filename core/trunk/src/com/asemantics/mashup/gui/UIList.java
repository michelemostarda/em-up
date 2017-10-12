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

/**
 * Defines an abstract <i>User Interface</i> list.
 */
public interface UIList<T> extends UIComponent<T> {

    public static final String NAME = "UIList";

    /**
     * Adds an entry.
     *
     * @param entry
     */
    public void addEntry(String entry);

    /**
     * Removes an entry.
     *
     * @param entry
     */
    public void removeEntry(String entry);

    /**
     * Removes i-th entry.
     *
     * @param i
     */
    public void removeEntry(int i);

    /**
     * Removes all entries.
     */
    public void removeAll();

    /**
     * Returns a JSON array representing context of list.
     * 
     * @return list of elements as JSON.
     */
    public JsonArray getJSONList();

}
