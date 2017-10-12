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


package com.asemantics.lightparser;

import junit.framework.TestCase;

/**
 * Test class for {@link com.asemantics.lightparser.Parser} class.
 */
public class ParserTest extends TestCase {

    /**
     * Internal tokenizer.
     */
    private Tokenizer tokenizer;

    public void setUp() {
        tokenizer = new TokenizerImpl();
    }

    public void tearDown() throws TokenizerException {
        tokenizer.close();
        tokenizer = null;
    }

    /**
     * Tests error reporting with unespected end of parsed string.
     *
     * @throws ParserException
     */
    public void testUnexpectedEndOfStringErrorReporting() throws ParserException {
        Grammar grammar = new Grammar();
        GrammarTest.populateGrammar( grammar );
        Parser parser = new Parser(grammar, tokenizer);

        String input = "terminal2 terminal3";
        try {
            parser.parse(input);
            fail("Expected exception");
        } catch (BacktrackingParserException bpe) {
            System.out.println("bpe: " + bpe );
            assertTrue( bpe.getMessage().contains("Unexpected") );
            assertTrue( bpe.getMessage().contains("end") );
            assertTrue( bpe.getMessage().contains("string") );
            assertTrue( bpe.getMessage().contains("Expected") );
            assertTrue( bpe.getMessage().contains("terminal1") );
        }
    }

    /**
     * Tests error reporting with unespected token.
     *
     * @throws ParserException
     */
    public void testUnespectedTokenErrorReporting() throws ParserException {
        Grammar grammar = new Grammar();
        GrammarTest.populateGrammar( grammar );
        Parser parser = new Parser(grammar, tokenizer);

        String input = "terminal2 terminal3 terminalXXX";
        try {
            parser.parse(input);
            fail("Expected exception");
        } catch (BacktrackingParserException bpe) {
            assertTrue( bpe.getMessage().contains("20") );
            assertTrue( bpe.getMessage().contains("Expected") );
            assertTrue( bpe.getMessage().contains("terminal1") );
        }
    }

    /**
     * Tests a simple forwarding parsing.
     *
     * @throws ParserException
     */
    public void testForwardParsing() throws ParserException {
        Grammar grammar = new Grammar();
        GrammarTest.populateGrammar( grammar );
        Parser parser = new Parser(grammar, tokenizer);
       
        String input = "terminal2 terminal3 terminal1";
        ParseTree pt = parser.parse(input);
        System.out.println(pt);
    }

    /**
     * Tests a simple backtracking.
     *
     * @throws ParserException
     */
    public void testSimpleBacktracking() throws ParserException {
        String input = "terminal1 terminal2 terminal3 terminal4 terminal5";

        Parser parser = new Parser( createSimpleBTGrammar(), tokenizer );
        parser.addParserListener( new DebugParserListener() );
        ParseTree pt = parser.parse(input);
        System.out.println(pt);
    }

    /**
     * Tests a nested backtracking.
     *
     * @throws ParserException
     */
    public void testNestedBacktracking() throws ParserException {
        String input = "terminal1 terminal2 terminal3 terminal4 terminal5";

        Parser parser = new Parser( createNestedBTGrammar(), tokenizer );
        parser.addParserListener( new DebugParserListener() );
        ParseTree pt = parser.parse(input);
        System.out.println(pt);
    }

    /**
     * Tests a nested backtraking with a partial commit backtracked.
     *
     * @throws ParserException
     */
    public void testNestedBacktrackingWithPartialCommit() throws ParserException {
        String input = "terminal1 terminal2 terminal3 terminal4 terminal5 terminal6 terminal7";

        Parser parser = new Parser( createNestedBTPCGrammar(), tokenizer );
        parser.addParserListener( new DebugParserListener() );
        ParseTree pt = parser.parse(input);
        System.out.println(pt);
    }

    /**
     * Tests a recursive production.
     *
     * @throws ParserException
     */
    public void testRecursiveProduction() throws ParserException {
        String input = "terminal terminal terminal terminal terminal terminal terminal";

        Grammar grammar = new Grammar();
        grammar.createProduction("recursive_nonterminal")
                .getBody()
                .addTerminal("terminal").addNonTerminal("recursive_nonterminal")
                .addAlternative()
                .addTerminal("terminal");

        Parser parser = new Parser( grammar, tokenizer );
        parser.addParserListener( new DebugParserListener() );
        ParseTree pt = parser.parse(input);

        System.out.println(pt);
        Terminal[] terminals = pt.getTerminalNodes();
        assertEquals("Unespected number of terminals.", 7, terminals.length);
    }

    /**
     * Tests that a partially satisfied stream parsed with a list
     * is detected and rejected.
     */
    public void testPartiallySatisfiedStreamDetection() {
        final String input = "x x x x x x x y x x";

        Grammar grammar = new Grammar();

        grammar.createProduction("list")
                .getBody()
                    .addTerminal("x")
                    .addNonTerminal("list")
                .addAlternative()
                    .addTerminal("x");

        Parser parser = new Parser( grammar, tokenizer );
        parser.addParserListener(
            new DebugParserListener() {
                @Override
                public void parsingFailed(ParserException pe) {}
        } );
        try {
            parser.parse(input);
            fail("This must raise error.");
        } catch (ParserException pe) {
            // OK
            //pe.printStackTrace();
        }
    }

    /**
     * Creates a simple backtracking grammar.
     *
     * @return a grammar.
     * @throws ParserException
     */
    private Grammar createSimpleBTGrammar() throws ParserException {
        Grammar grammar = new Grammar();
        grammar.createProduction("root")
                .getBody()
                .addNonTerminal("nonterminal1")
                .addAlternative()
                .addNonTerminal("nonterminal2");

        grammar.createProduction("nonterminal1")
                .getBody()
                .addTerminal("terminal1")
                .addTerminal("terminal2")
                .addTerminal("UNESPECTED");

        grammar.createProduction("nonterminal2")
                .getBody()
                .addTerminal("terminal1")
                .addTerminal("terminal2")
                .addTerminal("terminal3")
                .addTerminal("terminal4")
                .addTerminal("terminal5");
        
        return grammar;
    }

    /**
     * Creates a nested backtraking grammar.
     *
     * @return a grammar.
     * @throws ParserException
     */
    private Grammar createNestedBTGrammar() throws ParserException {
        Grammar grammar = new Grammar();

        grammar.createProduction("root")
                .getBody()
                .addNonTerminal("nonterminal1")
                .addNonTerminal("nonterminal2")
                .addAlternative()
                .addNonTerminal("nonterminal3");

        grammar.createProduction("nonterminal1")
                .getBody()
                .addTerminal("terminal1")
                .addTerminal("terminal2")
                .addTerminal("terminal3");

        grammar.createProduction("nonterminal2")
                .getBody()
                .addTerminal("terminal4")
                .addTerminal("UNESPECTED");

        grammar.createProduction("nonterminal3")
                .getBody()
                .addTerminal("terminal1")
                .addTerminal("terminal2")
                .addTerminal("terminal3")
                .addTerminal("terminal4")
                .addTerminal("terminal5");

        return grammar;
    }

    /**
     * Creares a nested backtracking grammar inducing a partial commit.
     *
     * @return a grammar.
     * @throws ParserException
     */
    private Grammar createNestedBTPCGrammar() throws ParserException {
        Grammar grammar = new Grammar();

        grammar.createProduction("root")
                .getBody()
                .addNonTerminal("nonterminal1")
                .addNonTerminal("nonterminal2")
                .addAlternative()
                .addNonTerminal("nonterminal3");

        grammar.createProduction("nonterminal1")
                .getBody()
                .addTerminal("terminal1")
                .addNonTerminal("nonterminal4")
                .addTerminal("terminal4");

        grammar.createProduction("nonterminal2")
                .getBody()
                .addTerminal("terminal5")
                .addTerminal("UNESPECTED");

        grammar.createProduction("nonterminal3")
                .getBody()
                .addTerminal("terminal1")
                .addTerminal("terminal2")
                .addTerminal("terminal3")
                .addTerminal("terminal4")
                .addTerminal("terminal5")
                .addTerminal("terminal6")
                .addTerminal("terminal7");

        grammar.createProduction("nonterminal4")
                .getBody()
                .addTerminal("terminal2")
                .addTerminal("terminal3");

        return grammar;
    }

}
