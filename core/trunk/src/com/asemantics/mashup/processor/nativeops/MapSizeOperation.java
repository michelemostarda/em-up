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
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Implements the <i>MSize(map)</i> operation.
 */
public class MapSizeOperation extends NativeInvocable {

    /**
     * List argument.
     */
    private static final String MAP_ARGUMENT = "map";

    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(
                        FormalParameter.Type.MAP,
                            MAP_ARGUMENT
                    )
            }
    );

    /**
     * Constructor.
     */
    public MapSizeOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Returns the size of the given map.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        MapValue map = context.getIthValueAsMap(0);
        try {
            return new NumericValue( map.size() );
        } catch (Exception e) {
            throw new InvocationException("Error while retrieving map size.");
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[0];
    }
}
