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
import com.asemantics.mashup.processor.*;

import java.util.List;

/**
 * Defines a <i>predicate</i> non terminal.
 */
public class MUPredicate extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public MUPredicate(String name) {
        super(name);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // PredicateName ( )
        if( childrenResults.length == 3 ) {
            String predicateName = (String) childrenResults[0];
            return new InvokeOperation( predicateName, new Argument[]{} );
        }

        // PredicateName ( ListOfTerms )
        if( childrenResults.length == 4 ) {
            String predicateName = (String) childrenResults[0];
            List           terms = (List  ) childrenResults[2];
            return processTerms( predicateName, terms );
        }

        // Declaration.
        assert childrenResults.length == 1;
        assert childrenResults[0] instanceof Operation;
        return childrenResults[0];
    }

    /**
     * Processes terms consiring eventual nested predicates.
     *
     * @param pn
     * @param terms
     * @return
     * @see com.asemantics.mashup.processor.EnqueueArgumentOperation
     * @see com.asemantics.mashup.processor.DequeueArgument
     */
    private Operation processTerms(String pn, List terms) {
        Argument[] arguments = new Argument[ terms.size() ];
        OperationsSequence sequence = null;
        int i = 0;
        for(Object term : terms) {
            if(term instanceof Argument) {
                arguments[i] = (Argument) term;
            } else {
                assert term instanceof Operation;
                arguments[i] = DequeueArgument.getInstance();
                if( sequence == null ) {
                    sequence = new OperationsSequence();
                }
                sequence.addOperations( ((Operation) term).getInnerOperations() );
                sequence.addOperation ( EnqueueArgumentOperation.getInstance()  );
            }
            i++;
        }
        if(sequence != null) {
            sequence.addOperation( new InvokeOperation(pn, arguments) );
            sequence.complete();
            return sequence;
        }
        return new InvokeOperation(pn, arguments);
    }
}
