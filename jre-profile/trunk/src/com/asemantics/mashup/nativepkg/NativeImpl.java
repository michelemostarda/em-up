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

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.asemantics.mashup.gui.JUIFactory;
import com.asemantics.mashup.gui.UIFactory;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonObject;
import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONBoolean;
import com.sdicons.json.model.JSONComplex;
import com.sdicons.json.model.JSONDecimal;
import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONSimple;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * <i>JRE</i> implementation of {@link com.asemantics.mashup.nativepkg.Native} interface.
 */
public class NativeImpl extends Native {

    /**
     * Times of retry in performing an HTTP request.
     */
    public static final int HTTP_RETRY_TIMES = 3;

    /**
     * initialization of native impl.
     */
    private static boolean initialized = false;

    /**
     * Static instance of UI factory.
     */
    private static JUIFactory uiFactory;

    /**
     * Returns the unique instance of this native implementation.
     *
     * @return instance static implementation.
     */
    public static Native getInstance() {
        if( ! initialized ) {
            initialized = true;
            initInstance( new NativeImpl() );
        }
        return Native.getInstance();
    }

    /**
     * Constructor.
     */
    public NativeImpl() {
        // Empty.
    }

    public void sleep(int timemillis) throws NativeException {
        try {
            Thread.sleep(timemillis);
        } catch (InterruptedException ie) {
            throw new NativeException("Error while sleeping thread.", ie);
        }
    }

    public String getSimpleClassName(Object obj) throws NativeException {
        try {
            return obj.getClass().getSimpleName();
        } catch (Exception e) {
            throw new NativeException("Error in processing getSimpleName.", e);
        }
    }

    public String httpGetRequest(String url) throws NativeException {
        return performHttpRequest( new GetMethod(url) );
    }

    public String httpPostRequest(String url, Map<String,String> parameters) throws NativeException {
        PostMethod postMethod = new PostMethod(url);
        if( parameters != null ) {
            Part[] parts = new Part[ parameters.size() ];
            int i = 0;
            for(Map.Entry<String,String> entry : parameters.entrySet()) {
                parts[i++] = new StringPart( entry.getKey(), entry.getValue() );
            }
            postMethod.setRequestEntity( new MultipartRequestEntity(parts, postMethod.getParams()) );
        }
        return performHttpRequest( postMethod );
    }

    public String httpPutRequest(String url, String data) throws NativeException {
        PutMethod putMethod = new PutMethod(url);
        putMethod.setRequestEntity( new StringRequestEntity(data) ); // TODO: remove deprecation.
        return performHttpRequest( putMethod );
    }

    public String httpDeleteRequest(String url) throws NativeException {
        DeleteMethod deleteMethod = new DeleteMethod(url);
        return performHttpRequest( deleteMethod );
    }

    public UIFactory getUIFactory() {
        if(uiFactory == null) {
            uiFactory = new JUIFactory();
        }
        return uiFactory;
    }

    public JsonBase parseJSON(String jsonString) throws NativeException {
        return processJSONString( jsonString );
    }

    /**
     * Performs <i>HTTP</i> request with specified method.
     *
     * @param method method to be applied.
     * @return HTTP response body.
     * @throws NativeException if an error occurs.
     */
    protected String performHttpRequest(HttpMethodBase method) throws NativeException {

        HttpClient client = new HttpClient();

        // Provide custom retry handler.
        method.getParams().setParameter(
                HttpMethodParams.RETRY_HANDLER,
    		    new DefaultHttpMethodRetryHandler(HTTP_RETRY_TIMES, false)
        );

        try {

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new NativeException( "Method failed: " + method.getStatusLine() );
            }

            // Read response body.
            byte[] responseBody = method.getResponseBody();

            return new String( responseBody );

        } catch (HttpException e) {
            throw new NativeException( "Fatal protocol violation: " + e.getMessage() );
        } catch (IOException e) {
            throw new NativeException("Fatal transport error: " + e.getMessage() );
        } finally {
            method.releaseConnection();
        }
    }

    /**
     * Processes the JSON string.
     *
     * @param json
     * @return JSON representation.
     * @throws NativeException
     */
    protected JsonBase processJSONString(String json) throws NativeException {
        if( json == null ) {
            throw new NativeException("Invalid JSON: null");
        }

        StringReader sr = new StringReader(json);
        JSONParser parser = new JSONParser(sr);
        JSONValue src;
        try {
            src = parser.nextValue();
        } catch (TokenStreamException tse) {
            throw new NativeException("Error while tokenizing JSON.", tse);
        } catch (RecognitionException re) {
            throw new NativeException("Error while recognizing JSON.", re);
        }

        return convertJSONValue(src);
    }

    /**
     * Converts JSON simple value.
     *
     * @param simple input ass simple JSON.
     * @return associated base.
     */
    protected JsonBase convertJSONValue(final JSONSimple simple) {
        if( simple.isString() ) {
            return JsonFactory.newJsonString(  ((JSONString)  simple).getValue() );
        }
        if( simple.isBoolean() ) {
            return JsonFactory.newJsonBoolean( ((JSONBoolean) simple).getValue() );
        }
        if( simple.isDecimal() ) {
            return JsonFactory.newJsonDouble ( ((JSONDecimal) simple).getValue().doubleValue() );
        }
        if( simple.isInteger() ) {
            return JsonFactory.newJsonInteger( ((JSONInteger) simple).getValue().intValue() );
        }
        if( simple.isNull() ) {
            return JsonFactory.newJsonNull();
        }
        throw new IllegalArgumentException("Unknown argument: " + simple.getClass() );
    }

    /**
     * Converts JSON array value.
     *
     * @param array input JSON.
     * @return associated base.
     */
    protected JsonBase convertJSONValue(final JSONArray array) {
        JsonArray result = JsonFactory.newJsonArray();
        for( JSONValue value : array.getValue() ) {
            result.add( convertJSONValue(value) );
        }
        return result;
    }

    /**
     * Converts JSON object value.
     *
     * @param obj input JSON.
     * @return associated base.
     */
    protected JsonBase convertJSONValue(final JSONObject obj) {
        JsonObject result = JsonFactory.newJsonObject();
        for( Map.Entry<String, JSONValue> entry : obj.getValue().entrySet() ) {
            result.put( entry.getKey(), convertJSONValue( entry.getValue() ) );
        }
        return result;
    }

    /**
     * Converts a JSON complex value.
     *
     * @param value input JSON.
     * @return associated base.
     */
    protected JsonBase convertJSONValue(final JSONComplex value) {
        if( value.isArray() ) {
            return convertJSONValue( (JSONArray) value  );
        }
        if( value.isObject() ) {
            return convertJSONValue( (JSONObject) value );
        }
        throw new IllegalArgumentException("Unknown type: '" + value.getClass() + "'");
    }

    /**
     * Converts a generic JSON value.
     * 
     * @param value input JSON.
     * @return associated base.
     */
    protected JsonBase convertJSONValue(final JSONValue value) {
        if( value.isSimple() ) {
            return convertJSONValue( (JSONSimple) value );
        }
        if( value.isComplex() ) {
            return convertJSONValue( (JSONComplex) value );
        }
        throw new IllegalArgumentException("Unknown type: '" + value.getClass() + "'");
    }

}
