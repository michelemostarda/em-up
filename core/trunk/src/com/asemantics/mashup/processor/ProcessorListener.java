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


package com.asemantics.mashup.processor;

import com.asemantics.mashup.processor.Invocable;

/**
 * Defines a listener for {@link com.asemantics.mashup.processor.Processor} events. 
 */
public interface ProcessorListener {

    /**
     * Notifies listener that a native predicate has been added.
     *
     * @param name
     * @param invocable
     */
    void nativePredicateAdded(String name, Invocable invocable);

    /**
     * Notifies listener that a programmative predicate has been added.
     * 
     * @param name
     * @param invocable
     */
    void addedProgrammativePredicate(String name, Invocable invocable);

    /**
     * Notifies listener that a programmative predicate has been removed.
     * 
     * @param name
     * @param invocable
     */
    void predicateRemoved(String name, Invocable invocable);

}
