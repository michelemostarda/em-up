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


package com.asemantics.mashup.parser.jsonpath;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.jsonpath.Accessor;
import com.asemantics.mashup.processor.jsonpath.DefaultPathImpl;

import java.util.List;


/**
 * Defines the JSON Path root non terminal.
 */
public class JPRoot extends DefaultNonTerminal {

    public JPRoot(String name) {
        super(name);
    }

    /*
     * <jp_root> ::= <accessors_list>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 1;
        List<Accessor> accessors = (List<Accessor>)childrenResults[0];
        return new DefaultPathImpl(accessors);
    }
}
