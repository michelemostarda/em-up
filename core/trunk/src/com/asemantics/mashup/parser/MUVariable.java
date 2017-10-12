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


package com.asemantics.mashup.parser;

import com.asemantics.lightparser.*;
import com.asemantics.mashup.processor.VariableArgument;

/**
 * Defines the <i>variable</i> non terminal.
 */
public class MUVariable extends DefaultTerminal {

    /**
     * Constructor.
     * 
     * @param name
     */
    public MUVariable(String name) {
        super(name);
    }

    @Override
    public TreeNode satisfied(Token token) throws ParserException {
        String variable = token.getValue();
        char char0 = variable.charAt(0);
        if(Character.isLowerCase(char0) && Util.isValidIdentifierStart(char0) ) {
            return new TreeNode(variable, this);
        }
        throw new BacktrackingParserException("token value '" + variable + "' is not a valid identifier.", this, token);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 0;
        return new VariableArgument( associatedNode.getName() );
    }

}
