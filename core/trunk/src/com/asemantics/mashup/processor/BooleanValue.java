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


import com.asemantics.mashup.processor.json.JsonBoolean;


/**
 * Defines the <i>boolean</i> value.
 */
public class BooleanValue extends Value<Boolean> implements JsonBoolean {

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue(JSON_TYPE_BOOLEAN);

    /**
     * True constant.
     */
    public static final String TRUE  = "true";

    /**
     * True constant.
     */
    public static final String FALSE = "false";

    /**
     * Internal value.
     */
    private boolean value;

    /**
     * True singleton value.
     */
    protected static final BooleanValue TRUE_VALUE = new BooleanValue(true);

    /**
     * False singleton value.
     */
    protected static final BooleanValue FALSE_VALUE = new BooleanValue(false);

    /**
     * @return true value.
     */
    public static BooleanValue getTrueValue() {
        return TRUE_VALUE;
    }

    /**
     *
     * @return false value.
     */
    public static BooleanValue getFalseValue() {
        return FALSE_VALUE;
    }

    public static BooleanValue asValue(boolean b) {
        if(b) {
            return TRUE_VALUE;
        }
        return FALSE_VALUE;
    }

    /**
     * Constructor.
     *
     * @param v
     */
    public BooleanValue(boolean v) {
        value = v;
    }

    /**
     * Constructor.
     *
     * @param v
     */
    public BooleanValue(String v) {
        value = TRUE.equals(v);
    }

    /**
     * Constructor. Default is false.
     */
    public BooleanValue() {
        value = false;
    }

    /**
     *
     * @return returns the internal value.
     */
    public Boolean getNativeValue() {
        return value;
    }

    /**
     * Returns the inverted value represented by this boolean.
     * @return boolean value.
     */
    public BooleanValue negate() {
        return new BooleanValue( !value );
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        return new StringValue( value ? TRUE : FALSE );
    }

    public NumericValue asNumeric() {
        return new NumericValue( value ? 1 : 0 );
    }

    public BooleanValue asBoolean() {
        return this;
    }

    public ListValue asList() {
        return new ListValue( this );
    }

    public MapValue asMap() {
        return new MapValue( this, NullValue.getInstance() );
    }

    public GraphValue asGraph() {
        GraphValue graphValue = new GraphValue();
        if( value ) {
            graphValue.addNode( new BooleanValue(value) );
        }
        return graphValue;
    }

    public BooleanValue equalsTo(Value v) {
        if( v != null ) {
            return new BooleanValue( value == v.asBoolean().value );
        }
        return BooleanValue.getFalseValue();
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public BooleanValue cloneValue() {
        return new BooleanValue(value);
    }

    public String getJsonType() {
        return JSON_TYPE_BOOLEAN;
    }

    public String asJSON() {
        return value ? "true" : "false";
    }

    public String asPrettyJSON() {
        return asJSON();
    }

    public boolean boolValue() {
        return getNativeValue();
    }
}
