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
 * Implements a condition where a specific node name is found.
 */
public class NodeNameCondition implements Condition {

    /**
     * Node name found.
     */
    private String nodeName;

    /**
     * Constructor.
     * 
     * @param nn
     */
    public NodeNameCondition(String nn) {
        if(nn == null) {
            throw new NullPointerException();
        }
        nodeName = nn;
    }

    public boolean matches(NodeContext nc) {
        return nodeName.equals( nc.getNodeName() );
    }

    @Override
    public String toString() {
        return "NodeNameCondition{" + nodeName + "}"; 
    }
}
