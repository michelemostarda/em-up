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
import com.asemantics.mashup.processor.json.JsonDouble;
import com.asemantics.mashup.processor.json.JsonInteger;
import com.asemantics.mashup.processor.json.JsonNull;
import com.asemantics.mashup.processor.json.JsonNumber;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonSimple;
import com.asemantics.mashup.processor.json.JsonString;


/**
 * Defines any value handled by {@link com.asemantics.mashup.processor.Operation}.
 */
public abstract class Value<T> implements JsonBase, Cloneable {

    /**
     * This enumeration lists all supported types.
     */
    enum ValueType {
        NULL {
            public boolean isTypeFor(Value v) {
                return v instanceof NullValue;
            }
            public char getCharIdentifier() {
                return 'U';
            }
            public Value convertToType(JsonValue jsonValue) {
                return NullValue.getInstance();
            }
        },
        BOOLEAN {
            public boolean isTypeFor(Value v) {
                return v instanceof BooleanValue;
            }
            public char getCharIdentifier() {
                return 'B';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue.asBoolean();
            }
        },
        NUMERIC {
            public boolean isTypeFor(Value v) {
                return v instanceof NumericValue;
            }
            public char getCharIdentifier() {
                return 'N';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue.asNumeric();
            }
        },
        STRING {
            public boolean isTypeFor(Value v) {
                return v instanceof StringValue;
            }
            public char getCharIdentifier() {
                return 'S';
            }
            // TODO: find a better solution.
            public Value convertToType(JsonValue jsonValue) {
                JsonBase inner = jsonValue.getJsonBase();
                if( inner instanceof JsonString) {
                    return new StringValue( ((JsonString) inner).stringValue() );
                }
                return jsonValue.asString();
            }
        },
        LIST {
            public boolean isTypeFor(Value v) {
                return v instanceof ListValue;
            }
            public char getCharIdentifier() {
                return 'L';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue.asList();
            }
        },
        MAP {
            public boolean isTypeFor(Value v) {
                return v instanceof MapValue;
            }
            public char getCharIdentifier() {
                return 'M';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue.asMap();
            }
        },
        JSON {
            public boolean isTypeFor(Value v) {
                return v instanceof JsonValue;
            }
            public char getCharIdentifier() {
                return 'J';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue;
            }
        },
        GRAPH {
            public boolean isTypeFor(Value v) {
                return v instanceof GraphValue;
            }
            public char getCharIdentifier() {
                return 'G';
            }
            public Value convertToType(JsonValue jsonValue) {
                return jsonValue.asGraph();
            }
        };

        /**
         * Checks if the value type is the right type for <i>v</i>.
         *
         * @param v value to check.
         * @return <code>true</code> if is type, <code>false</code> otherwise.
         */
        public abstract boolean isTypeFor(Value v);

        /**
         * Returns a char identifying the type.
         *
         * @return
         */
        public abstract char getCharIdentifier();

        /**
         * Converted the given json value to the specific type represented
         * by the enumeration entry.
         *
         * @param jsonValue value to be converted.
         * @return converted value.
         */
        public abstract Value convertToType(JsonValue jsonValue);

        /**
         * Returns the tight identifier for the type associated with <i>v</i>.
         *
         * @param v the value of which retrieve the identifier.
         * @return the identifier of the type of <i>v</i>.
         */
        public static char getCharIdentifier(Value v) {
            ValueType[] valueTypes = ValueType.values();
            for(ValueType valueType : valueTypes) {
                if( valueType.isTypeFor(v) ) {
                    return valueType.getCharIdentifier();
                }
            }
            throw new IllegalArgumentException("Unknown value type for value: " + v);
        }

        /**
         * Returns the value type associated to the char <i>type</i>.
         * @param type chat type.
         * @return associated value type.
         */
        public static ValueType getValueType(char type) {
            ValueType[] valueTypes = ValueType.values();
            for(ValueType valueType : valueTypes) {
                if( valueType.getCharIdentifier() == type ) {
                    return valueType;
                }
            }
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    /**
     * Return the name of the type of this value.
     *
     * @return a string representing an existing type.
     */
    public abstract StringValue getValueTypeName();

    /**
     * Converts any value in a string value.
     * 
     * @return human readable representation of this value.
     */
    public abstract StringValue asString();

    /**
     * Returns the numeric representation of this value.
     *
     * @return numeric representation of value.
     */
    public abstract NumericValue asNumeric();

    /**
     * Returns the boolean prepresentation of value.
     *
     * @return boolean representation of value.
     */
    public abstract BooleanValue asBoolean();

    /**
     * Returns the list representation of value.
     *
     * @return list representation of value.
     */
    public abstract ListValue asList();

    /**
     * Returns the map representation of value.
     *
     * @return map representation of value.
     */
    public abstract MapValue asMap();

    /**
     * Returns the graph representation of value.
     *
     * @return graph representation of value.
     */
    public abstract GraphValue asGraph();

    /**
     * Returns the <i>null</i> element.
     *
     * @return null element.
     */
    public NullValue asNull() {
        return NullValue.getInstance();
    }

    /**
     * Returns the <i>JSON</i> representation of value.
     *
     * @return JSON representation of value.
     */
    public JsonValue asJsonValue() {
        return this instanceof JsonValue ? (JsonValue) this : new JsonValue(this);
    }

    @Override
    public String toString() {
        return asPrettyJSON();
    }

    /**
     * Returns the condition of equality with another value.
     *
     * @param v
     * @return the comparison result as boolean value.
     */
    public abstract BooleanValue equalsTo(Value v);

    /**
     * Compares this value with the given one.
     *
     * @param v
     * @return the distance between this value and the compared one.
     */
    public abstract NumericValue comparesTo(Value v);

    /**
     * Clones this value in a new one.
     *
     * @return the cloned value.
     */
    public abstract Value cloneValue();

    /**
     * Returns the wrapped Java native value.
     *
     * @return the native value wrabbed by this value.
     */
    public abstract T getNativeValue();

    /* Begin JSON integration. */

      public boolean isSimple() {
        return this instanceof JsonSimple;
    }

    public boolean isNull() {
        return this instanceof JsonNull;
    }

    public boolean isBoolean() {
        return this instanceof JsonBoolean;
    }

    public boolean isString() {
        return this instanceof JsonString;
    }

    public boolean isNumeric() {
        return this instanceof JsonNumber;
    }

    public boolean isInteger() {
        return this instanceof JsonInteger;
    }

    public boolean isDouble() {
        return this instanceof JsonDouble;
    }

    public boolean isComplex() {
        return this instanceof JsonComplex;
    }

    public boolean isArray() {
        return this instanceof JsonArray;
    }

    public boolean isObject() {
        return this instanceof JsonObject;
    }

    /* End JSON integration. */


    @Override
    public int hashCode() {
        return asString().stringValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof Value) {
            return equalsTo( (Value) obj ).getNativeValue();
        }
        return false;
    }

}
