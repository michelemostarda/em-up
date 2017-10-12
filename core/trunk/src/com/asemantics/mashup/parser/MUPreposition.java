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
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.OperationsSequence;

import java.util.List;

/**
 * Defines the root node of the grammar i.e. the <i>preposition</i> non terminal.
 */
public class MUPreposition extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param s
     */
    public MUPreposition(String s) {
        super(s);
    }

    /////

    /*
     *   <preposition> ::= <predicate_declaration> ':' <preposition_body> ';'
     *                     |
     *                     <preposition_body> ';'
     */

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // <predicate_declaration> ':' <preposition_body> ';'
        if( childrenResults.length == 4 ) {
            MUPredicateDeclaration.PredicateDeclarationResult pdr =
                    (MUPredicateDeclaration.PredicateDeclarationResult) childrenResults[0];
            List<Operation> operations = (List<Operation>) childrenResults[2];
            return new Definition(
                    pdr.predicateName,
                    pdr.listOfModels,
                    pdr.varargsPresent,
                    createOperationsSequence(operations),
                    associatedNode
            );
        }

        // <preposition_body>
        assert childrenResults.length == 2;
        List<Operation> operations = (List<Operation>) childrenResults[0];
        return new Evaluation( createOperationsSequence(operations) ); 
    }

    private OperationsSequence createOperationsSequence(List<Operation> operations) {
        OperationsSequence operationsSequence = new OperationsSequence();
        for(Operation operation : operations) {
            operationsSequence.addOperations( operation.getInnerOperations() );
        }
        operationsSequence.complete();
        return operationsSequence;
    }

}
