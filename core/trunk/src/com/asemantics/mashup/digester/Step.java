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
 * Defines a step of {@link LocationPathImpl}.
 *
 * @see Condition ;
 * @see com.asemantics.mashup.digester.NodeContext
 */
public class Step {

    /**
     * Step identifier.
     */
    private String identifier;

    /**
     * Step test condition.
     */
    private Condition condition;

    /**
     * Constructor.
     *
     * @param id step identifier.
     * @param cond test object.
     */
    public Step(String id, Condition cond) {
        if( id == null ) {
            throw new IllegalArgumentException();
        }

        identifier     = id;
        this.condition = cond;
    }

    /**
     * Constructor without test.
     *
     * @param id
     */
    public Step(String id) {
        this(id, null);
    }

    /**
     * Returns the step identifier.
     *
     * @return identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the step condition.
     * 
     * @return condition to be applied to this step.
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Verifies if this step mactches the given node context.
     *
     * @param nc node contect to be verified.
     * @return <code>true</i> if matches, <code>false</code> otherwise.
     */
    public boolean matches(NodeContext nc) {
        return identifier.equals( nc.getNodeName() ) && ( condition == null || condition.matches( nc ) );
    }

    @Override
    public int hashCode() {
        return identifier.hashCode() * ( condition == null ? 1 : condition.hashCode() );
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof Step ) {
            Step other = (Step) obj;
            return identifier.equals( other.identifier ) && ( condition == null || condition.equals( other.condition ) );
        }
        return false;
    }

    @Override
    public String toString() {
        return "Step{ identifier:'" + identifier + ( condition == null ? "'" :  "', condition:" + condition.toString() ) + "}";
    }
}
