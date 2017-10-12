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
import com.asemantics.mashup.processor.AssignmentOperation;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.OperationsSequence;
import com.asemantics.mashup.processor.unification.ModelElement;

/**
 * Defines the <i>named_predicate</i> non terminal.
 */
public class MUNamedPredicate extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param s
     */
    public MUNamedPredicate(String s) {
        super(s);
    }

    /*
     * <named_predicate> ::= <un_grammar_root> '=' <predicate>
     */    
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 3;
        ModelElement model  = (ModelElement) childrenResults[0];
        Operation operation = (Operation) childrenResults[2];

        OperationsSequence os = new OperationsSequence();
        os.addOperation( operation );
        os.addOperation( new AssignmentOperation(model) );
        os.complete();
        return os;
    }

}
