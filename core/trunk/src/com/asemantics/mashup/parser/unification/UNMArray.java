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
import com.asemantics.mashup.processor.VariableArgument;
import com.asemantics.mashup.processor.unification.JsonArrayModel;
import com.asemantics.mashup.processor.unification.ModelElement;

import java.util.List;

/**
 * Defines the <i>m_array</i> non terminal.
 */
public class UNMArray extends DefaultNonTerminal {

    /**
     * Constructor.
     *
     * @param name
     */
    public UNMArray(String name) {
        super(name);
    }

/*
         * <m_array> ::= '[' <m_array_elements> '|' variable ']'
         *                |
         *               '[' <m_array_elements> ']'
         *                |
         *               '[' ']'
         */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        // '[' ']'
        if( childrenResults.length == 2 ) {
            return new JsonArrayModel(); 
        }

        // '[' <m_array_elements> ']'
        if( childrenResults.length == 3 ) {
            List jsonModels = (List) childrenResults[1];
            JsonArrayModel jsonArrayModel = new JsonArrayModel();
            ModelElement modelElement;
            for(Object o : jsonModels) {
                modelElement = (ModelElement) o;
                jsonArrayModel.addElement(modelElement);
            }
            return jsonArrayModel;
        }

        // '[' <m_object_elements> '|' variable ']'
        assert childrenResults.length == 5;
        List jsonModels = (List) childrenResults[1];
        JsonArrayModel jsonArrayModel = new JsonArrayModel();
        ModelElement modelElement;
        for(Object o : jsonModels) {
            modelElement = (ModelElement) o;
            jsonArrayModel.addElement(modelElement);
        }
        VariableArgument variableArgument = (VariableArgument) childrenResults[3];
        jsonArrayModel.setRemainingVariable( variableArgument.getVarName() );
        return jsonArrayModel;
    }

}
