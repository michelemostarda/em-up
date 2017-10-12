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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Defines a grammar as a collection of productions.
 */
public class Grammar {

    /**
     * Productions of the grammar.
     */
    private Map<String, Production> productions;

    /**
     * Root production.
     */
    private Production first;

    /**
     * Constructor.
     */
    public Grammar() {
        productions = new HashMap<String,Production>();
    }

    /**
     * Adds a new production to this grammar.
     *
     * @param head the non terminal head of new production.
     */
    public Production createProduction(NonTerminal head) {
        Production production = new Production(this, head);

        if(first == null) {
            first = production;
        }

        productions.put(production.getHead().getContent(), production);

        return production;
    }

    /**
     * Adds a new production to this grammar with default non terminal head.
     *
     * @param head
     * @return new created production.
     * @see com.asemantics.lightparser.DefaultNonTerminal
     */
    public Production createProduction(String head) {
        return createProduction( new DefaultNonTerminal(head) );
    }

    /**
     * Retursn the root production.
     *
     * @return root production.
     */
    public Production getRootProduction() {
        return first;
    }

    /**
     * Returns the production with given head.
     * 
     * @param head
     * @return production corrisponding to <i>head</i>
     * @throws GrammarException
     */
    public Production getProduction(String head) {
        Production result = productions.get(head);
        if(result == null) {
            throw new GrammarException("Cannot resolve head '" + head + "' in current grammar.");
        }
        return result;
    }

    /**
     * Merges a grammar with a sub grammar, verifying possible conflicts between production names.
     *
     * @param other the other grammar to be merged.
     */
    public void mergeWith(Grammar other) {
        Set<String> productionNames = productions.keySet();
        for(Map.Entry<String, Production> entry : other.productions.entrySet() ) {
            if(  productionNames.contains( entry.getKey() ) ) {
                continue;
            }
            productions.put( entry.getKey(), entry.getValue() );
        }
    }

}
