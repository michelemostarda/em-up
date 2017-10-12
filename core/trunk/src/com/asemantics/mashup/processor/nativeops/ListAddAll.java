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
 * Defines operation <i>AddAll(&lt;listTarget&gt;, &lt;listSource&gt;)</i>.
 */
public class ListAddAll extends ListOperation {

   /**
     * List argument.
     */
    protected static final String LIST_TARGET = "listTarget";

    /**
     * Value element.
     */
    protected static final String LIST_SOURCE = "listSource";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.LIST, LIST_TARGET),
                    new FormalParameter(FormalParameter.Type.LIST, LIST_SOURCE)
            }
    );

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Adds all the elements in the sourceList inside the target list.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        ListValue targetList  = context.getIthValueAsList(0);
        ListValue sourceList  = context.getIthValueAsList(1);
        try {
            targetList.addAll( sourceList );
            return targetList;
        } catch (Exception e) {
            throw new InvocationException("Error while performing addition.", e);
        }
    }

}
