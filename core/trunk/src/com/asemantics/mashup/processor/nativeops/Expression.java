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

import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Defines an expression operation, providing support for aritmetics operators.
 * An expression always returns a
 * {@link com.asemantics.mashup.processor.NumericValue}.
 */
public abstract class Expression extends NativeInvocable {

    /**
     * First operator.
     */
    private static final String OP1 = "op1";

    /**
     * Second operator.
     */
    private static final String OP2 = "op2";

    /**
     * Expression signature.
     */
    private static final Signature SIGNATURE;

    static {
        SIGNATURE = new Signature(
                new FormalParameter[]{
                        new FormalParameter(FormalParameter.Type.ANY, OP1),
                        new FormalParameter(FormalParameter.Type.ANY, OP2)
                }
        );
    }

    public Expression() {
        // Empty.
    }

    /**
     * Valuates the expression result.
     * 
     * @param op1
     * @param op2
     * @return numeric value representing evaluation of this expression.
     */
    protected abstract NumericValue valuateExpression(Value op1, Value op2)
    throws InvocationException;

    public Signature getSignature() {
        return SIGNATURE;
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        Value op1 = context.getIthValue(0);
        Value op2 = context.getIthValue(1);
        try {
            return valuateExpression(op1, op2);
        } catch (Exception e) {
            throw new InvocationException("Error while computing expression.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }
}
