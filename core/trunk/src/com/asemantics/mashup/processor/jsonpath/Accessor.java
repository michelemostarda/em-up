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

/**
 * Defines a <i>JSON</i> accessor.
 *
 * @see com.asemantics.mashup.processor.jsonpath.Path
 * @see com.asemantics.mashup.processor.jsonpath.Context
 */
public interface Accessor {

    /**
     * Returns the accessor name.
     *
     * @return a descriptive name.
     */
    String getName();

    /**
     * Returns a description of the accessor.
     *
     * @return description of the accessor.
     */
    String getDescription();

    /**
     * Returns the application of this accessor on given context.
     *
     * @param context JSON object representing the context.
     * @return returned context.
     */
    Context access(Context context) throws AccessorException;

}
