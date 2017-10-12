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
 * Defines a test case for the {@link com.asemantics.lightparser.TokenizerImpl} class.
 *
 * @see com.asemantics.lightparser.Tokenizer
 */
public class TokenizerImplTest extends TestCase {

    /**
     * List of tokens.
     */
    private static String[] TOKEN_STRINGS = new String[] {"a", "bb", "ccc", "dddd", "eeeee", "ffffff"};

    /**
     * Test target.
     */
    private Tokenizer tokenizer;

    protected void setUp() throws Exception {
        tokenizer = new TokenizerImpl();
    }

    protected void tearDown() throws Exception {
        tokenizer = null;
    }

    /**
     * Tests verification on token separators.
     * @throws TokenizerException
     */
    public void testSetTokenSeparatorOk() throws TokenizerException {
        tokenizer.setTokenSeparators(
                new Tokenizer.TokenSeparator[]{
                        new Tokenizer.TokenSeparator('a'),
                        new Tokenizer.TokenSeparator('b'),
                        new Tokenizer.TokenSeparator('c')
                }
        );
    }

    /**
     * Tests verification on token separators.
     * @throws TokenizerException
     */
    public void testSetTokenSeparatorNokDuplicateToken() throws TokenizerException {
        try {
            tokenizer.setTokenSeparators(
            new Tokenizer.TokenSeparator[]{
                    new Tokenizer.TokenSeparator('a'),
                    new Tokenizer.TokenSeparator('b'),
                    new Tokenizer.TokenSeparator('a')
                }
            );
            fail("Expected exception here.");
        } catch (IllegalArgumentException iae) {
            // OK.
        }
    }

    /**
     * Tests comment blocks.
     */
    public void testCommentBlock() throws TokenizerException {
        tokenizer.setTokenSeparators( new Tokenizer.TokenSeparator[] { new Tokenizer.TokenSeparator(' ') } );
        tokenizer.setCommentBlocks(
                new Tokenizer.CommentBlock[] {
                    new Tokenizer.CommentBlock('#', '#'),
                    new Tokenizer.CommentBlock('/', '/'),
                    new Tokenizer.CommentBlock('{', '}')
                }
        );
        tokenizer.tokenize("aaa bbb#ccc ddd# eee fff/ggg/ hhh {iii jjj}kkk");
        assertEquals( "Unespected token value.", "aaa", tokenizer.nextToken().getValue());
        assertEquals( "Unespected token value.", "bbb", tokenizer.nextToken().getValue());
        assertEquals( "Unespected token value.", "eee", tokenizer.nextToken().getValue());
        assertEquals( "Unespected token value.", "fff", tokenizer.nextToken().getValue());
        assertEquals( "Unespected token value.", "hhh", tokenizer.nextToken().getValue());
        assertEquals( "Unespected token value.", "kkk", tokenizer.nextToken().getValue());
        assertNull("Unespected token.", tokenizer.nextToken() );
    }

    /**
     * Tests single quote test delimitation.
     *
     * @throws TokenizerException
     */
    public void testSingleQuoteDelimitedString() throws TokenizerException {
        tokenizer.tokenize( "'This is a single token'" );
        assertNotNull( tokenizer.nextToken() );
        assertNull( tokenizer.nextToken() );
    }

    /**
     * Tests double quote test delimitation.
     *
     * @throws TokenizerException
     */
    public void testDoubleQuoteDelimitedString() throws TokenizerException {
        tokenizer.tokenize( "\"This is a single token\"" );
        assertNotNull( tokenizer.nextToken() );
        assertNull( tokenizer.nextToken() );
    }

    public void testTokenSeparators() throws TokenizerException {
        Tokenizer.TokenSeparator[] separators = new Tokenizer.TokenSeparator[]{
                new Tokenizer.TokenSeparator(' '),
                new Tokenizer.TokenSeparator(','),
                new Tokenizer.TokenSeparator(';'),
                new Tokenizer.TokenSeparator('*', true)
        };
        Tokenizer tkz = new TokenizerImpl( separators );

        String input = "*0 1 2,3;4*5 ** 'a b * c d' 6 ,, 7 ;;\"; 10 * 9 * 8 *\"; 8 ,;9 10*";
        tkz.tokenize( input );

        int numberCounter = 0, starCounter = 0, stringCounter = 0;
        Token token;
        while( (token = tkz.nextToken()) != null) {
            String value = token.getValue();
            System.out.println("value : " + value);
            if( value.equals("*") ) {

                starCounter++;

            } else if(value.equals("'a b * c d'") || value.equals("\"; 10 * 9 * 8 *\"") ) {

                stringCounter++;

            } else {

                assertEquals("Invalid token.", numberCounter++, Integer.parseInt( token.getValue() ) );

            }
        }
        assertEquals("Invalid number of tokens."            , 11, numberCounter);
        assertEquals("Invalid number of passthrough tokens.", 5, starCounter   );
        assertEquals("Invalid number of string tokens."     , 2, stringCounter );
    }

    /**
     * Tests that the ending of the stream without separator is
     * recognized correctly.
     *
     * @throws TokenizerException
     */
    public void testTokenizeWithEndStream() throws TokenizerException {
        // Creates input.
        String input = "0 1 2 3 4 5 6 7 8 9 10";
        tokenizer.tokenize( input );

        int counter = 0;
        Token token;
        while( (token = tokenizer.nextToken()) != null) {
            assertEquals("Invalid token.", counter++, Integer.parseInt( token.getValue() ) );
        }
        assertEquals("Invalid number of tokens.", 11, counter);
    }

    /**
     * Tests the tokenization without backtracking.
     *
     * @throws TokenizerException
     */
    public void testTokenize() throws TokenizerException {
        assertEquals("Unespected status.", Tokenizer.Status.WAITING, tokenizer.getStatus());

        tokenizer.tokenize( createInput() );
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());

        Token token;
        int t = 0;
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());
        token = tokenizer.nextToken();
        assertEquals("Unespected status.", Tokenizer.Status.PARSING, tokenizer.getStatus());
        assertEquals( "Invalid token value", TOKEN_STRINGS[t++], token.getValue() );
        while( ( token = tokenizer.nextToken() ) != null ) {
            assertEquals( "Invalid token value", TOKEN_STRINGS[t++], token.getValue() );
        }
        assertEquals("Unespected status.", Tokenizer.Status.DONE, tokenizer.getStatus());
    }

    /**
     * Tests tokenization with backtracking sections.
     *
     * @throws TokenizerException
     */
    public void testTokenizeWithBacktracking() throws TokenizerException {
        assertEquals("Unespected status.", Tokenizer.Status.WAITING, tokenizer.getStatus());

        tokenizer.tokenize( createInput() );
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );
        assertEquals("Unespected status.", Tokenizer.Status.PARSING, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");

        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");

        assertEquals("Unespected status.", Tokenizer.Status.PARSING, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");

        assertEquals("Invalid token", TOKEN_STRINGS[4], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[5], getNextTokenValue() );

        tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");

        assertEquals("Invalid token", TOKEN_STRINGS[4], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[5], getNextTokenValue() );

        tokenizer.nextToken();
        assertEquals("Unespected status.", Tokenizer.Status.DONE, tokenizer.getStatus());
    }

    /**
     * Tests tokenization with backtracking at begin.
     * 
     * @throws TokenizerException
     */
    public void testWithBacktrackingAtBegin() throws TokenizerException {
        assertEquals("Unespected status.", Tokenizer.Status.WAITING, tokenizer.getStatus());

        tokenizer.tokenize( createInput() );
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[4], getNextTokenValue() );

        tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.PARSING, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[4], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[5], getNextTokenValue() );

        tokenizer.nextToken();
        assertEquals("Unespected status.", Tokenizer.Status.DONE, tokenizer.getStatus());

        assertNull("Expected null value.", getNextTokenValue() );

    }

    /**
     * Tests a scenario with nested transactions.
     */
    public void testNestedTransactions() throws TokenizerException {
        assertEquals("Unespected status.", Tokenizer.Status.WAITING, tokenizer.getStatus());

        tokenizer.tokenize( createInput() );
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );

         tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[3], getNextTokenValue() );

         tokenizer.consumeBacktrack();
        System.out.println("Consume backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[4], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[5], getNextTokenValue() );

        tokenizer.consumeBacktrack();
        System.out.println("Consume backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.DONE, tokenizer.getStatus());

        tokenizer.nextToken();
        assertEquals("Unespected status.", Tokenizer.Status.DONE, tokenizer.getStatus());
    }

    public void testCleanupAfterConsume() throws TokenizerException {
        assertEquals("Unespected status.", Tokenizer.Status.WAITING, tokenizer.getStatus());

        tokenizer.tokenize( createInput() );
        assertEquals("Unespected status.", Tokenizer.Status.READY, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );

        tokenizer.consumeBacktrack();
        System.out.println("Consume backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.beginBacktrack();
        System.out.println("Begin backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.TRANSACTION, tokenizer.getStatus());

        tokenizer.rejectBacktrack();
        System.out.println("Reject backtrack");
        assertEquals("Unespected status.", Tokenizer.Status.PARSING, tokenizer.getStatus());

        assertEquals("Invalid token", TOKEN_STRINGS[0], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[1], getNextTokenValue() );
        assertEquals("Invalid token", TOKEN_STRINGS[2], getNextTokenValue() );

    }

    /**
     * Retrieves the next token to the tokenizer and prints it to <i>System.out</i>.
     * @return
     * @throws TokenizerException
     */
    private String getNextTokenValue() throws TokenizerException {
        Token token = tokenizer.nextToken();
        System.out.println( token != null ? token : "null" );
        return token != null ? token.getValue() : null;
    }

    /**
     * Creates the input string by concatenating TOKEN_STRINGS is a single string.
     * @return
     */
    private String createInput() {
        StringBuilder input = new StringBuilder();
        int i = 0;
        for(String ts: TOKEN_STRINGS) {
            input.append(ts).append( i++ % 2 == 0 ? ' ' : '\n' );
        }
        return input.toString();
    }
}
