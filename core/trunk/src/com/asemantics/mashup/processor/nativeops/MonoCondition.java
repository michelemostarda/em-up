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

import com.asemantics.mashup.processor.*;

/**
 * Defines conditions accepting single argument.
 */
public abstract class MonoCondition extends NativeInvocable {

    /**
     * Argument.
     */
    private static final String ARG = "arg";

    /**
     * Condition signature.
     */
    private static final Signature SIGNATURE;

    static {
        SIGNATURE = new Signature(
                new FormalParameter[]{
                        new FormalParameter(FormalParameter.Type.ANY, ARG)
                }
        );
    }

    /**
     * Valuates the condition result.
     *
     * @param v
     * @return condition result.
     */
    protected abstract BooleanValue valuateCondition(Value v)
    throws InvocationException;

    /**
     * Constructor.
     */
    public MonoCondition() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        Value arg = context.getIthValue(0);
        try {
            return valuateCondition(arg);
        } catch (Exception e) {
            throw new InvocationException("Error while evaluating condition.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}
