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


package com.asemantics.mashup.parser.unification;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.ConstArgument;
import com.asemantics.mashup.processor.unification.ModelElement;

/**
 * Defines the <i>m_object_element</i> non terminal. 
 */
public class UNMObjectElement extends DefaultNonTerminal {

    /**
     * Object element utility class.
     */
    class ObjectElement {

        /** Object key. */
        String key;

        /** Object value. */
        ModelElement modelElement;

        /**
         * Constructor.
         * 
         * @param k
         * @param me
         */
        ObjectElement(String k, ModelElement me) {
            key = k;
            modelElement = me;
        }
    }

    /**
     * Constructor.
     * 
     * @param name
     */
    public UNMObjectElement(String name) {
        super(name);
    }

    /*
     * <m_object_element> ::= key ':' <m_element>
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        assert childrenResults.length == 3;
        ConstArgument constArgument = (ConstArgument) childrenResults[0];
        String key = constArgument.getValue().asString().getNativeValue();
        assert ":".equals( childrenResults[1] );
        ModelElement modelElement = (ModelElement) childrenResults[2];
        return new ObjectElement(key, modelElement);
    }

}
