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


package com.asemantics.mashup.processor;

import junit.framework.TestCase;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.asemantics.mashup.processor.json.JsonString;
import com.asemantics.mashup.processor.graph.GraphException;
import com.asemantics.mashup.processor.graph.GraphDifference;

/**
 * Test for class {@link com.asemantics.mashup.processor.GraphValue}.
 */
//TODO: this test case contains a RANDOM TEST. Sometimes the test fails, discover the reason behind.
public class GraphValueTest extends TestCase {

    /**
     * Triple count.
     */
    private static final int SIZE = 20;

    /* Some prefixes. */

    private static final String FROM_PREFIX = "from";

    private static final String TO_PREFIX = "to";

    private static final String LABEL_PREFIX = "label";

    /**
     * Target.
     */
    private GraphValue graphValue;

    /**
     * Random number generator.
     */
    private Random random = new Random();


    @Override
    protected void setUp() throws Exception {
        graphValue = new GraphValue();
    }

    @Override
    protected void tearDown() throws Exception {
        graphValue = null;
    }

    /**
     * Tests the GraphValue --> JsonValue --> GraphValue round trip with a graph of strings.
     */
    public void testStringGraphToJSONRoundTrip() {
        populateStringGraphValue(graphValue);

        final JsonValue asJSON = graphValue.asJsonValue();

        final GraphValue reverted = asJSON.asGraph();

        assertTrue("Error in round trip.", graphValue.equals(reverted));
    }

    /**
     * Tests the GraphValue --> JsonValue --> GraphValue round trip with a RANDOM graph.
     */
    public void testRendomGraphToJSONRoundTrip() {
        populateRandomGraphValue(graphValue);
        System.out.println("original:" + graphValue);

        final JsonValue asJSON = graphValue.asJsonValue();

        final GraphValue reverted = asJSON.asGraph();
        System.out.println("reverted:" + reverted);

        GraphDifference difference = GraphDifference.difference(graphValue.getNativeValue(),reverted.getNativeValue() );
        System.out.println("Difference: " + difference);

        final boolean originalRevertedEquals = graphValue.getNativeValue().equals( reverted.getNativeValue() ) ;
        System.out.println("Original - reverted equality: " + originalRevertedEquals);

        if(originalRevertedEquals && ! difference.isNoDifference() ) {
            System.out.println("UNESPECTED CONDITION");
            System.out.println(graphValue.getNativeValue().equals( reverted.getNativeValue() ) );
            GraphDifference.difference(graphValue.getNativeValue(),reverted.getNativeValue());
        }

        if( ! originalRevertedEquals) {
            System.out.println("break on:" + graphValue.getNativeValue().equals( reverted.getNativeValue() ));
        }

        assertTrue("Must be equal.", graphValue.getNativeValue().equals( reverted.getNativeValue() ) );

        assertTrue("Error in round trip.", difference.isNoDifference() );
    }

    /**
     * Populates the given graph value with arcs composed of strings.
     *
     * @param gv the graph value to populate.
     */
    private void populateStringGraphValue(GraphValue gv) {
        for (int i = 0; i < SIZE; i++) {
            gv.addArc(
                    new StringValue(FROM_PREFIX + i), new StringValue(TO_PREFIX + i), new StringValue(LABEL_PREFIX + i)
            );
        }
    }

    /**
     * Populates the given graph value with random arcs.
     *
     * @param gv the graph value to populate.
     */
    private void populateRandomGraphValue(GraphValue gv) {
        int skipped = 0;
        for (int i = 0; i < SIZE; i++) {
            try {
                gv.addArc(
                        createRandomType(i), createRandomType(i), createRandomType(i)
                );
            } catch (GraphException ge) {
                skipped++;
            }
        }
        System.out.println("number of skipped arcs: " + skipped);
    }

    /**
     * Creates a random type.
     *
     * @param c index.
     * @return random type.
     */
    private Value createRandomType(int c) {
        final JsonString jsonString = new StringValue(Integer.toString(c));
        final JsonValue jsonValue = new JsonValue(jsonString);

        Value.ValueType[] types = Value.ValueType.values();
        // Removing graph.
        List<Value.ValueType> typesList = new ArrayList<Value.ValueType>(Arrays.asList(types));
        typesList.remove(Value.ValueType.GRAPH);
        //typesList.remove(Value.ValueType.STRING);
        types = typesList.toArray(new Value.ValueType[types.length -1]);

        // Detects the graph presence.
        int index = random.nextInt(types.length);
        if (types[index] == Value.ValueType.GRAPH) {
           // System.out.println("GRAPH detected.");
        }
        if (types[index] == Value.ValueType.STRING) {
           // System.out.println("STRING detected.");
        }

        return types[index].convertToType(jsonValue);
    }

}
