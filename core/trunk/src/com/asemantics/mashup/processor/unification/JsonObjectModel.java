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

import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.JsonValue;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.parser.ValidationException;

import java.util.*;

/**
 * Defines a <i>JSON</i> object model like this:
 * 
 * <pre>
 * { "k1" : v1, "k2" : [ sv1, sv2] | o]
 * </pre>
 *
 * @see com.asemantics.mashup.processor.unification.JsonModelElement
 */
public class JsonObjectModel extends JsonModel {

    /**
     * Map of model elements: key -> model.
     */
    private Map<String,ModelElement> elements;

    /**
     * Constructor.
     */
    public JsonObjectModel() {
        elements = new HashMap<String,ModelElement>();
    }

    /**
     * Adds a model element associated with specified key.
     *
     * @param key key
     * @param me  model element.
     */
    public void putElement(String key, ModelElement me) {
        if( elements.put(key,me) != null) {
            throw new JsonModelException("key '" + key + "' already defined in model.");
        }
    }

    /**
     * Adds a key -> variable entry.
     * @param key
     * @param variable
     */
    public void putElement(String key, String variable) {
        elements.put(key, new VariableModelElement(variable));
    }

    /**
     * Adds a key -> nested model entry.
     *
     * @param key
     * @param jm
     */
    public void putElement(String key, JsonModel jm) {
        elements.put(key, new JsonModelElement(jm));
    }

    public void unify(ResultBuilder rb, JsonBase in) throws UnificationException {

        // TODO: FIX THIS.
        if( in instanceof JsonValue) {
            in = ((JsonValue) in).getJsonBase();
        }

        // Check type.
        if( ! in.isObject() ) {
            rb.abort("Object model can unify only with JSON object.");
            return;
        }

        // Check size.
        JsonObject jsonObject = (JsonObject) in;
        if( isRemainingVariableDefined() ) {
            if( elements.size() > jsonObject.size() ) {
                rb.abort("Expected al least same size of model elements and object elements.");
            }
        } else {
            if( elements.size() != jsonObject.size() ) {
                rb.abort("Expected same size of model elements and object elements.");
            }
        }

        // Unification of single entries.
        Set<String> keys = elements.keySet();
        JsonBase base;
        for( String key : keys ) {
            base = jsonObject.get(key);
            if(base == null) {
                rb.abort("Cannot find key '" + key + "' in JSON object.");
            }
            elements.get(key).unify(rb, base);
        }
        // Unification of remaining part.
        if( elements.size() < jsonObject.size() ) {
            JsonObject tail = new MapValue();
            for( String jsonKey : jsonObject.getKeys() ) {
                if( ! elements.containsKey(jsonKey) ) {
                    tail.put(jsonKey, jsonObject.get(jsonKey));
                }
            }
            rb.addUnification( getRemainingVariable(), tail );
        }
    }

    public void validate(Set<String> vars) throws ValidationException {
        for(Map.Entry<String,ModelElement> entry : elements.entrySet()) {
            if( vars.contains(entry.getKey()) ) {
                throw new ValidationException(
                    new JsonModelException("Variable '" + entry.getKey() + "' is already defined as key in model")
                );
            }
            vars.add( entry.getKey() );
            entry.getValue().validate(vars);
        }
        super.validate(vars);
    }

    public String[] getVariables() {
        Set<String> variables = elements.keySet();
        variables.add( super.getRemainingVariable() );
        
        for( ModelElement me : elements.values() ) {
            variables.addAll( Arrays.asList(me.getVariables()) );
        }
        return variables.toArray( new String[variables.size()] );
    }

    public String asString() {
        return "JSON OBJECT MODEL";
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
        if(! (o instanceof JsonObjectModel)) {
            return false;
        }
        JsonObjectModel other = (JsonObjectModel) o;
        return compareEntries(elements.entrySet(), other.elements.entrySet());
    }

    @Override
    public int hashCode() {
        int hashcode = super.hashCode();
        for(Map.Entry<String,ModelElement> entry : elements.entrySet()) {
            hashcode *= entry.getKey().hashCode() * entry.getValue().hashCode();
        }
        return hashcode;
    }

    /**
     * Compares entries of a couple of model data.
     * @param a
     * @param b
     * @return
     */
    private boolean compareEntries(Set<Map.Entry<String,ModelElement>> a, Set<Map.Entry<String,ModelElement>> b) {
        if(a.size() != b.size()) {
            return false;
        }
        for(Map.Entry<String,ModelElement> aEntry: a) {
            if( ! b.contains(aEntry) ) {
                return false;
            }
        }
        return true;
    }
}
