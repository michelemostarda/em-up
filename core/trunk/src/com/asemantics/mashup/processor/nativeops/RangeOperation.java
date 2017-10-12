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

/**
 * Defines the <i>Range(begin, end, step)</i> operation.
 * Returns a range [begin,end] with progression <i>step</i>.
 */
public class RangeOperation extends NativeInvocable {

    /**
     * Begin parameter.
     */
    private static final String BEGIN = "begin";

    /**
     * End parameter.
     */
    private static final String END   = "end";

    /**
     * Step parameter.
     */
    private static final String STEP  = "step";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.NUMERIC, BEGIN),
                    new FormalParameter(FormalParameter.Type.NUMERIC, END  ),
                    new FormalParameter(FormalParameter.Type.NUMERIC, STEP )
            }
    );


    /**
     * Constructor.
     */
    public RangeOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Creates a list of numeric of given range.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
   throws InvocationException {

        // Retireves parameters.
        int begin = context.getIthValueAsNumeric(0).integer();
        int end   = context.getIthValueAsNumeric(1).integer();
        int step  = context.getIthValueAsNumeric(2).integer();

        // Checks parameters.
        if( begin < end && step <= 0 ) {
            throw new InvocationException("Infinite loop with step = " + step);
        }
        if( begin > end && step >= 0 ) {
            throw new InvocationException("Infinite loop with step = " + step);
        }

        try {
            // Creates list range.
            ListValue result = new ListValue();
            boolean progressive = begin < end;
            for( int i = begin; progressive ? i <= end : i >= end ; i += step ) {
                result.add( new NumericValue(i) );
            }
            return result;
        } catch (Exception e) {
            throw new InvocationException("Error while generating range.", e);
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}