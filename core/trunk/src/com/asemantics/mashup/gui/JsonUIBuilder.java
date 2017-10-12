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


package com.asemantics.mashup.gui;

import static com.asemantics.mashup.gui.UIAttribute.*;
import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonArray;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Implementation of {@link com.asemantics.mashup.gui.UIBuilder}
 * that generates a <i>JSON UI</i> model object hierarchy.
 */
public class JsonUIBuilder implements UIBuilder<JsonObject> {

    /**
     * Element of JSON stack.
     * A generic object is espressed in the form:
     * <pre>
     *  { "component_type" : { ... component body ... } }
     * </pre>
     * where the internal object is the body and the external
     * object is the head.
     */
    class StackElement {

        /**
         * The JSON object head.
         */
        private JsonObject head;

        /**
         * the JSON object body.
         */
        private JsonObject body;

        /**
         * is container flag.
         */
        private boolean isContainer;

        /**
         * Constructor.
         *
         * @param jsonObject
         * @param isContainer
         */
        StackElement(JsonObject head, JsonObject jsonObject, boolean isContainer) {
            this.head = head;
            this.body = jsonObject;
            this.isContainer = isContainer;
        }
    }

    /**
     * Stack of JSON objects.
     */
    private Stack<StackElement> jsonStack;

    /**
     * Current object.
     */
    private JsonObject currentObject;

    /**
     * Constructor.
     */
    public JsonUIBuilder() {
        jsonStack   = new Stack<StackElement>();
    }

    public UIBuilder createButton() {
        pushInStack(UIButton.NAME, createJSONButton(), false);
        return this;
    }

    public UIBuilder createLabel() {
        pushInStack(UILabel.NAME, createJSONLabel(), false);
        return this;
    }

    public UIBuilder createTextArea() {
        pushInStack(UITextArea.NAME, createJSONTextArea(), false);
        return this;
    }

    public UIBuilder createList() {
        pushInStack(UIList.NAME, createJSONList(), false);
        return this;
    }

    public UIBuilder createPanel() {
        pushInStack(UIPanel.NAME, createJSONContainer(), true);
        return this;
    }

    public UIBuilder createWindow() {
        pushInStack(UIWindow.NAME, createJSONWindow(), true);
        return this;
    }

    public UIBuilder pop() {
        if( ! jsonStack.isEmpty() ) {
            jsonStack.pop();
        }
        return this;
    }

    public UIBuilder setWidth(int w) {
        if( ! currentObject.containsKey(WIDTH_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(WIDTH_ATTRIBUTE, w);
        return this;
    }

    public UIBuilder setHeight(int h) {
        if( ! currentObject.containsKey(HEIGHT_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(HEIGHT_ATTRIBUTE, h);
        return this;

    }

    public UIBuilder setVisibility(boolean b) {
        if( ! currentObject.containsKey(VISIBILITY_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(VISIBILITY_ATTRIBUTE, b);
        return this;
    }

    public UIBuilder setScrollable(boolean s) {
        if( ! currentObject.containsKey(SCROLLABLE_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(SCROLLABLE_ATTRIBUTE, s);
        return this;

    }

    public UIBuilder setTitle(String t) {
        if( ! currentObject.containsKey(TITLE_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(TITLE_ATTRIBUTE, t);
        return this;
    }

    public UIBuilder setModal(boolean m) {
        if( ! currentObject.containsKey(MODAL_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(MODAL_ATTRIBUTE, m);
        return this;
    }

    public UIBuilder setOrientation(UIContainer.Orientation o) {
        if( ! currentObject.containsKey(ORIENTATION_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(ORIENTATION_ATTRIBUTE, o.toString());
        return this;
    }

    public UIBuilder setCaption(String caption) {
        if( ! currentObject.containsKey(CAPTION_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(CAPTION_ATTRIBUTE, caption);
        return this;
    }

    public UIBuilder setText(String text) {
        if( ! currentObject.containsKey(TEXT_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        currentObject.put(TEXT_ATTRIBUTE, text);
        return this;
    }

    public UIBuilder setList(String[] elems) {
        if( ! currentObject.containsKey(WIDTH_ATTRIBUTE) ) {
            throw new UIBuilderException("Unsupported attribute.");
        }
        ListValue list = new ListValue();
        for(String elem : elems) {
            list.add(elem);
        }
        currentObject.put(LIST_ATTRIBUTE, list);
        return this;
    }

    public JsonObject result() {
        StackElement root;
        try {
            root = jsonStack.peek();
        } catch (EmptyStackException ese) {
            throw new UIBuilderException("No root available: stack empty.");
        }
//        if(jsonStack.size() != 1) {
//            throw new UIBuilderException("Invalid stack size");
//        }
        return  root.head;
    }

    public void reset() {
        jsonStack.clear();
        currentObject = null;
    }

    private void pushInStack(String key, JsonObject body, boolean isContainer) {
        JsonObject head = new MapValue();
        head.put(key, body);
        if( ! jsonStack.isEmpty() ) {
            StackElement peek = jsonStack.peek();
            if(peek.isContainer) {
                JsonArray components = (JsonArray) peek.body.get(COMPONENTS_ATTRIBUTE);
                components.add(head);
            } else {
                throw new UIBuilderException("Expected container in stack's peek element.");
            }
        }
        if(isContainer) {
            jsonStack.push( new StackElement(head, body, isContainer) );
        }
        currentObject = body;
    }

    private JsonObject createJSONComponent() {
        JsonObject jsonObject = new MapValue();
        jsonObject.put(WIDTH_ATTRIBUTE , 0);
        jsonObject.put(HEIGHT_ATTRIBUTE, 0);
        jsonObject.put(VISIBILITY_ATTRIBUTE, true);
        return jsonObject;
    }

    private JsonObject createJSONContainer() {
        JsonObject jsonObject = createJSONComponent();
        jsonObject.put(ORIENTATION_ATTRIBUTE, UIContainer.Orientation.HORIZONTAL.toString());
        jsonObject.put(SCROLLABLE_ATTRIBUTE, new BooleanValue(false));
        jsonObject.put(COMPONENTS_ATTRIBUTE, new ListValue());
        jsonObject.put(TITLE_ATTRIBUTE, new StringValue(""));
        return jsonObject;
    }

    private JsonObject createJSONButton() {
        JsonObject button = createJSONComponent();
        button.put(CAPTION_ATTRIBUTE, "<no caption>");
        return button;
    }

    private JsonObject createJSONLabel() {
        JsonObject label = createJSONComponent();
        label.put(TEXT_ATTRIBUTE, "<no text>");
        return label;
    }

    private JsonObject createJSONTextArea() {
        JsonObject textArea = createJSONComponent();
        textArea.put(TEXT_ATTRIBUTE, "<no text>");
        return textArea;
    }

    private JsonObject createJSONList() {
        JsonObject list = createJSONComponent();
        list.put(LIST_ATTRIBUTE, new ListValue() );
        return list;
    }

    private JsonObject createJSONWindow() {
        JsonObject window = createJSONContainer();
        window.put(MODAL_ATTRIBUTE, new BooleanValue(false) );
        window.put(TITLE_ATTRIBUTE, new StringValue(""));
        return window;
    }

}
