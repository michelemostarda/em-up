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
 * This operation provides support for nested sequence invocations.
 *
 * @see DequeueArgument
 */
public class EnqueueArgumentOperation implements Operation {

    /**
     * Singleton instance.
     */
    private static final EnqueueArgumentOperation INSTANCE = new EnqueueArgumentOperation();

    /**
     * Returns the singleton instance.
     * 
     * @return instance.
     */
    public static EnqueueArgumentOperation getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor.
     */
    private EnqueueArgumentOperation() {
        // Empty.
    }

    public void validate(Set<String> context) throws ValidationException {
        // Empty.
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        context.enqueueArgument( stack.getLastExecutionValue() );
        return NullValue.getInstance();
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
