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
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.NumericValue;

/**
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: $
 */
public class MUNumber extends DefaultNonTerminal {

    public MUNumber(String name) {
        super(name);
    }

    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        Double value;
        if( childrenResults.length == 2) { // [+ / -] numeric.
            boolean negative = childrenResults[0] == "-";
            Double d = (Double) childrenResults[1];
            value = negative ? -d : d;
        } else { // numeric.
            assert childrenResults.length == 1 : "Unespected number of children.";
            value = (Double) childrenResults[0];
        }
        return new ConstArgument( new NumericValue(value) );
    }
}
