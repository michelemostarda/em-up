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
import com.asemantics.mashup.digester.LocationPath;
import com.asemantics.mashup.digester.LocationPathImpl;

import java.util.List;

public class XPPath extends DefaultNonTerminal {

    public XPPath(String name) {
        super(name);
    }

    /**
     * Expected: '/' <path_list>  // absolute path.
     *           |
     *           <path_list>      // relative path.
     *
     * @param associatedNode
     * @param childrenResults
     * @return compiled XPath.
     */
    @Override
    public Object postcompile(TreeNode associatedNode, Object[] childrenResults) {

        if( childrenResults.length == 2 ) {
            assert "/".equals(childrenResults[0]);
            List pathElems = (List) childrenResults[1];
            return createLocationPath( false, pathElems );
        }

        assert childrenResults.length == 1;
        List pathElems = (List) childrenResults[0];
        return createLocationPath(true, pathElems);
    }

    /**
     * Creates a {@link com.asemantics.mashup.digester.LocationPath} on given list of path elements.
     *
     * @param relative
     * @param pathElems
     * @return
     */
    private LocationPath createLocationPath(boolean relative, List<XPPathElem.PathElem> pathElems) {
        LocationPathImpl result = new LocationPathImpl(relative);
        for( XPPathElem.PathElem pathElem : pathElems ) {
            result.addStep( pathElem.createStep() );
        }
        return result;
    }
}
