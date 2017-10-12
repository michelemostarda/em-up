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


package com.asemantics.mashup.digester.syntax;

import com.asemantics.lightparser.DefaultNonTerminal;
import com.asemantics.lightparser.TreeNode;

import java.util.LinkedList;
import java.util.List;

/**
 * Defines a list of path element in defition of <i>XPath</i>.
 */
public class XPPathList extends DefaultNonTerminal {

    /**
     * Constructor.
     * 
     * @param name
     */
    public XPPathList(String name) {
        super(name);
    }

    /**
     * Expected:  <path_elem> '/' <path_list>
     *            |
     *            <path_elem>
     *
     * @param associatedNode
     * @param childrenResults
     * @return compiled list of XPAth elements.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {
        
        if( childrenResults.length == 1 ) {
            XPPathElem.PathElem pathElem = (XPPathElem.PathElem) childrenResults[0];
            List pathElems = new LinkedList();
            pathElems.add( pathElem );
            return pathElems;
        }

        assert childrenResults.length == 3;
        assert "/".equals(childrenResults[1]);
        XPPathElem.PathElem pathElem = (XPPathElem.PathElem) childrenResults[0];
        List result = (LinkedList) childrenResults[2];
        result.add(0, pathElem );
        return result;
    }
    
}
