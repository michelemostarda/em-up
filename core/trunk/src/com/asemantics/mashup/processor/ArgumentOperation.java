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
 * Returns the value of argument contained inside it when executed.
 * TODO: Argument and Operation should be merged in the same entity.
 *
 * @see Argument
 */
public class ArgumentOperation implements Operation {

    /**
     * Argument to be returned by this operation.
     */
    private Argument argument;

    /**
     * Const argument.
     *
     * @param a
     */
    public ArgumentOperation(Argument a) {
        argument = a;
    }

    public void validate(Set<String> context) throws ValidationException {
        if( argument instanceof Validable ) {
            ((Validable) argument).validate(context);
        }
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        try {
        return argument.getValue(context, stack);
        } catch (VariableNotFoundException vnfe) {
            throw new ArgumentEvaluationException("Cannot find argument variable.", vnfe);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}

