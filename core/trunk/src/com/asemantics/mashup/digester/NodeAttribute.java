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
 * Defines an <i>SGML</i> node attribute.
 */
class NodeAttribute {

    /**
     * Attribute name.
     */
    protected String name;

    /**
     * Attribute value.
     */
    protected String value;

    /**
     * Constructor.
     *
     * @param n
     * @param v
     */
    NodeAttribute(String n, String v) {
        if( n == null ) {
            throw new NullPointerException();
        }
        name  = n;
        value = v;
    }

    /**
     * Constructor. Name without value.
     *
     * @param n
     */
    NodeAttribute(String n) {
        this(n, null);
    }

    /**
     * @return attribute name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return attribute value.
     */
    public String getValue() {
        return value;
    }

    public int hashCode() {
        return name.hashCode() * ( value == null ? 1 : value.hashCode() );
    }

    public boolean equals(Object obj) {
        if( obj == null) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof NodeAttribute ) {
            NodeAttribute other = (NodeAttribute) obj;
            return name.equals( other.name ) && ( value == null || value.equals( other.value ) );
        }
        return false;
    }

    public String toString() {
        return "{ name: " + name + (value != null ? ", value: " + value : "") + " }";
    }
}
