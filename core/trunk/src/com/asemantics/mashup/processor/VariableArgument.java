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
 * Defines a variable argument.
 *
 * @see com.asemantics.mashup.processor.Signature
 */
public class VariableArgument implements Argument, Validable {

    /**
     * Name of variable.
     */
    private String varName;

    /**
     * Constructor.
     *
     * @param vn
     */
    public VariableArgument(String vn) {
        if(vn == null) {
            throw new IllegalArgumentException();
        }
        varName = vn;
    }

    /**
     * Return variable name.
     * 
     * @return name of variable.
     */
    public String getVarName() {
        return varName;
    }

    public void validate(Set<String> context) throws ValidationException {
        if( ! context.contains(varName) ) {
            throw new ValidationException("Variable '" + varName + "' was not defined in this context.");
        }
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es) throws VariableNotFoundException {
        Value result = ec.getValue(varName);
        if( result == null ) {
            throw new VariableNotFoundException("cannot find variable '" + varName + "'");
        }
        return result;
    }
}
