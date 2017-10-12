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

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the <i>list_of_triples</i> non terminal.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MUListOfTriples.java 444 2009-06-29 16:16:42Z michelemostarda $
 */
public class MUListOfTriples extends DefaultNonTerminal {

    public MUListOfTriples(String name) {
        super(name);
    }


    /*
     * <list_of_triples> ::= <triple> ';' <list_of_triples>
     *                       |
     *                       <triple>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // <triple>
        if( childrenResults.length == 1 ) {
            List result = new ArrayList();
            result.add( childrenResults[0] );
            return result;
        }

        // <triple> ';' <list_of_triples>
        assert childrenResults.length == 3;
        Triple triple = (Triple) childrenResults[0];
        List<Triple> listOfTriples = (List<Triple>) childrenResults[2];
        listOfTriples.add(triple);
        return listOfTriples;
    }

}
