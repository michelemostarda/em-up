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

import java.util.Stack;
import java.util.EmptyStackException;

/**
 * Implementation of {@link com.asemantics.mashup.gui.UIBuilder}
 * that generates a {@link com.asemantics.mashup.gui.UIComponent}
 * object hierarchy.
 */
public class ConcreteUIBuilder implements UIBuilder<UIComponent> {

    /**
     * User Interface factory.
     */
    private final UIFactory uiFactory;

    /**
     * Stack of component's hierarchy.
     */
    private Stack<UIContainer> containersStack;

    /**
     * Last component added to the builder.
     */
    private UIComponent currentComponent;

    /**
     * Constructor with explicit user interface factory.
     *
     * @param uif
     */
    public ConcreteUIBuilder(UIFactory uif) {
        if(uif == null) {
            throw new NullPointerException("uif cannot be null.");
        }
        uiFactory = uif;
        currentComponent = null;
        containersStack = new Stack<UIContainer>();
    }

    /**
     * Constructor for the default user interface factory.
     */
    public ConcreteUIBuilder() {
        this( NativeImpl.getInstance().getUIFactory() );
    }

    public UIBuilder createButton() {
        pushInStack( uiFactory.createButton() );
        return this;
    }

    public UIBuilder createLabel() {
        pushInStack( uiFactory.createLabel() );
        return this;
    }

    public UIBuilder createTextArea() {
        pushInStack( uiFactory.createTextArea() );
        return this;
    }

    public UIBuilder createList() {
        pushInStack( uiFactory.createList() );
        return this;
    }

    public UIBuilder createPanel() {
        pushInStack( uiFactory.createPanel() );
        return this;
    }

    public UIBuilder createWindow() {
        pushInStack( uiFactory.createWindow() );
        return this;
    }

    public UIBuilder pop() {
        containersStack.pop();
        currentComponent = containersStack.isEmpty() ? null : containersStack.peek();
        return this;
    }

    public UIComponent result() {
        if( containersStack.isEmpty() ) {
            if(currentComponent == null) {
                throw new UIBuilderException("No components has been generated.");
            }
            return currentComponent;
        }
        return  containersStack.firstElement();
    }

    public void reset() {
        currentComponent = null;
        containersStack.clear();
    }

    public UIBuilder setWidth(int w) {
        try {
            currentComponent.setWidth(w);
        } catch (EmptyStackException ese) {
             throw new UIBuilderException("Error while setting attribute.");
        }
        return this;
    }

    public UIBuilder setHeight(int h) {
        try {
            currentComponent.setHeight(h);
        } catch (EmptyStackException ese) {
             throw new UIBuilderException("Error while setting attribute.");
        }
        return this;
    }

    public UIBuilder setVisibility(boolean b) {
        try {
            currentComponent.setVisible(b);
        } catch (EmptyStackException ese) {
             throw new UIBuilderException("Error while setting attribute.");
        }
        return this;
    }

    public UIBuilder setScrollable(boolean s) {
        try {
            ((UIContainer) currentComponent).setScrollable(s);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setTitle(String t) {
        try {
            ((UIPanel) currentComponent).setTitle(t);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setModal(boolean m) {
        try {
            ((UIWindow) currentComponent).setModal(m);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setOrientation(UIContainer.Orientation o) {
        try {
            ((UIContainer) currentComponent).setOrientation(o);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setCaption(String caption) {
        try {
            ((UIButton) currentComponent).setCaption(caption);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setText(String text) {
        try {
            ((UITextContainer) currentComponent).setText(text);
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    public UIBuilder setList(String[] entries) {
        try {
            UIList uiList = (UIList) currentComponent;
            for(String entry : entries) {
                uiList.addEntry(entry);
            }
        } catch (Exception e) {
             throw new UIBuilderException("Error while setting attribute.", e);
        }
        return this;
    }

    /**
     * Pushes an element into the stack, managing the inclusion policies between
     * containers and components.
     *
     * @param component the component to be added.
     */
    private void pushInStack(UIComponent component) {
        if( ! containersStack.isEmpty() ) {
            UIContainer peek = containersStack.peek();
            peek.addComponent(component);
        }
        if(component instanceof UIContainer) {
            containersStack.push( (UIContainer) component);
        }
        currentComponent = component;
    }
}
