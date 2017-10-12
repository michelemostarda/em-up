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
import com.asemantics.mashup.processor.unification.ModelElement;
import com.asemantics.mashup.processor.unification.Unifier;
import com.asemantics.mashup.processor.unification.UnificationResult;

import java.util.Set;

/**
 * Defines a variable inside the execution context assigning it the
 * value of given argument.
 *
 * @see com.asemantics.mashup.processor.ExecutionContext
 */
public class AssignmentOperation implements Operation {

    /**
     * Internal unifier.
     */
    private static final Unifier unifier;

    /**
     * Model to be assigned.
     */
    private ModelElement model;

    /**
     * Value of variable.
     */
    private Argument argument;

    static {
        unifier = new Unifier();
    }

    /**
     * Constructor.
     * Assign <i>argument</i> to the given <i>variable</i>.
     *
     * @param model
     * @param argument
     */
    public AssignmentOperation(ModelElement model, Argument argument) {
        if( model == null ) {
            throw new IllegalArgumentException();
        }
        if( argument == null ) {
            throw new IllegalArgumentException();
        }
        this.model    = model;
        this.argument = argument;
    }

    /**
     * Constructor.
     * Assign last operation value to the given <i>variable</i>.
     *
     * @param model
     */
    public AssignmentOperation(ModelElement model) {
        if( model == null ) {
            throw new IllegalArgumentException();
        }
        this.model = model;
        argument = null;
    }

    /**
     * Model to be assigned to argument evaluation.
     *
     * @return variable name.
     */
    public ModelElement getAssignmentModel() {
        return model;
    }

    /**
     * Argument to be evaluated and then assigned to variable.
     * 
     * @return argment to be evaluated.
     */
    Argument getArgument() {
        return argument;
    }

    public void validate(Set<String> context) throws ValidationException {
        // If argunent is an invocation argument then it will be validated.
        if( argument instanceof InvocationArgument) {
            InvocationArgument ia = (InvocationArgument) argument;
            ia.getInvocationValue().getInvocation().validate( context );
        }
        // Operation validation will be propagated to the model.
        model.validate(context);
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        // Evaluate argument.
        Value value;
        try {
            value = argument == null ? stack.getLastExecutionValue() : argument.getValue( context, stack );
        } catch (VariableNotFoundException vnfe) {
            throw new ArgumentEvaluationException("Cannot find argument variable.", vnfe);
        }
        // Perform unification.
        UnificationResult unificationResult = unifier.unify(
                model,
                value instanceof JsonValue ? (JsonValue) value : new JsonValue(value) // TODO: check this and resolve.
        );
        if( unificationResult.isFailed() ) {
            throw new ArgumentEvaluationException(
                    "Error while unifying value'" + value + "' to model '" + model + "'.",
                    unificationResult.getUnificationException()
            );
        }
        // Inject result in cotext.
        for(String variable : unificationResult.getVariables()) {
            context.addVariable(variable, (Value) unificationResult.getValue(variable));
        }
        return value;
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }
}
