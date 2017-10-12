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


package com.asemantics.mashup.processor.nativeops;

import com.asemantics.mashup.interpreter.Utils;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

import java.util.HashMap;
import java.util.List;

/**
 * Base operation for <i>HTTP</i> request.
 */
public abstract class AbstractSourceOperation extends NativeInvocable {

    /**
     * GET method.
     */
    public static final String GET_METHOD = "GET";

    /**
     * POST method.
     */
    public static final String POST_METHOD = "POST";

    /**
     * PUT method.
     */
    public static final String PUT_METHOD = "PUT";

    /**
     * DELETE method.
     */
    public static final String DELETE_METHOD = "DELETE";

    /**
     * URL to be retrieved.
     */
    private static final String URL    = "url";

    /**
     * List of parameters to be expanded.
     */
    private static final String PARAMS = "params";

    /**
     * Operation signature.
     */
    private static final Signature SIGNATURE = new Signature(
            new FormalParameter[]{
                    new FormalParameter(FormalParameter.Type.STRING, URL),
                    new FormalParameter(FormalParameter.Type.ANY, PARAMS)
            }
    );

    /**
     * Constructor.
     */
    public AbstractSourceOperation() {
        // Empty.
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    /**
     * Returns the method to be applied.
     *
     * @return method to be applied.
     */
    public abstract String getMethod();

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {

        final String url = context.getIthValueAsString(0).getNativeValue();

        String result = "<null>";
        try {
            if ( GET_METHOD.equals( getMethod() ) ) {

                final List<Value> paramsList = context.getIthValueAsList(1).getNativeValue();
                final String expandedURL = Utils.expandsString( url, paramsList.toArray( new Value[paramsList.size()] ) );
                result = NativeImpl.getInstance().httpGetRequest(expandedURL);

            } else if( POST_METHOD.equals( getMethod() ) ) {

                final List<Value> paramsList = context.getIthValueAsList(1).getNativeValue();

                HashMap<String,String> parameters = null;
                if(paramsList.size() > 0 ) {
                    parameters = new HashMap<String,String>();
                    for(int i = 0; i < paramsList.size(); i+=2) {
                        String key   = paramsList.get(i).asString().getNativeValue();
                        String value = (i + 1) < paramsList.size() ? paramsList.get(i+1).asString().getNativeValue() : "";
                        parameters.put(key,value);
                    }
                }
                result = NativeImpl.getInstance().httpPostRequest(url, parameters);
                
            } else if( PUT_METHOD.equals( getMethod() ) ) {

                final String data = context.getIthValueAsString(1).getNativeValue();
                NativeImpl.getInstance().httpPutRequest(url, data);

            } else if( DELETE_METHOD.equals( getMethod() ) ) {

               final List<Value> paramsList = context.getIthValueAsList(1).getNativeValue();
               final String expandedURL = Utils.expandsString( url, paramsList.toArray( new Value[paramsList.size()] ) );
               result = NativeImpl.getInstance().httpDeleteRequest(expandedURL); 

            } else {
                throw new IllegalStateException("Invalid method: " + getMethod());
            }
        } catch (Exception e) {
            throw new InvocationException( e.getMessage() + ( e.getCause() != null ? " cause: " + e.getCause().getMessage() : "" ) + "." );
        }

        return new StringValue(result);
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }
}