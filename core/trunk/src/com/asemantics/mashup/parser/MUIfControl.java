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
import com.asemantics.mashup.processor.nativeops.IfFlowControl;

/**
 * Defines the <i>if_control</i> non terminal.
 */
public class MUIfControl extends DefaultNonTerminal {

    protected static final String IF = "if";

    /**
     * Constuctor.
     * 
     * @param name
     */
    public MUIfControl(String name) {
        super(name);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 5 || childrenResults.length == 6;
        assert IF.equals( childrenResults[0] );

        //  if ( condition [,] IfPredicate )
        if( childrenResults.length == 5 ) {
            return new IfFlowControl(
                    new Argument[] {
                            processConditionArgument( childrenResults[2] ),
                            new InvocationArgument( new InvocationValue( (InvokeOperation) childrenResults[3] ) )
                    }
            );
        }

        // if ( condition [,] IfPredicate [,] ElsePredicate )
        assert childrenResults.length == 6;
        return new IfFlowControl(
                    new Argument[] {
                            processConditionArgument( childrenResults[2] ),
                            processConditionArgument( childrenResults[3] ),
                            processConditionArgument( childrenResults[4] )
                    }
            );
    }

    private Argument processConditionArgument(Object o) {
        if( o instanceof InvokeOperation) {
            return  new InvocationArgument( new InvocationValue( (InvokeOperation) o) );
        }
        if( o instanceof OperationsSequence ) {
            return new OperationsSequenceArgument( (OperationsSequence) o );
        }
        return (Argument) o;
    }

}
