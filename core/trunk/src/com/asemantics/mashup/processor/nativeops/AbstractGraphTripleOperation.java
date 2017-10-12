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
import com.asemantics.mashup.processor.GraphValue;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Abstract class for handling graph triples.
 */
public abstract class AbstractGraphTripleOperation extends NativeInvocable {

       /**
     * List argument.
     */
    protected static final String GRAPH = "graph";

    /**
     * From value element.
     */
    protected static final String FROM_VALUE  = "from_value";

    /**
     * Label value element.
     */
    protected static final String LABEL_VALUE = "label_value";

    /**
     * To value element.
     */
    protected static final String TO_VALUE    = "to_value";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.GRAPH, GRAPH),
                    new FormalParameter(FormalParameter.Type.ANY , FROM_VALUE ),
                    new FormalParameter(FormalParameter.Type.ANY , LABEL_VALUE),
                    new FormalParameter(FormalParameter.Type.ANY , TO_VALUE   )
            }
    );

    public AbstractGraphTripleOperation() {
        // Empty.
    }

    /**
     * Processes the triple.
     * @param gv
     * @param from
     * @param label
     * @param to
     * @return the returning value.
     */
    public abstract Value processTriple(GraphValue gv, Value from, Value label, Value to);

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Adds a node to a graph";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        GraphValue graphValue = context.getIthValueAsGraph(0);
        Value      from  = context.getIthValue(1);
        Value      label = context.getIthValue(2);
        Value      to    = context.getIthValue(3);
        try {
            return processTriple(graphValue, from, label, to);
        } catch (Exception e) {
            throw new InvocationException("Error while adding value to graph.");
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }
}