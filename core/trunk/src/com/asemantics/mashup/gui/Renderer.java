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

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonComplex;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonSimple;
import com.asemantics.mashup.processor.JsonValue;

import java.util.Arrays;

/**
 * Defines a Renderer class.
 */
public class Renderer<T> {

    /**
     * Internal builder.
     */
    private UIBuilder<T> uiBuilder;

    /**
     * Constructor, allows to specify a custom builder.
     */
    public Renderer(UIBuilder<T> uib) {
        if(uib == null) {
            throw new IllegalArgumentException("builder cannot be null.");
        }
        uiBuilder = uib;
    }

    /**
     * Resets the renderer internal status.
     */
    public void reset() {
        uiBuilder.reset();
    }

    /**
     * Renderizes a simple JSON.
     *
     * @param simple type to be rendered.
     */
    private void renderize(JsonSimple simple) {
        Object nv = simple.getNativeValue();
        uiBuilder.createLabel().setText( nv == null ? "<null>" : nv.toString() );
    }

    /**
     * Renderizes an array.
     *
     * @param array array to be rendered.
     */
    private void renderize(JsonArray array) {
        uiBuilder.createPanel();
        for( int i = 0; i < array.size(); i++ ) {
            renderize( array.get(i) );
        }
        uiBuilder.pop();
    }

    /**
     * Renderizes an object.
     *
     * @param obj object to be rendered.
     */
    private void renderize(JsonObject obj) {
        uiBuilder.createPanel()
                .setOrientation(UIContainer.Orientation.VERTICAL);

        String[] keys = obj.getKeys();
        Arrays.sort(keys);
        JsonBase value;
        for( int i = 0; i < keys.length; i++ ) {
            uiBuilder
                .createPanel()
                .setTitle( keys[i] );
            value = obj.get(keys[i]);
            assert value != null : "Value must be found.";
            renderize(value);
            uiBuilder.pop();
        }
        uiBuilder.pop();
    }

    /**
     * Renderizes a complex type.
     *
     * @param complex complex type to be rendered.
     * @return user interface providing renderization for given <i>complex</i>.
     */
    private T renderize(JsonComplex complex) {
        if( complex.isArray() ) {
            renderize( (JsonArray)  complex );
        } else {
            renderize( (JsonObject) complex );
        }
        return uiBuilder.result();
    }

    /**
     * Renderizes a generic type.
     *
     * @param base JSON base type to be rendered.
     * @return interface component representing given <i>base</i>.
     */
    protected T renderize(JsonBase base) {
        //TODO: fix this.
        if(base instanceof JsonValue) {
            base = ((JsonValue) base).getNativeValue();
        }
        if( base.isSimple() ) {
            renderize( (JsonSimple ) base );
        } else {
            renderize( (JsonComplex) base );
        }
        return uiBuilder.result();
    }

    /**
     * Renderizes a panel containing the representation of the
     * given JSON object.
     * 
     * @param base
     * @return the UI native widget.
     */
    public T renderizeAsPanel(JsonBase base) {
        uiBuilder.reset();
        uiBuilder.createPanel();
        renderize(base);
        return uiBuilder.result();
    }

}
