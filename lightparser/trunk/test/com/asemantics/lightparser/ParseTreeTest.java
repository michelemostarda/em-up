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

import junit.framework.TestCase;

/**
 * Tests the {@link com.asemantics.lightparser.ParseTree} class.
 *
 * @see com.asemantics.lightparser.TreeNode
 */
public class ParseTreeTest extends TestCase {

    private static final int DEPTH = 5;

    private static final int SIZE  = 5;

    /**
     * Test target.
     */
    private ParseTree parseTree;

    protected void setUp() throws Exception {
        parseTree = new ParseTree("parse tree");
    }

    protected void tearDown() throws Exception {
        parseTree = null;
    }

    /**
     * Tests that root is not <code>null</code>.
     */
    public void testRootNotNull() {
        assertNotNull( "Root cannot be null.", parseTree.getRoot() );
    }

    /**
     * Tests the method ParseTree#getNodesString.
     */
    public void testPrintTree() {
        // Populates tree.
        populate( parseTree.getRoot() );
        String result = parseTree.getNodesString();

        // Prints content.
        System.out.println(result);

        // Checks content.
        String expected;
        for(int s = 0; s < SIZE; s++) {
            for(int d = 0; d < DEPTH; d++) {
                expected = "node_" + s + "_" + d;
                assertTrue( "Cannot find expected: '" + expected + "'", result.contains(expected) );
            }
        }
    }

    private void populate(TreeNode tn, int depth) {
        if(depth > DEPTH) {
            return;
        }

        for(int i = 0; i < SIZE; i++) {
            TreeNode child = new TreeNode("node_" + i + "_" + depth, new DefaultTerminal("default_term") );
            tn.addChild( child  );
        }

        for(TreeNode child : tn.getChildren()) {
            populate( child, ++depth );
        }

    }

    private void populate(TreeNode tn) {
        populate(tn, 0);
    }

}
