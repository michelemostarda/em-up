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

import com.asemantics.mashup.gui.ConcreteUIBuilder;
import com.asemantics.mashup.gui.Renderer;
import com.asemantics.mashup.gui.UIComponent;
import com.asemantics.mashup.gui.UIWindow;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Implements the <i>Renderer(json)</i> operation.
 */
public class RenderizeOperation extends NativeInvocable {

    /**
     * JSON parameter.
     */
    private static final String JSON = "json";

    /**
     * Static signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.JSON, JSON)
            }
    );

    /**
     * Internal renderer.
     */
    private Renderer<UIComponent> renderer;

    /**
     * Constructor.
     */
    public RenderizeOperation() {
        renderer = new Renderer<UIComponent>( new ConcreteUIBuilder() );
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Renderizes a JSON object";
    }

    public String getDescription() {
        return getShortDescription();
    }

    @SuppressWarnings("unchecked")
    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        // Retries json string.
        JsonValue jsonValue = context.getIthValueAsJson(0);

        try {
            // Define the right view for the JSON model.
            renderer.reset();
            UIComponent uiComponent = renderer.renderizeAsPanel( jsonValue.getJsonBase() );

            // Create Popup window.
            UIWindow window = NativeImpl.getInstance().getUIFactory().createWindow();

            window.addComponent( uiComponent );
            window.show();

            // Returns the widget description.
            return new StringValue( uiComponent.getDescriptionScheme().asJSON() );

        } catch (Exception e) {
            throw new InvocationException("Error while renderizing JSON: " + e.getMessage(), e );
        }

    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }
}
