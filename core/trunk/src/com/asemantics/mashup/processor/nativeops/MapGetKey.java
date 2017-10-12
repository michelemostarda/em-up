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
 * Defines operation <i>GetKey(&lt;map&gt;, &lt;key&gt;)</i>.
 */
public class MapGetKey extends MapOperation {

    /**
     * Map argument.
     */
    protected static final String MAP = "map";

    /**
     * Key element.
     */
    protected static final String KEY = "key";

    /**
     * Static signature.
     */
    public static final Signature SIGNATURE = new Signature(
            new FormalParameter[] {
                    new FormalParameter(FormalParameter.Type.MAP   , MAP),
                    new FormalParameter(FormalParameter.Type.STRING, KEY),
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
        MapValue map = context.getIthValueAsMap(0);
        Value    key = context.getIthValueAsString(1);
        try {
            return map.get(key);
        } catch (Exception e) {
            throw new InvocationException("Error while retrieving element from map.", e);
        }
    }
}