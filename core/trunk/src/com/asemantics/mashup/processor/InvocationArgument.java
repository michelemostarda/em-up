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
 * Defines an argument containing an invocation value.
 */
public class InvocationArgument implements Argument {

    /**
     * Internal invocation value.
     */
    private InvocationValue value;

    /**
     * Constructor.
     *
     * @param iv
     */
    public InvocationArgument(InvocationValue iv) {
        if(iv == null) {
            throw new IllegalArgumentException();
        }
        value = iv;
    }

    /**
     * Returns the invocation value.
     * 
     * @return internal invocation value.
     */
    public InvocationValue getInvocationValue() {
        return value;
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es) {
        try {
            return value.getInvocation().execute( ec, es );
        } catch (Exception e) {
            throw new RuntimeException("Cannot evaluate invocation.", e);
        }
    }

}
