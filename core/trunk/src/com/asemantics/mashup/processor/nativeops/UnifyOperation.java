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
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.unification.UnificationResult;
import com.asemantics.mashup.processor.unification.Unifier;

/**
 * Defines the <i>Unify(&lt;JsonModel&gt;, &lt;Json&gt;)</i> operation.
 */
public class UnifyOperation extends NativeInvocable {

    /**
     * Model parameter.
     */
    private static final String MODEL = "model";

    /**
     * JSON data.
     */
    private static final String JSON  = "json";

    /**
     * Formal parameters.
     */
    protected static final FormalParameter[] FORMAL_PARAMETERS = new FormalParameter[] {
            new FormalParameter(FormalParameter.Type.STRING, MODEL),
            new FormalParameter(FormalParameter.Type.JSON,   JSON)
    };

    /**
     * Signature.
     */
    protected static final Signature SIGNATURE = new Signature(FORMAL_PARAMETERS);

    /**
     * Internal unifier instance.
     */
    private Unifier unifier;

    /**
     * Constructor.
     */
    public UnifyOperation() {
        unifier = new Unifier();
    }
    
    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Performs unification between model and JSON value.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack) 
    throws InvocationException {
        StringValue model = context.getIthValueAsString(0);
        Value value       = context.getIthValue(1);

        try {
            UnificationResult unificationResult = unifier.unify( model.getNativeValue(), value.asJsonValue() );
            return new StringValue( unificationResult.toString() );
        } catch (Exception e) {
            throw new InvocationException("Error while performing unification.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}
