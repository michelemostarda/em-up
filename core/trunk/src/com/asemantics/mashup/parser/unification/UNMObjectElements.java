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

import java.util.List;
import java.util.ArrayList;

/**
 * Defines the <i>m_object_elements</i> non terminal.
 */
public class UNMObjectElements extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param name
     */
    public UNMObjectElements(String name) {
        super(name);
    }

    /*
     * <m_object_elements> ::= <m_object_element> [,] <m_object_elements>
     *                         |
     *                         <m_object_element>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        if( childrenResults.length == 1 ) {
            List term = new ArrayList();
            term.add( childrenResults[0] );
            return term;
        }

        assert childrenResults.length == 2;
        List list = (ArrayList) childrenResults[1];
        list.add( 0, childrenResults[0] );
        return list;
    }

}
