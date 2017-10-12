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

import com.asemantics.mashup.processor.json.JsonDouble;
import com.asemantics.mashup.processor.json.JsonInteger;

/**
 * Defines the <i>numeric</i> value.
 */
public class NumericValue extends Value<Double> implements JsonDouble, JsonInteger {

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue(JSON_TYPE_NUMERIC);


    /**
     * Internal native representation.
     */
    private double value;

    /**
     * Constructor.
     *
     * @param v
     */
    public NumericValue(double v) {
        value = v;
    }

    /**
     * Constructor at default <i>zero</i> value.
     */
    public NumericValue() {
        this(0);
    }

    /**
     * Returns internal value.
     * @return internal value.
     */
    public Double getNativeValue() {
        return value;
    }

    /**
     * Sets internal value.
     *
     * @param v
     */
    public void setValue(double v) {
        value = v;
    }

    /**
     * Increments current value of a given <i>delta</i>.
     * @param delta
     * @return  incremented value.
     */
    public double increment(double delta) {
        return value += delta;
    }

    /**
     * Increments current value of unit value (1).
     * @return incremented value.
     */
    public double increment() {
        return increment(1);
    }

    /**
     * Returns <code>true</code> if internal value
     * is <i>infinite</i>, <code>false</code> otherwise.
     *
     * @return condition of infinity on internal value. 
     */
    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

   /**
     * Returns <code>true</code> if internal value
     * is <i>Nan</i>, <code>false</code> otherwise.
     *
     * @return condition of not a number on internal value.
     */
    public boolean isNan() {
        return Double.isNaN(value);
    }

    /**
     * Returns the integer value of this numeric.
     *
     * @return integer rounded value.
     */
    public NumericValue integerValue() {
        return new NumericValue( (int) value );
    }

    /**
     * Returns the integer casted value.
     *
     * @return integer casted value.
     */
    public int integer() {
        return (int) value;
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        return new StringValue( asJSON() );
    }

    public NumericValue asNumeric() {
        return this;
    }

    public BooleanValue asBoolean() {
        return new BooleanValue(value != 0);
    }

    public ListValue asList() {
        return new ListValue( this );
    }

    public MapValue asMap() {
        return new MapValue( this, NullValue.getInstance() );
    }

    //TODO: the reversibility is not guaranteded if the value is non integer,
    //      in fact D -> graph(Round(D)) -> N ==> N != Round(D)
    public GraphValue asGraph() {
        GraphValue graphValue = new GraphValue();
        int roundValue = (int) value;
        for( int i = 0; i < roundValue; i++) {
            graphValue.addArc( new NumericValue(i), NullValue.getInstance(), NullValue.getInstance() );
        }
        return graphValue;
    }

    public BooleanValue equalsTo(Value v) {
        if( v != null ) {
            return new BooleanValue( value == v.asNumeric().value);
        }
        return BooleanValue.FALSE_VALUE;
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - value );
    }

    public NumericValue cloneValue() {
        return new NumericValue(value);
    }

    public String getJsonType() {
        return JSON_TYPE_NUMERIC;
    }

    public String asJSON() {
        if( isIntegerValue() ) {
            return Integer.toString((int) value);    
        }
       return Double.toString(value);
    }

    public String asPrettyJSON() {
        return asJSON();
    }

    public Double asDouble() {
        return value;
    }

    public double doubleValue() {
        return value;
    }

    public boolean isIntegerValue() {
        return value - (int) value == 0.0;
    }

    public int intValue() {
        return integer();
    }

}
