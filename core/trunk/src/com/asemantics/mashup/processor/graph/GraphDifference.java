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

/**
 * Represents the difference between two graphs.
 *
 * @see com.asemantics.mashup.processor.graph.SetDifference
 */
public class GraphDifference<T> {

    /**
     * Compares a couple of graphs, returning the differences.
     *
     * @param subtracting the subtracting graph.
     * @param subtractor the subtractor graph.
     * @return the difference graph.
     */
    public static <I> GraphDifference<I> difference(Graph<I> subtracting, Graph<I> subtractor) {
        return new GraphDifference<I>(
                SetDifference.<I>subtract(subtracting.getNodes()    , subtractor.getNodes() ),
                SetDifference.<Arc<I>>subtract(subtracting.getArcs(), subtractor.getArcs()  )
        );
    }

    /**
     * Difference between node's set.
     */
    private SetDifference<T>      nodesDifference;

    /**
     * Difference between arc's set.
     */
    private SetDifference<Arc<T>> arcssDifference;

    /**
     * Constructor.
     *
     * @param nsd
     * @param asd
     */
    protected GraphDifference(SetDifference<T> nsd, SetDifference<Arc<T>> asd) {
        if(nsd == null || asd == null) {
            throw new IllegalArgumentException();
        }
        nodesDifference = nsd;
        arcssDifference = asd;
    }

    /**
     * @return the difference of nodes.
     */
    public SetDifference<T> getNodesDifference() {
        return nodesDifference;
    }

    /**
     * @return the difference of arcs.
     */
    public SetDifference<Arc<T>> getArcssDifference() {
        return arcssDifference;
    }

    /**
     * @return <code>true</code> if there is no difference, <code>false</code>otherwise.
     */
    public boolean isNoDifference() {
        return nodesDifference.isNoDifference() && arcssDifference.isNoDifference();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("nodesD:").append( nodesDifference.toString() );
        result.append("arcsD:") .append( arcssDifference.toString() );
        return result.toString();
    }
}
