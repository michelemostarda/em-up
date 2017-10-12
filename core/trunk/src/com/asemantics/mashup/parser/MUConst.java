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
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.StringValue;

/**
 * Defines the <i>constant</i> terminal.
 */
public class MUConst extends DefaultTerminal {

    /**
     * constructor.
     *
     * @param name
     */
    public MUConst(String name) {
        super(name);
    }

    @Override
    public TreeNode satisfied(Token token) throws ParserException {
        String constValue = token.getValue();
        char charFirst = constValue.charAt(0);
        char charLast  = constValue.charAt( constValue.length() - 1 );
        if(
            ( charFirst == Tokenizer.SINGLE_QUOTE_STRING_DELIMITER && charLast == Tokenizer.SINGLE_QUOTE_STRING_DELIMITER )
            ||
            ( charFirst == Tokenizer.DOUBLE_QUOTE_STRING_DELIMITER && charLast == Tokenizer.DOUBLE_QUOTE_STRING_DELIMITER )
         ) {
            return new TreeNode(constValue.substring(1, constValue.length() - 1), this);
        }
        throw new BacktrackingParserException("token value '" + constValue + "' is not a valid const value.", this, token);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResult) {
        assert childrenResult.length == 0;
        return new ConstArgument( new StringValue( associatedNode.getName() ) );
    }

}
