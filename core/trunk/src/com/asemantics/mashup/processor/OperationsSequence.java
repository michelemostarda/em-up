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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Defines a sequence of operations.
 *
 * @see com.asemantics.mashup.processor.Operation
 */
public class OperationsSequence implements Operation {

    /**
     * Sequence of operations to be executed.
     */
    private Operation[] operations;

    /**
     * Temporary data used for the construction of <i>operations</i> list.
     */
    private List<Operation> temporaryList;

    /**
     * Constructor
     */
    public OperationsSequence() {
        // Empty.
    }

    /**
     * Adds an operation to the sequence.
     *
     * @param operation
     */
    public void addOperation(Operation operation) {
        if( isCompleted() ) {
            throw new IllegalStateException("This sequence was completed.");
        }

        if( temporaryList == null ) {
            temporaryList = new ArrayList<Operation>();
        }
        temporaryList.add(operation);
    }

    /**
     * Adds a list of operations in the order they are provided.
     * @param operations
     */
    public void addOperations(Operation[] operations) {
        if( isCompleted() ) {
            throw new IllegalStateException("This sequence was completed.");
        }

        if( temporaryList == null ) {
            temporaryList = new ArrayList<Operation>();
        }
        temporaryList.addAll( Arrays.asList(operations) );
    }

    /**
     * Returns <code>true</code> if this sequence is completed,
     * <code>false</code> otherwise.
     * @return check result.
     */
    public boolean isCompleted() {
        return operations != null;
    }

    /**
     * Finalizes this sequence, setting it ready to be executed.
     */
    public void complete() {
        if( operations != null ) {
            return;
        }
        
        operations = temporaryList.toArray( new Operation[ temporaryList.size() ] );
        temporaryList.clear();
        temporaryList = null;
    }

    public void validate(Set<String> context) throws ValidationException {
        for(Operation o : operations) {
            o.validate(context);
        }
    }

    /**
     * Executes the operations sequence on the specified <i>context</i> and
     * <i>stack</i> starting from <i>begin</i> index.
     *
     * @param context execution context.
     * @param stack execution stack.
     * @param begin begin index.
     * @return evalutation result.
     * @throws SequenceNotFoundException if invoked sequence cannot be found.
     * @throws ArgumentEvaluationException if SCM cannot be populated.
     * @throws InvocationException if an error occurs during invocation.
     */
    public Value execute(ExecutionContext context, ExecutionStack stack, final int begin)
    throws SequenceNotFoundException, ArgumentEvaluationException {
        Value result = null;
        stack.beginSequence(this);
        for(int i = begin; i < operations.length; i++) {
            try {
                result = operations[i].execute(context, stack);
                stack.nextOperation(result);                
            } catch (InvocationException ie) {
                stack.raisedException(ie);
            }
            if( stack.isBreak() ) {
                break;
            }
        }
        stack.endSequence(this);
        return result;
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException {
        return execute(context, stack, 0);
    }

    public Operation[] getInnerOperations() {
        if( operations == null ) {
            throw new IllegalStateException();
        }
        return operations;
    }

    public int hashCode() {
        int result = 1;
        for(Operation operation : operations) {
            result *= operation.hashCode();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if( obj instanceof OperationsSequence) {
            OperationsSequence other = (OperationsSequence) obj;
            if( operations.length != other.operations.length ) {
                return false;
            }
            for(int i = 0; i < operations.length; i++) {
                if( ! operations[i].equals( other.operations[i] ) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
