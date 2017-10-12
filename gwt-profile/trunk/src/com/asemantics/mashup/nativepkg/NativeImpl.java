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


package com.asemantics.mashup.nativepkg;

import com.asemantics.mashup.gui.GwtUIFactory;
import com.asemantics.mashup.gui.UIFactory;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import java.util.Map;

public class NativeImpl extends Native {

    /**
     * Mantains the native implementation instance.
     */
    protected static boolean initialized = false;

    /**
     * Static instance of UI factory.
     */
    private static GwtUIFactory uiFactory;

    /**
     * Returns the unique instance of this native implementation.
     *
     * @return the native instance.
     */
    public static Native getInstance() {
        if( ! initialized ) {
            initialized = true;
            initInstance( new NativeImpl() );
        }
        return Native.instance;
    }

    /**
     * Source data.
     */
    private Source source;

    /**
     * Constructor.
     */
    private NativeImpl() {
        source = new Source();
    }

    public void sleep(int timemillis) {
        JSFunctions.sleepThread(timemillis);
    }

    public String getSimpleClassName(Object obj) throws NativeException {
        return GWT.getTypeName(obj);
    }

    public String httpGetRequest(String url) throws NativeException {
        try {
            return source.retrieveContent(Source.GET_METHOD, url, null);
        } catch (Exception e) {
            throw new NativeException("Error in performing GET.", e);
        }
    }

    public String httpPostRequest(String url, Map<String, String> params) throws NativeException {
        try {
            return source.retrieveContent(Source.POST_METHOD, url, params);
        } catch (Exception e) {
            throw new NativeException("Error while performing POST.", e);
        }
    }

    public String httpPutRequest(String url, String data) throws NativeException {
        throw new NativeException("Error while performing PUT request.", new UnsupportedOperationException() );
    }

    public String httpDeleteRequest(String url) throws NativeException {
        throw new NativeException("Error while performing DELETE request.", new UnsupportedOperationException() );
    }

    public UIFactory getUIFactory() {
        if(uiFactory == null) {
            uiFactory = new GwtUIFactory();
        }
        return uiFactory;
    }

    public JsonBase parseJSON(String jsonString) throws NativeException {
        return processJSONString( jsonString );
    }

    /**
     * Processes a JSON string generating the native value.
     *
     * @param json
     * @return the process result.
     * @throws NativeException
     */
    protected JsonBase processJSONString(String json) throws NativeException {
        if( json == null ) {
            throw new NativeException("Invalid JSON: null");
        }

        JSONValue value;
        try {
            value = JSONParser.parse( json );
        } catch (Exception e) {
            throw new NativeException("Error while parsing JSON '" + json + "'", e);
        }

        return convertJSONValue(value);
    }

    /**
     * Converts the JSON base value.
     *
     * @param value
     * @return the conversion result.
     */
    protected JsonBase convertJSONValue(final JSONValue value) {

        if( value.isNull() != null ) {
            return JsonFactory.newJsonNull();
        }

        JSONString jsonString;
        if( (jsonString = value.isString()) != null ) {
            return JsonFactory.newJsonString( jsonString.stringValue() );
        }

        JSONBoolean jsonBoolean;
        if( (jsonBoolean = value.isBoolean()) != null ) {
            return JsonFactory.newJsonBoolean( jsonBoolean.booleanValue() );
        }

        JSONNumber jsonNumber;
        if( (jsonNumber = value.isNumber()) != null ) {
            double doubleValue = jsonNumber.doubleValue();
            if( doubleValue == (int) doubleValue ) {
                return JsonFactory.newJsonInteger( (int) doubleValue);
            } else {
                return JsonFactory.newJsonDouble( doubleValue );
            }
        }

        JSONArray jsonArray;
        if( (jsonArray = value.isArray()) != null ) {
            return convertJSONValue( jsonArray );
        }

        JSONObject jsonObject;
        if( (jsonObject = value.isObject()) != null ) {
            return convertJSONValue( jsonObject );
        }

        throw new IllegalArgumentException("Unknown argument." );
    }

    /**
     * Converts a JSON array value.
     *
     * @param array
     * @return conversion result.
     */
    protected JsonBase convertJSONValue(final JSONArray array) {
        JsonArray result = JsonFactory.newJsonArray();
        for( int i = 0; i < array.size(); i++) {
            JSONValue value = array.get(i);
            result.add( convertJSONValue(value) );
        }
        return result;
    }

    /**
     * Converts a JSON object value.
     *  
     * @param obj
     * @return conversion result.
     */
    protected JsonBase convertJSONValue(final JSONObject obj) {
        JsonObject result = JsonFactory.newJsonObject();
        for( String key : obj.keySet() ) {
            result.put( key, convertJSONValue( obj.get(key) ) );
        }
        return result;
    }

}
