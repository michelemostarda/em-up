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


package com.asemantics.integration.client.proxy;

import java.util.List;
import java.util.Map;

import com.asemantics.mashup.nativepkg.NativeException;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.interpreter.Utils;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Implements the <i>HTTP Proxy</i> GET operation.
 */
public abstract class AbstractHttpProxyOperation extends NativeInvocable {

    /**
     * Sleep time while waiting.
     */
    public static final int SLEEP_TIME = 200;

    /**
     * HTTP method.
     */
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    /**
     * URL parameter.
     */
    public static final String URL = "url";

    /**
     * Paramters parameter.
     */
    public static final String PARAMS = "params";

    /**
     * Operation signature.
     */
    private static final Signature SIGNATURE = new Signature( new String[] {URL, PARAMS} );

    private String    result;
    private Throwable throwable;

    protected AbstractHttpProxyOperation() {}

    /**
     * @return the operation signature.
     */
    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }

    /**
     * Performs the HTTP request.
     *
     * @param url
     * @return
     */
    protected String performRequest(Method method, String url, Map<String,String> params, String data) {

        //@RemoteServiceRelativePath("httpProxyService")
        HttpProxyServiceAsync httpProxy = (HttpProxyServiceAsync) GWT.create(HttpProxyService.class);

        result    = null;
        throwable = null;

        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onSuccess(String r) {
                result = r;
            }
            public void onFailure(Throwable t) {
                throwable = t;
            }
        };

        if( Method.GET.equals( method ) ) {

            httpProxy.performGetRequest(url, callback);
            
        } else if( Method.POST.equals( method ) ) {

            httpProxy.performPostRequest(url, params, callback);

        } else if( Method.PUT.equals( method ) ) {

            httpProxy.performPutRequest(url, data, callback);

        } else if( Method.DELETE.equals( method ) ) {

            httpProxy.performDeleteRequest(url, callback);
            
        } else {
            throw new IllegalArgumentException("Invalid method: " + method );
        }

        while( result == null && throwable == null) {
            try {
                NativeImpl.getInstance().sleep(SLEEP_TIME);
            } catch (NativeException ne) {
                GWT.log("Error while sleeping.", ne);
            }
        }

        if(result != null) {
            return result;
        }
        return throwable.getCause().getMessage();

    }

}