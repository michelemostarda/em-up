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

import com.asemantics.mashup.processor.graph.Arc;
import com.asemantics.mashup.processor.graph.Graph;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonString;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Defines a value type representing a Graph.
 */
public class GraphValue extends Value<Graph<Value>> {

    /**
     * JSON type name.
     */
    public static final String JSON_TYPE = "object";

    /**
     *  Static value type name.
     */
    private static final StringValue VALUE_TYPE_NAME = new StringValue("graph");

    /**
     * Native graph object.
     */
    private Graph<Value> graph;

    /**
     * Constructor.
     *
     * @param g internal graph.
     */
    public GraphValue(Graph g) {
        if(g == null) {
            throw new IllegalArgumentException("g cannot be null.");
        }
        graph = g;
    }

    /**
     * Constructor.
     */
    public GraphValue() {
        this( new Graph<Value>() );
    }

    /**
     * Adds a node from the graph value.
     *
     * @param node node to add.
     */
    public void addNode(Value node) {
        graph.addNode(node);
    }

    /**
     * Removes a node from the graph value.
     *
     * @param node node to remove.
     * @return <code>true</code> if remotion appens.
     */
    public boolean removeNode(Value node) {
        return graph.removeNode(node);
    }

    /**
     * Adds an arc to the graph.
     *
     * @param from from value.
     * @param to to value.
     * @param label label value.
     */
    public void addArc(Value from, Value to, Value label) {
        graph.addArc(from, to, label);
    }

    /**
     * Removes an arc from the graph.
     *
     * @param from from value.
     * @param to to value.
     * @param label label value.
     */
    public void removeArc(Value from, Value to, Value label) {
        graph.removeArc(from, to, label);
    }

    public StringValue getValueTypeName() {
        return VALUE_TYPE_NAME;
    }

    public StringValue asString() {
        return new StringValue( asJSON() );
    }

    public NumericValue asNumeric() {
        return new NumericValue( graph.arcsSize() );
    }

    public BooleanValue asBoolean() {
        return graph.isEmpty() ? BooleanValue.FALSE_VALUE : BooleanValue.TRUE_VALUE;
    }

    @Override
    public String toString() {
        return asString().getNativeValue();
    }

    /**
     * Returns the list of all the present arcs represented as JSON.
     *
     * @return the list representation.
     */
    public ListValue asList() {
        ListValue result = new ListValue();
        Set<Arc<Value>> arcs = graph.getArcs();
        for(Arc<Value> arc : arcs) {
            result.add( new JsonValue( arcAsJSON(arc) ) );
        }
        return result;
    }

    /**
     * Returns a map of type <pre>< Arc -> null ></pre>.
     *
     * @return the map representation.
     */
    public MapValue asMap() {
        MapValue result = new MapValue();
        Set<Arc<Value>> arcs = graph.getArcs();
        for(Arc<Value> arc : arcs) {
            result.put( new JsonValue( arcAsJSON(arc) ), NullValue.getInstance() );
        }
        return result;
    }

    /**
     * Returns itself.
     *
     * @return the graph representation.
     */
    public GraphValue asGraph() {
        return this;
    }

    public BooleanValue equalsTo(Value v) {
        if( v == null) {
            return BooleanValue.FALSE_VALUE;
        }
        GraphValue other = v.asGraph();
        return new BooleanValue( graph.equals( other.graph ) );
    }

    public NumericValue comparesTo(Value v) {
         return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
    }

    public Value cloneValue() {
        GraphValue cloned = new GraphValue();
        Graph clonedGraph = graph.cloneGraph();
        cloned.graph = clonedGraph;
        return cloned;
    }

    public Graph<Value> getNativeValue() {
        return graph;
    }

    @Override
    public JsonValue asJsonValue() {
        return new JsonValue( graphAsJSON() );
    }

    public String getJsonType() {
        return JSON_TYPE;
    }

    public String asJSON() {
        return graphAsJSON().asJSON();
    }

    public String asPrettyJSON() {
        return graphAsJSON().asPrettyJSON();
    }

    /**
     * Converts an arc of the graph as a JSON object.
     * 
     * @param arc
     * @return
     */
    private JsonObject arcAsJSON(Arc<Value> arc) {
        JsonObject triple          = new MapValue();
        JsonObject predicateObject = new MapValue();
        predicateObject.put( arc.getArcLabel().toString(), arc.getToNode().toString() );
        triple.put( arc.getFromNode().toString(), predicateObject );
        return triple;
    }

    /**
     * Given a <i>sub</i>, <i>pred</i> and <i>obj</i>, it creates an JSON like:
     * <pre>
     *    { sub : { "pre" : "obj" } }
     * </pre>
     * @param sub
     * @param pred
     * @param obj
     * @return
     */
    private JsonObject createJsonTriple(String sub, String pred, String obj) {
        JsonObject triple          = new MapValue();
        JsonObject predicateObject = new MapValue();
        predicateObject.put( pred, obj );
        triple.put( sub, predicateObject );
        return triple;
    }

    /**
     * Converts a graph as JSON.
     * The details of the convertion are explained in the documentation.
     *
     * @return
     */
    private JsonObject graphAsJSON() {

        Map<Value,String> nodeToID  = new HashMap<Value,String>();
        Map<Value,String> labelToID = new HashMap<Value,String>();

        // Nodes map.
        Set<Value> nodes = graph.getNodes();
        JsonObject nodesObject = new MapValue();
        int i = 0;
        String nodeID;
        for(Value node : nodes) {
            nodeID = Value.ValueType.getCharIdentifier(node) + "" + i++;
            nodesObject.put(nodeID, node);
            nodeToID.put(node, nodeID);
        }

        // Labels map.
        Set<Value> labels = graph.getLabels();
        JsonObject arcsObject = new MapValue();
        int j = 0;
        String labelID;
        for(Value label : labels) {
            labelID = Value.ValueType.getCharIdentifier(label) + "" +  + j++;
            arcsObject.put(labelID, label);
            labelToID.put(label, labelID);
        }

        // Arcs.
        Set<Arc<Value>> arcs = graph.getArcs();
        JsonArray triplesArray = new ListValue();
        int k = 0;
        for(Arc<Value> arc : arcs) {
            triplesArray.add(
                    createJsonTriple(
                            nodeToID.get( arc.getFromNode()  ),
                            labelToID.get( arc.getArcLabel() ),
                            nodeToID.get( arc.getToNode()    )
                    )
            );

        }

        // Result.
        JsonObject result = new MapValue();
        result.put(NODES_OBJECT , nodesObject );
        result.put(LABELS_OBJECT, arcsObject  );
        result.put(ARCS_ARRAY, triplesArray);

        return result;
    }

    private static final String NODES_OBJECT  = "nodes";

    private static final String LABELS_OBJECT = "labels";

    private static final String ARCS_ARRAY    = "arcs";

    /**
     * The complementary of #graphAsJSON.
     *
     * An arc element can be one of:
     * <ol>
     * <li>Triple</li>
     * <pre>
     *  Fi : { Li : Ti }
     * </pre>
     * <li>Spread</li>
     * <pre>
     *  Fi : { L1i : T1i, L2i : T2i, ..., LNi : TNi }
     * </pre>
     * <li>Bag</li>
     * <pre>
     *  Fi : [ { L1i : T1i}, {L2i : T2i}, ..., {LNi : TNi} ]
     * </pre>
     * </ol>
     *
     * @param jsonObject JSON to be converted in graph.
     */
    //TODO: add spread and Bag support.
    public void loadJSONAsGraph(JsonObject jsonObject) {
        try {
            JsonObject nodesObject  = (JsonObject) jsonObject.get(NODES_OBJECT );
            JsonObject labelsObject = (JsonObject) jsonObject.get(LABELS_OBJECT);
            JsonArray  arcsArray    = (JsonArray)  jsonObject.get(ARCS_ARRAY   );

            // Load arcs.
            for(JsonBase arcElement : arcsArray) {
                // Triple / Spread.
                if( arcElement instanceof JsonObject) {
                    JsonObject triple = (JsonObject) arcElement;
                    if(triple.size() != 1 ) {
                        throw new RuntimeException("Unespected size.");
                    }
                    String from     = triple.getKeys()[0];
                    JsonObject body = (JsonObject) triple.get(from);
                    if(body.size() != 1) {
                        throw new RuntimeException("Expected triple body here.");
                    }
                    String label = body.getKeys()[0];
                    String to    = ((JsonString) body.get(label)).stringValue();

                    Value fromValue  = fromJSONToType( getType(from) , nodesObject.get(from)   );
                    Value toValue    = fromJSONToType( getType(to)   , nodesObject.get(to)     );
                    Value labelValue = fromJSONToType( getType(label), labelsObject.get(label) );

                    addArc( fromValue, toValue, labelValue );
                }
            }

            // Load nodes.
            for(Map.Entry<String,JsonBase> entry : nodesObject) {
                addNode( fromJSONToType( getType(entry.getKey()), entry.getValue() ) );
            }

        } catch (RuntimeException re) {
            throw new RuntimeException("Error while converting JSON in GraphValue.", re);
        }
    }

    private char getType(String name) {
        return name.charAt(0);
    }

    private Value fromJSONToType(char type, JsonBase base) {
        return Value.ValueType.getValueType(type).convertToType( new JsonValue(base) );
    }
}
