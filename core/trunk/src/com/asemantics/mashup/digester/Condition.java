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


package com.asemantics.mashup.digester;

/**
 * Defines a condition to be matched on a node.
 *
 * @see com.asemantics.mashup.digester.NodeContext
 */
public interface Condition {

    /**
     * Verifies that this condition matches with node context.
     *
     * @param nc current node context
     * @return <code>true</code> if match found, <code>false</code> otherwise.
     */
    public boolean matches(NodeContext nc);

}
