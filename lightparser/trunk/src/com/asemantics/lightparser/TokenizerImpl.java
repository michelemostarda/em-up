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

import java.util.LinkedList;
import java.util.Stack;

/**
 * Defines an inplementation of the {@link com.asemantics.lightparser.Tokenizer}
 * interface.
 */
public class TokenizerImpl implements Tokenizer {

    /**
     * Debug mode.
     */
    private boolean DEBUG = false;

    /**
     * List of comment blocks.
     */
    private CommentBlock[] commentBlocks;

    /**
     * List of separators.
     */
    private TokenSeparator[] tokenSeparators;

    /**
     * Tokenizer status.
     */
    private Status status = Status.WAITING;

    /**
     * Tokenizer input.
     */
    private String inputString;

    /**
     * Current index in input string.
     */
    private int location;

    /**
     * List of tokens cumulated during transaction.
     */
    private LinkedList<Token> tokensQueue;

    /**
     * Stack of queue indexes used to implement nested
     * backtracking.
     */
    private Stack<Integer> queueIndexStack;

    /**
     * Token row.
     */
    private int row;

    /**
     * Token column.
     */
    private int col;

    /**
     * Buffer user to create words.
     */
    private StringBuilder wordBuffer;

    /**
     * Active comment block if any.
     */
    private CommentBlock activeCommentBlock = null;

    /**
     * Single quote open flag.
     */
    private boolean singleQuoteStringOpen = false;

    /**
     * Double quote open flag.
     */
    private boolean doubleQuoteStringOpen = false;

    /**
     * <code>true</i> if single quote is enabled.
     */
    private boolean singleQuoteEnabled = true;

    /**
     * <code>true</i> if double quote is enabled.
     */
    private boolean doubleQuoteEnabled = true;

    /**
     * The passthrough char.
     */
    private Character passthroughChar = null;

    /**
     * Constructor.
     *
     * @param cs list of token separators.
     */
    public TokenizerImpl(CommentBlock[] cb, TokenSeparator[] cs) {
        setCommentBlocks(cb);
        setTokenSeparators(cs);

        tokensQueue = new LinkedList<Token>();
        wordBuffer  = new StringBuilder();

        // Intialized queue index stack.
        queueIndexStack = new Stack<Integer>();
        queueIndexStack.push(0);
    }

    /**
     * Constructor. Specifies token separators.
     */
    public TokenizerImpl(TokenSeparator[] cs) {
        this( DEFAULT_COMMENT_BLOCKS, cs );
    }

    /**
     * Constructor.
     */
    public TokenizerImpl() {
        this( DEFAULT_COMMENT_BLOCKS, DEFAULT_TOKEN_SEPARATORS );
    }

    public void setCommentBlocks(CommentBlock[] blocks) {
        if(Status.PARSING.equals(status) || Status.TRANSACTION.equals(status)) {
            throw new IllegalStateException("Cannot change comment blocks during parsing.");
        }

        if(blocks == null) {
            throw new NullPointerException();
        }

        checkCommentBlocksIntegrity(blocks);

        commentBlocks = blocks;       
    }

    public void setTokenSeparators(TokenSeparator[] separators) {
        if(Status.PARSING.equals(status) || Status.TRANSACTION.equals(status)) {
            throw new IllegalStateException("Cannot change token separators during parsing.");
        }

        if(separators == null) {
            throw new NullPointerException();
        }
        if( separators.length == 0 ) {
            throw new IndexOutOfBoundsException("Expected at least a separator.");
        }

        checkTokensIntegrity(separators);

        tokenSeparators = separators;
    }

    public void setSingleQuoteEnabled(boolean b) {
        singleQuoteEnabled = b;
    }

    public boolean isSingleQuoteEnabled() {
        return singleQuoteEnabled;
    }

    public void setDoubleQuoteEnabled(boolean b) {
        doubleQuoteEnabled = b;
    }

    public boolean isDoubleQuoteEnabled() {
        return doubleQuoteEnabled;
    }

    public void tokenize(String input) throws TokenizerException {
        if(input == null) {
            throw new TokenizerException("input cannot be null");
        }

        if( Status.PARSING.equals(status) || Status.TRANSACTION.equals(status) ) {
            // TODO : deactivated bacause the reading of a passthrough char in not considered end of stream.
            //throw new TokenizerException("Cannot start another parsing now.");
        }
        status = Status.READY;

        activeCommentBlock = null; 
        singleQuoteStringOpen = doubleQuoteStringOpen = false;

        inputString = input;
        location    = 0;

        // Resets data structures.
        row = col = 0;

        tokensQueue.clear();

        queueIndexStack.clear();
        queueIndexStack.push(0);

        wordBuffer.delete( 0, wordBuffer.length() );

        passthroughChar = null;
    }

    public String getInputString() {
        return inputString;
    }

    public Token nextToken() throws TokenizerException {
        if(Status.DONE.equals(status) ) {
            return null;
        }
        if( Status.WAITING.equals(status) ) {
            throw new TokenizerException("Illegal status for invoking nextToken:" + status);
        }
        if( Status.READY.equals(status) ) {
            status = Status.PARSING;
        }

        if(DEBUG) {
            log("-------------------------");
            log("QUEUEINDEX: " + getCurrentIndex());
        }

        Token token;
        if( ! tokensQueue.isEmpty() && getCurrentIndex() < tokensQueue.size() ) {

            if(DEBUG) {
                log("QUEUE: " + getCurrentIndex());
            }

            return tokensQueue.get( incrementCurrentIndex() );

        } else {

            if(DEBUG) {
                log("EXTRACT with status: " + status);
            }

            token = extractToken();

            if(DEBUG) {
                log("TOKEN: " + token);
            }

            if( Status.TRANSACTION.equals(status) || Status.DONE.equals(status) ) {
                tokensQueue.addLast(token);
                incrementCurrentIndex();

                if(DEBUG) {
                    log("ADDLAST: "+ tokensQueue);
                }
            }
            return token;

        }
    }

    public boolean endReached() {
        return Status.DONE.equals(status);
    }

    public void beginBacktrack() throws TokenizerException {
        if(
                !Status.READY.equals(status)
                        &&
                ! Status.PARSING.equals(status)
                        &&
                !Status.TRANSACTION.equals(status)
        ) {
            throw new TokenizerException("Illegal status: '" + status + "'");
        }

        pushCurrentIndex();

        if(Status.TRANSACTION.equals(status)) {
            return;
        }

        removeAcceptedTokens( getCurrentIndex() );
        status = Status.TRANSACTION;
        setCurrentIndex(0);
    }

    public boolean isBacktracking() {
        return Status.TRANSACTION.equals(status);
    }

    public void consumeBacktrack() throws TokenizerException {
        if( !Status.TRANSACTION.equals(status) && !Status.DONE.equals(status)) {
            throw new TokenizerException("Illegal status: '" + status + "'");
        }

        if( popIndexBackward() ) {
            return;
        }

        if(status != Status.DONE) {
            status = Status.PARSING;
        }
        removeAcceptedTokens( getCurrentIndex() );
        setCurrentIndex(0);
    }

    public void rejectBacktrack() throws TokenizerException {
        if( !Status.TRANSACTION.equals(status) && !Status.DONE.equals(status) ) {
            throw new TokenizerException("Illegal status: '" + status + "'");
        }

        if( popCurrentIndex() ) {
            status = Status.TRANSACTION;
            return;
        }

        status = Status.PARSING;
        setCurrentIndex(0);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void close() {
        status = Status.DONE;
        inputString = null;
    }

    public Status getStatus() {
        return status;
    }

    private boolean pushCurrentIndex() {
        int current = queueIndexStack.peek();
        queueIndexStack.push(current);
        if(DEBUG) {
            log("PUSHED:" + queueIndexStack);
        }
        return queueIndexStack.size() > 1;
    }

    private boolean popIndexBackward() {
        int currentIndex = queueIndexStack.pop();
        setCurrentIndex(currentIndex);
        return queueIndexStack.size() > 1;
    }

    private boolean popCurrentIndex() {
        queueIndexStack.pop();
        return queueIndexStack.size() > 1;
    }

    private int getCurrentIndex() {
        return queueIndexStack.peek();
    }

    private int incrementCurrentIndex() {
        int current = queueIndexStack.pop();
        int newCurrent = current + 1;
        queueIndexStack.push(newCurrent);
        return current;
    }

    private void setCurrentIndex(int i) {
        queueIndexStack.pop();
        queueIndexStack.push(i);
    }

    /**
     * Extracts next noken from <i>input stream</i>.
     * @return
     * @throws TokenizerException
     */
    private Token extractToken() throws TokenizerException {

        // Handles passthrough character.
        if (passthroughChar != null) {
            String ptc = passthroughChar.toString();
            passthroughChar = null;
            return new Token(location - ptc.length(), row, col, ptc); // then passthrough char is emitted.
        }

        wordBuffer.delete(0, wordBuffer.length());
        char c;
        while ( location < inputString.length() ) {

            // Current char.
            c = inputString.charAt( location++ );

            // Handles carriage return.
            if( c == CARRAGE_RETURN ) {
                col = 0;
                row++;
            } else {
                col++;
            }

            // Handles comment blocks.
            if( activeCommentBlock != null && activeCommentBlock.endChar == c ) {
                activeCommentBlock = null;
                continue;
            }
            if(activeCommentBlock != null) {
                continue;
            }
            activeCommentBlock = commentBlockOpened(c);
            if( activeCommentBlock != null ) {
                continue;
            }

            // Handles string delimiters.
            if( singleQuoteEnabled && c == SINGLE_QUOTE_STRING_DELIMITER ) {
                singleQuoteStringOpen = ! singleQuoteStringOpen;
                wordBuffer.append(c);
                continue;
            }
            if( doubleQuoteEnabled && c == DOUBLE_QUOTE_STRING_DELIMITER ) {
                doubleQuoteStringOpen = ! doubleQuoteStringOpen;
                wordBuffer.append(c);
                continue;
            }
            if( singleQuoteStringOpen || doubleQuoteStringOpen ) {
                wordBuffer.append(c);
                continue;
            }

            // Handles token separators.
            TokenSeparator ts = findTokenSeparator(c);
            if ( ts != null ) { // Is token separator.
                if( ts.passthrough ) {

                    // There isn't a token before.
                    if(wordBuffer.length() == 0) {
                        return new Token( location - 1, row, col, Character.toString(c) );
                    } else {
                        passthroughChar = c; // Passthrough char armed.
                        break;
                    }

                } else if (wordBuffer.length() > 0) {
                    break;
                }
            } else {
                wordBuffer.append(c);
            }
        }

        // Check if EOS has been reached.
        if( location == inputString.length() && passthroughChar == null ) {

            status = Status.DONE;

            if( wordBuffer.length() == 0 ) {
                return null;
            }
        }

        return new Token( location - wordBuffer.length(), row, col, wordBuffer.toString() );

    }

    /**
     * Verifies if <i>c</i> opens a comment block.
     *
     * @param c char to be verified.
     * @return <code>true</code> if opens, <code>false</code> otherwise.
     */
    private CommentBlock commentBlockOpened(char c) {
        for(CommentBlock cb : commentBlocks) {
            if( cb.beginChar == c ) {
                return cb;
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> if <i>c</i> is a token separator,
     * <code>false</code> otherwise.
     *
     * @param c
     * @return
     */
    private TokenSeparator findTokenSeparator(char c) {
        for(TokenSeparator ts : tokenSeparators) {
            if( ts.separator == c ) {
                return ts;
            }
        }
        return null;
    }

    /**
     * Cleans the tokens queue from accepted tokens.
     *
     * @param queueIndex
     */
    private void removeAcceptedTokens(final int queueIndex) {
        for(int i = 0; i < queueIndex; i++) {
            if( tokensQueue.isEmpty() ) {
                return;
            }
            Object o = tokensQueue.removeFirst();
            if(DEBUG) {
                log("REMOVED FROM QUEUE: " + o);
            }
        }
    }

    /**
     * Verifies that comment blocks begin chars are all different.
     *
     * @param blocks blocks to check.
     */
    private void checkCommentBlocksIntegrity(CommentBlock[] blocks) {

        // Token separators all different.
        for(int i = 0; i < blocks.length; i++) {
            for(int j = 0; j < blocks.length; j++) {
                if( i == j ) { continue; };
                if( blocks[i].beginChar == blocks[j].beginChar ) {
                    throw new IllegalArgumentException(
                            "Duplicated begin chars: '" + blocks[i].beginChar + "' at indexes " + i + ", " + j + "."
                    );
                }
            }
        }
    }

    /**
     * Verifies that token separator chars are all different.
     *
     * @param separators separators to check.
     */
    private void checkTokensIntegrity(TokenSeparator[] separators) {

        // Token separators all different.
        for(int i = 0; i < separators.length; i++) {
            for(int j = 0; j < separators.length; j++) {
                if( i == j ) { continue; };
                if( separators[i].separator == separators[j].separator ) {
                    throw new IllegalArgumentException(
                            "Duplicated separator: '" + separators[i].separator + "' at indexes " + i + ", " + j + "."
                    );
                }
            }
        }
    }

    /**
     * Logs actions on system out for debugging purposes.
     *
     * @param s
     */
    private void log(String s) {
        if(DEBUG) {
            System.out.println(s);
        }
    }

}
