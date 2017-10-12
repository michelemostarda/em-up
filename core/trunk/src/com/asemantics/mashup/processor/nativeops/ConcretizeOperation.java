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

import com.asemantics.mashup.gui.*;
import com.asemantics.mashup.processor.*;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.nativepkg.NativeImpl;

/**
 * Implements the <i>Concretize(jsonData)</i> operation.
 */
public class ConcretizeOperation extends NativeInvocable {

    /**
     * JSON UI model parameter.
     */
    private static final String JSON_UI_MODEL = "jsonUI";

    /**
     * Operation's signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(
                        FormalParameter.Type.JSON,
                            JSON_UI_MODEL
                    )
            }
    );

    /**
     * Constructor.
     */
    public ConcretizeOperation() {
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Creates a UI Native interface on the given JSON UI model.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

        JsonValue jsonValue = context.getIthValueAsJson(0);

        try {

            UIComponent uiComponent = JsonTranslator.convertToUI( (JsonObject) jsonValue.getJsonBase() );

            // Create Popup window.
            UIWindow window = NativeImpl.getInstance().getUIFactory().createWindow();

            window.addComponent( uiComponent );
            window.show();

            // Returns the widget description.
            return new StringValue( jsonValue.getJsonBase().asPrettyJSON() );

        } catch (Exception e) {
            throw new InvocationException("Cannot modelize JSON data.");
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}
