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
import com.asemantics.mashup.processor.Argument;
import com.asemantics.mashup.processor.AssignmentOperation;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.OperationsSequence;
import com.asemantics.mashup.processor.unification.ModelElement;

/**
 * Defines a non terminal <i>declaration</i>.
 */
public class MUDeclaration extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public MUDeclaration(String name) {
        super(name);
    }

    /*
     * <declaration> ::= <un_grammar_root> '=' <term>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 3;

        ModelElement model = (ModelElement) childrenResults[0];

        if( childrenResults[2] instanceof Operation) {
            OperationsSequence os = new OperationsSequence();
            os.addOperation( (Operation) childrenResults[2] );
            os.addOperation( new AssignmentOperation( model ) );
            os.complete();
            return os;
        } else {
            assert childrenResults[2] instanceof Argument;
            return new AssignmentOperation(model, (Argument) childrenResults[2]);
        }
    }

}
