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


package com.asemantics.mashup.parser.unification;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.VariableArgument;
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.unification.JsonModel;
import com.asemantics.mashup.processor.unification.JsonModelElement;
import com.asemantics.mashup.processor.unification.VariableModelElement;
import com.asemantics.mashup.processor.unification.ConstantModelElement;

/**
 * Defines the <i>m_element</i> non terminal.
 */
public class UNMElement extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param name
     */
    public UNMElement(String name) {
        super(name);
    }

     /*
      * <m_element> ::= variable | const | number | <m_object> | <m_array>
      */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
         assert childrenResults.length == 1;

         // variable
         if(childrenResults[0] instanceof VariableArgument) {
             return new VariableModelElement( ((VariableArgument) childrenResults[0] ).getVarName() );
         }

         // const | number
         if(childrenResults[0] instanceof ConstArgument) {
             return new ConstantModelElement( ((ConstArgument) childrenResults[0]).getValue() );
         }

         // <m_object> | <m_array>
         return new JsonModelElement( (JsonModel) childrenResults[0] );
    }

}
