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


package com.asemantics.mashup.parser;

import com.asemantics.mashup.processor.Processor;
import com.asemantics.mashup.processor.ProcessorException;
import com.asemantics.mashup.processor.Value;

/**
 * Defines any entity that can be handled by a {@link com.asemantics.mashup.processor.Processor}.
 *
 * @see com.asemantics.mashup.parser.MUPreposition
 */
public interface Processable {

    /**
     * Validates the current processable to verify that is always executable.
     * 
     * @throws ValidationException
     */
    void validate() throws ValidationException;

    /**
     * Processes the current entity.
     * 
     * @param processor
     * @throws com.asemantics.mashup.processor.ProcessorException if an error occurs while processing
     *         this processable.
     */
    Value process(Processor processor) throws ProcessorException;

}
