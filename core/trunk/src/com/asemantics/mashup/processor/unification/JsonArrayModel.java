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

import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.parser.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines a <i>JSON</i> array model like this:
 * <pre>
 * [v1, v2, [ sv1, sv2] | o]
 * </pre>
 *
 * @see com.asemantics.mashup.processor.unification.JsonModelElement
 */
public class JsonArrayModel extends JsonModel {

    /**
     * Constant list used to fill the empty rest.
     */
    private static final ListValue EMPTY_REST = new ListValue();

    /**
     * List of model elements.
     */
    private List<ModelElement> elements;

    /**
     * Constructor.
     */
    public JsonArrayModel() {
        elements = new ArrayList<ModelElement>();
    }

    /**
     * Adds an element in model.
     *
     * @param me
     */
    public void addElement(ModelElement me) {
        elements.add(me);
    }

    /**
     * Adds a variable name as element.
     *
     * @param variable
     */
    public void addElement(String variable) {
        addElement( new VariableModelElement(variable) );
    }

    /**
     * Adds a nested model in element.
     * 
     * @param jm
     */
    public void addElement(JsonModel jm) {
        addElement( new JsonModelElement(jm) );
    }

    /**
     * Adds a constant model element.
     *
     * @param v
     */
    public void addConstantElement(Value v) {
        addElement( new ConstantModelElement(v) );
    }

    public void unify(ResultBuilder rb, JsonBase in) throws UnificationException {

        final boolean restDefined = isRemainingVariableDefined();

        // TODO: FIX THIS.
        if( in instanceof JsonValue) {
            in = ((JsonValue) in).getJsonBase();
        }

        // Check input type.
        if( ! in.isArray() ) {
            rb.abort("Array model can unify only with JSON array.");
            return;
        }

        // Check input size.
        JsonArray jsonArray = (JsonArray) in;
        if( restDefined ) {
            if( elements.size() > jsonArray.size() ) {
                rb.abort("Expected al least same size of model elements and array elements.");
            }
        } else {
            if( elements.size() != jsonArray.size() ) {
                rb.abort("Expected same size of model elements and array elements.");
            }
        }

        // Unification of single elements.
        int i = 0;
        for( ModelElement element : elements ) {
            element.unify(rb, jsonArray.get(i++));
        }

        if( restDefined ) {
            // Unification of rest part.
            if( i < jsonArray.size() ) {
                JsonArray tail = new ListValue();
                while (i < jsonArray.size()) {
                    tail.add( jsonArray.get(i++) );
                }
                rb.addUnification( getRemainingVariable(), tail );
            } else {
                rb.addUnification( getRemainingVariable(), EMPTY_REST );
            }
        }
    }

    @Override
    public void validate(Set<String> vars) throws ValidationException {
        try {
            for(ModelElement modelElement : elements) {
                modelElement.validate(vars);
            }
        } catch (ValidationException ve) {
            throw new JsonModelException("Error while validating array element.", ve);
        }
        super.validate(vars);
    }

    public String[] getVariables() {
        Set<String> variables = new HashSet<String>();
        variables.add( super.getRemainingVariable() );

        for(ModelElement me : elements) {
            variables.addAll( Arrays.asList(me.getVariables()) );
        }
        return variables.toArray( new String[variables.size()] );
    }

    public String asString() {
        return "JSON ARRAY MODEL";
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o) ) {
            return false;
        }

        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if(! (o instanceof JsonArrayModel)) {
            return false;
        }

        JsonArrayModel other = (JsonArrayModel) o;
        if( elements.size() != other.elements.size() ) {
            return false;
        }
        for(int i = 0; i < elements.size(); i++) {
            if( ! elements.get(i).equals( other.elements.get(i) ) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = super.hashCode();
        for(ModelElement element : elements) {
            hashcode *= element.hashCode();
        }
        return hashcode;
    }

}
