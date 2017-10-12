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
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonBoolean;
import com.asemantics.mashup.processor.json.JsonComplex;
import com.asemantics.mashup.processor.json.JsonNumber;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonString;

import java.util.Map;

/**
 * Defines the <i>JSON</i> type.
 */
public class JsonValue extends Value<JsonBase> {

    /**
     * Internal JSON value.
     */
    private JsonBase jsonBase;

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue("json");

    /**
     * Constructor.
     *
     * @param jb
     */
    public JsonValue(JsonBase jb) {
        if(jb == null) {
            throw new NullPointerException();
        }
        if(jb instanceof JsonValue) {
            throw new IllegalArgumentException("Cannot nest a JsonValue inside a JsonValue.");
        }
        jsonBase = jb;
    }

    /**
     *
     * @return internal jsonBase.
     */
    public JsonBase getJsonBase() {
        return jsonBase;
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        if( jsonBase.isString() ) {
            return new StringValue( ((JsonString) jsonBase).stringValue() );
        } else {
            return new StringValue( jsonBase.asJSON() );
        }
    }

    public NumericValue asNumeric() {

        // If jsonBase is implemented by value then is converted in a natural way.
        if( jsonBase instanceof Value) {
            return ((Value) jsonBase).asNumeric();
        }

        if( jsonBase.isNull() ) {
            return new NumericValue(0);
        }
        if( jsonBase.isNumeric() ) {
            return new NumericValue( ((JsonNumber) jsonBase).asDouble() );
        }
        if( jsonBase.isString() ) {
            return new NumericValue( ((JsonString) jsonBase).getNativeValue().length() );
        }
        if( jsonBase.isComplex() ) {
            return new NumericValue( ((JsonComplex) jsonBase).size() );
        }
        throw new IllegalArgumentException();
    }

    public BooleanValue asBoolean() {
        if( jsonBase.isBoolean() ) {
            return BooleanValue.asValue( ((JsonBoolean) jsonBase).boolValue() );
        }
        if( jsonBase.isNull() ) {
            return BooleanValue.FALSE_VALUE;
        }
        return BooleanValue.TRUE_VALUE;
    }

    public ListValue asList() {

        // If jsonBase is implemented by value then is converted in a natural way.
        if( jsonBase instanceof Value) {
            return ((Value) jsonBase).asList();
        }

        ListValue result = new ListValue();
        if( jsonBase.isNull() ) {
            result.add( NullValue.getInstance() );
            return result;
        }
        if( jsonBase.isNumeric() ) {
            result.add( new NumericValue( ((JsonNumber) jsonBase).asDouble() ) );
            return result;
        }
        if( jsonBase.isString() ) {
            result.add( new StringValue( ((JsonString) jsonBase).getNativeValue() ) );
            return result;
        }
        if( jsonBase.isArray() ) {
            for( JsonBase json : ((JsonArray) jsonBase) ) {
                result.add( new JsonValue(json) );
            }
            return result;
        }
        if( jsonBase.isObject() ) {
            for( String key : ((JsonObject) jsonBase).getKeys() ) {
                result.add( new StringValue(key) );
            }
            return result;
        }
        throw new IllegalArgumentException();
    }

    public MapValue asMap() {
        // If jsonBase is implemented by value then is converted in a natural way.
        if( jsonBase instanceof Value) {
            return ((Value) jsonBase).asMap();
        }

        MapValue result = new MapValue();
        if( jsonBase.isNull() ) {
            result.put( NullValue.getInstance(), NullValue.getInstance() );
            return result;
        }
        if( jsonBase.isNumeric() ) {
            result.put( new NumericValue( ((JsonNumber) jsonBase).asDouble() ), NullValue.getInstance() );
            return result;
        }
        if( jsonBase.isString() ) {
            result.put( new StringValue( ((JsonString) jsonBase).getNativeValue() ), NullValue.getInstance() );
            return result;
        }
        if( jsonBase.isArray() ) {
            for( JsonBase elem : (JsonArray) jsonBase ) {
                result.put( new JsonValue( elem ), NullValue.getInstance() );
            }
            return result;
        }
        if( jsonBase.isObject() ) {
            for( Map.Entry<String,JsonBase> entry : ((JsonObject) jsonBase) ) {
                result.put( new StringValue(entry.getKey()), new JsonValue(entry.getValue()) );
            }
            return result;
        }
        throw new IllegalArgumentException();
    }

    public GraphValue asGraph() {
        try {
            GraphValue graphValue = new GraphValue();
            graphValue.loadJSONAsGraph( (JsonObject) jsonBase);
            return graphValue;
        } catch (Exception e) {
            e.printStackTrace();
            GraphValue graphValue = new GraphValue();
            graphValue.addNode(this);
            return graphValue;
        }
    }

    @Override
    public JsonValue asJsonValue() {
        return this;
    }

   public BooleanValue equalsTo(Value v) {
        if( v != null ) {
            return new BooleanValue( jsonBase.equals( v.asJsonValue().getJsonBase() ) );
        }
        return BooleanValue.FALSE_VALUE;
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        JsonValue cloned = new JsonValue( ((Value) jsonBase).cloneValue() );
        return cloned;
    }

    public JsonBase getNativeValue() {
        return jsonBase;
    }

    @Override
    public boolean isSimple() {
        return jsonBase.isSimple();
    }

    @Override
    public boolean isNull() {
        return jsonBase.isNull();
    }

    @Override
    public boolean isBoolean() {
        return jsonBase.isBoolean();
    }

    @Override
    public boolean isString() {
        return jsonBase.isString();
    }

    @Override
    public boolean isNumeric() {
        return jsonBase.isNumeric();
    }

    @Override
    public boolean isInteger() {
        return jsonBase.isInteger();
    }

    @Override
    public boolean isDouble() {
        return jsonBase.isDouble();
    }

    @Override
    public boolean isComplex() {
        return jsonBase.isComplex();
    }

    @Override
    public boolean isArray() {
        return jsonBase.isArray();
    }

    @Override
    public boolean isObject() {
        return jsonBase.isObject();
    }

    public String getJsonType() {
        return jsonBase.getJsonType();
    }

    public String asJSON() {
        return jsonBase.asJSON();
    }

    public String asPrettyJSON() {
        return jsonBase.asPrettyJSON();
    }
}
