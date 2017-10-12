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

import java.util.Comparator;

/**
 * Extends the {@link Arc} object to add a sortable support.
 */
public class BagArc<T> extends Arc<T> {

    private static int COUNTER = 0;

    private static final int nextIndex() {
        return COUNTER++;
    }

    protected static class BagArcComparator<T> implements Comparator<T> {

        public int compare(T o1, T o2) {
              BagArc ba1;
              BagArc ba2;
            try {
                ba1 = (BagArc) o1;
                ba2 = (BagArc) o2;
            } catch (ClassCastException cce) {
                throw new GraphException("Error while sorting bag arcs, expected bag here.");
            }
            return ba1.index - ba2.index;
        }

    }

    /**
     * Static instance.
     */
    protected static final BagArcComparator BAG_ARC_COMPARATOR = new BagArcComparator();

    /**
     * The index of the bag.
     */
    private int index;

    /**
     * Constructor.
     *
     * @param from  from node.
     * @param to    to node.
     * @param label label value.
     */
    public BagArc(T from, T to, T label, int index) {
        super(from, to, label);
        this.index = index;
    }

    /**
     * Constuctor, defines index by using a global counter.
     *
     * @param from from node.
     * @param to to node.
     * @param label label value.
     */
    public BagArc(T from, T to, T label) {
        this(from, to, label, nextIndex() );
    }

}
