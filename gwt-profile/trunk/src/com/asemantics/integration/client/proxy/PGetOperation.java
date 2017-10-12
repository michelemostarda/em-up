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

import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.SequenceNotFoundException;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.interpreter.Utils;

import java.util.List;

/**
 * Implements the <i>HTTP Proxy</i> GET operation.
 */
public class PGetOperation extends AbstractHttpProxyOperation {

    public Method getMethod() {
        return Method.GET;
    }

    public String getShortDescription() {
        return "Proxy HTTP GET request";
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

        String url         = context.getValue(URL)   .asString().getNativeValue();
        List<Value> params = context.getValue(PARAMS).asList().getNativeValue();

        String expandedUrl = Utils.expandsString(url, params.toArray( new Value[params.size()] ));

        return new StringValue( performRequest(Method.GET, expandedUrl, null, null) );
    }
}
