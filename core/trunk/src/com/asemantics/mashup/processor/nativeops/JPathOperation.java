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
import com.asemantics.mashup.processor.jsonpath.DefaultExtractorImpl;
import com.asemantics.mashup.processor.jsonpath.Extractor;

public class JPathOperation extends NativeInvocable {


    /**
     * Input argument.
     */
    public static final String INPUT    = "input";

    /**
     * Path argument.
     */
    public static final String JSONPATH = "jsonpath";

    /**
     * Operation signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.JSON  , INPUT   ),
                    new FormalParameter(FormalParameter.Type.STRING, JSONPATH)
            }
    );

    /**
     * JSONPath extractor.
     */
    private Extractor extractor;

    /**
     * Constructor.
     */
    public JPathOperation() {
        extractor = new DefaultExtractorImpl();
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Applies a JSON Path to the given object.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        JsonValue   value    = context.getIthValueAsJson  (0);
        StringValue jsonpath = context.getIthValueAsString(1);
        try {
            return extractor.extractPath(jsonpath.getNativeValue(), value.getJsonBase() ).asValue();
        } catch (Exception e) {
            throw new InvocationException("Error while applying JPath.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
