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


package com.asemantics.mashup.processor;

/**
 * Defines a constant argument.
 */
public class ConstArgument implements Argument {

    /**
     * Argument value.
     */
    private Value value;

    /**
     * Constructor.
     *
     * @param v
     */
    public ConstArgument(Value v) {
        if(v == null) {
            throw new IllegalArgumentException();
        }
        value = v;
    }

    /**
     * @return the constant value.
     */
    public Value getValue() {
        return value;
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es) {
        return value;
    }
}
