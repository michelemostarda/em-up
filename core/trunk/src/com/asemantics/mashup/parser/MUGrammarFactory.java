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


package com.asemantics.mashup.parser;

import static com.asemantics.lightparser.Tokenizer.TokenSeparator;
import com.asemantics.lightparser.Grammar;
import com.asemantics.lightparser.GrammarUtils;
import com.asemantics.lightparser.NonTerminal;
import com.asemantics.lightparser.Tokenizer;
import com.asemantics.lightparser.TokenizerImpl;
import com.asemantics.mashup.parser.unification.UNGrammarFactory;

/**
 * Defines the grammar of  <i>MashUp</i> language.
 */
//TODO: remove duplicated terms from the grammar.
public class MUGrammarFactory {

    /**
     * Preposition separator.
     */
    public static final char PREPOSITION_SEPARATOR = ';';

    /**
     * Single quote string delimiter.
     */
    public static final char SINGLE_QUOTE = Tokenizer.SINGLE_QUOTE_STRING_DELIMITER;

    /**
     * Double quote string delimiter.
     */
    public static final char DOUBLE_QUOTE = Tokenizer.DOUBLE_QUOTE_STRING_DELIMITER;

    /**
     * Begin comment block char.
     */
    public static final char BEGIN_COMMENT_BLOCK = '#';

    /**
     * Begin comment block string.
     */
    public static final String BEGIN_COMMENT_BLOCK_STR = Character.toString('#');

    /**
     * End comment block char.
     */
    public static final char END_COMMENT_BLOCK   = '#';

    /**
     * End comment block string.
     */
    public static final String END_COMMENT_BLOCK_STR = Character.toString('#');

    /**
     * Comment block delimiters.
     */
    public static final Tokenizer.CommentBlock[] COMMENT_BOCKS =
            new Tokenizer.CommentBlock[] {
                new Tokenizer.CommentBlock(BEGIN_COMMENT_BLOCK, END_COMMENT_BLOCK)
            };

    /**
     * List of token separators.
     */
    public static final TokenSeparator[] LANGUAGE_SEPARATORS
            = new TokenSeparator[]{
                new TokenSeparator(' ' , false),  // Char separator, passthrough.
                new TokenSeparator('\n', false),
                new TokenSeparator(',' , false),

                new TokenSeparator('&', true ),
                new TokenSeparator('|', true ),
                new TokenSeparator(':', true ),
                new TokenSeparator('(', true ),
                new TokenSeparator(')', true ),
                new TokenSeparator('_', true ),
                new TokenSeparator('=', true ),
                new TokenSeparator('<', true ),
                new TokenSeparator('>', true ),
                new TokenSeparator('+', true ),
                new TokenSeparator('-', true ),
                new TokenSeparator('*', true ),
                new TokenSeparator('/', true ),

                new TokenSeparator(PREPOSITION_SEPARATOR, true )
            };

    /**
     * Sub grammar for unification expressions. Unification expressions are used
     * to in predicate declarations, instead of variables.
     */
    private static final UNGrammarFactory unificationGrammarFactory = new UNGrammarFactory();

    /**
     * Tokenizer unique instance.
     */
    private Tokenizer tokenizer;

    /**
     * Grammar unique instance.
     */
    private Grammar grammar;

    /**
     * Takes a grammag and creates in i a <i>number</i> non terminal.
     *
     * @param g input grammar.
     */
    public static final void createNumberNT(Grammar g) {
        /*
         * <number> ::= '+' <numeric> | '-' <numeric> | <numeric>
         */
        g.createProduction( new MUNumber("number") )
            .getBody()
                .addTerminal("+")
                .addTerminal( new MUNumeric("numeric") )
            .addAlternative()
                .addTerminal("-")
                .addTerminal( new MUNumeric("numeric") )
            .addAlternative()
                .addTerminal( new MUNumeric("numeric") );
    }

    /**
     * Constructor.
     */
    public MUGrammarFactory() {
        // Empty.
    }

    /**
     * Returns the tokenizer instance.
     *
     * @return a tokenizer instance.
     */
    public Tokenizer createTokenizer() {
        if( tokenizer == null ) {
            // Merging grammar and sub grammar tokens.
            tokenizer = new TokenizerImpl(
                    GrammarUtils.mergeTokenSeparators(
                            LANGUAGE_SEPARATORS,
                            unificationGrammarFactory.LANGUAGE_SEPARATORS
                    )
            );
            tokenizer.setCommentBlocks( COMMENT_BOCKS );
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
            grammar.mergeWith( unificationGrammarFactory.createGrammar() );
        }
        return grammar;
    }

    /**
     * Returns the head of unification grammar (UN).
     * 
     * @return single instance of UN grammar root non terminal production.
     */
    private NonTerminal getUnificationGrammarRoot() {
        return unificationGrammarFactory.createGrammar().getRootProduction().getHead();
    }

    /**
     * Initializes grammar structure.
     */
    private void initGrammar() {
        grammar = new Grammar();

        /*
         * <program> ::= <preposition> <program>
         *               |
         *               <preposition>
         */
        grammar.createProduction( new MUProgram("program") )
                .getBody()
                    .addNonTerminal( new MUPreposition("preposition") )
                    .addNonTerminal( new MUProgram("program") )
                .addAlternative()
                    .addNonTerminal( new MUPreposition("preposition") );

        /*
         *   <preposition> ::= <predicate_declaration> ':' <preposition_body> ';'
         *                     |
         *                     <preposition_body> ';'
         */
        grammar.createProduction( new MUPreposition("preposition") )
                .getBody()
                    .addNonTerminal( new MUPredicateDeclaration("predicate_declaration") )
                    .addTerminal(":")
                    .addNonTerminal( new MUPrepositionBody("preposition_body") )
                    .addTerminal(PREPOSITION_SEPARATOR)
                .addAlternative()
                    .addNonTerminal( new MUPrepositionBody("preposition_body") )
                    .addTerminal(PREPOSITION_SEPARATOR);

        /*
         * <preposition_body> ::= <preposition_element> and_connector <preposition_body>
         *                        |
         *                        <preposition_element> or_connector  <preposition_body>
         *                        |
         *                        <preposition_element> [,] <preposition_body>
         *                        |
         *                        <preposition_element>
         *                        |
         *                        <graph>
         *                        |
         *                        const
         *                        |
         *                        number
         *                        |
         *                        variable
         *                        |
         *                        true
         *                        |
         *                        false
         */
        grammar.createProduction( new MUPrepositionBody("preposition_body") )
                .getBody()
                    .addNonTerminal( new MUPrepositionElement("preposition_element") )
                    .addTerminal( new MUAndConnector("and_connector") )
                    .addNonTerminal( new MUPrepositionBody("preposition_body") )
                .addAlternative()
                    .addNonTerminal( new MUPrepositionElement("preposition_element") )
                    .addTerminal( new MUOrConnector("or_connector") )
                    .addNonTerminal( new MUPrepositionBody("preposition_body") )
                .addAlternative()
                    .addNonTerminal( new MUPrepositionElement("preposition_element") )
                    .addNonTerminal( new MUPrepositionBody("preposition_body") )
                .addAlternative()
                    .addNonTerminal( new MUPrepositionElement("preposition_element") )
                .addAlternative()
                    .addNonTerminal( new MUGraph("graph") )
                .addAlternative()
                    .addNonTerminal( new MUNumber("number") )
                .addAlternative()
                    .addTerminal( new MUConst("const") )
                .addAlternative()
                    .addTerminal( new MUVariable("variable") )
                .addAlternative()
                    .addTerminal( new MUTrue("true") )
                .addAlternative()
                    .addTerminal( new MUFalse("false") );


        /**
         * <preposition_element> ::= <control>
         *                           |
         *                           <predicate>
         */
        grammar.createProduction( new MUPrepositionElement("preposition_element" ) )
                .getBody()
                    .addNonTerminal( new MUControl("control") )
                .addAlternative()
                    .addNonTerminal( new MUPredicate("predicate") );

        /*
         *  <predicate_declaration> ::= predicate_name '(' <list_of_models> [,] varargs ')'  // List of formal parameters with varargs tail.
         *                             |
         *                             predicate_name '(' <list_of_models> ')'               // fixed list of formal parameters.
         *                             |
         *                             predicate_name '(' varargs ')'                        // Only varargs parameters.
         *                             |
         *                             predicate_name '(' ')'                                // No arguments allowed.
         */
        grammar.createProduction( new MUPredicateDeclaration("predicate_declaration") )
                .getBody()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addTerminal(")")
                .addAlternative()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addNonTerminal( new MUListOfModels("list_of_models") )
                    .addTerminal( new MUVarargs("varargs") )
                    .addTerminal(")")
                .addAlternative()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addNonTerminal( new MUListOfModels("list_of_models") )
                    .addTerminal(")")
                .addAlternative()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addTerminal( new MUVarargs("varargs") )
                    .addTerminal(")");

        /*
         * <predicate> ::= predicate_name '(' ')'
         *                 |
         *                 predicate_name '(' <list_of_terms> ')'
         *                 |
         *                 <declaration>
         */
        grammar.createProduction( new MUPredicate("predicate") )
                .getBody()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addTerminal(")")
                .addAlternative()
                    .addTerminal( new MUPredicateName("predicate_name") )
                    .addTerminal("(")
                    .addNonTerminal( new MUListOfTerms("list_of_terms") )
                    .addTerminal(")")
                .addAlternative()
                    .addNonTerminal( new MUDeclaration("declaration") );

        /*
         * <list_of_models> ::= <un_grammar_root> <list_of_models>  // Used in predicate declarations.
         *                    |
         *                    <un_grammar_root>
         */
        grammar.createProduction( new MUListOfModels("list_of_models") )
                .getBody()
                    .addNonTerminal( getUnificationGrammarRoot() )
                    .addNonTerminal( new MUListOfModels("list_of_models") )
                .addAlternative()
                    .addNonTerminal( getUnificationGrammarRoot() );

        /*
         * <list_of_terms> ::= <term> <list_of_terms>  //  Used in predicate invocations.
         *                    |
         *                    <term>
         */
        grammar.createProduction( new MUListOfTerms("list_of_terms") )
                .getBody()
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUListOfTerms("list_of_terms") )
                .addAlternative()
                    .addNonTerminal( new MUTerm("term") );

        /*
         * <term> ::= true | false | <named_predicate> | variable | const | last_value | <number> | <vararg> | <predicate> | <graph>
         */
        grammar.createProduction( new MUTerm("term") )
                .getBody()
                    .addTerminal( new MUTrue("true") )
                .addAlternative()
                    .addTerminal( new MUFalse("false") )
                .addAlternative()
                    .addNonTerminal( new MUNamedPredicate("named_predicate") )
                 .addAlternative()
                    .addTerminal( new MUVariable("variable") )
                .addAlternative()
                    .addTerminal( new MUConst("const") )
                .addAlternative()
                    .addTerminal( new MULastValue("last_value") )
                .addAlternative()
                    .addNonTerminal( new MUNumber("number") )
                .addAlternative()
                    .addNonTerminal( new MUVararg("vararg") )  // TODO: remove varargs placing it in a more appropriate location.
                .addAlternative()
                    .addNonTerminal( new MUPredicate("predicate") )
                .addAlternative()
                    .addNonTerminal( new MUGraph("graph") );


        // <number> ::= ...
        createNumberNT( grammar );

        /*
         * <vararg> ::= varargs <number>
         *              |
         *              varargs
         */
        grammar.createProduction( new MUVararg("vararg") )
                .getBody()
                    .addTerminal( new MUVarargs("varargs") )
                    .addNonTerminal( new MUNumber("number")   )
                .addAlternative()
                    .addTerminal( new MUVarargs("varargs") );    

        /*
         * <named_predicate> ::= <un_grammar_root> '=' <predicate>
         */
        grammar.createProduction( new MUNamedPredicate("named_predicate") )
                .getBody()
                    .addNonTerminal( getUnificationGrammarRoot() )
                    .addTerminal("=")
                    .addNonTerminal( new MUPredicate("predicate") );

        /*
         * <declaration> ::= <un_grammar_root> '=' <term>
         */
        grammar.createProduction( new MUDeclaration("declaration") )
                .getBody()
                    .addNonTerminal( getUnificationGrammarRoot() )
                    .addTerminal("=")
                    .addNonTerminal( new MUTerm("term") );


        /*
         * <control> ::= <if_control>
         *               |
         *               <for_control>
         */
        grammar.createProduction( new MUControl("control") )
                .getBody()
                    .addNonTerminal( new MUIfControl("if_control") )
                .addAlternative()
                    .addNonTerminal( new MUForControl("for_control") );

        /*
         * <if_control> ::= 'if' '(' <term> [,] <predicate> ')'                 // condition if_case
         *                  |
         *                  'if' '(' <term> [,] <predicate> [,] <predicate> ')' // condition if_case else_case
         */
        grammar.createProduction( new MUIfControl("if_control")  )
                .getBody()
                    .addTerminal("if")
                    .addTerminal("(")
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUTerm("term") )
                    .addTerminal(")")
                .addAlternative()
                    .addTerminal("if")
                    .addTerminal("(")
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUTerm("term") )
                    .addTerminal(")");

        /*
         * <for_control> ::= 'for' '(' <term> [,] variable [,] <predicate> ')'
         */
        grammar.createProduction( new MUForControl("for_control") )
                .getBody()
                    .addTerminal("for")
                    .addTerminal("(")
                    .addNonTerminal( new MUTerm("term")           )
                    .addTerminal   ( new MUVariable("variable")   )
                    .addNonTerminal( new MUPredicate("predicate") )
                    .addTerminal(")");

        /*
         * <graph> ::= '<' <list_of_triples> '>'
         */
        grammar.createProduction( new MUGraph("graph") )
                .getBody()
                    .addTerminal("<")
                    .addNonTerminal( new MUListOfTriples("list_of_triples") )
                    .addTerminal(">");

        /*
         * <list_of_triples> ::= <triple> ';' <list_of_triples>
         *                       |
         *                       <triple> 
         */
        grammar.createProduction( new MUListOfTriples("list_of_triples") )
                .getBody()
                    .addNonTerminal( new MUTriple("triple") )
                    .addTerminal(PREPOSITION_SEPARATOR)
                    .addNonTerminal( new MUListOfTriples("list_of_triples") )
                .addAlternative()
                    .addNonTerminal( new MUTriple("triple") );

        /*
         * <triple> ::= <term> [,] <term> [,] <term>
         */
        grammar.createProduction( new MUTriple("triple") )
                .getBody()
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUTerm("term") )
                    .addNonTerminal( new MUTerm("term") );

    }


    // TODO: complete expression syntax.
    /*
     * <expression> ::= '(' <expression> ')'
     */

    /*
     * <expression> ::= <expression> <operator> <expression>
     */

    /*
     * <expression> ::= <term>
     */

    /*
     * <operator> ::=  '+' | '-' | '*' | '/'
     */



}
