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
 * Defines the node context during every step of digestion.
 *
 * @see com.asemantics.mashup.digester.NodeAttribute
 */
public class NodeContext {

    /**
     * No attributes has been specified.
     */
    protected static final NodeAttribute[] NO_ATTRIBUTES = new NodeAttribute[0];

    /**
     * Node name.
     */
    private String nodeName;

    /**
     * Node attributes.
     */
    private NodeAttribute[] attributes;

    /**
     * Number of times this node head and same following sequence has been visited.
     */
    private int occurrences;

    /**
     * Constructor.
     *
     * @param n node name.
     * @param attrs  node attributes.
     */
    protected NodeContext(String n, NodeAttribute[] attrs ) {
        if( n == null ) {
            throw new IllegalArgumentException("Invalid nodeName name.");
        }
        if( attrs == null ) {
            throw new IllegalArgumentException("Invalid attributes");
        }

        // Asserts doesn't add node names with closure char.
        assert n.indexOf(Parser.CLOSE_NODE_STR) == -1;

        nodeName    = n;
        attributes  = attrs;
        occurrences = 1;
    }

    /**
     * Constructor with no attributes.
     *
     * @param n node name.
     */
    protected NodeContext(String n) {
        this(n, NO_ATTRIBUTES);
    }

    /**
     *
     * @return the context node.
     */
    protected String getNodeName() {
        return nodeName;
    }

    /**
     * Returns the value of an attribute.
     *
     * @param attrName name of attribute.
     * @return value of attribute or <code>null</code>.
     */
    protected String getValue(String attrName) {
        for(NodeAttribute attribute : attributes ) {
            if( attribute.name.equals( attrName) ) {
                return attribute.value;
            }
        }
        return null;
    }

    /**
     * Find an attribute name in this node context.
     *
     * @param attrName name to find.
     * @return <code>true</code> if found, <code>false</code>otherwise.
     */
    protected boolean containsAttribute(String attrName) {
        for(NodeAttribute attribute : attributes ) {
            if( attribute.name.equals( attrName) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns node attributes.
     *
     * @return list of attributes.
     */
    protected NodeAttribute[] getAttributes() {
        return attributes;
    }

    /**
     *
     * @return returns number of occurrences.
     */
    protected int getOccurrences() {
        return occurrences;
    }

    /**
     * Increments of <i>+ 1</i> the number of occurrences.
     */
    protected void incrementOccurrences() {
        occurrences++;
    }

    public String toString() {
        return "NodeContext { node: " + nodeName + ", occurrences: " + occurrences + "}"; 
    }
}
