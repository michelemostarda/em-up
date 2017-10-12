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

import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.FormalParameter;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.Value;

/**
 * Defines operation <i>MPut(&lt;map&gt;, &lt;key&gt;, &lt;value&gt;)</i>.
 */
public class MapPut extends MapOperation {

    /**
     * Map argument.
     */
    protected static final String MAP = "map";

    /**
     * key element.
     */
    protected static final String KEY = "key";

    /**
     * value element.
     */
    protected static final String VALUE = "value";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.MAP, MAP  ),
                    new FormalParameter(FormalParameter.Type.ANY, KEY  ),
                    new FormalParameter(FormalParameter.Type.ANY, VALUE)
            }
    );


    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Returns the value associated to key in given map.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        MapValue map   = context.getIthValueAsMap(0);
        Value    key   = context.getIthValue     (1);
        Value    value = context.getIthValue     (2);
        try {
            return map.put(key, value);
        } catch (Exception e) {
            throw new InvocationException("Error while adding element in map.", e);
        }
    }
}