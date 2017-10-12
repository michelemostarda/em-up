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

import junit.framework.TestCase;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.StringValue;

import java.util.List;

/**
 * Test class for {@link com.asemantics.mashup.processor.graph.Graph}.
 */
public class GraphTest extends TestCase {

    private static final int SIZE = 1000;
    private static final String FROM_PREFIX  = "from";
    private static final String TO_PREFIX    = "to";
    private static final String LABEL_PREFIX = "label";

    private Graph<Value> graph;

    @Override
    protected void setUp() throws Exception {
        graph = new Graph<Value>();
    }

    @Override
    protected void tearDown() throws Exception {
        graph = null;
    }

    /**
     * Tests the addition, checking and removal of a node.
     */
    public void testNodeAddCheckRemoveEmptyNode() {
        Value node  = new StringValue("node");
        Value external = new StringValue("other");

        graph.addNode(node);
        assertTrue("Node doesn't contain the added node.", graph.containsNode(node));
        assertFalse("Node cannot contain the external node.", graph.containsNode(external));
        assertEquals( "Unespected nomber of nodes.", 1, graph.nodesSize() );

        assertTrue("The node must be found.", graph.removeNode(node) );

        assertTrue("The graph must be empty.", graph.isEmpty());
        assertEquals( "Unespected nomber of nodes.", 0, graph.nodesSize() );
    }

    /**
     * Tests the addition, checking and removal of an arc.
     */
    public void testNodeAddCheckRemoveEmptyArc() {
        Value from  = new StringValue("from");
        Value to    = new StringValue("to");
        Value label = new StringValue("label");

        graph.addArc( from, to, label);
        assertTrue("Node doesn't contain the added arc.", graph.containsArc(from, to, label));
        assertFalse("Node cannot contain the intverted arc.", graph.containsArc(to, from, label));
        assertEquals( "Unespected nomber of arcs.", 1, graph.arcsSize() );

        assertTrue("The arc must be found.", graph.removeArc(from, to, label) );

        assertTrue("The graph must be empty.", graph.hasNoArcs());
        assertEquals( "Unespected nomber of arcs.", 0, graph.arcsSize() );
    }

    /**
     * Tests the methods to search specific arcs.
     */
    public void testArcSearchMethods() {
        populateTestGraph( graph );

        for(int i = 0; i < SIZE; i++) {

            assertTrue("Just one arc expected.",  graph.getOutingArcs  ( new StringValue(FROM_PREFIX + i) ).size() == 1);
            assertTrue("No arcs expected."     ,  graph.getEnteringArcs( new StringValue(FROM_PREFIX + i) ).size() == 0);

            assertTrue("Just one arc expected.",  graph.getEnteringArcs( new StringValue(TO_PREFIX + i) ).size() == 1);
            assertTrue("No arcs expected."     ,  graph.getOutingArcs  ( new StringValue(TO_PREFIX + i) ).size() == 0);

            assertTrue(
                    "Expected just one arc.",
                    graph.getArcsBetween(
                        new StringValue(FROM_PREFIX + i),
                        new StringValue(TO_PREFIX + i) ).size() == 1 
            );
        
        }
    }

    /**
     * Tests the bag utilities.
     */
    public void testBagArcs() {

        final String BAG_NODE  = "bag";
        final String BAG_LABEL = "bag_label";

        // Creating bag.
        for(int i = SIZE -1; i >= 0; i--) {
            graph.addBagArc( new StringValue(BAG_NODE), new StringValue("to" + i), new StringValue(BAG_LABEL + i) );
            graph.addArc   ( new StringValue(BAG_NODE), new StringValue("to" + i), new StringValue("label"   + i) );
        }

        assertTrue( "The bag node must be recognized.", graph.isBag(new StringValue(BAG_NODE)) );

        List<Arc<Value>> bagArcs = graph.getBag( new StringValue(BAG_NODE) );
        assertEquals( "Unespected bag size.", SIZE, bagArcs.size() );
        int i = SIZE -1;
        for(Arc<Value> arc : bagArcs) {
            assertEquals("Expected to be equal.", new StringValue(BAG_LABEL + i--), arc.getArcLabel() );
        }

    }

    /**
     * Tests a generic usage scenario.
     */
    public void testUsageScenario() {

        populateTestGraph(graph);

        // Checking sizes.
        assertEquals( "Unespected nomber of nodes.", SIZE * 2, graph.nodesSize() );
        assertEquals( "Unespected nomber of arcs." , SIZE    , graph.arcsSize()  );

        assertEquals("Unespected number of labels.", SIZE, graph.getLabels().size() );

        // Checking content.
        for(int i = 0; i < SIZE; i++) {
            Value from, to;
            from = new StringValue(FROM_PREFIX + i);
            to   = new StringValue(TO_PREFIX   + i);
            assertTrue("Expected from node: "+ i, graph.containsNode( from ) );
            assertTrue("Expected to   node: "+ i, graph.containsNode( to   ) );
            assertTrue("Expected to   node: "+ i, graph.containsArc ( from, to, new StringValue(LABEL_PREFIX + i) ) );
            assertTrue("Expected label:     "+ i, graph.containsLabel( new StringValue(LABEL_PREFIX + i) ) );
        }

    }

    /**
     * Populates a given graph.
     *
     * @param g the graph.
     */
    private void populateTestGraph(Graph<Value> g) {
        for(int i = 0; i < SIZE; i++) {
            g.addArc( new StringValue(FROM_PREFIX + i), new StringValue(TO_PREFIX + i), new StringValue(LABEL_PREFIX + i) );
        }
    }


}
