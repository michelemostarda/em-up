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
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Defines operation <i>LRemove(&lt;list&gt;, &lt;index&gt;)</i>.
 */
public class ListRemove extends ListOperation {

    /**
     * List argument.
     */
    protected static final String LIST = "list";

    /**
     * Value element.
     */
    protected static final String INDEX = "index";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.LIST   , LIST ),
                    new FormalParameter(FormalParameter.Type.NUMERIC, INDEX),
            }
    );

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Removes i-th element from list";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        ListValue list = context.getIthValueAsList(0);
        Value index    = context.getIthValueAsNumeric(1);
        try {
            return list.remove( index );
        } catch (Exception e) {
            throw new InvocationException("Error while removing element from list.", e);
        }
    }
}