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
import com.asemantics.mashup.processor.OrConnectorOperation;

public class MUOrConnector extends DefaultTerminal {

    /**
      * Comma costant.
      */
     protected static final String OR = "|";

     /**
      * Constructor.
      *
      * @param name connector name.
      */
     public MUOrConnector(String name) {
         super(name);
     }

     @Override
     public TreeNode satisfied(Token token) throws ParserException {
         String value = token.getValue();
         if( OR.equals(value) ) {
             return new TreeNode(value, this);
         } else {
             throw new BacktrackingParserException("token value is not OR.", this, token);
         }
     }

     @Override
     public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
         assert childrenResults.length == 0;
         return new OrConnectorOperation();
     }

}
