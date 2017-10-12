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


package com.asemantics.mashup.parser.jsonpath;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.VariableArgument;
import com.asemantics.mashup.processor.jsonpath.ObjectAccessor;

/**
 * Defines the <i>object_accessor</i> non terminal.
 */
public class JPObjectAccessor extends DefaultNonTerminal {

    public JPObjectAccessor(String name) {
        super(name);
    }

    /*
     * <object_accessor> ::= '.' variable
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 2;
        VariableArgument variable = (VariableArgument) childrenResults[1];
        return new ObjectAccessor( variable.getVarName() );
    }

}
