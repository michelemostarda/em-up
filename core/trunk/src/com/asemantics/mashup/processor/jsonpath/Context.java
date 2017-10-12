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

import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.Value;

/**
 * Represents the current context of an accessor.
 *
 * @see com.asemantics.mashup.processor.jsonpath.Accessor
 * @see com.asemantics.mashup.processor.jsonpath.Path
 * @see com.asemantics.mashup.processor.jsonpath.Extractor
 */
public interface Context extends Iterable<JsonBase> {

    /**
     * Number of JSON elements present in this context.
     *
     * @return number of elements in context.
     */
    int getContextSize();

    /**
     * Returns the i-th element in this context.
     *
     * @param i element index.
     * @return JSON element.
     */
    JsonBase getElement(int i);

    /**
     * Adds a value to this context.
     *
     * @param jb value to be added.
     */
    void addElement(JsonBase jb);

    /**
     * Returns the representation of this context as value.
     *
     * @return a value.
     */
    Value asValue();
}
