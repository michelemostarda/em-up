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
 * Defines an invocation of code.
 */
public class InvocationValue extends Value<InvokeOperation> {

    /**
     * Internal invocation.
     */
    private InvokeOperation invocation;

    /**
     * Constructor.
     * 
     * @param io
     */
    public InvocationValue(InvokeOperation io) {
        if(io == null) {
            throw new IllegalArgumentException();
        }
        invocation = io;
    }

    public InvokeOperation getInvocation() {
        return invocation;
    }

    public StringValue getValueTypeName() {
        return new StringValue("invocation");
    }

    public StringValue asString() {
        return new StringValue( "!<" + invocation.getTargetSequence() + invocation.getArguments().length + ">" );
    }

    public NumericValue asNumeric() {
        return new NumericValue( invocation.getArguments().length );
    }

    public BooleanValue asBoolean() {
        return new BooleanValue(true);
    }

    public ListValue asList() {
        return new ListValue(this);
    }

    public MapValue asMap() {
        return new MapValue( this, NullValue.getInstance() );
    }

    public GraphValue asGraph() {
        return asMap().asGraph();
    }

    public BooleanValue equalsTo(Value v) {
        if( v != null && v instanceof InvocationValue ) {
            new BooleanValue( invocation.equals( ((InvocationValue) v).invocation) );
        }
        return BooleanValue.getFalseValue();
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        throw new UnsupportedOperationException();
    }

    public InvokeOperation getNativeValue() {
        return invocation;
    }

    public String getJsonType() {
        //TODO: herarchy problem.
        throw new UnsupportedOperationException();
    }

    public String asJSON() {
        JsonArray json = JsonFactory.newJsonArray();
        for(Argument argument : invocation.getArguments()) {
            json.add( argument.toString() );
        }
        return json.asJSON();
    }

    public String asPrettyJSON() {
        return asJSON();
    }
}
