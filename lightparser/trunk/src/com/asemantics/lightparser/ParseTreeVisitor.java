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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Defines a <i>visitor</i> on {@link com.asemantics.lightparser.ParseTree},
 * performing a post-order traversal visit.
 *
 * @see com.asemantics.lightparser.TreeNode
 */
public class ParseTreeVisitor {

    /**
     * Debug status.
     */
    private static final boolean DEBUG = false;

    /**
     * Visit stack.
     */
    private LinkedList<TreeNode> stack;

    //TODO: find a better solution.
    private Set<TreeNode> done = new HashSet<TreeNode>();

    /**
     * Mantains list of partial results.
     */
    private LinkedList childrenCompilationResults = new LinkedList();

    /**
     * Mantains last result.
     */
    private Object lastResult;

    /**
     * Constructor.
     *
     * @param tn
     */
    public ParseTreeVisitor(TreeNode tn) {
        if(tn == null) {
            throw new NullPointerException();
        }

        stack = new LinkedList<TreeNode>();
        stack.add( tn );
    }

    /**
     * Constructor.
     *
     * @param pt
     */
    public ParseTreeVisitor(ParseTree pt) {
        this( pt.getRoot() );
    }

    /**
     * Returns compilation visit result.
     *
     * @return result of compilation.
     * @throws IllegalStateException if end of compilation has not been reached.
     */
    public Object getResult() {
        if( hasNext() ) {
            throw new IllegalStateException("End of compilation has not been reached.");
        }

        return lastResult;
    }

    /**
     * Compiles the entire tree and returns result.
     *
     * @return compiled result.
     */
    public Object compile() {
        while( hasNext() ) {
            next();
        }
        return getResult();
    }

    /**
     * Verifies if there is another element in tree visit.
     *
     * @return  <code>true</code> if there is something else,
     *          <code>false</code> otherwise.
     */
    protected boolean hasNext() {
        return ! stack.isEmpty();
    }

    /**
     * Returns the next Tree node to visit.
     *
     * @return tree node of this step.
     */
    protected TreeNode next() {
        TreeNode top = stack.removeFirst();

        // Notification.
        Term term = top.getTerm();

        // Backtracking.
        if( done.contains(top) ) {

            final int back = top.getChildrenCount();
            lastResult = term.postcompile( top, getChildrenPartialResults(back) );
            replaceResults(lastResult, back);

            if(DEBUG) {
                log("POSTCOMPILE: " + term);
            }

        } else { // Forward.
            term.precompile();

            if(DEBUG) {
                log("PRECOMPILE: " + term);
            }
        }

        // Visit.
        if( ! done.contains(top) ) {

            done.add(top);

            if( top.hasChildren() ) {

                if(DEBUG) {
                    log("HASCHILDREN: " + top.getChildrenCount());
                }

                stack.addFirst(top);
                
            } else {

                lastResult = term.postcompile( top, new Object[]{} );
                childrenCompilationResults.add(lastResult);

                if(DEBUG) {
                    log("POSTCOMPILE LEAF: " + top.getName());
                }

            }

            TreeNode[] children = top.getChildren();
            for(int i = children.length -1; i >= 0; i--) {
                stack.addFirst(children[i]);
            }

        }

        return top;
    }

    /**
     * Returns the content of compilation result list removing obsolete nodes.
     *
     * @param back size of back results.
     * @return
     */
    private Object[] getChildrenPartialResults(int back) {
        List sublist = Util.subList(
                childrenCompilationResults,
                childrenCompilationResults.size() - back,
                childrenCompilationResults.size()
        );
        return sublist.toArray();
    }

    /**
     * Replaces result with expected ones.
     *
     * @param o
     * @param back
     */
    private void replaceResults(Object o, final int back) {
        for( int i = 0; i < back; i++) {
            childrenCompilationResults.removeLast();
        }
        childrenCompilationResults.addLast(o);
    }

    /**
     * Logs actions on system out for debugging purposes.
     *
     * @param s
     */    
    private void log(String s) {
        if(DEBUG) {
            System.out.println(s);
        }
    }

}
