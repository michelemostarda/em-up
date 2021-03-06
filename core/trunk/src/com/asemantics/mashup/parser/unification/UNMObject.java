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
import com.asemantics.mashup.processor.unification.JsonObjectModel;
import com.asemantics.mashup.processor.VariableArgument;

import java.util.List;

/**
 * Defines the <i>m_object</i> non terminal.
 */
public class UNMObject extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public UNMObject(String name) {
        super(name);
    }

    /*
     * <m_object> ::= '{' <m_object_elements> '|' variable '}'
     *                 |
     *                '{' <m_object_elements> '}'
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // '{' <m_object_elements> '}'
        if( childrenResults.length == 3 ) {
            List objectElements = (List) childrenResults[1];
            JsonObjectModel jsonObjectModel = new JsonObjectModel();
            UNMObjectElement.ObjectElement objectElement;
            for(Object o : objectElements) {
                objectElement = (UNMObjectElement.ObjectElement) o;
                jsonObjectModel.putElement(objectElement.key, objectElement.modelElement);
            }
            return jsonObjectModel;
        }

        // '{' <m_object_elements> '|' variable '}'
        assert childrenResults.length == 5;
        List objectElements = (List) childrenResults[1];
        JsonObjectModel jsonObjectModel = new JsonObjectModel();
        UNMObjectElement.ObjectElement objectElement;
        for(Object o : objectElements) {
            objectElement = (UNMObjectElement.ObjectElement) o;
            jsonObjectModel.putElement(objectElement.key, objectElement.modelElement);
        }
        VariableArgument variableArgument = (VariableArgument) childrenResults[3];
        jsonObjectModel.setRemainingVariable( variableArgument.getVarName() );
        return jsonObjectModel;
    }
    
}
