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

import com.asemantics.mashup.processor.Argument;
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.InvokeOperation;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonVisitor;
import com.asemantics.mashup.interpreter.Interpretative;

/**
 * Defines the <i>Apply(&lt;predicateName&gt;, &lt;argument&gt;)</i> operation.
 * This operation visits recursively the JSON <i>argument</i> and invokes the
 * predicate with name <i>predicateName</i> for each element found.
 *
 * @see com.asemantics.mashup.processor.InvokeOperation
 */
public class ApplyOperation extends NativeInvocable {

    /**
     * Name of predicate to be iterated.
     */
    private static final String PREDICATE_NAME = "predicateName";

    /**
     * JSON argument to be visited.
     */
    private static final String ARGUMENT       = "argument";

    /**
     * Operation's signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                new FormalParameter(FormalParameter.Type.STRING, PREDICATE_NAME),
                new FormalParameter(FormalParameter.Type.JSON  , ARGUMENT)
            }
    );

    /**
     * Interpretative used to evaluate the recursive invocation.
     */
    private Interpretative interpretative;

    /**
     * Constructor.
     */
    public ApplyOperation(Interpretative i) {
        if(i == null) {
            throw new NullPointerException("interpretative context cannot be null.");
        }
        interpretative = i;
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Applies recursively the sum components of " + ARGUMENT + " to predicate with name " + PREDICATE_NAME;
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        final StringValue predicateName = context.getIthValueAsString(0);
        final JsonValue jsonValue       = context.getIthValueAsJson(1);

        InvokeOperation invokeOperation;
        try {
            JsonVisitor jsonVisitor = new JsonVisitor(jsonValue.getJsonBase());
            ListValue result = new ListValue();
            while( jsonVisitor.hasNext() ) {

                invokeOperation = new InvokeOperation(
                    predicateName.getNativeValue(),
                    new Argument[] { new ConstArgument( new JsonValue( jsonVisitor.next() ) ) }
                );

                result.add( interpretative.processOperation(invokeOperation) );

            }
            return result;
        } catch (Exception e) {
            throw new InvocationException("Error while applying JSON value on predicate.", e);
        }

    }

    public Operation[] getInnerOperations() {
        return new Operation[]{this};
    }
}
