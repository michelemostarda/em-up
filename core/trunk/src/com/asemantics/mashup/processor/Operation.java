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
 * Defines any kind of operation that can be executed by {@link com.asemantics.mashup.processor.Processor}
 *
 * @see com.asemantics.mashup.processor.OperationsSequence
 */
public interface Operation extends Validable {

    /**
     * Executes this operation on the current environment.
     *
     * @param context execution context.
     * @param stack execution stack.
     * @return  the value generated by the execution of this operation.
     * @throws SequenceNotFoundException
     * @throws ArgumentEvaluationException
     * @throws InvocationException
     */
    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException;

    /**
     * Due an operation can be a complex operation, it is needed a method returning
     * content of inner operations.
     * 
     * @return list of operations inside this operation.
     */
    public Operation[] getInnerOperations();

}
