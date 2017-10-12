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


package com.asemantics.integration.client.gui;

import com.asemantics.mashup.processor.Invocable;


/**
 * Defines a listener of {@link com.asemantics.integration.client.gui.ContextInspector}.
 */
public interface ContextInspectorListener {

    /**
     * Notifies the listener that a predicate has been selected.
     *
     * @param predicateName name of selected predicate.
     * @param predicate predicate object.
     */
    void predicateSelected(String predicateName, Invocable predicate);

    /**
     * Notifies the listener that a predicate has been double clicked.
     *
     * @param predicateName name of double clicked predicate.
     * @param predicate predicate object.
     */
    void predicateDblClicked(String predicateName, Invocable predicate);

}
