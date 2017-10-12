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

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines the <i>list</i> type.
 */
public class ListValue extends Value<List<Value>> implements JsonArray {

    /**
     * Defines an interator over a JsonArray.
     */
    class ListIterator implements Iterator<JsonBase> {

        private Iterator<? extends JsonBase> iterator;

        ListIterator( List<? extends JsonBase> list) {
            iterator = list.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public JsonBase next() {
            return iterator.next();
        }

        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue("list");

    /**
     * Internal representation as {@link LinkedList}.
     */
    private LinkedList<Value> list;

    /**
     * Constructor for empty list ( [] ).
     */
    public ListValue() {
        list = new LinkedList<Value>();
    }

    /**
     * Constructor for single value list ( [v] ).
     *
     * @param v value to be used as model.
     */
    public ListValue(Value v) {
        this();
        list.add(v);
    }

    /**
     * Constructor for an array of values.
     *
     * @param vs array of values.
     */
    public ListValue(Value[] vs) {
        this();
        for(Value v : vs) {
            list.add(v);
        }
    }

    /**
     * Constructor creating cloned list.
     *
     * @param lv list value to be used as model.
     */
    public ListValue( ListValue lv ) {
        list = new LinkedList<Value>( lv.list );
    }

    /**
     * Constructor from linked list.
     *
     * @param l list to be used as model.
     */
    protected ListValue(LinkedList<Value> l) {
        if(l == null) {
            throw new NullPointerException();
        }
        list = l;
    }

    /**
     * Adds a value to list.
     *
     * @param v value to add.
     */
    public void add(Value v) {
        list.add(v);
    }

    /**
     * Adds a collection of values to list.
     *
     * @param values collection of values to append to the list.
     */
    public void add(Collection<Value> values) {
        list.addAll(values);
    }

    /**
     * Adds all the elements of a list value inside this list.
     *
     * @param lv the source list.
     */
    public void addAll(ListValue lv) {
        for( Value v : lv.list ) {
            add(v);
        }
    }

    /**
     * Adds a value at first position.
     *
     * @param v value to add at first position.
     */
    public void addFirst(Value v) {
        list.addFirst(v);
    }

    /**
     * Adds a value at last position.
     *
     * @param v value to add.
     */
    public void addLast(Value v) {
        list.addLast(v);
    }

    /**
     * Returns the value at position <i>index-th</i>.
     *
     * @param index index of element to return.
     * @return value at index-th position, <i>NullValue</i> if out of range.
     */
    public Value getElementAt(int index) {
        if( index < 0 || index >= list.size() ) {
            return NullValue.getInstance();
        }
        return list.get(index);
    }

    /**
     * Returns the value at position <i>index-th</i>.
     *
     * @param index index of element to return expressed as numeric.
     * @return value at index-th position, <i>NullValue</i> if out of range.
     */
    public Value getElementAt(NumericValue index) {
        int i = (int) index.getNativeValue().doubleValue();
        if( i < 0 || i >= list.size() ) {
            return NullValue.getInstance();
        }
        return list.get(i);
    }

    /**
     * Returns index of given value in this list, <i>NullValue</i> if not found.
     *
     * @param value value to search.
     * @return index of given value in this list, <i>NullValue</i> if not found.
     */
    public Value indexOf(Value value) {
        int i = list.indexOf( value );
        if(i < 0) {
            return NullValue.getInstance();
        } else {
            return new NumericValue( i );
        }
    }

    /**
     * Removes the <i>index-th</i> element from this list.
     *
     * @param index index of element to remove.
     * @return the removed value.
     */
    public Value remove(Value index) {
        int i = index.asNumeric().getNativeValue().intValue();
        if (i < 0 || i >= list.size()) {
            return NullValue.getInstance();
        }
        return list.remove(i);
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        if( list.size() == 1 ) {
            return new StringValue( list.get(0).asString().getNativeValue() );
        }
        String listStr = asJSONArray(list, true);
        return new StringValue( listStr );
    }

    public NumericValue asNumeric() {
        return new NumericValue( list.size() );
    }

    public BooleanValue asBoolean() {
        return new BooleanValue( ! list.isEmpty() );
    }

    public ListValue asList() {
        return this;
    }

    public MapValue asMap() {
        return new MapValue(list);
    }

    public GraphValue asGraph() {
        GraphValue graphValue = new GraphValue();
        for(Value elem : list) {
            graphValue.addNode(elem);
        }
        return graphValue;
    }

    public BooleanValue equalsTo(Value v) {
        if( v == null ) {
            return BooleanValue.getFalseValue();
        }

        ListValue other = v.asList();
        if( list.size() != other.list.size() ) {
            return BooleanValue.getFalseValue();
        }

        Iterator<Value> otherIter = other.list.iterator();
        Value otherElem;
        for( Value elem : list ) {
            otherElem = otherIter.next();
            if( ! elem.equalsTo( otherElem ).getNativeValue() ) {
                return BooleanValue.FALSE_VALUE;
            }
        }
        return BooleanValue.TRUE_VALUE;
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public ListValue cloneValue() {
        LinkedList<Value> newList = new LinkedList<Value>();
        for(Value v : list) {
            newList.add( v.cloneValue() );
        }
        return new ListValue(newList);
    }

    public List<Value> getNativeValue() {
        return list;
    }

    public String getJsonType() {
        return JSON_TYPE_ARRAY;
    }

    public String asJSON() {
        return asJSONArray(list, true);
    }

    public String asPrettyJSON() {
        return asJSON();
    }

    public JsonBase get(int i) {
        return list.get(i);
    }

    public void add(JsonBase v) {
        list.add((Value) v);
    }

    public void add(boolean b) {
        add( new BooleanValue(b) );
    }

    public void add(String s) {
        add( new StringValue(s) );
    }

    public void add(int i) {
        add( new NumericValue(i) );
    }

    public void add(double d) {
        add( new NumericValue(d) );
    }

    public void remove(int i) {
        list.remove(i);
    }

    public int size() {
        return list.size();
    }

    public Iterator<JsonBase> iterator() {
        return new ListIterator(list);
    }

    /**
     * Converts a list of {@link Value}s in a JSON array.
     *
     * @param list input list.
     * @param strict  if <code>true</code> a complete JSON will be returned.
     * @return JSON array string.
     */
    protected static String asJSONArray(List<Value> list, boolean strict) {
        StringBuilder sb = new StringBuilder();
        int i = 0, last = list.size() - 1;
        sb.append('[');
        for(Value v : list) {
            if( strict ) {
                sb.append( v.asJsonValue().asJSON() );
            } else {
                sb.append( v.asString().getNativeValue() );
            }
            if( i < last ) {
                sb.append(',');
            }
            i++;
        }
        sb.append(']');
        return sb.toString();
    }

   /**
     * Converts a list of {@link Value}s in a JSON array.
     *
     * @param list input list.
     * @return JSON array string.
     */
    protected static String asJSONArray(List<Value> list) {
       return asJSONArray(list, true);
   }

}
