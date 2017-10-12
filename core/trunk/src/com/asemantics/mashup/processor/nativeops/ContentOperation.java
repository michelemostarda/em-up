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

import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Defines the <i>Content(&lt;String&gt;)</i> operation.
 *
 * @see com.asemantics.mashup.common.Utils.ContentType
 */
public class ContentOperation extends NativeInvocable {

    /**
     *  Input string parameter.
     */
    private static final String IN = "in";


    /**
     * Formal parameters.
     */
    protected static final FormalParameter[] FORMAL_PARAMETERS = new FormalParameter[] {
            new FormalParameter(FormalParameter.Type.STRING, IN),
    };

    /**
     * Signature.
     */
    protected static final Signature SIGNATURE = new Signature(FORMAL_PARAMETERS);

    /**
     * Constructor.
     */
    public ContentOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Induces possible content of given input.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        StringValue model = context.getIthValueAsString(0);

        try {
        Utils.ContentType contentType = Utils.induceType( model.getNativeValue() );
        return new StringValue( contentType.toString() );
        } catch (Exception e) {
            throw new InvocationException("Error while inducing type.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}