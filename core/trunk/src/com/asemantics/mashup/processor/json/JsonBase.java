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


package com.asemantics.mashup.processor.json;

/**
 * Defines the <i>JSON</i> base class.
 */
public interface JsonBase {

    /**
     * <i>null</i> type name.
     */
    public static final String JSON_TYPE_NULL = "null";

    /**
     * <i>boolean</i> type name.
     */
    public static final String JSON_TYPE_BOOLEAN = "boolean";

    /**
     * <i>numeric</i> type name.
     */
    public static final String JSON_TYPE_NUMERIC = "numeric";

    /**
     * <i>string</i> type name.
     */
    public static final String JSON_TYPE_STRING = "string";

    /**
     * <i>array</i> type name.
     */
    public static final String JSON_TYPE_ARRAY = "array";

    /**
     * <i>object</i> type name.
     */
    public static final String JSON_TYPE_OBJECT = "object";

    /**
     * Returns a string description of the <i>JSON</i> type.
     *
     * @return one of <i>JSON_TYPE</i> constants.
     */
    public String getJsonType();

    /**
     * Converts this JSON object to string representation.
     *
     * @return a flat json string.
     */
    public String asJSON();

    /**
     * Converts this JSON object to a pretty string representation.
     *
     * @return a prettyfied json string.
     */
    public String asPrettyJSON();

    /* Cast operators. */
    
    public boolean isSimple();

    public boolean isNull();

    public boolean isBoolean();

    public boolean isString();

    public boolean isNumeric();

    public boolean isInteger();

    public boolean isDouble();

    public boolean isComplex();

    public boolean isArray();

    public boolean isObject();

}
