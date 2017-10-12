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


import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonBoolean;
import com.asemantics.mashup.processor.json.JsonFactory;
import com.asemantics.mashup.processor.json.JsonInteger;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonString;

import static com.asemantics.mashup.gui.UIAttribute.*;

/**
 * The <i>JSON Translator</i> is responsible of two types of
 * trasfomation:
 * <ul>
 *    <li><b>convertToJSON(nativeUI)</b> that provides a UI to JSON transformation.</li>
 *    <li><b>convertToUI(JSONUImodel)</b> that provides a JSON UI Model to Native UI transformation.</li>
 * </ul>
 */
public class JsonTranslator {

    /**
     * User Interface factory.
     */
     private static final UIFactory UIFACTORY = NativeImpl.getInstance().getUIFactory();

    /**
     * Private constructor.
     * <i>Cannot be instantiated.</i>
     */
    private JsonTranslator() {
        // Empty.
    }

    /* Begin Native UI --> JSON UI */

    /**
     * Converts a User Interface component to a JSON UI representation.
     *
     * @param component the component to be converted.
     * @return the JSON UI representation.
     */
    public static JsonObject convertToJson(UIComponent component)
    throws JsonTranslationException {

        JsonObject obj2;

        if( component instanceof UIButton) {
            obj2 = convertToJson((UIButton) component);
        } else if( component instanceof UIWindow) {
            obj2 = convertToJson((UIWindow) component);
        } else if( component instanceof UILabel) {
            obj2 = convertToJson((UILabel) component);
        } else if( component instanceof UIList) {
            obj2 = convertToJson((UIList) component);
        } else if( component instanceof UIPanel) {
            obj2 = convertToJson((UIPanel) component);
        } else if( component instanceof UITextArea) {
            obj2 = convertToJson((UITextArea) component);
        } else {
            throw new JsonTranslationException("Unsupported component: " + component);
        }

        JsonObject obj1 = JsonFactory.newJsonObject();
        obj1.put( component.getComponentName(), obj2 );

        return obj1;
    }

    /**
     * Converts a component to a JSON UI representation.
     *
     * @param component the component to be converted.
     * @return the JSON UI representation.
     */
    private static final JsonObject convertComponent(UIComponent component) {
        JsonObject json = JsonFactory.newJsonObject();
        json.put( WIDTH_ATTRIBUTE  , component.getWidth()  );
        json.put( HEIGHT_ATTRIBUTE , component.getHeight() );
        json.put( VISIBILITY_ATTRIBUTE, component.isVisible() );
        return json;
    }

    /**
     * Converts a container to a JSON UI representation.
     *
     * @param container the container to be converted.
     * @return the JSON UI representation.
     */
    private static JsonObject convertContainer(UIContainer container)
    throws JsonTranslationException {
        JsonObject json = convertComponent(container);

        json.put( ORIENTATION_ATTRIBUTE, container.getOrientation().toString() );
        json.put( SCROLLABLE_ATTRIBUTE , container.isScrollable() );

        JsonArray components = JsonFactory.newJsonArray();
        for( UIComponent c : container.getComponents() ) {
            components.add( convertToJson(c) );
        }
        json.put(COMPONENTS_ATTRIBUTE, components);

        return json;
    }

    /**
     * Convers a button to a JSON UI representation.
     *
     * @param button button to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UIButton button) {
         JsonObject json = convertComponent(button);
         json.put( CAPTION_ATTRIBUTE, button.getCaption() );
         return json;
    }

    /**
     * Convers a label to a JSON UI representation.
     *
     * @param label label to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UILabel label) {
        JsonObject json = convertComponent(label);
        json.put( TEXT_ATTRIBUTE, label.getText() );
        return json;
    }

    /**
     * Convers a list to a JSON UI representation.
     *
     * @param list list to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UIList list) {
         JsonObject json = convertComponent(list);
         json.put( LIST_ATTRIBUTE, list.getJSONList() );
         return json;
    }

    /**
     * Convers a text area to a JSON UI representation.
     *
     * @param textArea text area to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UITextArea textArea) {
         JsonObject json = convertComponent(textArea);
         json.put( TEXT_ATTRIBUTE, textArea.getText() );
         return json;
    }

    /**
     * Convers a panel to a JSON UI representation.
     *
     * @param panel panel to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UIPanel panel)
    throws JsonTranslationException {
        JsonObject json = convertContainer(panel);
        json.put( TITLE_ATTRIBUTE, panel.getTitle() );        
        return json;
    }

    /**
     * Convers a window to a JSON UI representation.
     *
     * @param window window to be converted.
     * @return JSON UI representation.
     */
    private static JsonObject convertToJson(UIWindow window)
    throws JsonTranslationException {
        JsonObject json = convertContainer(window);
        json.put( MODAL_ATTRIBUTE, window.isModal() );
        json.put( TITLE_ATTRIBUTE, window.getTitle() );
        return json;
    }

    /* End Native UI --> JSON UI */

    /* Begin JSON UI --> Native UI */

    /**
     * Converts a JSON UI object to a Native UI.
     *
     * @param uiModel the JSON Model to be converted.
     * @return the user interface.
     */
    public static UIComponent convertToUI(JsonObject uiModel)
    throws JsonTranslationException {

        // Checks the JSON format.
        String[] keys = uiModel.getKeys();
        if( keys.length != 1 ) {
            throw new JsonTranslationException("Error while converting JSON to UI.");
        }

        // Window.
        if( UIWindow.NAME.equals(keys[0]) ) {
            return createWindow( (JsonObject) uiModel.get(UIWindow.NAME) );
        }

        // Container.
        if( UIContainer.NAME.equals(keys[0]) ) {
            return createContainer( (JsonObject) uiModel.get(UIContainer.NAME) );
        }

        // Component.
        return createComponent( keys[0], uiModel );
    }

    /**
     * Loads the detected attributes of a JSON UI into the given component.
     *
     * @param uiComponent the component to set attributes.
     * @param uiModel the JSON object to use to extract data.
     */
    private static void loadComponentData(UIComponent uiComponent, JsonObject uiModel) {
        JsonInteger width      = (JsonInteger) uiModel.get(WIDTH_ATTRIBUTE);
        JsonInteger height     = (JsonInteger) uiModel.get(HEIGHT_ATTRIBUTE);
        JsonBoolean visibility = (JsonBoolean) uiModel.get(VISIBILITY_ATTRIBUTE);
        if( width  != null ) { uiComponent.setWidth (width.intValue() ); }
        if( height != null ) { uiComponent.setHeight(height.intValue()); }
        if( visibility != null ) { uiComponent.setVisible(visibility.boolValue()); }
    }

    /**
     * Loads the detected attributes of a JSON UI into the given container.
     *
     * @param uiContainer the container to set attributes.
     * @param uiModel the JSON Model to use to extract data.
     * @see #loadComponentData(UIComponent, com.asemantics.mashup.processor.json.JsonObject)
     * @see #createComponent(String, com.asemantics.mashup.processor.json.JsonObject) 
     */
    private static void loadContainerData(UIContainer uiContainer, JsonObject uiModel)
    throws JsonTranslationException {
        // Loads component data.
        loadComponentData(uiContainer, uiModel);

        // Loads container data.
        JsonString  orientation = (JsonString) uiModel.get(ORIENTATION_ATTRIBUTE);
        JsonBoolean scrollable  = (JsonBoolean) uiModel.get(SCROLLABLE_ATTRIBUTE);
        if( orientation != null ) { uiContainer.setOrientation( UIContainer.Orientation.valueOf(orientation.stringValue()) ); }
        if( scrollable  != null ) { uiContainer.setScrollable(scrollable.boolValue()); }

        // Loads the contained objects.
        if( uiModel.containsKey(COMPONENTS_ATTRIBUTE) ) {
            JsonArray components = (JsonArray) uiModel.get(COMPONENTS_ATTRIBUTE);
            for(JsonBase component : components) {
                JsonObject componentObj = (JsonObject) component;
                String[] keys = componentObj.getKeys();
                if( keys.length != 1 ) {
                    throw new JsonTranslationException("Invalid component structure.");
                }
                uiContainer.addComponent( createComponent(keys[0], (JsonObject) componentObj.get(keys[0])) );
            }
        }
    }

    /**
     * Creates a {@link com.asemantics.mashup.gui.UIWindow} on the basis of the given
     * JSON model.
     *
     * @param uiModel input JSON UI.
     * @return generated window.
     * @see #loadContainerData(UIContainer, com.asemantics.mashup.processor.json.JsonObject)
     */
    private static UIWindow createWindow(JsonObject uiModel)
    throws JsonTranslationException {
        UIWindow uiWindow = UIFACTORY.createWindow();
        loadContainerData(uiWindow, uiModel);
        return uiWindow;
    }

    /**
     * Creates a {@link com.asemantics.mashup.gui.UIContainer} on the basis of the given
     * JSON Model.
     *
     * @param uiModel input JSON UI.
     * @return generated container.
     * @see #loadContainerData(UIContainer, com.asemantics.mashup.processor.json.JsonObject)
     */
    private static UIContainer createContainer(JsonObject uiModel)
    throws JsonTranslationException {
        UIContainer uiContainer = UIFACTORY.createPanel();
        loadContainerData(uiContainer, uiModel);
        return uiContainer;
    }

    /**
     * Creates a {@link com.asemantics.mashup.gui.UIComponent} on the basis of the given
     * JSON UI model.
     *
     * @param uiModel input JSON UI Model.
     * @return generated component.
     * @see #createContainer(com.asemantics.mashup.processor.json.JsonObject)
     */
    private static UIComponent createComponent(final String componentKey, final JsonObject uiModel)
    throws JsonTranslationException {

        // UIContainer.
        if(UIContainer.NAME.equals(componentKey)) {
            return createContainer(uiModel);
        }

        // UILabel.
        if( UILabel.NAME.equals(componentKey) ) {
            UILabel label = UIFACTORY.createLabel();
            loadComponentData( label, uiModel );
            String text = asJSON( uiModel.get(TEXT_ATTRIBUTE) );
            label.setText(text);
            return label;
        }

        // UIButton.
        if( UIButton.NAME.equals(componentKey) ) {
            UIButton button = UIFACTORY.createButton();
            loadComponentData( button, uiModel );
            String caption = asJSON( uiModel.get(CAPTION_ATTRIBUTE) );
            button.setCaption(caption);
            return button;
        }

        // UITextArea.
        if( UITextArea.NAME.equals(componentKey) ) {
            UITextArea textArea = UIFACTORY.createTextArea();
            loadComponentData( textArea, uiModel );
            String text = asJSON( uiModel.get(TEXT_ATTRIBUTE) );
            textArea.setText(text);
            return textArea;
        }

        // UIList.
        if( UIList.NAME.equals(componentKey) ) {
            UIList list = UIFACTORY.createList();
            loadComponentData( list, uiModel );
            JsonArray listData = (JsonArray) uiModel.get(LIST_ATTRIBUTE);
            if(listData != null) {
                for(JsonBase element : listData) {
                    list.addEntry( element.asJSON() );
                }
            }
            return list;
        }

        throw new IllegalArgumentException("Cannot find a valid component name in object: " + uiModel.asJSON() );
    }

    /**
     * Utility method.
     * 
     * @param base
     * @return
     */
    private static String asJSON(JsonBase base) {
        return base == null ? "" : ((JsonString) base).stringValue();
    }

    /* END JSON UI --> Native UI */

}

