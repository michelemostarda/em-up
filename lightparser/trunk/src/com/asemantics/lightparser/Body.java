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

import java.util.List;
import java.util.ArrayList;

/**
 * Defines the body of a production, as a list of alternatives.
 */
public class Body {

    /**
     * List of alternatives.
     */
    private List<List<Term>> alternatives;

    /**
     * Constructor.
     */
    protected Body() {
        alternatives = new ArrayList<List<Term>>();
        addAlternative();
    }

    /**
     * Adds a non terminal to the current alternative.
     *
     * @param nonTerminal
     * @return this body
     */
    public Body addNonTerminal(NonTerminal nonTerminal) {
        getPeekAlternative().add( nonTerminal );
        return this;
    }

    /**
     * Adds a defualt non terminal to the current alternative.
     *
     * @param nonTerminal
     * @return this body
     * @see com.asemantics.lightparser.DefaultNonTerminal
     */
    public Body addNonTerminal(String nonTerminal) {
        addNonTerminal( new DefaultNonTerminal(nonTerminal) );
        return this;
    }

    /**
     * Adds a terminal to the current alternative.
     *
     * @param terminal
     * @return this body
     */
    public Body addTerminal(Terminal terminal) {
        getPeekAlternative().add(terminal );
        return this;
    }

    /**
     * Adds a default terminal term to the current alternative.
     *
     * @param terminal terminal expressed as string.
     * @return this body.
     * @see com.asemantics.lightparser.DefaultTerminal
     */
    public Body addTerminal(String terminal) {
        addTerminal( new DefaultTerminal(terminal) );
        return this;
    }

    /**
     *  Adds a default terminal term to the current alternative.
     *
     * @param terminal terminal expressed as character.
     * @return this body.
     */
    public Body addTerminal(char terminal) {
        return addTerminal( Character.toString(terminal) );
    }

    /**
     * Adds a new alternative.
     *
     * @return this body.
     */
    public Body addAlternative() {
        alternatives.add( new ArrayList<Term>() );

        return this;
    }

    /**
     * Gets the available terms, the first dimension are the available
     * alternatives, the second are terms of every alternative.
     *
     * @return productions as list of list of terms.
     */
    public Term[][] getTerms() {
        Term[][] result = new Term[alternatives.size()][];
        int i = 0;
        for( List<Term> alternative : alternatives ) {
            result[i++] = alternative.toArray( new Term[ alternative.size() ] );
        }
        return result;
    }

    /**
     * Returns the last added alternative.
     * 
     * @return alternative as list of terms.
     */
    protected List<Term> getPeekAlternative() {
        return alternatives.get( alternatives.size() - 1 );
    }
}
