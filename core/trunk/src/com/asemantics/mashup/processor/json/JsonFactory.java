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

import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.NullValue;
import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.StringValue;

/**
 * Factory class for <i>JSON</i> types.
 */
public final class JsonFactory {

    /**
     * Constructor. Uninstantiable.
     */
    private JsonFactory() {
        // Empty.
    }

    /**
     * Returns a JSON null instance.
     *
     * @return the <i>JsonNull</i> singleton instance.
     */
    public static JsonNull newJsonNull() {
        return NullValue.getInstance();
    }

    /**
     * Returns a JSON boolean instance.
     *
     * @param b boolean value.
     * @return JSON instance.
     */
    public static JsonBoolean newJsonBoolean(boolean b) {
        return new BooleanValue(b);
    }

    /**
     * Returns a JSON integer instance.
     *
     * @param i integer value.
     * @return JSON instance.
     */
    public static JsonInteger newJsonInteger(int i) {
        return new NumericValue(i);
    }

    /**
     * Returns a JSON double instance.
     *
     * @param d double value.
     * @return JSON instance.
     */
    public static JsonDouble newJsonDouble(double d) {
        return new NumericValue(d);
    }

    /**
     * Returns a JSON integer instance.
     *
     * @param s string value.
     * @return JSON instance.
     */
    public static JsonString newJsonString(String s) {
        return new StringValue(s);
    }

    /**
     * Returns a JSON integer instance.
     * 
     * @return JSON instance.
     */
    public static JsonArray newJsonArray() {
        return new ListValue();
    }

    /**
     * Returns a JSON object instance.
     *
     * @return JSON instance.
     */
    public static JsonObject newJsonObject() {
        return new MapValue();
    }

}
