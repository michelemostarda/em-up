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


package com.asemantics.integration.server.proxy;

import com.asemantics.mashup.nativepkg.NativeException;
import com.asemantics.integration.client.proxy.HttpProxyService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

/**
 * Implementation of {@link com.asemantics.integration.client.proxy.HttpProxyService}.
 */
public class HttpProxyServiceImpl extends RemoteServiceServlet implements HttpProxyService {

    public String performGetRequest(String url) throws NativeException {
        return performHttpRequest( new GetMethod(url) );
    }

    public String performPostRequest(String url, Map<String, String> params) throws NativeException {
        PostMethod postMethod = new PostMethod(url);
         if( params != null ) {
            Part[] parts = new Part[ params.size() ];
            int i = 0;
            for(Map.Entry<String,String> entry : params.entrySet()) {
                parts[i++] = new StringPart( entry.getKey(), entry.getValue() );
            }
            postMethod.setRequestEntity( new MultipartRequestEntity(parts, postMethod.getParams()) );
        }
        return performHttpRequest( postMethod );
    }
 
    public String performPutRequest(String url, String data) throws NativeException {
        PutMethod putMethod = new PutMethod(url);
        putMethod.setRequestBody( data );
        return performHttpRequest( putMethod );
    }

    public String performDeleteRequest(String url) throws NativeException {
        DeleteMethod deleteMethod = new DeleteMethod(url);
        return performHttpRequest( deleteMethod );
    }

    public String performPostRequest(String url) throws NativeException {
        return performHttpRequest( new PostMethod(url) );
    }

    /**
     * Performs <i>HTTP</i> request with specified method.
     *
     * @param method method to be applied.
     * @return HTTP response body.
     */
    protected String performHttpRequest(HttpMethodBase method) throws NativeException {

        HttpClient client = new HttpClient();

        // Provide custom retry handler.
        method.getParams().setParameter(
                HttpMethodParams.RETRY_HANDLER,
    		    new DefaultHttpMethodRetryHandler(3, false)
        );

        try {

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new NativeException( "Method failed: " + method.getStatusLine() );
            }

            // Read response body.
            byte[] responseBody = method.getResponseBody();
            return new String( responseBody );

        } catch (IOException e) {
            throw new NativeException("Fatal transport error: " + e.getMessage() );
        } finally {
            method.releaseConnection();
        }
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) 
     throws ServletException, IOException {
        doPost(httpServletRequest, httpServletResponse);
    }
}
