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

import com.asemantics.lightparser.Grammar;
import com.asemantics.lightparser.Tokenizer;
import static com.asemantics.lightparser.Tokenizer.TokenSeparator;
import com.asemantics.lightparser.TokenizerImpl;
import com.asemantics.mashup.parser.MUConst;
import com.asemantics.mashup.parser.MUVariable;
import com.asemantics.mashup.parser.MUNumber;
import com.asemantics.mashup.parser.MUGrammarFactory;

/**
 * Defines the sub grammar of <i>MashUp</i> language unification.
 */
//TODO: remove duplicated terms from grammar.
public class UNGrammarFactory {

    /**
     * List of token separators.
     */
    public static final TokenSeparator[] LANGUAGE_SEPARATORS
            = new TokenSeparator[]{
                new TokenSeparator(' ' , false),  // Char separator, passthrough.
                new TokenSeparator('\n', false),
                new TokenSeparator(',' , false),

                new TokenSeparator('[', true ),
                new TokenSeparator(']', true ),
                new TokenSeparator('{', true ),
                new TokenSeparator('}', true ),
                new TokenSeparator('|', true ),
                new TokenSeparator(':', true ),
                new TokenSeparator('+', true ),
                new TokenSeparator('-', true ),
            };


    /**
     * Tokenizer unique instance.
     */
    private Tokenizer tokenizer;

    /**
     * Grammar unique instance.
     */
    private Grammar grammar;

    /**
     * Constructor.
     */
    public UNGrammarFactory() {
        // Empty.
    }

    /**
     * Returns the tokenizer instance.
     *
     * @return a tokenizer instance.
     */
    public Tokenizer createTokenizer() {
        if( tokenizer == null ) {
            tokenizer = new TokenizerImpl(LANGUAGE_SEPARATORS);
        }
        return tokenizer;
    }

    /**
     * Returns the grammar instance.
     *
     * @return the created grammar.
     */
    public Grammar createGrammar() {
        if( grammar == null ) {
            initGrammar();
        }
        return grammar;
    }

    /**
     * Initializes grammar structure.
     */
    private void initGrammar() {

        grammar = new Grammar();

        /*
         * <un_root> ::= variable | const | <number> | <m_array> | <m_object>
         */
        // Root production, merges array and object parsing.
        grammar.createProduction( new UNRoot("un_root") )
                .getBody()
                    .addTerminal( new MUVariable("variable") )
                .addAlternative()
                    .addTerminal( new MUConst("const") )
                .addAlternative()
                    .addNonTerminal( new MUNumber("number") )
                .addAlternative()
                    .addNonTerminal( new UNMArray("m_array") )
                .addAlternative()
                    .addNonTerminal( new UNMObject("m_object") );

        /*
         * <m_array> ::= '[' <m_array_elements> '|' variable ']'
         *                |
         *               '[' <m_array_elements> ']'
         *                |
         *               '[' ']'
         */
        grammar.createProduction( new UNMArray("m_array") )
                .getBody()
                    .addTerminal("[")
                    .addNonTerminal( new UNMArrayElements("m_array_elements") )
                    .addTerminal( "|" )
                    .addTerminal( new MUVariable("variable") )
                    .addTerminal("]")
                .addAlternative()
                    .addTerminal("[")
                    .addNonTerminal( new UNMArrayElements("m_array_elements") )
                    .addTerminal("]")
                .addAlternative()
                    .addTerminal("[")
                    .addTerminal("]");

        /*
         * <m_object> ::= '{' <m_object_elements> '|' variable '}'
         *                 |
         *                '{' <m_object_elements> '}'
         */
        grammar.createProduction( new UNMObject("m_object") )
                .getBody()
                    .addTerminal("{")
                    .addNonTerminal( new UNMObjectElements("m_object_elements") )
                    .addTerminal( "|" )
                    .addTerminal( new MUVariable("variable") )
                    .addTerminal("}")
                .addAlternative()
                    .addTerminal("{")
                    .addNonTerminal( new UNMObjectElements("m_object_elements") )
                    .addTerminal("}");

        /*
         * <m_array_elements> ::= <m_element> [,] <m_array_elements>
         *                        |
         *                        <m_element>
         */
        grammar.createProduction( new UNMArrayElements("m_array_elements") )
                .getBody()
                    .addNonTerminal( new UNMElement("m_element") )
                    .addNonTerminal( new UNMArrayElements("m_array_elements") )
                .addAlternative()
                    .addNonTerminal( new UNMElement("m_element") );

        /*
         * <m_object_elements> ::= <m_object_element> [,] <m_object_elements>
         *                         |
         *                         <m_object_element>
         */
        grammar.createProduction( new UNMObjectElements("m_object_elements") )
                .getBody()
                    .addNonTerminal( new UNMObjectElement("m_object_element") )
                    .addNonTerminal( new UNMObjectElements("m_object_elements") )
                .addAlternative()
                    .addNonTerminal( new UNMObjectElement("m_object_element") );

        /*
         * <m_object_element> ::= key ':' <m_element>
         */
        grammar.createProduction( new UNMObjectElement("m_object_element") )
                .getBody()
                    .addTerminal( new MUConst("const") )
                    .addTerminal( ":" )
                    .addNonTerminal( new UNMElement("m_element") );

        /*
         * <m_element> ::= variable | const | <number> | <m_object> | <m_array>
         */
        grammar.createProduction( new UNMElement("m_element") )
                .getBody()
                    .addTerminal( new MUVariable("variable") )
                .addAlternative()
                    .addTerminal( new MUConst("const") )
                .addAlternative()
                    .addNonTerminal( new MUNumber("number"))
                .addAlternative()
                    .addNonTerminal( new UNMObject("m_object") )
                .addAlternative()
                    .addNonTerminal( new UNMArray("m_array") );

        /*
         * <number> ::= ...
         */
        MUGrammarFactory.createNumberNT( grammar );

    }

}