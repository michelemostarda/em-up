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


package com.asemantics.mashup.digester.syntax;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.digester.ComparisonCondition;

public class XPOperator extends DefaultNonTerminal {

    public XPOperator(String name) {
        super(name);
    }

    /**
     * Expected: '<''='  '>''='  '!''='
     *
     * @param associatedNode
     * @param childrenResults
     * @return compiled operator.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        String token0 = (String) childrenResults[0];

        if( childrenResults.length == 2 ) {

            assert "=".equals( childrenResults[1] );

            if( "<".equals(token0)  ) {
                return ComparisonCondition.Operator.MINUS_EQUAL;
            }
            if( ">".equals(token0) ) {
                return ComparisonCondition.Operator.MAJOR_EQUAL;
            }
            assert "!".equals(token0);
            return ComparisonCondition.Operator.NOT_EQUAL;
        }

        assert childrenResults.length == 1;
        if( "<".equals(token0) ) {
            return ComparisonCondition.Operator.MINUS;
        }
        if( ">".equals(token0) ) {
            return ComparisonCondition.Operator.MAJOR;
        }
        assert"=".equals(token0);
        return ComparisonCondition.Operator.EQUAL;

    }

}
