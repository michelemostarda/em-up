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

import java.util.Stack;
import java.util.Map;
import java.util.Iterator;

/**
 * This visitor performs a breath-first visit over a <i>JSON</i> data structure.
 */
public class JsonVisitor implements Iterable<JsonBase>, Iterator<JsonBase> {

    /**
     * Visitor stack.
     */
    private Stack<JsonBase> stack;

    /*
     * Constructor.
     *
     * @param base object to inspect.
     */
    public JsonVisitor(JsonBase base) {
        if(base == null) {
            throw new NullPointerException();
        }

        stack = new Stack<JsonBase>();
        stack.push(base);
    }

    /**
     * Returns <code>true</code> if the visit is not ended,
     * <code>false</code> otherwise.
     *
     * @return termination status.
     */
    public boolean hasNext() {
        return ! stack.isEmpty();
    }

    /**
     * The next visitable JSON data.
     *
     * @return JSON data.
     */
    public JsonBase next() {
        JsonBase peek = stack.pop();
        if( peek.isComplex() ) {
            pushComplexElements((JsonComplex) peek);
        }
        return peek;
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException("Object remotion not supported.");
    }

    /**
     * Expands a complex element into the visitor's stack.
     *
     * @param complex the complex element to be expanded.
     */
    private void pushComplexElements(JsonComplex complex) {
        if(complex.isArray()) {

            JsonArray array = (JsonArray) complex;
            for(JsonBase arrayElem : array) {
                stack.add(0, arrayElem);
            }

        } else if(complex.isObject()) {

            JsonObject obj = (JsonObject) complex;
            for(Map.Entry<String,JsonBase> entry : obj) {
                stack.add(0, entry.getValue());
            }

        } else {
            throw new IllegalArgumentException("Unespected object.");
        }
    }

    /**
     * Returns the iterator object.
     * 
     * @return an iterator.
     */
    public Iterator iterator() {
        return this;
    }
}
