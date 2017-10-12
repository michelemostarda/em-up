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

import com.asemantics.mashup.parser.ValidationException;

import java.util.Set;

/**
 * Base class for every {@link Operation}.
 *
 * @see com.asemantics.mashup.processor.Operation
 */
public abstract class AbstractOperation implements Operation {

    /**
     * invocation arguments.
     */
    private Argument[] arguments;

    /**
     * Constructor.
     *
     * @param args list of arguments.
     */
    public AbstractOperation(Argument[] args) {
        if( args == null) {
            throw new IllegalArgumentException();
        }
        arguments = args;
    }

    /**
     * Returns the arguments of operation.
     *
     * @return list of arguments.
     */
    public Argument[] getArguments() {
        return arguments;
    }

    public void validate(Set<String> context) throws ValidationException {
        // Checks that all arguments are defined when this operation is evaluated.
        for( Argument argument : getArguments() ) {
            if(argument instanceof Validable) {
                ((Validable) argument).validate(context);
            }
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[]{ this };
    }

}
