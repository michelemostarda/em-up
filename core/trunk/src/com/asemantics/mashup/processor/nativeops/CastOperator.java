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
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Defines the base class for any cast operation.
 */
public abstract class CastOperator extends NativeInvocable {

    private static final String VALUE = "value";

    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.ANY, VALUE)
            }
    );

    /**
     * Abstract method to be implemented to perform cast.
     *
     * @param target the value to cast.
     * @return the casted value.
     */
    protected abstract Value castValue(Value target);

    public Signature getSignature() {
        return SIGNATURE;
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        Value target = context.getIthValue(0);
        try {
            return castValue( target );
        } catch (Exception e) {
            throw new InvocationException("Error while performing casting.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
