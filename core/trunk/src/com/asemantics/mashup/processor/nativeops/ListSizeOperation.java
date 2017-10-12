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

import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Implements the <i>LSize(list)</i> operation.
 */
public class ListSizeOperation extends NativeInvocable {

    /**
     * List argument.
     */
    private static final String LIST_ARGUMENT = "list";

    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(
                        FormalParameter.Type.LIST,
                        LIST_ARGUMENT
                    )
            }
    );

    /**
     * Constructor.
     */
    public ListSizeOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Returns the size of the given list.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        ListValue list = context.getIthValueAsList(0);
        try {
            return new NumericValue( list.size() );
        } catch (Exception e) {
            throw new InvocationException("Error while retrieving list size.");
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[0];
    }
}
