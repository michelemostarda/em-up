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


package com.asemantics.mashup.processor.unification;

import com.asemantics.mashup.processor.json.JsonBase;


/**
 * Defines any <i>JSON</i> model able to perform inification with a
 * <i>JSON</i> data structure.
 */
public interface Unifiable {

    /**
     * Unifies current object with given <i>jsonBase</i> data, returing result
     * into result builder <i>rb</i>.
     *
     * @param rb result builder.
     * @param jb JSON data.
     * @throws UnificationException if unification cannot be satisfied.
     */
    void unify(ResultBuilder rb, JsonBase jb) throws UnificationException;

}
