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
import com.asemantics.mashup.processor.NullValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.VariableArgument;
import com.asemantics.mashup.processor.VariableNotFoundException;

import java.util.Set;

/**
 * Defines the flow control <i>if</i> operator.
 */
public class IfFlowControl extends AbstractFlowControlOperation {

    /**
     * If predicate parameter.
     */
    private static final String IF   = "if";

    /**
     * Else predicate parameter.
     */
    private static final String ELSE = "else";

    /**
     * Constructor.
     *
     * @param args
     */
    public IfFlowControl(Argument[] args) {
        super(args);
    }

    /**
     *
     * @return returns the condition arugment.
     */
    public Argument getConditionArgument() {
        return getArguments()[0];
    }

    /**
     *
     * @return returns the if statement.
     */
    public Argument getIfStatement() {
        return getArguments()[1];
    }

    /**
     *
     * @return <code>true</code> of has else statement,
     *         <code>false</code> otherwise.
     */
    public boolean hasElseStatement() {
        return getArguments().length == 3;
    }

    /**
     *
     * @return returns the else statement.
     */
    public Argument getElseStatement() {
        return getArguments()[2];
    }

    @Override
    public void validate(Set<String> context) throws ValidationException {
        // Validates condition.
        Argument conditionArgument = getConditionArgument();
        if( conditionArgument instanceof InvocationArgument ) {
            ((InvocationArgument) conditionArgument).getInvocationValue().getInvocation().validate(context);
        } else {
            validateVariable(context, conditionArgument);
        }

        // Validates if statement.
        Argument ifArgument = getIfStatement();
        if(ifArgument instanceof InvocationArgument) {
            ((InvocationArgument) ifArgument).getInvocationValue().getInvocation().validate(context);
        } else {
            validateVariable(context, ifArgument);
        }

        // Validates else statement.
        if( hasElseStatement() ) {
            Argument elseArgument = getElseStatement();
            if( elseArgument instanceof InvocationArgument) {
                ((InvocationArgument) elseArgument).getInvocationValue().getInvocation().validate(context);
            } else {
                validateVariable(context, elseArgument);
            }
        }
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws ArgumentEvaluationException, InvocationException {
        try {
            Value condition = getConditionArgument().getValue( context, stack );
            Argument ifPredicate, elsePredicate = null;
            try {
                ifPredicate = getIfStatement();
            } catch (ClassCastException cce) {
                throw new InvocationException( IF + " argument must be a sequence.");
            }
            if( hasElseStatement() ) {
                try {
                    elsePredicate = getElseStatement();
                } catch (ClassCastException cce) {
                    throw new InvocationException( ELSE + " argument must be a sequence.");
                }
            }

            Value result = NullValue.getInstance();
            if( condition.asBoolean().getNativeValue() ) {
    
                result = ifPredicate.getValue(context, stack);

            } else if( elsePredicate != null ) {

                result = elsePredicate.getValue(context, stack);

            }
            return result;
        } catch (VariableNotFoundException vnfe) {
            throw new ArgumentEvaluationException("Error while retrieving variables.", vnfe);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }

    /**
     * Validates variable argument.
     * @param ctx
     * @param a
     * @throws com.asemantics.mashup.parser.ValidationException
     */
    private void validateVariable(Set<String> ctx, Argument a) throws ValidationException {
        if(! (a instanceof VariableArgument) ) {
            return;
        }
        VariableArgument va = (VariableArgument) a;
        if( ! ctx.contains(va.getVarName()) ) {
            throw new ValidationException("Cannot find variable '" + va.getVarName() + "' defined in if statement");
        }
    }
}
