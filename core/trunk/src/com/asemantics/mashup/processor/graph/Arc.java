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

import com.asemantics.mashup.processor.Value;


/**
 * Defines an arc of {@link com.asemantics.mashup.processor.graph.Graph}.
 *
 * @see com.asemantics.mashup.processor.graph.Graph
 */
public class Arc<T> implements Cloneable {

    protected T fromNode;

    protected T toNode;

    protected T arcLabel;

    Arc(T from, T to, T label) {
        if(from == null) {
            throw new NullPointerException("fromNode cannot be null.");
        }
        if(to == null) {
            throw new NullPointerException("toNode cannot be null.");
        }
        if(label == null) {
            throw new NullPointerException("label cannot be null.");
        }
        fromNode = from;
        toNode   = to;
        arcLabel = label;
    }

    public T getFromNode() {
        return fromNode;
    }

    public T getToNode() {
        return toNode;
    }

    public T getArcLabel() {
        return arcLabel;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if( ! (obj instanceof Arc) ) {
            return false;
        }
        Arc other = (Arc) obj;
        return
                fromNode.equals( other.fromNode )
                &&
                toNode.equals( other.toNode )
                &&
                arcLabel.equals( other.arcLabel );
    }

    public Arc<T> cloneArc() {
        T clonedFrom  = fromNode instanceof Value ? (T) ((Value) fromNode).cloneValue() : fromNode;
        T clonedTo    = toNode   instanceof Value ? (T) ((Value) toNode  ).cloneValue() : toNode;
        T clonedLabel = arcLabel instanceof Value ? (T) ((Value) arcLabel).cloneValue() : arcLabel;
        return new Arc<T>(clonedFrom, clonedTo, clonedLabel);
    }

    @Override
    public int hashCode() {
        return fromNode.hashCode() * toNode.hashCode() * arcLabel.hashCode();
    }

    @Override
    public String toString() {
        return "(" + fromNode + "--" + arcLabel + "-->" + toNode + ")";
    }
}