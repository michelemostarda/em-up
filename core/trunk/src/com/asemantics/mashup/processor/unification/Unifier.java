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


package com.asemantics.mashup.processor.unification;

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.ParseTreeVisitor;
import com.asemantics.mashup.parser.unification.UNParser;
import com.asemantics.mashup.parser.unification.UNParserException;
import com.asemantics.mashup.processor.JsonValue;

/**
 * Main class managing unification process.
 */
public class Unifier {

    /**
     * model parser instance.
     */
    private UNParser modelParser;

    /**
     * Constructor.
     */
    public Unifier() {
        modelParser = new UNParser();
    }

    /**
     * Performs a unification between model and data.
     *
     * @param resultBuilder
     * @param model JSON model.
     * @param value  JSON data.
     * @return unification result.
     */
    public UnificationResult unify(ResultBuilder resultBuilder, Unifiable model, JsonValue value) {
        try {
            model.unify(resultBuilder, value);
        } catch (UnificationException ue) {
            // Empty.
        }
        return resultBuilder;
    }

    /**
     * Performs a unification between model and data.
     *
     * @param model JSON model to be unified.
     * @param value JSON value to be used as root in unification.
     * @return list of unification matches.
     */
    public UnificationResult unify(Unifiable model, JsonValue value) {
        ResultBuilder resultBuilder = new ResultBuilder();
        return unify(resultBuilder, model, value);
    }

    /**
     * Performs a unification between model and data.
     * Model is espressed as string.
     *
     * @param modelStr model string.
     * @param value JSON value to be used as root for unification.
     * @return list of unification matches.
     */
    public UnificationResult unify(String modelStr, JsonValue value) {
        ResultBuilder resultBuilder = new ResultBuilder();
        try {
            JsonModel model = compileModel(modelStr);
            return unify(resultBuilder, model, value);
        } catch (UNParserException unpe) {
            try {
                resultBuilder.abort(unpe);
            } catch (UnificationException e) {
                // Empty.
            }
            return resultBuilder;
        }
    }

    /**
     * Compiles the given model string and returns the <i>JsonModel</i>.
     *
     * @param modelStr JSON model expressed as string.
     * @return JSON model parsed by <i>modelStr</i>.
     * @throws UNParserException if a error occours during parsing.
     */
    protected JsonModel compileModel(String modelStr)
    throws UNParserException {

        // Parse model string.
        ParseTree parseTree = modelParser.parse(modelStr);

        // Compile model string.
        ParseTreeVisitor parseTreeVisitor = new ParseTreeVisitor(parseTree);
        Object[] box = (Object[]) parseTreeVisitor.compile();
        assert box.length == 1;
        return (JsonModel) box[0];
    }

}
