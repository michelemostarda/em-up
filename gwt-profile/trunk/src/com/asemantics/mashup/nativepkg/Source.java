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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import java.util.Map;

/**
 * <i>HTTP</i> request utility class based on com.google.gwt.http package.
 */
public class Source {

    /**
     * GET method.
     */
    public static final String GET_METHOD  = "get";

    /**
     * POST method.
     */
    public static final String POST_METHOD = "post";

    /**
     * Timeout millis.
     */
    public static final int TIMEOUT_MILLIS = 3000;

    /**
     * Wait millis.
     */
    public static final int WAIT_MILLIS = 100;

    /**
     * HTTP response OK status code.
     */
    public static final int STATUS_CODE_OK = 200;

    /**
     * Response.
     */
    private Response response = null;

    /**
     * Exception.
     */
    private Throwable exception = null;



    /**
     * Constructor.
     */
    public Source() {
        // Empty.
    }

    /**
     * Retrieves content of given url using specified <i>HTTP</i> method.
     *
     * @param method HTTP method to use.
     * @param url URL to be accessed.
     * @return HTTP response result.
     */
    public String retrieveContent(final String method, final String url, Map<String,String> params) {

        // Sending request.
        if( GET_METHOD.equalsIgnoreCase(method) ) {
            doGet( url );
        } else if(POST_METHOD.equalsIgnoreCase(method)) {
            doPost( url, params );
        }

        // Waiting response.
        while( response == null && exception == null) {
            try {
                JSFunctions.sleepThread(WAIT_MILLIS);
            } catch (Exception e) {
                throw new RuntimeException("Error while sleeping.", e);
            }
        }

        // Return result.
        if( response != null ) {
            if( response.getStatusCode() == STATUS_CODE_OK ) {
                return response.getText();
            } else {
                return "Status code: " + response.getStatusCode();
            }
        } else {
            return exception.getMessage();
        }
    }

    /**
     * Performs an <i>HTTP</i> GET request.
     *
     * @param url
     */
    protected void doGet(String url) {
        response  = null;
        exception = null;

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        sendRequest(builder);
    }

    /**
     * Performs an <i>HTTP</i> POST request.
     *
     * @param url
     */
    protected void doPost(String url, Map<String,String> params) {
        response  = null;
        exception = null;

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
        if( params != null ) {
            StringBuilder data = new StringBuilder();
            for(Map.Entry<String,String> entry : params.entrySet()) {
                data.append( entry.getKey() ).append("=").append(entry.getValue()).append("\n");
            }
            builder.setRequestData(data.toString());
        }
        sendRequest(builder);
    }

    protected void sendRequest(RequestBuilder builder) {
       builder.setTimeoutMillis(TIMEOUT_MILLIS);
        try {
            builder.sendRequest(
                null,
                new RequestCallback() {
                    public void onError(Request req, Throwable th) {
                        exception = th;
                    }

                    public void onResponseReceived(Request req, Response res) {
                        response = res;
                    }
                }
            );
        } catch (RequestException re) {
            exception = re;
        }
    }

}


