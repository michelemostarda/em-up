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


package com.asemantics.mashup.processor.nativeops;

import com.asemantics.mashup.parser.ValidationException;
import com.asemantics.mashup.processor.Argument;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.InvocationArgument;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.InvocationValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.VariableArgument;
import com.asemantics.mashup.processor.VariableNotFoundException;

import java.util.Set;

/**
 * Defines the flow control <i>for</i> operator.
 */
public class ForFlowControl extends AbstractFlowControlOperation {

    /**
     * Constructor.
     *
     * @param args
     */
    public ForFlowControl(Argument[] args) {
        super(args);
    }

    /**
     *
     * @return the list argument.
     */
    public Argument getListArgument() {
        return getArguments()[0];
    }

    /**
     * Returns the variable used by <i>For</i> to cycle on given list.
     *
     * @return variable used to cycle.
     */
    public VariableArgument getCycleVariable() {
        return (VariableArgument) getArguments()[1];
    }

    /**
     * Returns the cycle invocation argument.
     *
     * @return invocation value.
     */
    public InvocationArgument getInvocationArgument() {
        return ((InvocationArgument) getArguments()[2]);
    }

    public void validate(Set<String> context) throws ValidationException {
        // Validates list statement.
        Argument listArgument = getListArgument();
        if(listArgument instanceof InvocationArgument) {
            ((InvocationArgument) listArgument).getInvocationValue().getInvocation().validate(context);
        }
        // Validates invocation value.
        InvocationValue iv = getInvocationArgument().getInvocationValue();
        String cycleVarName = getCycleVariable().getVarName();
        String varName;
        for(Argument a : iv.getInvocation().getArguments() ) {
            if( a instanceof VariableArgument ) {
                varName = ((VariableArgument) a).getVarName();
                if( ! cycleVarName.equals(varName) && ! context.contains(varName) ) {
                    throw new ValidationException("Error while validating for cycle: variable '" + varName + "' undefined.");
                }
            }
        }
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

        Value list;
        try {
             list = getListArgument().getValue( context, stack );
        } catch (VariableNotFoundException vnfe) {
            throw new ArgumentEvaluationException("Error while retrieving list variable.", vnfe);
        }
        final ListValue listValue = list.asList();
        final String variable     = getCycleVariable()     .getVarName();
        InvocationValue predicate = getInvocationArgument().getInvocationValue();

        ListValue result = new ListValue();
        for(Value v  : listValue.getNativeValue() ) {
            context.addVariable( variable, v );
            result.add( predicate.getInvocation().execute( context, stack ) );
        }
        return result;
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }

}