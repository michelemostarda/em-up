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

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.NumericValue;

/**
 * Defines the <i>vararg</i> non terminal.
 */
public class MUVararg extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public MUVararg(String name) {
        super(name);
    }

    /*
     * <vararg> ::= varargs number
     *              |
     *              varargs
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        if( childrenResults.length == 2 ) {
            int index = ((NumericValue) ((ConstArgument) childrenResults[1]).getValue()).integer();
            return new VarargArgument(index);
        }

        assert childrenResults.length == 1;
        return new VarargArgument();
    }
}
