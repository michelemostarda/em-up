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

/**
 * Defines a stream tokenizer with backtracking support.
 */
public interface Tokenizer {

    /**
     * Single quote string delimiter.
     */
    public static final char SINGLE_QUOTE_STRING_DELIMITER = '\'';

    /**
     * Double quote string delimiter.
     */
    public static final char DOUBLE_QUOTE_STRING_DELIMITER = '"';

    /**
     * Carriage return char.
     */
    public static final char CARRAGE_RETURN = '\n';

    /**
     * Token separator: space.
     */
    public static final TokenSeparator SPACE = new TokenSeparator(' ', false);

    /**
     * Token separator: carriage return.
     */
    public static final TokenSeparator LINE_SEPARATOR = new TokenSeparator('\n', false);

    /**
     * Default comment block.
     */
    public static final CommentBlock[] DEFAULT_COMMENT_BLOCKS = new CommentBlock[0];

    /**
     * Default token separators.
     */
    public static final TokenSeparator[] DEFAULT_TOKEN_SEPARATORS = new TokenSeparator[] { SPACE, LINE_SEPARATOR };

    /**
     * Enables single quote.
     *
     * @param b
     */
    void setSingleQuoteEnabled(boolean b);

    /**
     *
     * @return single quote abilitation.
     */
    boolean isSingleQuoteEnabled();

    /**
     * Enables double quote.
     *
     * @param b
     */
    void setDoubleQuoteEnabled(boolean b);

    /**
     *
     * @return double quote abilitation.
     */
    boolean isDoubleQuoteEnabled();

    /**
     * The possible tokenizer statuses.
     */
    enum Status {
        WAITING,
        READY,
        PARSING,
        TRANSACTION,
        DONE
    }

    /**
     * Defines a comment block.
     */
    class CommentBlock {

        /**
         * Char beginning comment block.
         */
        public final char beginChar;

        /**
         * Char ending comment block.
         */
        public final char endChar;

        /**
         * Constructor.
         *
         * @param bc begin char.
         * @param ec end char.
         */
        public CommentBlock(char bc, char ec) {
            beginChar = bc;
            endChar   = ec;
        }

    }

    /**
     * Defines a token separator char.
     */
    class TokenSeparator {

        /**
         * Char representing a separator.
         */
        public final char separator;

        /**
         * Passthrough condition.
         */
        public final boolean passthrough;

        /**
         * Constructor.
         *
         * @param s
         * @param p
         */
        public TokenSeparator(char s, boolean p) {
            separator   = s;
            passthrough = p;
        }

        /**
         * Constructor.
         * 
         * @param s
         */
        public TokenSeparator(char s) {
            this(s, false);
        }

        /**
         * Two token separators are equal if they represent the same character.
         * @param obj
         * @return equality condition.
         */
        @Override
        public boolean equals(Object obj) {
            if( obj == null ) {
                return false;
            }
            if( obj == this ) {
                return true;
            }
            if( obj instanceof TokenSeparator) {
                TokenSeparator other = (TokenSeparator) obj;
                return separator == other.separator;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return separator;
        }
    }

    /**
     * Defines supported comment blocks.
     *
     * @param commentBlocks list of comment blocks.
     */
    void setCommentBlocks(CommentBlock[] commentBlocks);

    /**
     * Defines the token separators list.
     *
     * @param separators
     * @throws TokenizerException if invoked after tokenizer has started.
     */
    void setTokenSeparators(TokenSeparator[] separators) throws TokenizerException;

    /**
     * Returns the current tokenizer status.
     *
     * @return status identifier.
     */
    Status getStatus();

    /**
     * Sets the input to be tokenized.
     *
     * @param input string.
     * @throws TokenizerException
     */
    void tokenize(String input) throws TokenizerException;

    /**
     * Returns the submitted input string.
     *
     * @return input string.
     */
    String getInputString();

    /**
     * Returns the next token or <code>null</code> if the end of <i>input</i>
     * stream has been reached.
     *
     * @return next available token.
     * @throws TokenizerException
     */
    Token nextToken() throws TokenizerException;

    /**
     * Returns <code>true</code> if end of <i>input</i>
     * has been reached, <code>false</code> otherwise.
     * 
     * @return end reached status.
     */
    boolean endReached();

    /**
     * Begins the backtrack mode.
     *
     * @throws TokenizerException
     */
    void beginBacktrack() throws TokenizerException;

    /**
     * Returns <code>true</code> if backtracking mode is active,
     * <code>false</code> otherwise.
     *
     * @return backtracking status.
     */
    boolean isBacktracking();

    /**
     * Closes the backtracking mode by consuming tokens retrieved while this mode
     * has been active.
     *
     * @throws TokenizerException
     */
    void consumeBacktrack() throws TokenizerException;

    /**
     * Closes the backtracking mode by ripristinating tokens retieved while this mode
     * has been active.
     *
     * @throws TokenizerException
     */
    void rejectBacktrack() throws TokenizerException;

    /**
     * Closes the tokenizer.
     * 
     * @throws TokenizerException
     */
    void close();

}
