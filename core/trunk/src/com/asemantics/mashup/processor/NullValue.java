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

import com.asemantics.mashup.processor.json.JsonNull;

/**
 * Defines the <i>null</i> value.
 */
public class NullValue extends Value implements JsonNull {

    /**
     * Null value graph representation.
     */
    public static final GraphValue NULL_VALUE_AS_GRAPH = new GraphValue();

    /**
     * Static instance.
     */
    private static final NullValue INSTANCE = new NullValue();

    /**
     * Static constructor.
     */
    static {
        NULL_VALUE_AS_GRAPH.addNode(new NullValue());
    }

    /**
     * Returns common instance.
     *
     * @return static instance.
     */
    public static NullValue getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    private NullValue() {
        // Empty.
    }

    public StringValue getValueTypeName() {
        return new StringValue("null");
    }

    public StringValue asString() {
        return new StringValue("null");
    }

    public NumericValue asNumeric() {
        return new NumericValue(0);
    }

    public BooleanValue asBoolean() {
        return new BooleanValue();
    }

    public ListValue asList() {
        return new ListValue( this );
    }

    public MapValue asMap() {
        return new MapValue(this, this);
    }

    public GraphValue asGraph() {
        return NULL_VALUE_AS_GRAPH;
    }


    public BooleanValue equalsTo(Value v) {
        return new BooleanValue( v != null && v instanceof NullValue );
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        return INSTANCE;
    }

    public Object getNativeValue() {
        return null;
    }

    public String getJsonType() {
        return JSON_TYPE_NULL;
    }

    public String asJSON() {
        return "null";
    }

    public String asPrettyJSON() {
        return asJSON();
    }
}