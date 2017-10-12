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

import java.util.List;

/**
 * Defines a <i>predicate_declaration</i> non terminal.
 */
public class MUPredicateDeclaration extends DefaultNonTerminal {

    /**
     * Result of predicate_declaration compilation.
     */
    protected class PredicateDeclarationResult {

        /**
         * Predicate name.
         */
        protected String                predicateName;

        /**
         * List of arguments.
         */
        protected ModelElement[]        listOfModels;

        /**
         * Flag to define varargs.
         */
        protected boolean               varargsPresent;

        /**
         * Constructor.
         *
         * @param pn
         * @param mes
         */
        private PredicateDeclarationResult( String pn, ModelElement[] mes, boolean  vp) {
            predicateName  = pn;
            listOfModels   = mes;
            varargsPresent = vp;
        }
    }

    /**
     * Constructor.
     * 
     * @param s
     */
    public MUPredicateDeclaration(String s) {
        super(s);
    }

    /*
     *  <predicate_declaration> ::= predicate_name '(' <list_of_models> [,] varargs ')'  // List of formal parameters with varargs tail.
     *                             |
     *                             predicate_name '(' <list_of_models> ')'               // fixed list of formal parameters.
     *                             |
     *                             predicate_name '(' varargs ')'                        // Only varargs parameters.
     *                             |
     *                             predicate_name '(' ')'                                // No arguments allowed.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        String predicateName = (String) childrenResults[0];

        if(childrenResults.length == 5) { // predicate_name '(' <list_of_models> [,] varargs ')'
            assert childrenResults[3] instanceof MUVarargs : "Expected varargs here, found:" + childrenResults[4];
            List<ModelElement> listofModels = (List<ModelElement>) childrenResults[2];
            return new PredicateDeclarationResult( predicateName, listofModels.toArray( new ModelElement[ listofModels.size() ] ), true );
        }

        if( childrenResults.length == 4 ) {
            if( childrenResults[2] instanceof MUVarargs ) { // predicate_name '(' varargs ')'
                return new PredicateDeclarationResult( predicateName, new ModelElement[0], true );
            } else {  // predicate_name '(' <list_of_models> ')'
                List<ModelElement> listofModels = (List<ModelElement>) childrenResults[2];
                return new PredicateDeclarationResult( predicateName, listofModels.toArray( new ModelElement[ listofModels.size() ] ), false );
            }
        }
        // predicate_name '(' ')'
        assert childrenResults.length == 3;
        return new PredicateDeclarationResult( predicateName, new ModelElement[]{}, false );

    }


}
