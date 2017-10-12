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
 * Defines the default terminal implemenation.
 */
public class DefaultTerminal extends Terminal {

    /**
     * Constructor.
     * 
     * @param content
     */
    public DefaultTerminal(String content) {
        super(content);
    }

    public String getSimpleName() {
        return "DefaultTerminal";
    }

    public TreeNode satisfied(Token token) throws ParserException {

        if( ! getContent().equals( token.getValue() ) ) {
            throw new BacktrackingParserException("Unexpected token: '" + token + "'.", this, token);
        }
        return new TreeNode(token.getValue(), this, token.getLocation() );
        
    }

    public void precompile() {
        // Empty.
    }

    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 0;
        return getContent();
    }
}
