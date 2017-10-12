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

public class BacktrackingParserException extends ParserException {

    /**
     * Terminal unsatisfied by <i>errorToken</i>.
     */
    private Terminal unsatisfiedTerminal;

    /**
     * Token causing error.
     */
    private Token errorToken;

    /**
     * String that has been parsed.
     */
    private String parsedString;

   /**
     * Constructor used to trace backtracking.
     *
     * @param msg descriptive message of exception.
     * @param ut terminal being unsatisfied.
     * @param et token raising exception.
     */
    public BacktrackingParserException(String msg, Terminal ut, Token et) {
        super(msg);
       if(ut == null) {
           throw new IllegalArgumentException("ut cannot be null");
       }

        unsatisfiedTerminal = ut;
        errorToken          = et;
    }

    /**
     * Constructor used to trace backtracking with unexpected End Of String.
     *
     * @param msg descriptive message of exception.
     * @param ut terminal being unsatisfied.
     */
    public BacktrackingParserException(String msg, Terminal ut) {
        this(msg, ut, null);
    }

    /**
     * Constructor used to generate error report.
     *
     * @param msg descriptive messafe of exception.
     * @param ut terminal being unsatified.
     * @param et token raising exception.
     * @param ps parsed string.
     */
    public BacktrackingParserException(String msg, Terminal ut, Token et, String ps) {
        this( msg, ut, et );
        parsedString = ps;
    }

    /**
     * Returns the unsatisfied terminal.
     *
     * @return the terminal that was unsatisfied.
     */
    public Terminal getUnsatisfiedTerminal() {
        return unsatisfiedTerminal;
    }

    /**
     * Returns the token causing error.
     *
     * @return the errored token.
     */
    public Token getErrorToken() {
        return errorToken;
    }

    /**
     * Returns the parsed string.
     *
     * @return the parsed string raising exception.
     */
    public String getParsedString() {
        return parsedString;
    }

    @Override
    public String getMessage() {
        String errorLocation;
        if( errorToken == null ) {
            errorLocation = " Unexpected end of string. ";
        } else {
            errorLocation = " Error at location: " + errorToken.getBegin() + ". ";
        }
        String errorMessage = "Expected: '" + unsatisfiedTerminal.getContent()  + "'.";
        return
            super.getMessage() + errorLocation + errorMessage;
    }
}
