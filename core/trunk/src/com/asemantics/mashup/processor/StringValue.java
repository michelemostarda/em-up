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

import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.nativepkg.NativeException;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonString;
import com.asemantics.mashup.processor.json.JsonObject;

/**
 * Defines the <i>string</i> value.
 */
public class StringValue extends Value<String> implements JsonString {

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue(JSON_TYPE_STRING);

    /**
     * Null string value.
     */
    public static final StringValue NULL_STRING_VALUE = new StringValue("<null>");

    /**
     * The value returned for <code>null</code>.
     */
    public static final String NULL_STRING = "null";

    /**
     * Internal string value.
     */
    private String value;

    /**
     * Constructor.
     *
     * @param v the string  value. Can be <code>null</code>.
     */
    public StringValue(String v) {
        value = v;
    }

    /**
     * Constructor for empty string.
     */
    public StringValue() {
        this("");
    }

    /**
     * Returns internal value.
     *
     * @return value
     */
    public String getNativeValue() {
        return value;
    }

    /**
     * Append value <i>v</i> to the internal value. 
     *
     * @param v
     * @return concatenation result.
     */
    public String appendValue(Value v) {
        if(value == null) {
            value = v.asString().getNativeValue();
        } else {
            value += v.asString().getNativeValue();
        }
        return value;
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        return this;
    }

    public NumericValue asNumeric() {
        if( value == null) {
            return new NumericValue(0);
        }

        try {
            return new NumericValue( Double.parseDouble(value) );
        } catch (NumberFormatException nfe) {
            return new NumericValue( value.length() );
        }
    }

    public BooleanValue asBoolean() {
        return new BooleanValue(value);
    }

    public ListValue asList() {
        ListValue result = new ListValue();
        if(value == null) {
            return result;
        }

        try {
            JsonBase json = NativeImpl.getInstance().parseJSON( value );
            if( json.isArray() ) {
                return (ListValue) json;
            }
        } catch (NativeException ne) {}

        for( int c = 0; c < value.length(); c++ ) {
            result.add( new StringValue( Character.toString(value.charAt(c)) ) );
        }
        return result;
    }

    public MapValue asMap() {
        try {
            JsonBase json = NativeImpl.getInstance().parseJSON( value );
            if( json.isObject() ) {
                return (MapValue) json;
            }
        } catch (NativeException ne) {}

        return new MapValue( this, NullValue.getInstance() );
    }

    public GraphValue asGraph() {
        if( value == null ) {
            return new GraphValue();
        }

        try {

            JsonBase json = NativeImpl.getInstance().parseJSON( value );
            GraphValue graphValue = new GraphValue();
            graphValue.loadJSONAsGraph((JsonObject) json);
            return graphValue;

        } catch (Exception e) {
            GraphValue graphValue = new GraphValue();
            graphValue.addNode(this);
            return graphValue;
        }
    }

    @Override
    public JsonValue asJsonValue() {
        if(value == null) {
            return new JsonValue(this);
        }

        try {
            JsonBase json = NativeImpl.getInstance().parseJSON( value );
            return new JsonValue( json );
        } catch (NativeException ne) {
            return new JsonValue( this );
        }
    }

    public BooleanValue equalsTo(Value v) {
        if( value != null && v != null) {
            return new BooleanValue( value.equals( v.asString().value) );
        }
        return BooleanValue.FALSE_VALUE;
    }

    public NumericValue comparesTo(Value v) {
        return value == null ? new NumericValue(0) : new NumericValue( value.compareTo( v.asString().getNativeValue() ) );
    }

    public StringValue cloneValue() {
        return new StringValue( value );
    }

    public String toString() {
        return getNativeValue();
    }

    public String getJsonType() {
        return JSON_TYPE_STRING;
    }

    public String asJSON() {
        return value == null ? NULL_STRING : "\"" + Utils.encodeStringAsHTML(value) + "\"";
    }

    public String asPrettyJSON() {
        return asJSON();
    }

    public String stringValue() {
        return getNativeValue();
    }
}
