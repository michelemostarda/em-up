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


package com.asemantics.mashup.processor.graph;

import com.asemantics.mashup.processor.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines a generic graph.
 */
public class Graph<T> implements Cloneable {

    /**
     * Internal list of nodes.
     */
    private Set<T> nodes;

    /**
     * Internal list of arcs.
     */
    private Set<Arc<T>> arcs;

    /**
     * Constructor.
     */
    public Graph() {
        nodes = new HashSet<T>();
        arcs  = new HashSet<Arc<T>>();
    }

    @Override
    public int hashCode() {
        return nodes.hashCode() * arcs.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if( obj instanceof Graph) {
            Graph<T> other = (Graph<T>) obj;
            return
                    nodes.size() == other.nodes.size()
                        &&
                    nodes.containsAll(other.nodes)
                        &&
                    arcs.size() == other.arcs.size()
                        &&
                    arcs.containsAll( other.arcs );
        }
        return false;
    }

    public Graph cloneGraph() {
        Graph cloned = new Graph();

        // Clone nodes.
        Set<T> clonedNodes = new HashSet<T>();
        for(T node :  nodes) {
            if( node instanceof Value) {
                clonedNodes.add( (T) ((Value) node).cloneValue() );
            } else {
                clonedNodes.add( node );
            }
        }

        // Clone arcs.
        Set<Arc<T>> clonedArcs = new HashSet<Arc<T>>();
        for(Arc<T> arc : arcs) {
            clonedArcs.add( arc.cloneArc() );
        }

        cloned.nodes = clonedNodes;
        cloned.arcs  = clonedArcs;
        return cloned;
    }

    /**
     * @return <code>true</code> if the graph has no arcs,
     *         <code>false</code> otherwise.
     */
    public boolean hasNoArcs() {
        return arcs.isEmpty();
    }

    /**
     * @return <code>true</code> if the graph is empty,
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Returns the number of nodes.
     *
     * @return number of nodes.
     */
    public int nodesSize() {
        return nodes.size();
    }

    /**
     * Returns the number of arcs.
     *
     * @return number of arcs.
     */
    public int arcsSize() {
        return arcs.size();
    }

    /**
     * Clears the content of the graph.
     */
    public void clear() {
        nodes.clear();
        arcs .clear();
    }

    /**
     * Checks if a node is present in the graph.
     *
     * @param node the node to check.
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    public boolean containsNode(T node) {
        return nodes.contains(node);
    }

    /**
     * Checks if an arc is present in the graph.
     *
     * @param from the from node of the arc.
     * @param to the to node of the arc.
     * @param label the label of the arc.
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    public boolean containsArc(T from, T to, T label) {
        return arcs.contains( new Arc<T>(from, to, label) );
    }

    /**
     * Checks if the graph contians the given <i>label</i>.
     *
     * @param label label to find.
     * @return <code>true</code> if found, <code>false</code> otherwise.
     */
    public boolean containsLabel(T label) {
        for(Arc<T> arc : arcs) {
            if( arc.getArcLabel().equals(label) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a node to the graph.
     *
     * @param node the node to add.
     * @param checked if the node presence must be checked.
     */
    public void addNode(T node, boolean checked) {
        if( ! nodes.add(node) && checked) {
            throw new GraphException("node already present in graph.");
        }
    }

    /**
     * Adds a node to the graph.
     *
     * @param node to be added.
     */
    public void addNode(T node) {
        addNode(node, false);
    }

    /**
     * Removes a node from the graph.
     * If the node is connected to any arc, the second is removed too.
     *
     * @param node node to remove.
     * @return <code>true</code> if the node was present,
     * <code>false</code> otherwise.
     */
    public boolean removeNode(T node) {
        // Remove node.
        if( ! nodes.remove(node) ) {
            return false;
        }

        // Remove arcs connected with that node.
        List<Arc<T>> labelsToBeDeleted = new ArrayList<Arc<T>>();
        for(Arc<T> arc : arcs) {
            if( node.equals( arc.getFromNode() ) || node.equals(arc.getToNode()) ) {
                labelsToBeDeleted.add( arc );
            }
        }
        for(Arc<T> arcTobeDeleted : labelsToBeDeleted) {
            arcs.remove(arcTobeDeleted);
        }

        return true;
    }

    public void addArc(Arc<T> arc, boolean checked) {
        boolean containEdges = false;
        if( checked) {
            if( ! containsNode(arc.fromNode) || ! containsNode(arc.toNode) ) {
                throw new GraphException("'from' and 'to' nodes must exist.");
            }
            containEdges = true;
        }
        if( ! arcs.add(arc) ) {
            throw new GraphException("arc already present in graph.");
        }
        if( !containEdges ) {
            nodes.add(arc.fromNode);
            nodes.add(arc.toNode  );
        }
    }

    /**
     * Adds an arc to the graph, connecting the <i>from</i> node to the
     * <i>to</i>  node and labeled with <i>label</i>. If the bounded nodes
     * don't exist an exception is thrown.
     *
     * @param from from node.
     * @param to to node.
     * @param label label arc.
     * @throws GraphException if the bounded nodes don't exist.
     */
    public void addCheckedArc(T from, T to, T label) {
        addArc( new Arc<T>(from, to, label), true );
    }

    /**
     * Adds an arc to the graph, connecting the <i>from</i> node to the
     * <i>to</i>  node and labeled with <i>label</i>.
     *
     * @param from from node.
     * @param to to node.
     * @param label label arc.
     */
    public void addArc(T from, T to, T label) {
        addArc( new Arc<T>(from, to, label), false );
    }

    /**
     * Adds a bag arc with specific index.
     *
     * @param from from node.
     * @param to to node.
     * @param label label value.
     * @param index index of bag arg.
     */
    public void addBagArc(T from, T to, T label, int index, boolean checked) {
        addArc( new BagArc<T>(from, to, label, index), checked );
    }

    /**
     * Adds a bag arc with defualt unchecked option and auto increment index.
     *
     * @param from from node.
     * @param to to node.
     * @param label label value.
     */
    public void addBagArc(T from, T to, T label) {
        addArc( new BagArc<T>(from, to, label), false );
    }

    /**
     * Removes a given arc.
     *
     * @param arc the arec to be removed.
     * @return <code>true</code> if the arc was removed,
     *         <code>false</code> otherwise.
     */
    public boolean removeArc(Arc<T> arc) {
        if(arc == null) {
            throw new NullPointerException();
        }
        return arcs.remove(arc);
    }

    /**
     * Removes an arc from the graph.
     *
     * @param from from node.
     * @param to to node.
     * @param label arc value.
     * @return <code>true</code> if the arc was removed,
     *         <code>false</code> otherwise.
     */
    public boolean removeArc(T from, T to, T label) {
        return removeArc( new Arc<T>(from, to, label) );
    }



    /**
     * Returns the list of all nodes.
     * 
     * @return the set of graph's nodes.
     */
    public Set<T> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Returns the list of all arcs.
     * 
     * @return  the set of graph's arcs.
     */
    public Set<Arc<T>> getArcs() {
        return Collections.unmodifiableSet(arcs);
    }

    /**
     * Returns the list of arc labels.
     *
     * @return list of arc labels.
     */
    public Set<T> getLabels() {
        Set<T> labels = new HashSet<T>();
        for(Arc<T> arc : arcs) {
            labels.add(arc.getArcLabel());
        }
        return labels;
    }

    /**
     * Returns the list of arcs which from node is equals to <i>node</i>.
     *
     * @param node from node.
     * @return list of outing arcs.
     */
    public List<Arc<T>> getOutingArcs(T node) {
        List<Arc<T>> result = new ArrayList<Arc<T>>();
        for( Arc<T> arc : arcs ) {
            if( arc.getFromNode().equals( node ) ) {
                result.add(arc);
            }
        }
        return result;
    }

    /**
     * Returns the list of arcs which to node is equals to <i>node</i>.
     *
     * @param node to node.
     * @return list of entering arcs.
     */
    public List<Arc<T>> getEnteringArcs(T node) {
        List<Arc<T>> result = new ArrayList<Arc<T>>();
        for( Arc<T> arc : arcs ) {
            if( arc.getToNode().equals( node ) ) {
                result.add(arc);
            }
        }
        return result;
    }

    /**
     * Returns the list of arcs comprised between
     * <i>from</i> and <i>to</i> node.
     *
     * @param from from node.
     * @param to to node.
     * @return list of comprised arcs.
     */
    public List<Arc<T>> getArcsBetween(T from, T to) {
        List<Arc<T>> result = new ArrayList<Arc<T>>();
        for( Arc<T> arc : arcs ) {
            if( arc.getFromNode().equals( from ) && arc.getToNode().equals(to)  ) {
                result.add(arc);
            }
        }
        return result;
    }

    /**
     * Returns <code>true</code> if the given <i>node</i> is a bag,
     * <code>false</code> otherwise.
     *
     * @param node node to check.
     * @return is a bag status.
     */
    public boolean isBag(T node) {
        if( node == null) {
            throw new NullPointerException();
        }
        for(Arc<T> arc : arcs) {
            if( arc.getFromNode().equals(node) && arc instanceof BagArc) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the bag of a node.
     *
     * @param node node to use to extract the bag.
     * @return list of arcs composing the bag ordered in the right logic.
     */
    public List<Arc<T>> getBag(T node) {
        if( node == null) {
            throw new NullPointerException();
        }
        List<Arc<T>> result = new ArrayList<Arc<T>>();
        for(Arc<T> arc : arcs) {
            if( arc.getFromNode().equals(node) && arc instanceof BagArc) {
                result.add(arc);
            }
        }
        Collections.sort(result, BagArc.BAG_ARC_COMPARATOR);
        return result;
    }

}
