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


package com.asemantics.mashup.parser.unification;

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.Parser;
import com.asemantics.lightparser.ParserException;

/**
 * Defines a parser able to process unification models.
 */
public class UNParser {

    /**
     * Grammar factory reference.
     */
    private UNGrammarFactory grammarFactory;

    /**
     * Internal parser.
     */
    private Parser parser;

    /**
     * Constructor.
     */
    public UNParser() {
        grammarFactory = new UNGrammarFactory();
        parser = new Parser( grammarFactory.createGrammar(), grammarFactory.createTokenizer() );
    }

    /**
     * Parses a given string on the specified production of <i>UNGrammar</i>
     * and returns the corrispondent parse tree.
     *
     * @param rootProductionName
     * @param in
     * @return parse tree resulting from parsing of string on root production.
     * @throws UNParserException
     */
    public ParseTree parse(String rootProductionName, String in) throws UNParserException {
        try {
            return parser.parse(rootProductionName, in);
        } catch (ParserException pe) {
            throw new UNParserException("Error while parsing string: '" + in + "'", pe);
        }
    }

    /**
     * Parses a given string on the <i>UNGrammar</i>
     * and returns the corrispondent parse tree.
     *
     * @param in
     * @return parse tree resulting from parsing of given string.
     * @throws com.asemantics.mashup.parser.MUParserException
     */
    public ParseTree parse(String in) throws UNParserException {
        return parse( grammarFactory.createGrammar().getRootProduction().getHead().getContent(), in );
    }

}