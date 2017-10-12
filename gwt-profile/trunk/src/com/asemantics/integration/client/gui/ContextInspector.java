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

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Tree;
import com.asemantics.mashup.processor.Invocable;
import com.asemantics.mashup.processor.ProcessorListener;
import com.asemantics.mashup.interpreter.Interpreter;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides inspection facility on {@link com.asemantics.mashup.interpreter.Interpreter}
 * context.
 *
 * @see com.asemantics.mashup.digester.Context
 */
public class ContextInspector extends Panel implements ProcessorListener {

    /**
     * Attribute used to bind TreeNode with Invocable object.
     */
    protected static final String INVOCABLE_ATTRIBUTE = "invocable_attribute";

    /**
     * Root nodes for native predicates.
     */
    protected final TreeNode NATIVE_PREDICATES_ROOT = new TreeNode("Native Predicates");

    /**
     * Root nodes fot user predicates.
     */
    protected final TreeNode USER_PREDICATES_ROOT   = new TreeNode("User Predicates");

    /**
     * Interpreter providing context.
     */
    private Interpreter interpreter;

    /**
     * Key index progressive counter.
     */
    protected static int keyIndex = 0;

    /**
     * Context tree.
     */
    private TreePanel contextTreePanel;

    /**
     * Details panel.
     */
    private Panel detailsPanel;

    /**
     * Node key to invocable map.
     */
    private Map<String,Invocable> nodeToInvocableMap;

    /**
     * Invocable name to node.
     */
    private Map<String,TreeNode> invocableNameToNode;

    /**
     * Panel window.
     */
    private Window window;

    /**
     * Currently selected node.
     */
    private transient TreeNode ctxNode;

    /**
     * Listeners.
     */
    private List<ContextInspectorListener> listeners = new ArrayList();


    /**
     * Constructor.
     */
    public ContextInspector(Interpreter interpreter) {

        if(interpreter == null) {
            throw new NullPointerException("interpreter cannot be null.");
        }
        this.interpreter = interpreter;

        contextTreePanel = new TreePanel();
        contextTreePanel.setAutoScroll(true);

        detailsPanel     = new Panel("Details");
        detailsPanel.setCollapsible(true);
        detailsPanel.setHeight(100);

        setLayout( new BorderLayout() );
        add( contextTreePanel, new BorderLayoutData(RegionPosition.CENTER) );
        add( detailsPanel,     new BorderLayoutData(RegionPosition.SOUTH) );

        TreeNode rootNode = new TreeNode("Context");
        rootNode.appendChild(NATIVE_PREDICATES_ROOT);
        rootNode.appendChild(USER_PREDICATES_ROOT);
        contextTreePanel.setRootNode(rootNode);

        // Registering TreePanel listener.
        contextTreePanel.addListener(
            new TreePanelListenerAdapter() {

                public void onClick(TreeNode node, EventObject e) {
                    ctxNode = node;
                    showNodeInfo( node, e);
                    notifyPredicateSelected( node.getText(), getInvocable(node) );
                }

                public void onDblClick(TreeNode node, EventObject e) {
                    notifyPredicateDblClicked( node.getText(), getInvocable(node) );
                }

                public void onMoveNode(Tree treePanel, TreeNode node, TreeNode oldParent, TreeNode newParent, int index) {
                    ctxNode = node;
                }

                public void onContextMenu(TreeNode node, EventObject e) {
                    // Empty.
                }
            }
        );

        nodeToInvocableMap  = new HashMap<String,Invocable>();
        invocableNameToNode = new HashMap<String,TreeNode>();

        interpreter.addProcessorListener(this);
        initTree();
    }

    public void addNativePredicate(String name, Invocable invocable) {
        addPredicate(NATIVE_PREDICATES_ROOT, name, invocable);
    }

    public void userPredicateAdded(String name, Invocable invocable) {
        addPredicate(USER_PREDICATES_ROOT, name, invocable);
    }

    /**
     * Shows this panel inside a window.
     */
    @Override
    public void show() {
        if(window == null) {
            // Initialize window.
            window = new Window("Context Inspector", false, true);
            window.setClosable(true);
            window.setPlain(true);
            window.setLayout(new BorderLayout());
            BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);
            centerData.setMargins(0, 0, 0, 0);
            window.setCloseAction(Window.CLOSE);

            window.add(this, new BorderLayoutData(RegionPosition.CENTER));

            // Deregisters processor listener.
            window.addListener(
                new WindowListenerAdapter() {
                    public boolean doBeforeClose(Panel panel) {
                        interpreter.removeProcessorListener(ContextInspector.this);
                        return true;
                    }
                }
            );

        }

        // Showing window.
        window.setWidth(300);
        window.setHeight(400);
        window.show();
    }

    /**
     * Registers a listener.
     *
     * @param cil
     */
    public void addListener(ContextInspectorListener cil) {
        listeners.add(cil);
    }

    /**
     * Removes a registered listener.
     *
     * @param cil
     */
    public void removeListener(ContextInspectorListener cil) {
        listeners.remove(cil);
    }

    /**
     * Initializes tree with pre loaded data.
     */
    protected void initTree() {
        // Init natives.
        Map<String,Invocable> natives = interpreter.getProcessor().getExecutionContext().getNativeInvocables();
        for( Map.Entry<String,Invocable> entry : natives.entrySet() ) {
            addNativePredicate(entry.getKey(), entry.getValue());
        }
        // Init programmatives.
        Map<String,Invocable> nonnatives = interpreter.getProcessor().getExecutionContext().getNotNativeInvocables();
        for( Map.Entry<String,Invocable> entry : nonnatives.entrySet() ) {
            userPredicateAdded(entry.getKey(), entry.getValue());
        }

    }

    protected void addPredicate(TreeNode root, String name, Invocable invocable) {
        TreeNode newNode = new TreeNode(name);

        String key = nextKey();
        nodeToInvocableMap.put(key, invocable);
        newNode.setAttribute(INVOCABLE_ATTRIBUTE, key);

        invocableNameToNode.put(name, newNode);

        newNode.setText(name);
        root.appendChild(newNode);
    }

    protected TreeNode getNodeOfInvocable(String invocableName) {
        return invocableNameToNode.get(invocableName);
    }

    protected void removeNodeByInvocableName(String invocableName) {
        TreeNode tobeRemoved = getNodeOfInvocable(invocableName);
        String key           = tobeRemoved.getAttribute(INVOCABLE_ATTRIBUTE);
        invocableNameToNode.remove(invocableName);
        nodeToInvocableMap.remove(key);
        tobeRemoved.remove();
    }

    protected Invocable getInvocable(String key) {
        return nodeToInvocableMap.get(key);
    }

    protected Invocable getInvocable(TreeNode tn) {
        String key = tn.getAttribute(INVOCABLE_ATTRIBUTE);
        if(key == null) { return null; }
        return getInvocable(key);
    }

    protected String nextKey() {
        return "invocable_" + keyIndex++;
    }

    private void setDescription(String txt) {
        detailsPanel.setHtml(txt);
    }

    private void showNodeInfo(TreeNode node, EventObject e) {
        Invocable invocable = getInvocable(node);
        if(invocable == null) { return; }
        setDescription( invocable.getDescription() );
    }


    public void nativePredicateAdded(String name, Invocable invocable) {
        addNativePredicate(name, invocable);
    }

    public void addedProgrammativePredicate(String name, Invocable invocable) {
        userPredicateAdded(name, invocable);
    }

    public void predicateRemoved(String name, Invocable invocable) {
        removeNodeByInvocableName(name);
    }

    private void notifyPredicateSelected(String predicateName, Invocable predicate) {
        for(ContextInspectorListener cil : listeners) {
            try {
                cil.predicateSelected(predicateName, predicate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyPredicateDblClicked(String predicateName, Invocable predicate) {
        for(ContextInspectorListener cil : listeners) {
            try {
                cil.predicateDblClicked(predicateName, predicate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
