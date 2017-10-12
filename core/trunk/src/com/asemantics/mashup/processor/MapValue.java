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

import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Defines the <i>map</i> value.
 */
public class MapValue extends Value<Map<Value,Value>> implements JsonObject {

    /**
     * Provides an iterator over map entries.
     */
    class MapIterator implements Iterator<Map.Entry<String,JsonBase>> {

        private Iterator<Map.Entry<String,JsonBase>> iterator;

        MapIterator(Map<Value,Value> map) {
            Map<String,JsonBase> converted = new HashMap<String,JsonBase>();
            for( Map.Entry<Value,Value> entry : map.entrySet() ) {
                converted.put( entry.getKey().asString().getNativeValue(), entry.getValue().asJsonValue().getNativeValue() );
            }
            iterator = converted.entrySet().iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Map.Entry<String,JsonBase> next() {
            return iterator.next();
        }

        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue("map");

    /**
     * Internal representation as {@link java.util.HashMap}.
     */
    private HashMap<Value,Value> map;

    /**
     * Constructor with empty map.
     */
    public MapValue() {
        map = new HashMap<Value,Value>();
    }

    /**
     * Constructor for single entry.
     * 
     * @param k
     * @param v
     */
    public MapValue(Value k, Value v) {
        HashMap<Value,Value> hm = new HashMap<Value, Value>();
        hm.put(k, v);
        map = hm;
    }

    /**
     * Constructor for collections.
     *
     * @param values collection of values.
     */
    public MapValue(Collection<Value> values) {
        HashMap<Value,Value> hm = new HashMap<Value, Value>();
        for(Value v : values) {
            hm.put(v, null);
        }
        map = hm;
    }

    /**
     * Constructor from HashMap.
     *
     * @param m initial map.
     */
    protected MapValue( HashMap<Value,Value> m) {
        if( m == null ) {
            throw new NullPointerException();
        }
        map = m;
    }

    /**
     * Adds an entry to map.
     *
     * @param key object key.
     * @param value object value.
     * @return last value inside map associated with <i>key</i>.
     */
    public Value put(Value key, Value value) {
        Value result = map.put(key,value);
        return result == null ? NullValue.getInstance() : result;
    }

    /**
     * Retrieves an entry from map.
     *
     * @param key object key.
     * @return found entry.
     */
    public Value get(Value key) {
        Value result = map.get(key);
        return result == null ? NullValue.getInstance() : result;
    }

    /**
     * Returns the list of original keys as values.
     *
     * @return list of key values.
     */
    public Value[] getKeyValues() {
        return map.keySet().toArray( new Value[ map.keySet().size() ] );
    }

    /**
     * Removes an entry from map.
     *
     * @param key
     * @return remove entry.
     */
    public Value remove(Value key) {
        Value result = map.remove(key);
        return result == null ? NullValue.getInstance() : result;
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        String jsonObject = asJSONObject(map, true);
        return new StringValue( jsonObject );
    }

    public NumericValue asNumeric() {
        return new NumericValue( map.size() );
    }

    public BooleanValue asBoolean() {
        return new BooleanValue( ! map.isEmpty() ); 
    }

    public ListValue asList() {
        return new ListValue( new LinkedList<Value>( map.keySet() ) );
    }

    public MapValue asMap() {
        return this;
    }

    /**
     * Default arc used to convert a map value to a graph value.
     */
    public static final StringValue ARC_VALUE = new StringValue("->");

    public GraphValue asGraph() {
        GraphValue graphValue = new GraphValue();
        for( Map.Entry<Value,Value> entry : map.entrySet() ) {
            graphValue.addArc(entry.getKey(), entry.getValue(), ARC_VALUE);
        }
        return graphValue;
    }

    public BooleanValue equalsTo(Value v) {
        if( v != null ) {
            return new BooleanValue( map.equals( v.asMap().map ) );
        }
        return BooleanValue.FALSE_VALUE;
    }

    public NumericValue comparesTo(Value v) {
        return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        MapValue cloned = new MapValue();
        HashMap<Value,Value> clonedMap = new HashMap<Value,Value>();
        for(Map.Entry<Value,Value> entry : map.entrySet() ) {
            clonedMap.put(entry.getKey().cloneValue(), entry.getValue().cloneValue() );
        }
        cloned.map = clonedMap;
        return cloned;
    }

    public Map<Value,Value> getNativeValue() {
        return map;
    }

    public boolean containsKey(String key) {
        return map.containsKey( new StringValue(key) );
    }

    public JsonBase get(String key) {
        Value result = map.get( new StringValue(key) );
        return result == null ? null : result.asJsonValue().getNativeValue();
    }

    public void put(String key, JsonBase value) {
        // TODO: find a better solution.
        if( value instanceof JsonValue) {
            map.put( new StringValue(key), (JsonValue) value );
        } else {
            map.put( new StringValue(key), new JsonValue(value) );
        }
    }

    public void put(String key, boolean value) {
        put(key, new BooleanValue(value) );
    }

    public void put(String key, String value) {
        put(key, new StringValue(value) );
    }

    public void put(String key, int value) {
        put(key, new NumericValue(value) );
    }

    public void put(String key, double value) {
        put(key, new NumericValue(value) );
    }

    public String[] getKeys() {
        String[] result = new String[ map.keySet().size() ];
        int i = 0;
        for( Value key : map.keySet() ) {
            result[i++] = key.asString().getNativeValue();
        }
        return result;
    }

    public int size() {
        return map.size();
    }

    public String getJsonType() {
        return JSON_TYPE_OBJECT;
    }

    public String asJSON() {
        return asJSONObject(map, true);
    }

    public String asPrettyJSON() {
        return asJSON();
    }

    public Iterator<Map.Entry<String,JsonBase>> iterator() {
       return new MapIterator(map);
    }

    protected static String asJSONObject(Map<Value,Value> map, boolean strict) {
        StringBuilder sb = new StringBuilder();
        final int lastCommaIndex = map.size() - 2;
        int i = 0;
        sb.append("{");
        for( Map.Entry<Value,Value> entry : map.entrySet() ) {
            if( strict ) {
            sb.append("\"").append( entry.getKey().asString().getNativeValue() ).append("\"");
            } else {
                sb.append( entry.getKey().asString().getNativeValue() );
            }
            sb.append(":");
            if( strict ) {
                Value entryValue = entry.getValue();
                sb.append( entryValue == null ? NullValue.getInstance().asJSON() : entryValue.asJSON() );
            } else {
                sb.append( entry.getValue().asString().getNativeValue() );
            }
            if( i++ <= lastCommaIndex ) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
