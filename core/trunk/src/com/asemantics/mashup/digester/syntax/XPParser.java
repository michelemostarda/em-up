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


package com.asemantics.mashup.digester.syntax;


import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.Parser;
import com.asemantics.lightparser.ParserException;


/**
 * Defines the <i>XPath</i> expression parser.
 */
public class XPParser {

    /**
     * Grammar factory reference.
     */
    private XPGrammarFactory grammarFactory;

    /**
     * Internal parser.
     */
    private Parser parser;

    /**
     * Constructor.
     */
    public XPParser() {
        grammarFactory = new XPGrammarFactory();
        parser = new Parser( grammarFactory.createGrammar(), grammarFactory.createTokenizer() );
    }

    /**
     * Parses a given string on the specified production of <i>XPGrammar</i>
     * and returns the corrispondent parse tree.
     *
     * @param rootProductionName
     * @param in
     * @return parse tree resulting from parsing of string on root production.
     * @throws XPParserException
     */
    public ParseTree parse(String rootProductionName, String in) throws XPParserException {
        try {
            return parser.parse(rootProductionName, in);
        } catch (ParserException pe) {
            throw new XPParserException("Error while parsing expression: '" + in + "'", pe);
        }
    }

    /**
     * Parses a given string on the <i>XPGrammar</i>
     * and returns the corrispondent parse tree.
     *
     * @param in
     * @return parse tree resulting from parsing of given string.
     * @throws XPParserException
     */
    public ParseTree parse(String in) throws XPParserException {
        return parse( grammarFactory.createGrammar().getRootProduction().getHead().getContent(), in );
    }

}

