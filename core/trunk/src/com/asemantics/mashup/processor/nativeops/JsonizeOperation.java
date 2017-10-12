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

import com.asemantics.mashup.interpreter.Utils;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.nativepkg.NativeImpl;

import java.util.List;

/**
 * Implements the <i>Jsonize(json, params[])</i> operation.
 */
public class JsonizeOperation extends NativeInvocable {

    /**
     * JSON param.
     */
    public static final String JSON = "json";

    /**
     * PARAMS params.
     */
    public static final String PARAMS = "params";

    /**
     * Operation signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[]{
                    new FormalParameter(FormalParameter.Type.STRING, JSON  ),
                    new FormalParameter(FormalParameter.Type.LIST  , PARAMS)
            }
    );

    /**
     * Constructor.
     */
    public JsonizeOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Expands given json model with params";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        final String json            = context.getIthValueAsString(0).getNativeValue();
        final List<Value> paramsList = context.getIthValueAsList  (1).getNativeValue();

        try {
            final String expandedJSON = Utils.expandsString( json, paramsList.toArray( new Value[ paramsList.size() ] ) );
            return new JsonValue( NativeImpl.getInstance().parseJSON( expandedJSON ) );
        } catch (Exception e) {
            throw new InvocationException("Error while Jsonize arguments.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}