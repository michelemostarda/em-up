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
 * Base class to define Predicate connector operators.
 *
 * @see com.asemantics.mashup.processor.OperationsSequence
 * @see com.asemantics.mashup.processor.ExecutionStack
 */
public abstract class AbstractConnectorOperation implements Operation {

    /**
     * Returns the connector decision.
     * 
     * @param ec execution context.
     * @param es execution stack.
     * @return <code>true</code> if sequence must be broken, <code>false</code> otherwise.
     */
    public abstract boolean mustBreakSequence( ExecutionContext ec, ExecutionStack es);

    public void validate(Set<String> context) throws ValidationException {
        // Empty.
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        if( mustBreakSequence(context, stack) ) {
            stack.breakSequence();
        }
        return stack.getLastExecutionValue();
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
