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


package com.asemantics.mashup.parser.jsonpath;

import com.asemantics.lightparser.Tokenizer;
import com.asemantics.lightparser.Grammar;
import com.asemantics.lightparser.TokenizerImpl;

import static com.asemantics.lightparser.Tokenizer.TokenSeparator;
import com.asemantics.mashup.parser.MUNumber;
import com.asemantics.mashup.parser.MUVariable;
import com.asemantics.mashup.parser.MUNumeric;
import com.asemantics.mashup.parser.MUGrammarFactory;


/**
 * Defines the sub grammar of <i>MashUp</i> language unification.
 */
public class JPGrammarFactory {

    public static final char OPEN_ARRAY         = '[';
    public static final char CLOSE_ARRAY        = ']';
    public static final char ACCESSOR_SEPARATOR = '.';
    public static final char STAR_ACCESSOR      = '*';
    public static final char SLICE_SEPARATOR    = ':';
    public static final char PLUS               = '+';
    public static final char MINUS              = '-';

    public static final String STAR_ACCESSOR_STR  = Character.toString(STAR_ACCESSOR);

    /**
     * List of token separators.
     */
    public static final TokenSeparator[] LANGUAGE_SEPARATORS
            = new TokenSeparator[]{
                new TokenSeparator(' ' , false),  // Char separator, passthrough.
                new TokenSeparator('\n', false),
                new TokenSeparator(',' , false),

                new TokenSeparator(OPEN_ARRAY        , true ),
                new TokenSeparator(CLOSE_ARRAY       , true ),
                new TokenSeparator(ACCESSOR_SEPARATOR, true ),
                new TokenSeparator(SLICE_SEPARATOR   , true ),
                new TokenSeparator(PLUS              , true ),
                new TokenSeparator(MINUS             , true )
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
    public JPGrammarFactory() {
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
         * <jp_root> ::= <accessors_list>        
         */
        grammar.createProduction( new JPRoot("jp_root") )
                .getBody()
                    .addNonTerminal( new JPAccessorsList("accessors_list") );

        /*
         * <accessors_list> ::= <accessor> <accessors_list>
         *                      |
         *                      <accessor>
         */
        grammar.createProduction( new JPAccessorsList("accessors_list") )
                .getBody()
                    .addNonTerminal( new JPAccessor ("accessor" ) )
                    .addNonTerminal( new JPAccessorsList("accessors_list") )
                .addAlternative()
                    .addNonTerminal( new JPAccessor ("accessor" ) );        

        /*
         * <accessor> ::= <array_accessor>
         *                |
         *                <object_accessor>
         */
        grammar.createProduction( new JPAccessor ("accessor" ) )
                .getBody()
                    .addNonTerminal( new JPArrayAccessor("array_accessor") )
                .addAlternative()
                    .addNonTerminal( new JPObjectAccessor("object_accessor") );

        /*
         * <array_accessor> ::= '[' '*' ']'
         *                       |
         *                      '[' <number> ']'
         *                       |
         *                       <slice_operator>
         */
        grammar.createProduction( new JPArrayAccessor("array_accessor") )
                .getBody()
                    .addTerminal(OPEN_ARRAY)
                    .addTerminal(STAR_ACCESSOR)
                    .addTerminal(CLOSE_ARRAY)
                .addAlternative()
                    .addTerminal(OPEN_ARRAY)
                    .addNonTerminal( new MUNumber("number"))
                    .addTerminal(CLOSE_ARRAY)
                .addAlternative()
                    .addNonTerminal( new JPSliceOperator("slice_operator") );

        /*
         * <slice_operator> ::= '[' <number> ':' <number> ']'
         *                       |
         *                      '[' <number> ':' ']'
         *                       |
         *                      '[' ':' <number> ']'
         *                       |
         *                      '[' <number> ':' <number> ':' <number> ']'
         */
         grammar.createProduction( new JPSliceOperator("slice_operator") )
                 .getBody()
                    .addTerminal(OPEN_ARRAY)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(SLICE_SEPARATOR)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(CLOSE_ARRAY)
                 .addAlternative()
                    .addTerminal(OPEN_ARRAY)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(SLICE_SEPARATOR)
                    .addTerminal(CLOSE_ARRAY)
                 .addAlternative()
                    .addTerminal(OPEN_ARRAY)
                    .addTerminal(SLICE_SEPARATOR)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(CLOSE_ARRAY)
                 .addAlternative()
                    .addTerminal(OPEN_ARRAY)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(SLICE_SEPARATOR)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(SLICE_SEPARATOR)
                    .addNonTerminal( new MUNumber("number") )
                    .addTerminal(CLOSE_ARRAY);

        /*
         * <object_accessor> ::= '.' variable
         */
        grammar.createProduction( new JPObjectAccessor("object_accessor") )
                .getBody()
                    .addTerminal(ACCESSOR_SEPARATOR)
                    .addTerminal( new MUVariable("variable") );

        /*
         * <number> ::= ...
         */
        MUGrammarFactory.createNumberNT( grammar );

    }

}