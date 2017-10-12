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

import com.asemantics.mashup.processor.OperationsSequence;
import com.asemantics.mashup.processor.Processor;
import com.asemantics.mashup.processor.ProcessorException;
import com.asemantics.mashup.processor.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines an evaluation.
 */
public class Evaluation implements Processable {

    /**
     * Sequence to be evaluated.
     */
    private OperationsSequence operations;

    /**
     * Constructor.
     *
     * @param os
     */
    protected Evaluation(OperationsSequence os) {
        operations = os;
    }

    /**
     * Returns the internal sequence.
     * @return internal operations sequence.
     */
    protected OperationsSequence getOperationsSequence() {
        return operations;
    }

    public void validate() throws ValidationException {

        // No arguments predefined.
        Set<String> definedArguments = new HashSet<String>();

        // Calls validation.
        operations.validate(definedArguments);

    }

    public Value process(Processor processor) throws ProcessorException {
        return processor.processOperation( getOperationsSequence() );
    }
}
