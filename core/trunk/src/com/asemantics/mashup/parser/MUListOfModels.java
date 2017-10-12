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
import com.asemantics.mashup.processor.unification.ModelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the <i>list_of_vars</i> non terminal.
 */
public class MUListOfModels extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param s
     */
    public MUListOfModels(String s) {
        super(s);
    }

    /*
     * <list_of_models> ::= <un_grammar_root> <list_of_models>
     *                    |
     *                    <un_grammar_root>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        if( childrenResults.length == 1 ) {
            ModelElement model = (ModelElement) childrenResults[0];
            List term = new ArrayList();
            term.add( model );
            return term;
        }

        assert childrenResults.length == 2;
        ModelElement model = (ModelElement) childrenResults[0];
        List list = (ArrayList) childrenResults[1];
        list.add( 0, model );
        return list;
    }

}
