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

/**
 * Main class of package, providing logic to apply a {@link Path} on a <i>JSON</i>
 * context represented by a {@link com.asemantics.mashup.processor.jsonpath.Context}.
 *
 * @see com.asemantics.mashup.processor.jsonpath.Path
 */
public interface Extractor {

    /**
     * Extracts a JSON path from a context returning a new context.
     *
     * @param path JSON path to be applied to the context.
     * @param context input context.
     * @return output context.
     */
    Context extractPath(Path path, Context context) throws ExtractorException;

    /**
     * Extracts a JSON path from a context returning a new context.
     *
     * @param pathStr JSON path expressed as string.
     * @param context input context.
     * @return output context.
     */
    Context extractPath(String pathStr, Context context) throws ExtractorException;

    /**
     * Extracts a JSON path from a context returning a new context.
     *
     * @param path JSON path expressed as a string.
     * @param base JSON value to be used as context.
     * @return the context extracted by this path.
     * @throws ExtractorException in an internal error occurs.
     */
    Context extractPath(String path, JsonBase base) throws ExtractorException;
}
