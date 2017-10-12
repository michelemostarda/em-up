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

import com.asemantics.mashup.parser.ValidationException;

import java.util.Set;

/**
 * Defines any element that can be submitted to validation on the
 * variables used in it.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: Validable.java 442 2009-06-25 23:02:34Z michelemostarda $
 */
public interface Validable {

    /**
     * Validates this operation on the given context to verify
     * if all variables are defined at the right time. The given
     * set contains the already defined variables.
     * If validation occurs, the validated entity adds its own defined
     * variables inside the given context.
     *
     * @param context
     * @throws com.asemantics.mashup.parser.ValidationException
     */
    void validate(Set<String> context) throws ValidationException;

}
