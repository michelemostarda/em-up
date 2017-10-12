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
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.jsonpath.Accessor;
import com.asemantics.mashup.processor.jsonpath.ArrayAccessor;
import com.asemantics.mashup.processor.jsonpath.StarArrayAccessor;

/**
 * Defines the <i>array_accessor</i> non terminal.
 */
public class JPArrayAccessor extends DefaultNonTerminal {

    public JPArrayAccessor(String name) {
        super(name);
    }

    /*
     * <array_accessor> ::= '[' '*' ']'
     *                       |
     *                      '[' number ']'
     *                       |
     *                       <slice_operator>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        if( childrenResults.length == 3 ) {
            if( childrenResults[1].equals(JPGrammarFactory.STAR_ACCESSOR_STR) ) {
                return new StarArrayAccessor();
            }
            ConstArgument constArgument = (ConstArgument) childrenResults[1];
            return new ArrayAccessor( constArgument.getValue().asNumeric().integer() );
        }
        assert childrenResults.length == 1;
        assert childrenResults[0] instanceof Accessor;
        return childrenResults[0];
    }
}
