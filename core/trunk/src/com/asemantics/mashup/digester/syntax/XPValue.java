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


package com.asemantics.mashup.digester.syntax;

import com.asemantics.lightparser.DefaultTerminal;
import com.asemantics.lightparser.ParserException;
import com.asemantics.lightparser.Token;
import com.asemantics.lightparser.TreeNode;

/**
 * Defines a value of a condition in a filtered node.
 *
 * @see com.asemantics.mashup.digester.syntax.XPCondition
 */
public class XPValue extends DefaultTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public XPValue(String name) {
        super(name);
    }

    @Override
    public TreeNode satisfied(Token token) throws ParserException {
        String value = token.getValue();
        if( value.length() == 0 ) {
            throw new ParserException("Invalid value: '" + value  + "'");
        }
        return new TreeNode(value, this);
    }

    @Override
     public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
         assert childrenResults.length == 0;
         return associatedNode.getName();
     }


}
