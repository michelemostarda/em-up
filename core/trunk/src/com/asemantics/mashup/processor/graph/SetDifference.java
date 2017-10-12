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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Defines the difference between a couple of sets.
 */
public class SetDifference<T> {

    /**
     * Computes the difference between a subtracting set S1 and a subtractor set S2,
     * and returns an object representing such difference. This object contains two
     * lists:
     * <ul>
     * <li><i>pluses<i/> : that is the list of elements present in subtracting but not in subtractor.</li>
     * <li><i>minuses<i/>: that is the list of elements present in subtractor but not in subtracting.</li>
     * </ul>
     * The operator is not symmetric.
     *
     * <pre>
     * given S1 composed of elements: A B C,
     * given S2 composed of elements: B C E,
     * an example of subtraction is:
     * subtract(S1,S2) returns: +A -E
     * subtract(S2,S1) returns: +E -A
     * </pre>
     *
     * @param subtracting subtracting set.
     * @param subtractor subtractor set.
     * @return subtraction.
     */
    public static <I> SetDifference<I> subtract(Set<I> subtracting, Set<I> subtractor) {
        SetDifference<I> result = new SetDifference<I>();
        for(I elem : subtracting) {
            elem.equals(null);
            if( ! subtractor.contains(elem) ) {
                result.addPluses(elem);
            }
        }
        for(I elem : subtractor) {
            if( ! subtracting.contains(elem) ) {
                result.addMinuses(elem);
            }
        }
        return result;
    }

    /**
     * Lists of elements present in subtracting but not in subtractor.
     */
    private List<T> pluses;

    /**
     * Lists of elements present in subtractor but not in subtracting.
     */
    private List<T> minuses;

    protected SetDifference() {
        // Empty.
    }

    /**
     * Returns the list of pluses.
     *
     * @return list of pluses.
     */
    public List<T> getPluses()  {
        return pluses == null ? Collections.<T>emptyList() : Collections.unmodifiableList(pluses);
    }

    /**
     * Returns the list of minuses.
     *
     * @return list of minuses.
     */
    public List<T> getMinuses()  {
        return minuses == null ? Collections.<T>emptyList() : Collections.unmodifiableList(minuses);
    }

    /**
     * @return <code>true</code> if there is no difference between <i>subtracting</i>
     *         and <i>subtractor</i>, <code>false</code> otherwise.
     */
    public boolean isNoDifference() {
        return (pluses == null || pluses.size() == 0) && (minuses == null || minuses.size() == 0);
    }

    /**
     * Adds a plus element.
     *
     * @param plus
     */
    protected void addPluses(T plus) {
        if(pluses == null) {
            pluses = new ArrayList<T>();
        }
        pluses.add(plus);
    }

    /**
     * Adds a misus element.
     *
     * @param minus
     */
    protected void addMinuses(T minus) {
        if(minuses == null) {
            minuses = new ArrayList<T>();
        }
        minuses.add(minus);
    }

    /**
     * Resets all lists.
     */
    protected void clear() {
        pluses.clear();
        minuses.clear();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if(pluses != null) {
            for(T plus : pluses) {
                result.append("+").append(plus);
                result.append(" ");
            }
        }
        if(minuses != null) {
            for(T minus : minuses) {
                result.append("-").append(minus);
                result.append(" ");
            }
        }
        return result.toString();
    }

}
