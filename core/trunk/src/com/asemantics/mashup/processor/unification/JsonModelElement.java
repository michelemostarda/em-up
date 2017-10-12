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
 * Defines a model element with JSON content.
 */
public class JsonModelElement implements ModelElement {

    /**
     * Internal <i>JSON</i> model.
     */
    private JsonModel jsonModel;

    /**
     * Constructor.
     *
     * @param jm
     */
    public JsonModelElement(JsonModel jm) {
        if(jm == null) {
            throw new NullPointerException();
        }
        jsonModel = jm;
    }

    public void unify(ResultBuilder rb, JsonBase jb) throws UnificationException {
        jsonModel.unify(rb, jb);
    }

    public void validate(Set<String> vars) throws ValidationException {
        jsonModel.validate(vars);
    }

    public String[] getVariables() {
        return jsonModel.getVariables();
    }

    public String asString() {
        return "JSON MODEL";
    }

    public int hashCode() {
        return jsonModel.hashCode();
    }

    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this) {
            return true;
        }
        if( !(obj instanceof JsonModelElement)) {
            return false;
        }
        JsonModelElement other = (JsonModelElement) obj;
        return jsonModel.equals(other.jsonModel);
    }
}
