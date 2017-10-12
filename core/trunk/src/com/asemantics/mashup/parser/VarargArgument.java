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


package com.asemantics.mashup.parser;

import com.asemantics.mashup.processor.Argument;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.NullValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.VarargsSignature;
import com.asemantics.mashup.processor.VariableNotFoundException;

/**
 * Defines the <i>vararg</i> argument.
 * An example of vararg argument is:
 * <pre>
 *   _1 ( varargs[1] )
 *   _4 ( varargs[4] )
 *   _  ( varargs    )
 * </pre>
 */
public class VarargArgument implements Argument {

    private static final int ALL_VARAGS = -1;

    /**
     * Internal index.
     */
    private int index;

    /**
     * constructor.
     *
     * @param i vararg i-th index.
     */
    public VarargArgument(int i) {
        if( i < 0) {
            throw new IllegalArgumentException("Invalid index for vararg: '" + i + "'.");
        }
        index = i;
    }

    /**
     * Returns all the varargs array.
     */
    public VarargArgument() {
        index = ALL_VARAGS;
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es)
    throws VariableNotFoundException {
        Value[] values = VarargsSignature.getVarargs(ec);

        if(index == ALL_VARAGS) {
            return new ListValue(values);
        }

        if( values.length <= index) {
            return NullValue.getInstance();
        }
        return values[index];
    }
}
