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


package com.asemantics.mashup.processor.unification;

import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.parser.ValidationException;

import java.util.Set;

/**
 * Defines a variable element.
 */
public class VariableModelElement implements ModelElement {

    /**
     * Variable name.
     */
    private String variable;

    /**
     * Constructor.
     * 
     * @param v
     */
    public VariableModelElement(String v) {
        if(v == null) {
            throw new NullPointerException();
        }
        variable = v;
    }

    /**
     * Returns the variable name.
     *
     * @return name of internal variable.
     */
    public String getVariableName() {
        return variable;
    }

    public void unify(ResultBuilder rb, JsonBase jb) throws UnificationException {
        rb.addUnification(variable, jb);
    }

    public void validate(Set<String> vars) throws ValidationException {
        if( vars.contains(variable) ) {
            throw new ValidationException(
                new JsonModelException("Variable '" + variable + "' is aready defined.")
            );
        }
        vars.add(variable);
    }

    public String[] getVariables() {
        return new String[]{ variable };
    }

    public String asString() {
        return variable;
    }

    @Override
    public int hashCode() {
        return variable.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return o instanceof VariableModelElement;
    }

}
