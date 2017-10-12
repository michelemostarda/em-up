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
import com.asemantics.mashup.processor.jsonpath.*;

/**
 * Defines the slice operator.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
public class JPSliceOperator extends DefaultNonTerminal {

    public JPSliceOperator(String name) {
        super(name);
    }

    /*
     * <slice_operator> ::= '[' number ':' number ':' number ']'
     *                       |
     *                      '[' number ':' number ']'
     *                       |
     *                      '[' number ':' ']'
     *                       |
     *                      '[' ':' number ']'
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        // '[' number ':' number ':' number ']'
        if(childrenResults.length == 7) {
            ConstArgument begin = (ConstArgument) childrenResults[1];
            ConstArgument end   = (ConstArgument) childrenResults[3];
            ConstArgument step  = (ConstArgument) childrenResults[5];
            return new ArrayAccessor(
                    begin.getValue().asNumeric().integer(),
                    end  .getValue().asNumeric().integer(),
                    step .getValue().asNumeric().integer() 
            );
        }

        // '[' number ':' number ']'
        if(childrenResults.length == 5) {
            ConstArgument begin = (ConstArgument) childrenResults[1];
            ConstArgument end   = (ConstArgument) childrenResults[3];
            return new ArrayAccessor(begin.getValue().asNumeric().integer(), end.getValue().asNumeric().integer(), 1 );
        }

        assert childrenResults.length == 4 : "Unespected children number.";
        //'[' number ':' ']'
        if( childrenResults[1] instanceof ConstArgument) {
            ConstArgument begin = (ConstArgument) childrenResults[1];
            return new RelativeArrayAccessor( begin.getValue().asNumeric().integer() );
        }
        //'[' ':' number ']'
        ConstArgument begin = (ConstArgument) childrenResults[2];
        return new ArrayAccessor(0, begin.getValue().asNumeric().integer(), 1);
    }
}
