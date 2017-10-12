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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for class {@link com.asemantics.mashup.processor.graph.SetDifference}.
 */
public class SetDifferenceTest extends TestCase {

    /**
     * Tests the SetDifference#difference() method.
     */
    public void testSetDifference() {
        Set<String> subtracting = new HashSet<String>( Arrays.<String>asList("A", "B", "C") );
        Set<String> subtractor  = new HashSet<String>( Arrays.<String>asList("B", "C", "E") );

        SetDifference<String> setDifference = SetDifference.subtract(subtracting, subtractor);
        System.out.println("setDifference: " + setDifference);

        assertFalse("Expected difference.", setDifference.isNoDifference());
        assertEquals( "Expected +A", setDifference.getPluses() ,  Arrays.<String>asList("A") );
        assertEquals( "Expected -E", setDifference.getMinuses(),  Arrays.<String>asList("E") );

        assertTrue("Expected equality.", SetDifference.subtract(subtracting, subtracting).isNoDifference() );
        assertTrue("Expected equality.", SetDifference.subtract(subtractor, subtractor).isNoDifference()   );
    }

}
