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
import com.asemantics.mashup.processor.*;
import com.asemantics.mashup.processor.nativeops.ForFlowControl;

/**
 * Defines the <i>for_control</i> non terminal.
 */
public class MUForControl extends DefaultNonTerminal {

    /**
     * constructor.
     *
     * @param name
     */
    public MUForControl(String name) {
        super(name);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 6;
        return new ForFlowControl( new Argument[] {
                processConditionArgument( childrenResults[2] ),
                (VariableArgument) childrenResults[3],
                new InvocationArgument( new InvocationValue( (InvokeOperation) childrenResults[4] ) )
        } );
    }

    /**
     * Processes list argument.
     * @param o
     * @return
     */
    private Argument processConditionArgument(Object o) {
        if( o instanceof InvokeOperation) {
            return  new InvocationArgument( new InvocationValue( (InvokeOperation) o) );
        }
        assert o instanceof Argument;
        return (Argument) o;
    }

}
