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


package com.asemantics.mashup.processor.jsonpath;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

/**
 * Default implementation of {@link com.asemantics.mashup.processor.jsonpath.Path}.
 */
public class DefaultPathImpl implements Path {

    /**
     * List of accessors.
     */
    private List<Accessor> accessors;

    /**
     * Default constructor.
     */
    public DefaultPathImpl() {
        accessors = new ArrayList<Accessor>();
    }

    /**
     * Constructor.
     *
     * @param acs list of accessors.
     */
    public DefaultPathImpl(Collection<Accessor> acs) {
        this();
        if(acs.size() == 0) {
            throw new IllegalArgumentException("Error while creating path: no accessors defined.");
        }
        accessors.addAll(acs);
    }

    /**
     * Adds a new accessor at the spefied location.
     *
     * @param accessor accessor to be added.
     * @param location accessor location.
     */
    public void add(Accessor accessor, int location) {
        accessors.add(location, accessor);
    }

    /**
     * Adds a new accessor to the tail of the path.
     *
     * @param accessor accessor the be added.
     */
    public void add(Accessor accessor) {
        accessors.add(accessor);
    }

    /**
     * Removes the first occurrencr of specified accessor.
     *
     * @param accessor accessor to be removed.
     */
    public  void remove(Accessor accessor) {
        accessors.remove(accessor);
    }

    /**
     * Removes the <i>i-th</i> accessor in path.
     *
     * @param i accessor index.
     */
    public void remove(int i) {
        accessors.remove(i);
    }

    public int size() {
        return accessors.size();
    }

    public Accessor getAccessor(int i) {
        return accessors.get(i);
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        for(Accessor accessor: accessors) {
            sb.append(accessor.getDescription());
        }
        return sb.toString();
    }

    public Iterator<Accessor> iterator() {
        return accessors.iterator();
    }
}
