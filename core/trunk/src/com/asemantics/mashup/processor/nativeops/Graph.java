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
import com.asemantics.mashup.processor.GraphValue;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.NullValue;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.VarargsSignature;

/**
 * Implements the <i>Graph([from1,label1,to1, ... , fromN,labelN,toN])</i> operation.
 * Returns a graph with given values.
 */
public class Graph extends NativeInvocable {

    public Graph() {
        // Empty.
    }

    public Signature getSignature() {
        return Signature.VARARGS;
    }

    public String getShortDescription() {
        return "Creates a graph of given triples";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        Value[] args = VarargsSignature.getVarargs(context);
        try {
            GraphValue gv = new GraphValue();
            int labelIndex, toIndex;
            for(int i = 0; i < args.length; i+=3) {
                labelIndex = i+1;
                toIndex    = i+2;
                gv.addArc(
                        args[i],
                        labelIndex < args.length ?  args[labelIndex] : NullValue.getInstance(),
                        toIndex    < args.length ?  args[toIndex]    : NullValue.getInstance()
                );
            }
            return gv;
        } catch (Exception e) {
            throw new InvocationException("Error while creating graph.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }
}