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


package com.asemantics.lightparser;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a generic node of the {@link com.asemantics.lightparser.ParseTree}.
 */
public class TreeNode {

    /**
     * Parent of this tree node, if <code>null</code>
     * then is <i>root</i>.
     */
    private TreeNode parent;

    /**
     * Node name.
     */
    private String name;

    /**
     * Associated term.
     */
    private Term term;

    /**
     * List of children of this node.
     */
    private List<TreeNode> children;

    /**
     * Tree node location.
     */
    private Location location;

    /**
     * Constructor. Allows to specify location.
     *
     * @param n node content.
     * @param t associated term.
     * @param l location of tree node in input stream.
     */
    public TreeNode(String n, Term t, Location l) {
        if( n == null || t == null || l == null ) {
            throw new IllegalArgumentException();
        }
        name = n;
        term = t;
        location = l;
    }

    /**
     * Constructor.
     *
     * @param n node content.
     * @param t term.
     */
    public TreeNode(String n, Term t) {
        this(n, t, Location.NATIVE_LOCATION);
    }

    /**
     * Returns the node name.
     *
     * @return name of tree node.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the node term.
     * 
     * @return  associated term.
     */
    public Term getTerm() {
        return term;
    }

    /**
     * Returns the tree node location.
     *
     * @return if leaf node the location itself,
     *         otherwise the first child location.
     */
    public Location getLocation() {
        TreeNode next = this;
        while( next.hasChildren() ) {
            next = next.getChild(0);
        }
        return next.location;
    }

    /**
     * Returns the tree root node.
     *
     * @return root tree node.
     */
    public TreeNode getRoot() {
        TreeNode nextParent = this;
        while (nextParent.getParent() != null) {
            nextParent = nextParent.getParent();
        }
        return nextParent;
    }

    /**
     * Returns the parent of this node
     * or <code>null</code> if <i>root</i>.
     * 
     * @return parent tree node.
     */
    public TreeNode getParent() {
        return parent;
    }

    /**
     * Returns the <i>i-th</i> child.
     *
     * @param i
     * @return child at guven index.
     */
    public TreeNode getChild(int i) {
        return children.get(i);
    }

    /**
     * Returns an array containing the children of this node.
     * @return  list of children.
     */
    public TreeNode[] getChildren() {
        return children == null ? new TreeNode[0] : children.toArray( new TreeNode[children.size()] );
    }

    /**
     * Returns the number of children of this node.
     * @return number of children.
     */
    public int getChildrenCount() {
        return children == null ? 0 : children.size();
    }

    /**
     * Returns the number of descendant nodes from this node.
     * @return number of descendants.
     */
    public int getDescendantsCount() {
        if(children == null) {
            return 1;
        }
        int counter = 1;
        for(TreeNode child : children) {
            counter += child.getDescendantsCount();
        }
        return counter;
    }

    /**
     * Returns <code>true</code> if this node has children,
     * <code>false</code> otherwise.
     * 
     * @return has children condition.
     */
    public boolean hasChildren() {
        return children != null && (children.size() > 0);
    }

    /**
     * Returns <code>true</code> if this node has the same parent of <i>tn</i>,
     * <code>false</code> otherwise.
     *
     * @param tn
     * @return comparison result.
     */
    public boolean sameParentOf(TreeNode tn) {
        return  parent == null ? tn.parent == null : parent.equals(tn.parent);
    }

    /**
     * Sets a new term value, used when backtracking
     * productions.
     * 
     * @param t
     */
    protected void setTerm(Term t) {
        term = t;
    }

    /**
     * Adds a child to this node.
     *
     * @param child
     */
    protected void addChild(TreeNode child) {
        if( children == null ) {
            children = new ArrayList<TreeNode>();
        }

        if( child.parent != null ) {
            throw new IllegalArgumentException("This child already has a parent: '" + child.parent.getName());
        }
        children.add(child);
        child.parent = this;
    }

    /**
     * Removes the specified child from list of children.
     *
     * @param child
     */
    protected void removeChild(TreeNode child) {
        if( children == null ) {
            return;
        }
        children.remove(child);
    }

    /**
     * Returns the <i>i-th</i> child from list of children.
     *
     * @param i
     * @return removed child
     */
    protected TreeNode removeChild(int i) {
        if( children == null ) {
            return null;
        }
        return children.remove(i);
    }

    /**
     * Removes all children from this node.
     */
    public void clear() {
        if(children  != null) {
            children.clear();
        }
    }

}
