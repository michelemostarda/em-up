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
import com.asemantics.mashup.digester.Condition;
import com.asemantics.mashup.digester.Step;

/**
 * Defines a step in an <i>XPath</i>.
 */
public class XPPathElem extends DefaultNonTerminal {

    /**
     * Path element.
     */
    abstract class PathElem {

        /**
         * Node name.
         */
        String node;

        /**
         * Constructor.
         *
         * @param n
         */
        PathElem(String n)  {
            node = n;
        }

        /**
         * Creates a step compatible with this path element.
         * @return a Step inplementation.
         */
        abstract Step createStep();

    }

    /**
     * Defines a simple node without conditions.
     */
    class Node extends PathElem {

        /**
         * Constructor.
         *
         * @param n
         */
        Node(String n) {
            super(n);
        }

        Step createStep() {
            return new Step(node);
        }
    }

    /**
     * Defines a node with condition.
     */
    class FilteredNode extends PathElem {

        /**
         * Condition to be applied to filter this node.
         */
        Condition condition;

        /**
         * Constructor.
         *
         * @param n
         * @param c
         */
        FilteredNode(String n, Condition c) {
            super(n);
            condition = c;
        }

        Step createStep() {
            return new Step( node, condition);
        }
    }

    /**
     * Constructor.
     * 
     * @param name
     */
    public XPPathElem(String name) {
        super(name);
    }

    /**
     * Expected: node '[' <condition> ']'   // Filtered node
     *           |
     *           node                       // Node
     *
     * @param associatedNode
     * @param childrenResults
     * @return compiled XPath element.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        String node = (String) childrenResults[0];

        // Filtered node.
        if( childrenResults.length == 4 ) {
            assert "[".equals(childrenResults[1]);
            assert "]".equals(childrenResults[3]);
            Condition condition = (Condition) childrenResults[2];
            return new FilteredNode( node, condition );
        }

        // Node.
        assert childrenResults.length == 1;
        return new Node( node );
    }


}
