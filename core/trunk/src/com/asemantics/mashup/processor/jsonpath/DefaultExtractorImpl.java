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

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.ParseTreeVisitor;
import com.asemantics.mashup.parser.jsonpath.JPParser;
import com.asemantics.mashup.parser.jsonpath.JPParserException;
import com.asemantics.mashup.processor.json.JsonBase;


/**
 * Defines the default implementation of the Extractor.
 *
 * @see com.asemantics.mashup.processor.jsonpath.Path
 * @see com.asemantics.mashup.processor.jsonpath.Context
 */
public class DefaultExtractorImpl implements Extractor {

    /**
     * Internal JSONPath parser.
     */
    private final JPParser parser = new JPParser();

    public Context extractPath(Path path, Context context) throws ExtractorException {
        Context currentContext = context;
        try {
            for(Accessor accessor : path) {
                currentContext = accessor.access(currentContext);
            }
        } catch (AccessorException ae) {
            throw new ExtractorException("Error while applying accessor.", ae);
        }
        return currentContext;
    }

    public Context extractPath(String pathStr, Context context) throws ExtractorException {
        // Parse path.
        ParseTree parseTree;
        try {
            parseTree = parser.parse(pathStr);
        } catch (JPParserException jppe) {
            throw new ExtractorException("Error while parsing path.", jppe);
        }

        // Compile path.
        ParseTreeVisitor parseTreeVisitor = new ParseTreeVisitor(parseTree);
        Path path = (Path) ((Object[]) parseTreeVisitor.compile())[0];

        // Process path.
        return extractPath(path, context);
    }

    public Context extractPath(String path, JsonBase base) throws ExtractorException {
        DefaultContextImpl context = new DefaultContextImpl();
        context.addElement(base);
        return extractPath(path, context);
    }
}
