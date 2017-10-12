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

import static com.asemantics.mashup.digester.syntax.XPGrammarFactory.PATH_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a pattern that can be matched by the {@link Parser}.
 *
 * @see com.asemantics.mashup.digester.Step
 */
public class LocationPathImpl implements LocationPath {

    /**
     * Absolute / relative flag.
     */
    private boolean relative;

    /**
     * List of steps.
     */
    private List<Step> steps;

    /**
     * Constructor with specification of relativity.
     */
    public LocationPathImpl(boolean rel) {
        relative = rel;
        steps    = new ArrayList<Step>();
    }

    /**
     * Constructor for absolute paths.
     */
    public LocationPathImpl() {
        this(false);
    }

    /**
     * Adds a step to location path.
     *
     * @param s
     */
    public void addStep( Step s ) {
        steps.add(s);
    }

    public boolean isRelative() {
        return relative;
    }

    public Step[] getSteps() {
        return steps.toArray( new Step[ steps.size() ] );
    }

    public Step getStep(int i) {
        return steps.get(i);
    }

    /**
     * Removes all steps from path.
     */
    public void removeSteps() {
        steps.clear();
    }

    public boolean matches(Context cs) {
        if( steps.isEmpty() ) {
            throw new MatchException("location path cannot be empty.");
        }

        if( steps.size() > cs.getDepth() ) {
            return false;
        }

        NodeContext[] nodesStack = cs.getNodesStack();
        return relative ?  checkRelative(nodesStack) : checkAbsolute(nodesStack);
    }

    @Override
    public int hashCode() {
        int hc = 1;
        for(Step s : steps) {
            hc *= s.hashCode();
        }
        return hc;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof LocationPath) {
            LocationPathImpl other = (LocationPathImpl) obj;

            if( steps.size() != other.steps.size() ) {
                return false;
            }

            int i = 0;
            for(Step s : steps) {
                if( ! s.equals( other.steps.get(i) ) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if( ! isRelative() ) { sb.append( PATH_SEPARATOR ); }
        for( Step s : steps ) {
            sb.append( s.toString() );
            sb.append( PATH_SEPARATOR );
        }
        return sb.toString();
    }

    /**
     * Checks absolute paths.
     *
     * @param nodesStack stack of nodes to be checked.
     * @return <code>true</code> if stack is absolute, <code>false</code> otherwise.
     */
    protected boolean checkAbsolute(NodeContext[] nodesStack) {
        int i = 0;
        for(Step step : steps) {

            boolean matches = step.matches( nodesStack[i++]);
            if( ! matches ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks relative paths.
     * 
     * @param nodesStack stack of nodes to be checked.
     * @return <code>true</code> if relative, <code>false</code> otherswise.
     */
    protected boolean checkRelative(NodeContext[] nodesStack) {
        boolean found = false;
        for( int i = 0; i <= nodesStack.length - steps.size(); i++) {
            for(int j = 0; j < steps.size(); j++) {
                found = steps.get(j).matches(nodesStack[i + j]);
                if( ! found) { break; }
            }
            if(found) { return true; }
        }
        return false;
    }

}
