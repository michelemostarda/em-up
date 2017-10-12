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

import com.asemantics.mashup.digester.Digester;
import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Implements the <i>Get(url, [params] )</i> operation.
 */
public class XPathOperation extends NativeInvocable {

    /**
     * Path argument.
     */
    public static final String PATH = "path";

    /**
     * Input argument.
     */
    public static final String INPUT = "input";

    /**
     * Operation signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[]{
                    new FormalParameter(FormalParameter.Type.STRING, PATH),
                    new FormalParameter(FormalParameter.Type.STRING, INPUT)
            }
    );

    /**
     * Digester unique instance.
     */
    private final Digester digester;

    /**
     * Constructor.
     */
    public XPathOperation() {
        digester = new Digester();
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Performs an XPath query on given input.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        final String path  = context.getIthValueAsString(0).getNativeValue();
        final String input = context.getIthValueAsString(1).getNativeValue();

        String[] result;
        try {
            result = digester.findMatches(path, input);

            ListValue lv = new ListValue();
            for(String r : result) {
                lv.add( new StringValue( Utils.encodeStringAsHTML(r) ) );
            }
            return lv;

        } catch (Exception e) {
            throw new InvocationException( "Error while computing XPath.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }

}