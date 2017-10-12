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

/**
 * Defines an argument constituted by an {@link OperationsSequence}.
 *
 * @see com.asemantics.mashup.processor.OperationsSequence
 * @see com.asemantics.mashup.processor.Argument
 */
public class OperationsSequenceArgument implements Argument {

    /**
     * Internal operations sequence.
     */
    private OperationsSequence operationsSequence;

    /**
     * Constructor.
     *
     * @param os operations sequence.
     */
    public OperationsSequenceArgument(OperationsSequence os) {
        operationsSequence = os;
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es)
    throws VariableNotFoundException {
        try {
            return operationsSequence.execute(ec, es);
        } catch (SequenceNotFoundException snfe) {
            throw new VariableNotFoundException("Cannot evaluate argument: sequence not found.", snfe);
        } catch (ArgumentEvaluationException scme) {
            throw new VariableNotFoundException("Cannot evaluate argument: error during evaluation.", scme);
        }
    }
}
