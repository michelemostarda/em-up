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


package com.asemantics.mashup.processor.jsonpath;

import com.asemantics.mashup.processor.jsonpath.Accessor;
import com.asemantics.mashup.processor.jsonpath.AccessorException;
import com.asemantics.mashup.processor.jsonpath.Context;
import com.asemantics.mashup.processor.jsonpath.DefaultContextImpl;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;

/**
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
public class RelativeArrayAccessor implements Accessor {

    /**
     * The relative index.
     */
    private int relativeIndex;

    /**
     * Constructor.
     *
     * @param relative the relative index.
     */
    public RelativeArrayAccessor(int relative) {
        if(relative >= 0) {
            throw new IllegalArgumentException("The relative index must be negative.");
        }
        relativeIndex = relative;
    }

    public String getName() {
        return "relative_array_accessor";
    }

    public String getDescription() {
        return "[" + relativeIndex + ":]";
    }

    public Context access(Context context) throws AccessorException {
        DefaultContextImpl result = new DefaultContextImpl();
        try {
            for(JsonBase element : context) {
                if( element.isArray() ) {
                    JsonArray arrayElement = (JsonArray) element;
                    int beginIndex = arrayElement.size() + relativeIndex;
                    for(int i = beginIndex; i < arrayElement.size(); i++ ) {
                        result.addElement( arrayElement.get(i) );
                    }
                } else {
                    throw new AccessorException("Expected array for this accessor.");
                }
            }
        } catch (Exception e) {
            throw new AccessorException("Error while applying accessor.", e);
        }
        return result;
    }
}
