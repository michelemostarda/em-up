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

import java.util.List;
import java.util.ArrayList;

/**
 * Defines the processor main class.
 */
public class Processor {
    
    /**
     * Execution context of this processor.
     */
    private ExecutionContext executionContext;

    /**
     * Execution stack of this processor.
     */
    private ExecutionStack   executionStack;

    /**
     * List of processor listeners.
     */
    private List<ProcessorListener> listeners;

    /**
     * 0 arguments constant.
     */
    private static final Argument[] ARGUMENTS = new Argument[]{};

    /**
     * Constructor.
     */
    public Processor() {
        executionContext = new ExecutionContext();
        executionStack   = new ExecutionStack();
    }

    /**
     * Returns the underlying execution context.
     *
     * @return execution context.
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Returns the underlying execution stack.
     *
     * @return execution stack.
     */
    public ExecutionStack getExecutionStack() {
        return executionStack;
    }

    /**
     * Adds a predicate to this processor.
     *
     * @param predicateName  name of newly added predicate.
     * @param operation operation representing body of predicate.
     * @return the invocable value corresponding to new defined predicate.
     */
    public Value addPredicate(String predicateName, Invocable operation) {
        validatePredicateName(predicateName);
        executionContext.addSequence(predicateName, operation, ExecutionContext.NATIVE);
        notifyAddedNativePredicate(predicateName, operation);
        return new InvocableValue(operation);
    }

    /**
     * Adds a programmative predicate to this processor.
     *
     * @param predicateName
     * @param operation
     * @return the invocable value corresponding to new defined predicate.
     */
    public Value addProgrammativePredicate(String predicateName, Invocable operation) {
        validatePredicateName(predicateName);
        executionContext.addSequence(predicateName, operation, (byte) (ExecutionContext.DELETABLE | ExecutionContext.OVERRIDABLE) );
        notifyAddedProgrammativePredicate(predicateName, operation);
        return new InvocableValue(operation);
    }

    /**
     * Removes a predicate from this processor.
     *
     * @param predicateName
     */
    public void removePredicate(String predicateName) {
        ExecutionContext.InvocableObject[] removed = executionContext.removeSequence(predicateName);
        for(ExecutionContext.InvocableObject invocableObject : removed) {
            notifyRemovedPredicate( predicateName, invocableObject.getInvocable() );
        }
    }

    /**
     * Checkes if a processor contains a predicate name with a given signature.
     *
     * @param predicateName
     * @param signature
     * @return check result.
     */
    public boolean definesPredicate(String predicateName, Signature signature) {
        return executionContext.getSequence(predicateName, signature) != null;
    }

    /**
     * Adds a variable with a given value.
     *
     * @param varName
     * @param value
     */
    public void addVariable(String varName, Value value) {
        validateVariableName(varName);
        executionContext.addVariable(varName, value);
    }

    /**
     * Adds a variable with a given string value.
     *
     * @param varName
     * @param value
     */
    public void addVariable(String varName, String value) {
        addVariable(varName, new StringValue(value) );
    }

    /**
     * Adds a variable with a given numeric value.
     *
     * @param varName
     * @param value
     */
    public void addVariable(String varName, double value) {
        addVariable(varName, new NumericValue(value) );
    }

    /**
     * Returns value of a variable.
     *
     * @param varName
     * @return variable value.
     */
    public Value getValue(String varName) {
        return executionContext.getValue(varName);
    }

    /**
     * Returns string value of a variable.
     *
     * @param varName
     * @return variable value.
     */
    public String getValueString(String varName) {
        Value v = executionContext.getValue(varName);
        return v == null ? null : v.asString().getNativeValue();
    }

    /**
     * Returns numeric value of a variable.
     *
     * @param varName
     * @return double value.
     */
    public Double getDoubleString(String varName) {
        Value v = executionContext.getValue(varName);
        return v == null ? null : ((NumericValue) v).getNativeValue();
    }

    public Value processOperation(InvokeOperation invoke) throws ProcessorException {
        if( invoke == null ) {
            throw new ProcessorException("Cannot process null invoke operation.");
        }
        try {
            executionStack.pushLevel(executionContext, invoke);
            return executionStack.getLastExecutionValue();
        } catch (Exception e) {
            throw new ProcessorException("Error while processing invocation.", e);
        }
    }

    /**
     * Processes the given anonynous operations senquence in this processor.
     *
     * @param sequence sequence to be processed.
     * @return result of processed sequence.
     * @throws ProcessorException
     */
    public Value processOperation(OperationsSequence sequence) throws ProcessorException {
         if( sequence == null ) {
            throw new ProcessorException("Cannot process null invoke operation.");
        }
        try {
            executionStack.pushLevel(executionContext, sequence);
            return executionStack.getLastExecutionValue();
        } catch (Exception e) {
            throw new ProcessorException("Error while processing sequence.", e);
        }
    }

    /**
     * Processes a given predicate name with specified arguments.
     *
     * @param predicateName
     * @param arguments
     * @return process result.
     * @throws ProcessorException
     */
    public Value processPredicate(String predicateName, Argument[] arguments) throws ProcessorException {
        InvokeOperation invokeOperation = new InvokeOperation(predicateName, arguments);
        return processOperation(invokeOperation);
    }

    /**
     * Processes a given predicate name with no arguments.
     *
     * @param predicateName
     * @return process result.
     * @throws ProcessorException
     */
    public Value processPredicate(String predicateName) throws ProcessorException {
        return processPredicate(predicateName, ARGUMENTS);
    }

    /**
     * Adds a processor listener.
     *
     * @param pl listener to be added.
     */
    public void addListener(ProcessorListener pl) {
        if(listeners == null) {
            listeners = new ArrayList<ProcessorListener>();
        }
        listeners.add(pl);
    }

    /**
     * Removes a processor listener.
     *
     * @param pl listener to be removed.
     */
    public void removeListener(ProcessorListener pl) {
        if( listeners != null && listeners.remove(pl) && listeners.isEmpty()) {
            listeners = null;
        }
    }

    /**
     * Validates a variable name.
     *
     * @param varName
     */
    private void validateVariableName(String varName) {
        if(varName == null || varName.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid predicate NAME: '" + varName + "'");
        }
    }

    /**
     * Validates a predicate name.
     * 
     * @param predicateName
     */
    private void validatePredicateName(String predicateName) {
        if(predicateName == null || predicateName.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid predicate NAME: '" + predicateName + "'");
        }
    }

    /**
     * Notifies all listeners that a native predicate has been added.
     *
     * @param name
     * @param invocable
     */
    private void notifyAddedNativePredicate(String name, Invocable invocable) {
        if(listeners == null) { return; }
        for(ProcessorListener listener : listeners) {
            try {
                listener.nativePredicateAdded(name, invocable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies all listeners that a programmative predicate has been added.
     *
     * @param name
     * @param invocable
     */
    private void notifyAddedProgrammativePredicate(String name, Invocable invocable) {
        if(listeners == null) { return; }
        for(ProcessorListener listener : listeners) {
            try {
                listener.addedProgrammativePredicate(name, invocable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Notifies all listeners that a native predicate has been removed.
     *
     * @param name
     * @param invocable
     */
    private void notifyRemovedPredicate(String name, Invocable invocable) {
        if(listeners == null) { return; }
        for(ProcessorListener listener : listeners) {
            try {
                listener.predicateRemoved(name, invocable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
