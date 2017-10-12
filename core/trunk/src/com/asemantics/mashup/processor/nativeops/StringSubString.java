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
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Defines operation <i>SubString(&lt;in&gt;, &lt;begin&gt;, &lt;end&gt;)</i>.
 */
public class StringSubString extends ListOperation {

    /**
     * String argument.
     */
    protected static final String IN = "in";

    /**
     * Begin index.
     */
    protected static final String BEGIN = "begin";

    /**
     * End index.
     */
    protected static final String END = "end";

    /**
     * Static signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.STRING,  IN   ),
                    new FormalParameter(FormalParameter.Type.NUMERIC, BEGIN),
                    new FormalParameter(FormalParameter.Type.NUMERIC, END  ) 
            }
    );

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Extracts the substring of a string.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        StringValue  in  = context.getIthValueAsString(0);
        int begin = context.getIthValueAsNumeric(1).integer();
        int end   = context.getIthValueAsNumeric(2).integer();

        try {
            return new StringValue( in.getNativeValue().substring(begin, end) );
        } catch (Exception e) {
            throw new InvocationException("Error in extracting substring.", e);
        }
    }
}