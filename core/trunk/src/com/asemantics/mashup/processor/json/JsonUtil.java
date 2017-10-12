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
 * Provides some utility operations on <i>JSON</i> entities.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: JsonUtil.java 434 2009-06-01 13:45:43Z michelemostarda $
 */
public class JsonUtil {

    /**
     * Compute the difference between a couple of <i>JSON</i> entities.
     */
    public static JsonDiff difference(JsonBase a, JsonBase b) {
        JsonDiff diff = new JsonDiff();
        diff.pushLocation("/");
        difference(diff, a, b);
        return diff;
    }

    private static void difference(JsonDiff diff, JsonBase a, JsonBase b) {
        final String aType = a.getJsonType();
        final String bType = b.getJsonType();

        if ( ! aType.equals(bType) ) {
            diff.addIssue("Expected type '" + aType + "' but found type '" + bType + "'");
            return;
        }

        if( JsonBase.JSON_TYPE_NULL.equals(aType) ) {
            return;
        } else if( JsonBase.JSON_TYPE_BOOLEAN.equals(aType) ) {
            checkBoolean(diff, a, b);
        } else if( JsonBase.JSON_TYPE_NUMERIC.equals(aType) ) {
            checkNumeric(diff, a, b);
        } else if( JsonBase.JSON_TYPE_STRING.equals(aType) ) {
            checkString(diff, a, b);
        } else if( JsonBase.JSON_TYPE_ARRAY.equals(aType) ) {
            checkArray(diff, a, b);
        } else if( JsonBase.JSON_TYPE_OBJECT.equals(aType) ) {
            checkObject(diff, a, b);
        } else {
            throw new IllegalArgumentException("Invalid type: " + aType);
        }
    }

    private static void checkBoolean(JsonDiff diff, JsonBase a, JsonBase b) {
        JsonBoolean aBoolean = (JsonBoolean) a;
        JsonBoolean bBoolean = (JsonBoolean) b;
        boolean aBooleanValue = aBoolean.boolValue();
        boolean bBooleanValue = bBoolean.boolValue();
        if( aBooleanValue != bBooleanValue ) {
            diff.addIssue("Expected " + aBooleanValue + " but found " + bBooleanValue);
        }
    }

    private static void checkNumeric(JsonDiff diff, JsonBase a, JsonBase b) {
        JsonNumber aNumber = (JsonNumber) a;
        JsonNumber bNumber = (JsonNumber) b;
        Double aDouble = aNumber.asDouble();
        Double bDouble = bNumber.asDouble();
        if( ! aDouble.equals(bDouble) ) {
            diff.addIssue( "Expected " + aDouble + " but found " + bDouble );
        }
    }

    private static void checkString(JsonDiff diff, JsonBase a, JsonBase b) {
        JsonString aString = (JsonString) a;
        JsonString bString = (JsonString) b;
        String aStringValue = aString.stringValue();
        String bStringValue = bString.stringValue();
        if( ! aStringValue.equals(bStringValue) ) {
            diff.addIssue( "Expected " + aStringValue + " but found " + bStringValue );
        }
    }

    private static void checkArray(JsonDiff diff, JsonBase a, JsonBase b) {
        JsonArray aArray = (JsonArray) a;
        JsonArray bArray = (JsonArray) b;
        int aArraySize = aArray.size();
        int bArraySize = bArray.size();
        if( aArraySize != bArraySize ) {
            diff.addIssue("Arrays differ in size, expected " + aArraySize + " found " + bArraySize);
        }
        int size = Math.min(aArraySize, bArraySize);
        for(int i = 0; i < size; i++) {
            diff.pushLocation("[" + i + "]");
            difference(diff, aArray.get(i), bArray.get(i));
            diff.popLocation();
        }
    }

    private static void checkObject(JsonDiff diff, JsonBase a, JsonBase b) {
        JsonObject aObject = (JsonObject) a;
        JsonObject bObject = (JsonObject) b;
        int aObjectSize = aObject.size();
        int bObjectSize = bObject.size();
        if( aObjectSize != bObjectSize ) {
            diff.addIssue("Objects differ in size, expected " + aObjectSize + " found " + bObjectSize);
        }
        String[] aKeys = aObject.getKeys();
        for(String key : aKeys) {
            if( bObject.containsKey(key) ) {
                diff.pushLocation("." + key);
                difference( diff, aObject.get(key), bObject.get(key) );
                diff.popLocation();
            } else {
                diff.addIssue("key '" + key + "' found in object a cannot be found in object b");
            }
        }
    }

}
