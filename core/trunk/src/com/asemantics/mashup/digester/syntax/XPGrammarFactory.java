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

import com.asemantics.lightparser.Grammar;
import com.asemantics.lightparser.Tokenizer;
import com.asemantics.lightparser.TokenizerImpl;

/**
 * Defines the grammar for an <i>XPath</i> subset
 * of operations applicable on <i>XML</i> parsing without backtracking. 
 */
public class XPGrammarFactory {

    public static final char PATH_SEPARATOR = '/';

    /**
     * List of token separators.
     */
    public static final Tokenizer.TokenSeparator[] XPATH_SEPARATORS
            = new Tokenizer.TokenSeparator[]{
                new Tokenizer.TokenSeparator( PATH_SEPARATOR, true),  // Char separator, passthrough.
                new Tokenizer.TokenSeparator('[' , true),
                new Tokenizer.TokenSeparator(']' , true),
                new Tokenizer.TokenSeparator('<' , true),
                new Tokenizer.TokenSeparator('>' , true),
                new Tokenizer.TokenSeparator('=' , true),
                new Tokenizer.TokenSeparator('!' , true),
                new Tokenizer.TokenSeparator('@' , true)
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
    public XPGrammarFactory() {
        // Empty.
    }

    /**
     * Returns the tokenizer instance.
     *
     * @return a tokenizer instance.
     */
    public Tokenizer createTokenizer() {
        if( tokenizer == null ) {
            tokenizer = new TokenizerImpl(XPATH_SEPARATORS);
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
         *      <path> ::= '/' <path_list>  // absolute path.
         *                 |
         *                 <path_list> ;    // relative path.
         */
        grammar.createProduction( new XPPath("path") )
                .getBody()
                    .addTerminal   ("/")
                    .addNonTerminal( new XPPathList("path_list") )
                .addAlternative()
                    .addNonTerminal( new XPPathList("path_list") );



        /*
         *      <path_list> ::= <path_elem> '/' <path_list>
         *                      |
         *                      <path_elem> ;
         */
        grammar.createProduction( new XPPathList("path_list") )
                .getBody()
                    .addNonTerminal( new XPPathElem("path_elem") )
                    .addTerminal   ( "/" )
                    .addNonTerminal( new XPPathList("path_list") )
                .addAlternative()
                    .addNonTerminal( new XPPathElem("path_elem") );

        /*
         *      <path_elem> ::= node '[' <condition> ']'   // Filtered node
         *                      |
         *                      node ;                     // Node
         */
        grammar.createProduction( new XPPathElem("path_elem") )
                .getBody()
                    .addTerminal( new XPNode("node") )
                    .addTerminal( "[" )
                    .addNonTerminal( new XPCondition("condition") )
                    .addTerminal( "]" )
                .addAlternative()
                    .addTerminal( new XPNode("node") );

        /*
         *      <condition> ::= <identifier> <operator> value | index ;
         */
        grammar.createProduction( new XPCondition("condition") )
                .getBody()
                    .addNonTerminal( new XPIdentifier("identifier") )
                    .addNonTerminal( new XPOperator("operator") )
                    .addTerminal   ( new XPValue("value") )
                .addAlternative()
                    .addTerminal( new XPIndex("index") );

        /*
         *      <identifier> ::= '@' attribute | node ;
         */
        grammar.createProduction( new XPIdentifier("identifier") )
                .getBody()
                    .addTerminal("@")
                    .addTerminal( new XPAttribute("attribute") )
                .addAlternative()
                    .addTerminal( new XPNode("node") );

        /*
         *      <operator> ::= '<' '=' | '>' '=' | '!' '=' | '<' | '>' | '=' ;
         */
        grammar.createProduction( new XPOperator("operator") )
                .getBody()
                    .addTerminal("<")
                    .addTerminal("=")
                .addAlternative()
                    .addTerminal(">")
                    .addTerminal("=")
                .addAlternative()
                    .addTerminal("!")
                    .addTerminal("=")
                .addAlternative()
                    .addTerminal("<")
                .addAlternative()
                    .addTerminal(">")
                .addAlternative()
                    .addTerminal("=");
    }
}
