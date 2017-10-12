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

import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonArray;

/**
 * Defines an accessor for an array element.
 */
public class ArrayAccessor implements Accessor {

    /**
     * Begin index.
     */
    private int beginIndex;

    /**
     * End index.
     */
    private int endIndex;

    /**
     * Progression step.
     */
    private int stepValue;

    /**
     * Constructor for range.
     *
     * @param begin begin index to be accessed.
     * @param end   end index to be accessed.
     * @param step  step dimension.
     */
    public ArrayAccessor(Integer begin, Integer end, Integer step) {
        if(step == 0) {
            throw new IllegalArgumentException("Step must be different from 0.");
        }

        if( begin < end && step < 0) {
            throw new IllegalArgumentException("Invalid negative step value for begin < end");
        }
        if( begin > end && step > 0) {
            throw new IllegalArgumentException("Invalid positive step value for begin > end");
        }
        beginIndex = begin;
        endIndex   = end;
        stepValue  = step;
    }

    /**
     * Constructor for single index.
     *
     * @param index
     */
    public ArrayAccessor(int index) {
        beginIndex = index;
        endIndex   = index;
        stepValue  = 1;
    }

    public String getName() {
        return "array_accessor";
    }

    public String getDescription() {
        return "[" + beginIndex + ":" + endIndex + ":" + stepValue + "]";
    }

    public Context access(Context context) throws AccessorException {
        DefaultContextImpl result = new DefaultContextImpl();
        try {
            for(JsonBase element : context) {
                if( element.isArray() ) {
                    JsonArray arrayElement = (JsonArray) element;
                    for(int i = beginIndex; i <= endIndex; i+= stepValue ) {
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
