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

import com.asemantics.mashup.gui.Renderer;
import com.asemantics.mashup.gui.JsonUIBuilder;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonObject;

/**
 * Implements the <i>Modelize(jsonData)</i> operation.
 */
public class ModelizeOperation extends NativeInvocable {

    /**
     * JSON data parameter.
     */
    private static final String JSON_DATA = "jsonData";

    /**
     * Operation's signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(
                        FormalParameter.Type.JSON,
                        JSON_DATA
                    )
            }
    );

    /**
     * Internal renderer.
     */
    private Renderer<JsonObject> renderer;

    /**
     * Constructor.
     */
    public ModelizeOperation() {
        renderer = new Renderer<JsonObject>( new JsonUIBuilder() );
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Generates a JSON UI model on the given JSON data.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

        JsonValue jsonValue = context.getIthValueAsJson(0);

        try {
            JsonObject result = renderer.renderizeAsPanel( jsonValue.getJsonBase() );
            return new JsonValue( result );
        } catch (Exception e) {
            throw new InvocationException("Cannot modelize JSON data.");
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}
