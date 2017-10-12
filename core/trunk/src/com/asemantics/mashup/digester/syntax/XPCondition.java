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
import com.asemantics.mashup.digester.IndexCondition;

/**
 * Defines a condition filter for a node.
 */
public class XPCondition extends DefaultNonTerminal {

    /**
     * Constrctor.
     * 
     * @param name
     */
    public XPCondition(String name) {
        super(name);
    }

    /**
     * Expected: <identifier> <operator> value | value
     *
     * @param associatedNode
     * @param childrenResults
     * @return compiled object.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        if( childrenResults.length == 3 ) {
            ComparisonCondition.Identifier identifier = (ComparisonCondition.Identifier) childrenResults[0];
            ComparisonCondition.Operator operator     = (ComparisonCondition.Operator) childrenResults[1];
            String value                              = (String) childrenResults[2];
            return new ComparisonCondition(identifier, operator, value);
        }

        assert childrenResults.length == 1;
        int index = (Integer) childrenResults[0];
        return new IndexCondition(index);
    }

}
