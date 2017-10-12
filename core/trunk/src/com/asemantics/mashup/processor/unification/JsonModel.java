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

import com.asemantics.mashup.parser.ValidationException;

import java.util.Set;

/**
 * Defines a generic <i>JSON</i> model. A <i>JSON</i> model is a
 * <i>JSON</i> data in which some values are variables instead of
 *  constant values.
 */
public abstract class JsonModel implements ModelElement {

    /**
     * Remaining variable has been defined.
     * Remaining variable is the variable matching with the
     * remaining part of data:
     * <pre>
     * [a,b|remaining]
     * </pre>
     */
    private String remainingVariable = null;

    /**
     * Sets the remaining variable.
     *
     * @param rv variable name.
     */
    public void setRemainingVariable(String rv) {
        if(rv == null) {
            throw new NullPointerException();
        }
        remainingVariable = rv;
    }

    /**
     * Returns the remaining variable.
     *
     * @return variable name.
     */
    public String getRemainingVariable() {
        return remainingVariable;
    }

    /**
     *
     * @return <code>true</code> if remaining variable is defined,
     *         <code>false</code> otherwise.
     */
    public boolean isRemainingVariableDefined() {
        return remainingVariable != null;
    }

    public void validate(Set<String> vars) throws ValidationException {
        if( vars.contains(remainingVariable) ) {
            throw new ValidationException(
                new JsonModelException("Remaining variable '" + remainingVariable + "' aready defined.")
            );
        }
        vars.add(remainingVariable);
    }

    @Override
    public int hashCode() {
        return remainingVariable == null ? 0 : remainingVariable.hashCode();
    }

    /**
     * Equality on json models is established if both models defines or not the remaining variable.
     * 
     * @param obj
     * @return equality result.
     */
    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if( ! (obj instanceof JsonModel) ) {
            return false;
        }
        JsonModel other = (JsonModel) obj;
        return remainingVariable == null ? other.remainingVariable == null : other.remainingVariable != null;
    }
}
