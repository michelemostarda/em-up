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


/**
 * Defines a path navigating a <i>JSON</i> object.
 *
 * @see com.asemantics.mashup.processor.jsonpath.Accessor
 */
public interface Path extends Iterable<Accessor> {

    /**
     * Number of steps.
     *
     * @return a positive number of steps.
     */
    int size();

    /**
     * Returns the <i>i-th</i> step in path.
     *
     * @param i index of path accessor.
     * @return accessor element.
     */
    Accessor getAccessor(int i);

    /**
     * Returns this path as a string.
     *
     * @return string representing this path.
     */
    String asString();
}
