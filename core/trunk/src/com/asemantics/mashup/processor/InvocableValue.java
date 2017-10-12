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

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonFactory;

/**
 * Defines any invocable senquence of operations.
 *
 * @see com.asemantics.mashup.processor.Invocable
 */
public class InvocableValue extends Value<Invocable> {

    /**
     * Internal invocable.
     */
    private Invocable invocable;

    /**
     * Constructor.
     *
     * @param i
     */
    protected InvocableValue(Invocable i) {
        if(i == null) {
            throw new IllegalArgumentException();
        }
        invocable = i;
    }

    /**
     *
     * @return the invocable value.
     */
    public Invocable getValue() {
        return invocable;
    }

    public StringValue getValueTypeName() {
        return new StringValue("invocable");
    }

    public StringValue asString() {
        String sv = "<" + invocable.getSignature().asString() + ">";
        return new StringValue(sv);
    }

    public NumericValue asNumeric() {
        return new NumericValue( invocable.getSignature().getFormalParameters().length );
    }

    public BooleanValue asBoolean() {
        return new BooleanValue(true);
    }

    public ListValue asList() {
        return new ListValue( this );
    }

    public MapValue asMap() {
        return new MapValue( this, NullValue.getInstance() );
    }

    public GraphValue asGraph() {
        return asMap().asGraph();
    }

    public BooleanValue equalsTo(Value v) {
        if( v != null && v instanceof InvocableValue) {
            return new BooleanValue( invocable.equals( ((InvocableValue) v).invocable));
        }
        return BooleanValue.getFalseValue();
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        throw new UnsupportedOperationException();
    }

    public Invocable getNativeValue() {
        return invocable;
    }

    public String getJsonType() {
        throw new UnsupportedOperationException();
    }

    public String asJSON() {
        FormalParameter[] formalParams = invocable.getSignature().getFormalParameters();
        JsonArray json = JsonFactory.newJsonArray();
        for( FormalParameter fp : formalParams ) {
            json.add( fp.toString() );
        }
        return json.asJSON();
    }

    public String asPrettyJSON() {
        return asJSON();
    }
}
