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

import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Defines a condition operation, providing support for decisions.
 * A condition operation always returns a
 * {@link com.asemantics.mashup.processor.BooleanValue}.
 */
public abstract class Condition extends NativeInvocable {

    /**
     * First argument.
     */
    private static final String ARG1 = "arg1";

    /**
     * Second argument.
     */
    private static final String ARG2 = "arg2";

    /**
     * Condition signature.
     */
    private static final Signature SIGNATURE;

    static {
        SIGNATURE = new Signature(
                new FormalParameter[]{
                        new FormalParameter(FormalParameter.Type.ANY, ARG1),
                        new FormalParameter(FormalParameter.Type.ANY, ARG2),
                }
        );
    }

    /**
     * Constructor.
     */
    public Condition() {
        // Empty.
    }

    /**
     * Checks the condition value.
     *
     * @param argument1
     * @param argument2
     * @return boolean result.
     */
    protected abstract BooleanValue checkCondition(Value argument1, Value argument2);

    public Signature getSignature() {
        return SIGNATURE;
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        Value arg1 = context.getIthValue(0);
        Value arg2 = context.getIthValue(1);
        try {
            return checkCondition(arg1, arg2);
        } catch (Exception e) {
            throw new InvocationException("Error while computing condition.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }

}
