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

import static com.asemantics.lightparser.Tokenizer.TokenSeparator;

/**
 * Class containing tests for {@link com.asemantics.lightparser.GrammarUtils}.
 */

public class GrammarUtilsTest extends TestCase{

    private final TokenSeparator[] TOKENS_SET_A = new TokenSeparator[] {
            new TokenSeparator('1', false),
            new TokenSeparator('2', false),
            new TokenSeparator('3', false)
    };

    private final TokenSeparator[] TOKENS_SET_B = new TokenSeparator[] {
            new TokenSeparator('3', false),
            new TokenSeparator('4', false),
            new TokenSeparator('5', false)
    };

    private final TokenSeparator[] TOKENS_SET_NOT_COMPATIBLE_WITH_A = new TokenSeparator[] {
            new TokenSeparator('3', true ),
            new TokenSeparator('4', false),
            new TokenSeparator('5', false)
    };


    public void testPositiveMergeTokenSeparators() {
        TokenSeparator[] result = GrammarUtils.mergeTokenSeparators(TOKENS_SET_A, TOKENS_SET_B);
        assertEquals("Unespected size", 5, result.length);
    }

    public void testNegativeMergeTokenSeparators() {
        try {
            GrammarUtils.mergeTokenSeparators(TOKENS_SET_A, TOKENS_SET_NOT_COMPATIBLE_WITH_A);
            fail("Expected exception here.");
        } catch (GrammarException ge) {
            // OK.
        }
    }
}
