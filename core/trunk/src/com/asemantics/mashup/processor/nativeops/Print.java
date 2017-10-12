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
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.VarargsSignature;

/**
 * Defines a native operation able to print on execution context out stream.
 */
public class Print extends NativeInvocable {

    public Signature getSignature() {
        return Signature.VARARGS;
    }

    public String getShortDescription() {
        return "Prints given params...";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        Value[] values = VarargsSignature.getVarargs(context);

        try {
            StringBuilder result = new StringBuilder();
            StringValue sv;
            for(Value value : values) {
                sv = value == null ? StringValue.NULL_STRING_VALUE : value.asString();
                context.print( sv );
                result.append( sv.getNativeValue() );
            }
            return new StringValue( result.toString() );
        } catch (Exception e) {
            throw new InvocationException("Error while printing arguments.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
