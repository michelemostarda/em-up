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


package com.asemantics.mashup.processor.json;

/**
 * Defines the <i>JSON</i> array.
 */
public interface JsonArray extends JsonComplex<JsonBase>, Iterable<JsonBase> {

    /**
     * Returns the i-th element of array.
     *
     * @param i
     * @return i-th element.
     */
    public JsonBase get(int i);

    /**
     * Adds an element to the array.
     *
     * @param v
     */
    public void add(JsonBase v);

    /**
     * Adds a boolean in the array.
     *
     * @param b
     */
    public void add(boolean b);

    /**
     * Adds a string in the array.
     *
     * @param s
     */
    public void add(String s);

    /**
     * Adds an integer in th array.
     *
     * @param i
     */
    public void add(int i);

    /**
     * Adds a double in the array.
     *
     * @param d
     */
    public void add(double d);

    /**
     * Removes an element from array.
     *
     * @param i
     */
    public void remove(int i);

}