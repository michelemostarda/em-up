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

import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Test for class {@link com.asemantics.mashup.processor.graph.GraphDifference}.
 */
public class GraphDifferenceTest extends TestCase {

    private static final int SIZE = 1000;

    private static final String FROM_PREFIX  = "from";
    private static final String TO_PREFIX    = "to";
    private static final String LABEL_PREFIX = "label";

    private static final StringValue ADDED_FROM  = new StringValue("added_from" );
    private static final StringValue ADDED_TO    = new StringValue("added_to"   );
    private static final StringValue ADDED_LABEL = new StringValue("added_label");
    /**
     * Tests the GraphDifference#difference() method.
     */
    public void testGraphDifference() {

        // Intial graphs.
        Graph<Value> subtracting = createStringGraph();
        Graph<Value> subtractor  = createStringGraph();

        // Check.
        assertTrue(
                "At this time the graphs must be equal.",
                GraphDifference.<Value>difference(subtracting, subtractor).isNoDifference()
        );

        // Modifying subtracting.
        subtracting.addArc( ADDED_FROM, ADDED_TO, ADDED_LABEL );
        final GraphDifference difference = GraphDifference.difference(subtracting, subtractor);
        System.out.println("Difference: " + difference);

        // Check difference.
        assertFalse("Expected difference this time.", difference.isNoDifference());
        assertTrue("Expected pluses nodes.", difference.getNodesDifference().getPluses().containsAll(
                Arrays.asList(ADDED_FROM, ADDED_TO) )
        );
        assertTrue("Expected minuses empty." , difference.getNodesDifference().getMinuses().isEmpty() );
        assertTrue(
                "Expected pluses empty." ,
                difference.getArcssDifference().getPluses().containsAll(Arrays.asList(
                        new Arc<Value>(ADDED_FROM, ADDED_TO, ADDED_LABEL)
                ) )
        );
        assertTrue("Expected minuses empty." ,difference.getArcssDifference().getMinuses().isEmpty() );

        // Adjusting subtractor.
        subtractor.addArc( ADDED_FROM, ADDED_TO, ADDED_LABEL );
        assertTrue("Now must be equal.", GraphDifference.difference(subtracting, subtractor).isNoDifference() );
    }

    private Graph<Value> createStringGraph() {
        Graph<Value> graph = new Graph<Value>();
        for(int i = 0; i < SIZE; i++) {
            graph.addArc( new StringValue(FROM_PREFIX + i), new StringValue(TO_PREFIX + i), new StringValue(LABEL_PREFIX + i) );
        }
        return graph;
    }

}
