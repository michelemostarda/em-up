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
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.ArgumentOperation;

/**
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MUTriple.java 444 2009-06-29 16:16:42Z michelemostarda $
 */
public class MUTriple extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public MUTriple(String name) {
        super(name);
    }

    /*
     * <triple> ::= <term> [,] <term> [,] <term>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 3 : "Unespected number of children.";
        return new Triple(
            getOperation( childrenResults[0] ),
            getOperation( childrenResults[1] ),
            getOperation( childrenResults[2] )
        );
    }


    private Operation getOperation(Object child) {
      return child instanceof Operation ? (Operation) child : new ArgumentOperation( (Argument) child ); 
    }
}
