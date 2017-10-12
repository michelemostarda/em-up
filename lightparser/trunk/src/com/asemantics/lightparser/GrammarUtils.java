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

import static com.asemantics.lightparser.Tokenizer.TokenSeparator;

import java.util.HashSet;
import java.util.Set;

/**
 * Class providing utility methods for grammar manipulation.
 */
public class GrammarUtils {

    /**
     * Merges a list of token separators is a safe way.
     *
     * @param tokenSeparatorsList list of token separators.
     * @return merget tokens.
     */
    public static TokenSeparator[] mergeTokenSeparators(TokenSeparator[] ... tokenSeparatorsList) {
        Set<TokenSeparator> result = new HashSet<TokenSeparator>();
        for(TokenSeparator[] tokenSeparators : tokenSeparatorsList) {
            for(TokenSeparator tokenSeparator : tokenSeparators) {
                TokenSeparator found = findToken(result, tokenSeparator);
                if( found != null) {
                    if(tokenSeparator.passthrough != found.passthrough ) {
                        throw new GrammarException("Error in merging token separators.");
                    }
                } else {
                    result.add(tokenSeparator);
                }
            }
        }
        return result.toArray( new TokenSeparator[ result.size() ] );
    }

    /**
     * find a token inside tokens list that is equal to <i>target</i> and returns it if found, <i>null</i>
     * otherwise.
     *
     * @param tokens
     * @param target
     * @return
     */
    private static TokenSeparator findToken( Set<TokenSeparator> tokens, TokenSeparator target) {
        for(TokenSeparator tokenSeparator : tokens) {
            if(tokenSeparator.equals(target)) {
                return tokenSeparator;
            }
        }
        return null;
    }
}
