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

import java.util.*;

/**
 * Defines the stack of parsed nodes contestualized with visit history.
 *
 * @see com.asemantics.mashup.digester.NodeContext
 */
public class ContextStackImpl implements ContextStack {

    /**
     * Debug flag.
     */
    public static final boolean DEBUG = false;

    /**
     * Defines key element for #pathContexts map. Every key is equal to another
     * is path is equal in size and sequence.
     */
    class PathKey {

        private String[] path;

        PathKey(String[] path) {
            this.path = path;
        }

        @Override
        public boolean equals(Object obj) {
            if( obj == null ) {
                return false;
            }
            if( obj == this ) {
                return true;
            }
            if( obj instanceof PathKey) {
                PathKey other = (PathKey) obj;
                if(path.length != other.path.length) {
                    return false;
                }
                int i = 0;
                for( String p : path ) {
                    if( ! p.equals( other.path[i++] ) ) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return path.length;
        }
    }

    /**
     * Defines a location path entry.
     */
    class LocationPathEntry {

        /**
         * Location path.
         */
        private LocationPath locationPath;

        /**
         * Location path listener.
         */
        private LocationPathListener locationPathListener;

        /**
         * Mantains the status of activity of this matching.
         */
        private boolean active = false;

        /**
         * Constuctor.
         *
         * @param lp location path.
         * @param lpl location path listener.
         * @throws IllegalArgumentException if lp or lpl are null.
         */
        LocationPathEntry(LocationPath lp,  LocationPathListener lpl) {
            if(lp == null) {
                throw new IllegalArgumentException();
            }
            if(lpl == null) {
                throw new IllegalArgumentException();
            }

            locationPath         = lp;
            locationPathListener = lpl;
        }

    }

    /**
     * Current stack of node contexts.
     */
    private Stack<NodeContext> stack;

    /**
     * List of node cotext used to optimize reading on the same level.
     */
    private NodeContext[] nodesStack;

    /**
     * This map contains as key every visited path of context at a certain time and as
     * value the context associated to the key.
     */
    private Map<PathKey,NodeContext> pathContexts;

    /**
     * List of location paths.
     */
    private List<LocationPathEntry> locationPaths;

    /**
     * Constructor.
     */
    public ContextStackImpl() {
        stack        = new Stack<NodeContext>();
        pathContexts = new HashMap<PathKey,NodeContext>();
        locationPaths = new LinkedList<LocationPathEntry>();
    }

    public void pushNode(String node, NodeAttribute[] attributes) {

        PathKey pathKey = createPathKey( stack, node );

        NodeContext nc = pathContexts.get( pathKey );
        if( nc == null ) {
            nc = new NodeContext(node, attributes);
            pathContexts.put( pathKey, nc );
        } else {
            nc.incrementOccurrences();
        }

        stack.push(nc);

        log("PUSH: " + nc);

        nodesStack = null;
        checkBeginningMatches();
    }

    public void popNode(String nodeName) {
        if( nodeName == null ) {
            return;
        }

        String peekNodeName;
        while( ! stack.isEmpty() ) {
            peekNodeName = stack.pop().getNodeName();

            log("POP NODE: " + peekNodeName);

            if( nodeName.equals( peekNodeName ) ) {
                break;
            }
        }

        nodesStack = null;
        checkEndingMatches();
    }

    public void popNode() {
        NodeContext nc = stack.pop();

        log("POP: "+ nc);

        nodesStack = null;
        checkEndingMatches();
    }


    public int getDepth() {
        return stack.size();
    }

    /**
     * Returns the nodes stack.
     *
     * @return list of nodes stack from deepest to peek.
     */
    public NodeContext[] getNodesStack() {
        if( nodesStack == null ) {
            nodesStack = stack.toArray( new NodeContext[stack.size()] );
        }
        return nodesStack;
    }

    public void clear() {

        stack.clear();
        nodesStack = null;
        pathContexts.clear();

        // Reset all location paths.
        for(LocationPathEntry lpe : locationPaths) {
            lpe.active = false;
        }
        
    }

    public void addLocationPath(LocationPath lp, LocationPathListener lpl) {
        locationPaths.add( new LocationPathEntry(lp, lpl) );
    }

    public void removeLocationPath(LocationPath lp) {
        Iterator<LocationPathEntry> iter = locationPaths.iterator();
        LocationPathEntry current;
        while( iter.hasNext() ) {
            current = iter.next();
            if( current.locationPath.equals( lp) ) {
                iter.remove();
            }
        }
    }

    public void removeLocationPaths() {
        locationPaths.clear();
    }

    /**
     * Creates a key representing the current path in stack.
     *
     * @param previousContext context before peek element.
     * @param peek the peeck element in stack.
     * @return string representing concatenation of nodes in stack.
     */
    protected PathKey createPathKey( Stack<NodeContext> previousContext, String peek ) {
        String[] path = new String[ previousContext.size() + 1 ];
        int i = 0;
        for(NodeContext nc : previousContext) {
            path[i++] = nc.getNodeName();
        }
        path[i] = peek;
        return new PathKey(path);
    }

    /**
     * Checks all available location paths beginning.
     */
    protected void checkBeginningMatches() {
        for(LocationPathEntry lpe : locationPaths) {
            // Skips matches aready active.
            if(lpe.active) {
                continue;
            }
            if( lpe.locationPath.matches(this) ) {
                lpe.locationPathListener.matchBegins( this, lpe.locationPath );
                lpe.active = true;
            }
        }
    }

   /**
     * Checks all available location paths on ending.
     */
    protected void checkEndingMatches() {
       for( LocationPathEntry lpe : locationPaths) {
           // Skips non active location paths.
           if( ! lpe.active ) {
               continue;
           }
           if( ! lpe.locationPath.matches(this) ) {
               lpe.active = false;
               lpe.locationPathListener.matchEnds(this, lpe.locationPath );
           }
       }
    }

    /**
     * Log method.
     *
     * @param msg message to be logged.
     */
    private void log(String msg) {
        if(DEBUG) {
            System.out.println("Context stack: " + msg);
        }
    }

}
