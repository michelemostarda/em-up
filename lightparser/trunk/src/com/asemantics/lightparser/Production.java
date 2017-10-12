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
 * Represents a {@link com.asemantics.lightparser.Grammar} production.
 */
public class Production {

    /**
     * The grammar to wich this production belongs.
     */
    private Grammar grammar;

    /**
     * Non Terminal head of this production.
     */
    private NonTerminal head;

    /**
     * Production body.
     */
    private Body body;

    /**
     * Constructor.
     *
     * @param g
     */
    protected Production(Grammar g, NonTerminal h) {
        grammar = g;
        body    = new Body();
        setHead(h);
    }

    /**
     * Sets the non terminal head of this production.
     * @param h
     * @return this production.
     */
    public Production setHead(NonTerminal h) {
        if( head != null ) {
            throw new GrammarException("Cannot set production head twice");
        }
        head = h;

        return this;
    }

    /**
     * Returns the production head.
     *
     * @return head of this production.
     */
    public NonTerminal getHead() {
        return head;
    }

    /**
     * Returns the production body.
     *
     * @return  body of this production.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Performs the parsing the of the current production.
     * 
     * @param productionStack
     * @param notifier
     * @param tokenizer
     * @param parentNode
     * @param parentNonTerminal
     * @throws ParserException
     */
    protected void parse(
            ProductionStack productionStack,
            Parser.ParserListenerNotifier notifier,
            Tokenizer tokenizer,
            TreeNode parentNode,
            NonTerminal parentNonTerminal
    ) throws ParserException {

        // Tree node of current parsing.
        TreeNode currentNode = new TreeNode( getHead().getContent(), getHead() );

        Token token          = null;
        int alternativeIndex = 0;

        Token mostAheadToken = null;
        Terminal unsatisfiedTerminal = null;

        Term[][] alternatives = getBody().getTerms();
        for( int alternativesIndex = 0; alternativesIndex < alternatives.length; alternativesIndex++  ) {
            Term[] alternative = alternatives[alternativesIndex];

            /*
              If during recursion end of token stream has been reached
              we need to move backward.
             */
            if( tokenizer.endReached() ) {
                throw new ParserException("End of stream has been reached.");
            }

            // Begin backtrack.
            try {
                tokenizer.beginBacktrack();
            } catch (TokenizerException te) {
                throw new RuntimeException("Error in beginning backtrack", te);
            }
            notifier.notifyBacktrackingStarted(productionStack);

            try {

                // Check every term of the alternative.
                for( Term term : alternative ) {

                    if( term instanceof Terminal ) { // Process terminal.

                        // Read next token.
                        try {
                            token = tokenizer.nextToken();
                        } catch (TokenizerException te) {
                            throw new ParserException("Error in reading token", te);
                        }
                        notifier.notifyNextTokenAsked(token);

                        if( token == null ) {
                            throw new BacktrackingParserException("Unexpected end of string", (Terminal) term);
                        }

                        final Terminal terminal = (Terminal) term;
                        TreeNode child = terminal.satisfied(token);
                        currentNode.addChild(child);
                        // Notifies non terminal parent that a terminal child has been added.
                        if(parentNonTerminal != null) {
                            parentNonTerminal.addTerminal(terminal);
                        }

                    } else if( term instanceof NonTerminal ) { // Process non terminal.

                        final NonTerminal nonTerminal = (NonTerminal) term;

                        // Recoursive step.
                        Production production = grammar.getProduction( term.getContent() );

                        // Listener handling.
                        productionStack.pushRecursion(production, alternative, alternativeIndex);
                        notifier.notifyProductionStackIn(productionStack);

                        nonTerminal.beginEvaluation();

                        try {
                            production.parse(productionStack, notifier, tokenizer, currentNode, nonTerminal);
                        } finally {
                            // Listener handling.
                            productionStack.popRecursion();
                            notifier.notifyProductionStackOut(productionStack);

                            nonTerminal.endEvaluation();                            
                        }

                        // Notifies non terminal parent that a non terminal has been added. 
                        if(parentNonTerminal != null) {
                            parentNonTerminal.addNonTerminal(nonTerminal);
                        }

                    } else {
                        throw new IllegalStateException();
                    }

                }

             } catch (ParserException pe) { // Error while processing alternative.

                // Reject backtracking.
                try {
                    tokenizer.rejectBacktrack();
                } catch (TokenizerException te) {
                    throw new RuntimeException("Error while rejecting backtrack.", te);
                }
                notifier.notifyBaktrackingRejected(productionStack);

                // Clear partial result.
                currentNode.clear();

                // Mantains bigger ahead token reached.
                if(  pe instanceof BacktrackingParserException) {
                    BacktrackingParserException bpe = (BacktrackingParserException) pe;
                    Token errorToken = bpe.getErrorToken();
                    if(
                            mostAheadToken == null
                                    ||
                           ( mostAheadToken != null && errorToken != null && errorToken.compareTo( mostAheadToken ) > 0 ) 
                    ) {
                        mostAheadToken      = errorToken;
                        unsatisfiedTerminal = bpe.getUnsatisfiedTerminal();
                    }
                }

                alternativeIndex++;
                continue; // Go to next alternative.
            }

            // Consuming backtracking.
            try {
                tokenizer.consumeBacktrack();
            } catch (TokenizerException te) {
                throw new ParserException("Error while consuming backtrack", te);
            }
            notifier.notifyBaktrackingConsumed(productionStack);

            // Copying children in parentNode.
            parentNode.addChild( currentNode );

            return;
        }

        // Cannot satisfy the current production.
        throw new BacktrackingParserException(
                "Cannot satisfy production.", unsatisfiedTerminal, mostAheadToken, tokenizer.getInputString()
        );

    }

    public String toString() {
        return "Production: " + head;
    }
}
