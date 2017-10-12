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


package com.asemantics.integration.client.gui;

import com.asemantics.mashup.common.Utils;
import com.asemantics.mashup.nativepkg.NativeException;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonComplex;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonSimple;
import com.asemantics.mashup.processor.JsonValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.ExtElement;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.Tree;
import com.gwtext.client.widgets.Editor;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.EditorListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.TreeEditor;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

import java.util.Map;

/**
 * Defines a utility panel to inspect - modify JSON data.
 */
public class ObjectInspector extends Window {

    /**
     * Node type qualifies.
     */
     enum JsonNodeType {
        NULL,
        BOOLEAN,
        STRING,
        NUMERIC,
        ARRAY,
        OBJECT,
        OBJECT_KEY,
        ROOT;

        public static JsonNodeType getType(String typeStr) {
            for( JsonNodeType type : JsonNodeType.values() ) {
                if(type.toString().equals(typeStr)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * @param t type to check.
         * @return <code>true</code> if simple JSON type, <code>false</code>
         *         otherwise. 
         */
        public static boolean isSimple(JsonNodeType t) {
            return t == NULL || t == BOOLEAN || t == STRING || t == NUMERIC;
        }
    }

    /**
     * Tree panel representing JSON tree view.
     */
    private final TreePanel treePanel = new TreePanel();

    /**
     * Root node.
     */
    private JsonTreeNode root = new JsonTreeNode("root", JsonNodeType.ROOT);

    /**
     * Editor used to edit JSON text.
     */
    private TextArea jsonBox;

    /**
     * Json Editor panel.
     */
    private Panel jsonEditor;

    /**
     * Right click menu.
     */
    private Menu menu;

    /**
     * Tree node editor.
     */
    private TreeEditor treeEditor;

    /**
     * Selected tree node.
     */
    private TreeNode ctxNode;

    /**
     * JSON model couterpart.
     */
    private JsonBase model;

    /**
     * Constructor.
     *
     * @param jb initial JSON model.
     */
    public ObjectInspector(JsonBase jb) {
        if (jb == null) {
            throw new NullPointerException();
        }

        // Configures tree panel.
        treePanel.setWidth(400);
        treePanel.setHeight(400);
        treePanel.setTitle("Editable Tree");
        treePanel.setAnimate(true);
        treePanel.setEnableDD(true);
        treePanel.setContainerScroll(true);
        treePanel.setRootVisible(true);

        // Registers root in tree panel.
        treePanel.setRootNode(root);

        treePanel.addTool(
                new Tool(
                        Tool.REFRESH,
                        new Function() {
                            public void execute() {

                                // Loading effect.
                                treePanel.getEl().mask("Loading", "x-mask-loading");

                                // Reloading model.
                                try {
                                    JsonBase json = NativeImpl.getInstance().parseJSON( jsonBox.getText() );
                                    setModel(json);
                                } catch (NativeException e) {
                                    MessageBox.alert("Cannot parse JSON: " + e.getMessage());
                                }

                                // Exapand node effect.
                                root.collapse(true, false);
                                Timer timer = new Timer() {
                                    public void run() {
                                        treePanel.getEl().unmask();
                                        treePanel.getEl().unmask();
                                        root.expand(true, true);
                                    }
                                };
                                timer.schedule(1000);
                            }
                        },
                        "Refresh"
                )
        );

        // Registering TreeNode editor.
        TextField field = new TextField();
        field.setSelectOnFocus(true);
        treeEditor = new TreeEditor(treePanel, field);
        treeEditor.addListener( new JsonNodeEditorListener() );

        // Registering TreePanel listener.
        treePanel.addListener(
            new TreePanelListenerAdapter() {
                public void onClick(TreeNode node, EventObject e) {
                    ctxNode = node;
                }

                public void onMoveNode(Tree treePanel, TreeNode node, TreeNode oldParent, TreeNode newParent, int index) {
                    ctxNode = node;
                }

                public void onContextMenu(TreeNode node, EventObject e) {
                    showContextMenu( node, e);
                }
            }
        );

        // Configuring Javascript editor.
        jsonEditor = new Panel( "JSON Editor" );
        jsonEditor.addTool(
                new Tool(
                        Tool.REFRESH,
                        new Function() {
                            public void execute() {
                                JsonBase base = toJsonBase(root);
                                jsonBox.setText( base.asPrettyJSON() ); 
                            }
                        },
                        "Reload"
                )
        );
        jsonEditor.setWidth(400);
        jsonEditor.setShadow(true);
        jsonEditor.setBorder(true);
        jsonBox = new TextArea();
        jsonBox.setWidth("395px");
        jsonEditor.add( jsonBox );

        // Defines the tree panel.
        Panel panel = new Panel();
        panel.setBorder(false);
        panel.setPaddings(15);
        panel.setLayout(new VerticalLayout(2));

        // Adding components.
        panel.add(treePanel);
        panel.add(jsonEditor);

        // Loads given model.
        setModel(jb);

        // Window configuration.
        setTitle("JSON Inspector");
        setClosable(true);
        setPlain(true);
        setLayout(new BorderLayout());
        BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
        centerData.setMargins(0, 0, 0, 0);
        setCloseAction(Window.CLOSE);

        add(panel, centerData);
    }

    @Override
    public void show() {
        setWidth(450);
        setHeight(510);
        super.show();
    }

    /**
     * Sets model in panel.
     *
     * @param json
     */
    public void setModel(JsonBase json) {
        model = json;
        load(model);
    }

    public void load(JsonBase jb) {
        removeAllChildren(root);
        loadNode(root, jb);
        root.expand();
        treePanel.expandAll();
        setCode( jb.asPrettyJSON() );
    }

    /**
     * Reloads model.
     */
    public void reload() {
        load(model);
    }

    /**
     * Sets code inside JSON box.
     *
     * @param code
     */
    private void setCode(String code) {
        jsonBox.setText(code);
    }

    /**
     * Utility method used to remove all children from a node.
     * @param parent
     */
    private void removeAllChildren(TreeNode parent) {
        Node[] children = parent.getChildNodes();
        for (Node node : children) {
            parent.removeChild(node);
        }
    }

    /* BEGIN: JSON to Tree Model conversion methods. */

    /**
     * Loads a JSON structure as child of given node.
     *
     * @param node
     * @param base
     */
    private void loadNode(TreeNode node, JsonBase base) {
        //TODO: fix this.
        if(base instanceof JsonValue) {
            base = ((JsonValue) base).getNativeValue();
        }
        if (base.isSimple()) {
            loadNode(node, (JsonSimple) base);
        } else {
            loadNode(node, (JsonComplex) base);
        }
    }

    /**
     * Loads a JSON complex structure as child of given node.
     *
     * @param node
     * @param complex
     */
    private void loadNode(TreeNode node, JsonComplex complex) {
        if (complex.isArray()) {
            loadNode(node, (JsonArray) complex);
        } else {
            loadNode(node, (JsonObject) complex);
        }
    }

    /**
     * Loads a JSON array as child of given node.
     *
     * @param node
     * @param array
     */
    private void loadNode(TreeNode node, JsonArray array) {
        JsonTreeNode arrayNode = new JsonTreeNode("array", JsonNodeType.ARRAY);
        node.appendChild(arrayNode);
        for (JsonBase base : array) {
            loadNode(arrayNode, base);
        }
    }

    /**
     * Loads a JSON object as child of given node.
     *
     * @param node
     * @param object
     */
    private void loadNode(TreeNode node, JsonObject object) {
        JsonTreeNode objectNode = new JsonTreeNode("object", JsonNodeType.OBJECT);
        node.appendChild(objectNode);
        for (Map.Entry<String, JsonBase> entry : object) {
            TreeNode key = new JsonTreeNode(entry.getKey(), JsonNodeType.OBJECT_KEY );
            objectNode.appendChild(key);
            loadNode(key, entry.getValue());
        }
    }

    /**
     * Loads a JSON basic type as child of given node.
     *
     * @param node
     * @param simple
     */
    private void loadNode(TreeNode node, JsonSimple simple) {
        node.appendChild( new JsonTreeNode(simple.asJSON(), asType(simple) ) );
    }

    /* END: Json to Tree Model */

    /* BEGIN: TreeModel to JSON */

    private JsonBase toJsonBase(JsonTreeNode jtn) {
        JsonBase json = asJson( jtn.getNodeType(), jtn.getText() );
        if( json.isComplex() ) {
            toJsonBase(jtn, (JsonComplex) json);
        }
        return json;
    }

    private void toJsonBase(JsonTreeNode jtn, JsonComplex complex) {
        if( complex.isArray() ) {
            JsonArray array = (JsonArray) complex;
            Node[] children = jtn.getChildNodes();
            for( Node child : children ) {
                JsonTreeNode childJtn = new JsonTreeNode(child);
                array.add( toJsonBase(childJtn) );
            }
        } else if( complex.isObject() ) {
            JsonObject object = (JsonObject) complex;
            Node[] children = jtn.getChildNodes();
            for( Node child : children ) {
                JsonTreeNode key = new JsonTreeNode(child);
                assert key.getNodeType().equals( JsonNodeType.OBJECT_KEY ) : "Expected key here.";
                Node[] childChildren =  key.getChildNodes();
                assert childChildren.length == 1;
                JsonBase value = toJsonBase( new JsonTreeNode(childChildren[0]) );
                object.put(  key.getText(), value );
            }
        } else {
            throw new IllegalArgumentException("Unknown type: " + complex.asJSON() );
        }
    }

    /* END: TreeModel to JSON */

    private JsonNodeType asType(JsonBase base) {
        if(base.isNull()) {
            return JsonNodeType.NULL;
        }
        if(base.isBoolean()) {
            return JsonNodeType.BOOLEAN;
        }
        if(base.isNumeric()) {
            return JsonNodeType.NUMERIC;
        }
        if(base.isString()) {
            return JsonNodeType.STRING;
        }
        if(base.isArray()) {
            return JsonNodeType.ARRAY;
        }
        if(base.isObject()) {
            return JsonNodeType.OBJECT;
        }
        throw new IllegalArgumentException("Unknown type: " + base);
    }

    private JsonBase asJson(JsonNodeType type, String value) {
        if(type == JsonNodeType.NULL) {
            return JsonFactory.newJsonNull();
        }
        if(type == JsonNodeType.BOOLEAN) {
            return JsonFactory.newJsonBoolean(Boolean.parseBoolean(value));
        }
        if(type == JsonNodeType.NUMERIC) {
            return JsonFactory.newJsonDouble(Double.parseDouble(value));
        }
        if(type == JsonNodeType.STRING || type == JsonNodeType.OBJECT_KEY) {
            return JsonFactory.newJsonString(value);
        }
        if(type == JsonNodeType.ARRAY || type == JsonNodeType.ROOT) {
            return JsonFactory.newJsonArray();
        }
        if(type == JsonNodeType.OBJECT) {
            return JsonFactory.newJsonObject();
        }
        throw new IllegalArgumentException("Unknown jsonType: " + type);
    }

    private void showContextMenu(final TreeNode node, EventObject e) {
        if (menu == null) {

            menu = new Menu();

            // Edit menu.
            Item editItem = new Item("Edit", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    treeEditor.startEdit(ctxNode);
                }
            });
            editItem.setId("edit-item");
            menu.addItem(editItem);

            // Clone menu.
            Item cloneItem = new Item("Clone", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    TreeNode clone = ctxNode.cloneNode();
                    clone.setText("Copy of " + clone.getText());
                    ctxNode.getParentNode().appendChild(clone);
                    treeEditor.startEdit(clone);
                }
            });
            cloneItem.setId("clone-item");
            menu.addItem(cloneItem);

            // New array.
            Item newArrayItem = new Item("New array", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    TreeNode arrayNode = new JsonTreeNode("array", JsonNodeType.ARRAY);
                    TreeNode arrayNodeElem = new JsonTreeNode("\"element0\"", JsonNodeType.STRING);
                    arrayNode.appendChild(arrayNodeElem);

                    // Establishing insertion type.
                    JsonNodeType parentNodeType = getNodeType(ctxNode);
                    if(parentNodeType == JsonNodeType.ARRAY) {
                        ctxNode.appendChild(arrayNode);
                    } else { // Object.
                        assert parentNodeType == JsonNodeType.OBJECT : "Unespected type.";
                        TreeNode arrayKeyNode = new JsonTreeNode("newobject", JsonNodeType.OBJECT_KEY);
                        arrayKeyNode.appendChild(arrayNode);
                        ctxNode.appendChild(arrayKeyNode);
                    }

                    treeEditor.startEdit(arrayNode);
                }
            });
            newArrayItem.setId("newarray-item");
            menu.addItem(newArrayItem);

            // New object.
            Item newObjectItem = new Item("New object", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    TreeNode objectNode = new JsonTreeNode("object", JsonNodeType.OBJECT);
                    TreeNode keyNode    = new JsonTreeNode("key", JsonNodeType.OBJECT_KEY);
                    TreeNode valueNode  = new JsonTreeNode("\"value\"", JsonNodeType.STRING);
                    objectNode.appendChild( keyNode );
                    keyNode.appendChild( valueNode );

                    // Establishing insertion type.
                    JsonNodeType parentNodeType = getNodeType(ctxNode);
                    if(parentNodeType == JsonNodeType.ARRAY) {
                        ctxNode.appendChild(objectNode);
                    } else { // Object.
                        assert parentNodeType == JsonNodeType.OBJECT : "Unespected type.";
                        TreeNode arrayKeyNode = new JsonTreeNode("newobject", JsonNodeType.OBJECT_KEY);
                        arrayKeyNode.appendChild(objectNode);
                        ctxNode.appendChild(arrayKeyNode);
                    }

                    treeEditor.startEdit(objectNode);
                }
            });
            newObjectItem.setId("newobject-item");
            menu.addItem(newObjectItem);

             // Convert to ...
            Menu subMenu = new Menu();

            // Convert to null.
            Item asNullItem = new Item("null", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                     setNodeType(ctxNode, JsonNodeType.NULL);
                }
            });
            asNullItem.setId("convert-to-null-item");
            subMenu.addItem(asNullItem);

            // Convert to boolean.
            Item asBooleanItem = new Item("boolean", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    setNodeType(ctxNode, JsonNodeType.BOOLEAN);
                }
            });
            asBooleanItem.setId("convert-to-boolean-item");
            subMenu.addItem(asBooleanItem);

            // Convert to boolean.
            Item asNumericItem = new Item("numeric", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    setNodeType(ctxNode, JsonNodeType.NUMERIC);
                }
            });
            asNumericItem.setId("convert-to-numeric-item");
            subMenu.addItem(asNumericItem);

            // Convert to string.
            Item asStringItem = new Item("string", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    setNodeType(ctxNode, JsonNodeType.STRING);
                }
            });
            asStringItem.setId("convert-to-string-item");
            subMenu.addItem(asStringItem);

            /*
            // Convert to array.
            Item asArrayItem = new Item("array", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                }
            });
            asArrayItem.setId("convert-to-array-item");
            subMenu.addItem(asArrayItem);

            // Convert to object.
            Item asObjectItem = new Item("object", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {

                }
            });
            asObjectItem.setId("convert-to-object-item");
            subMenu.addItem(asObjectItem);
            */

            MenuItem convertToItem = new MenuItem("Convert to ...", subMenu);
            convertToItem.setId("convert-to-item");
            menu.addItem(convertToItem);

        }

        // Assignment of Context node.
        ctxNode = node;

        // Handles item activations.
        JsonNodeType jsonNodeType = getNodeType(ctxNode);
        if( jsonNodeType == JsonNodeType.OBJECT || jsonNodeType == JsonNodeType.ARRAY ) {
            menu.getItem("newobject-item").enable();
            menu.getItem("newarray-item").enable();
        } else {
            menu.getItem("newobject-item").disable();
            menu.getItem("newarray-item").disable();
        }

        // Handles convert item activation.
        if( JsonNodeType.isSimple( getNodeType(ctxNode) ) ) {
            menu.getItem("convert-to-item").enable();
        } else {
            menu.getItem("convert-to-item").disable();
        }

        menu.showAt(e.getXY());
    }

    private static final String TYPE_ATTRIBUTE = "type_attribute";


    static JsonNodeType getNodeType(Node node) {
        return JsonNodeType.getType( node.getAttribute(TYPE_ATTRIBUTE) );
    }

    static void setNodeType(TreeNode node, JsonNodeType type) {
        String currentValue = node.getText();
        String newValue = null;
        if( type == JsonNodeType.NULL) {
            newValue = "null";
        } else if( type == JsonNodeType.NUMERIC) {
            try {
                newValue = Double.toString( Double.parseDouble( currentValue ) );
            } catch (Exception e) {
                newValue = "0";
            }
        } else if( type == JsonNodeType.BOOLEAN ) {
            try {
                newValue = Boolean.toString( Boolean.parseBoolean( currentValue ) );
            } catch (Exception e) {
                newValue = Boolean.FALSE.toString();
            }
        } else if( type == JsonNodeType.STRING) {
            newValue =
                    ( currentValue.charAt(0) == '"' ? "" : '"') +
                    currentValue +
                    ( currentValue.charAt(currentValue.length() - 1) == '"' ? "" : '"'); 
        } else {
            throw new IllegalStateException("Unknown type: " + type);
        }

        node.setAttribute(TYPE_ATTRIBUTE, type.toString());
        node.setText(newValue);
    }

    /**
     * Extends TreeNode to define a node able to represent a JSON type.
     */
    class JsonTreeNode extends TreeNode {

        private Node inner;

        /**
         * Constructor for boxing.
         *
         * @param id
         * @param type
         */
        JsonTreeNode(String id, JsonNodeType type) {
            super(id);
            inner = this;
            setAttribute(TYPE_ATTRIBUTE, type.toString());
        }

        /**
         * Constructor for unboxing.
         *
         * @param treeNode
         */
        JsonTreeNode(Node treeNode) {
            inner = treeNode;
        }

        /**
         * @return the JSON type.
         */
        public JsonNodeType getNodeType() {
            return ObjectInspector.getNodeType(inner);
        }

        @Override
        public Node[] getChildNodes() {
            return inner == this ? super.getChildNodes() : inner.getChildNodes();
        }

        @Override
        public String getText() {
            String text = Utils.decodeStringAsUnicode( inner == this ? super.getText() : ((TreeNode) inner).getText() );
            if( getNodeType() == JsonNodeType.STRING) {
                return text.substring(1, text.length() - 1 );
            }
            return text;
        }
    }

    /**
     * Checks the input type.
     */
    class JsonNodeEditorListener extends EditorListenerAdapter {

        /**
         * Handle start editing.
         * Avoid editing of root, array and object nodes.
         *
         * @param source
         * @param boundEl
         * @param value
         * @return
         */
        @Override
        public boolean doBeforeStartEdit(Editor source, ExtElement boundEl, Object value) {
            JsonNodeType nodeType = getNodeType(ctxNode);
            if( nodeType == JsonNodeType.ROOT || nodeType == JsonNodeType.OBJECT || nodeType == JsonNodeType.ARRAY) {
                return false;
            }
            return true;
        }

        /**
         * Handles validation of performed editing.
         * Checks that edited nodes preserves their own types.
         *
         * @param source
         * @param value
         * @param startValue
         * @return
         */
        @Override
        public boolean doBeforeComplete(Editor source, Object value, Object startValue) {
            String valueString = (String) value;

            // Null handling.
            if( "null".equals(valueString) ) {
                return true;
            }

            JsonNodeType expectedType = getNodeType(ctxNode);
            // String.
            if( expectedType == JsonNodeType.STRING ) {
                if( valueString.charAt(0) == '"' && valueString.charAt( valueString.length() - 1 ) == '"') {
                    return true;
                }
                MessageBox.alert("Expected string here.");
                return false;
            // Boolean.
            } else if( expectedType == JsonNodeType.BOOLEAN) {
                if( "true".equals(valueString) || "false".equals(valueString) ) {
                    return true;
                }
                MessageBox.alert("Expected boolean here.");
                return false;
            // Number.
            } else if( expectedType == JsonNodeType.NUMERIC ) {
                try {
                    Double.parseDouble( valueString );
                    return true;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            } else {
                return true;
            }
        }

    }

}

