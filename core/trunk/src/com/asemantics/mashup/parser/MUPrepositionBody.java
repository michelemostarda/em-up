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
import com.asemantics.mashup.processor.ArgumentOperation;
import com.asemantics.mashup.processor.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the <i>preposition_body</i> non terminal.
 */
public class MUPrepositionBody extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param s
     */
    public MUPrepositionBody(String s) {
        super(s);
    }

        /*
         * <preposition_body> ::= <preposition_element> and_connector <preposition_body>
         *                        |
         *                        <preposition_element> or_connector  <preposition_body>
         *                        |
         *                        <preposition_element> [,] <preposition_body>
         *                        |
         *                        <preposition_element>
         *                        |
         *                        <graph>
         *                        |
         *                        const
         *                        |
         *                        number
         *                        |
         *                        variable
         */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // <preposition_element> | const | number
        if( childrenResults.length == 1 ) {

            List predicate = new ArrayList();

            // <preposition_element>
            if( childrenResults[0] instanceof Operation ) {
                Operation operation = (Operation) childrenResults[0];
                predicate.add( operation );
                return predicate;
            }

            // <graph> | const | number | variable
            Object argument = childrenResults[0];
            assert argument instanceof Argument;
            ArgumentOperation argumentOperation = new ArgumentOperation( (Argument) argument );
            predicate.add(argumentOperation);
            return predicate;
        }

        // <preposition_element> [,] <preposition_body>
        if( childrenResults.length == 2 ) {
            Operation operation = (Operation) childrenResults[0];
            List result = (ArrayList) childrenResults[1];
            result.add(0, operation );
            return result;
        }

        // <preposition_element> and_connector <preposition_body>
        // <preposition_element> or_connector  <preposition_body>
        assert childrenResults.length == 3;
        Operation operation = (Operation) childrenResults[0];
        Operation connector = (Operation) childrenResults[1];
        List result = (ArrayList) childrenResults[2];
        result.add(0, connector );
        result.add(0, operation );
        return result;
    }

}
