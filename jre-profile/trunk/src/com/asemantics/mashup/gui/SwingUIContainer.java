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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.ScrollPane;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a Java Swing abstract container.
 */
public abstract class SwingUIContainer extends SwingUIComponent<Container> implements UIContainer<Container> {

    protected List<UIComponent<Container>> components;

    private boolean scrollable = false;

    private UIPanel.Orientation orientation = UIPanel.Orientation.HORIZONTAL;

    private Container container;

    private ScrollPane scrollPane = null;

    SwingUIContainer() {
        container = new TitlePanel();
        setComponent(container);
        components = new ArrayList<UIComponent<Container>>();
    }

    SwingUIContainer(Container c) {
        super( c );
        components = new ArrayList<UIComponent<Container>>();
    }

    @Override
    public String getComponentName() {
        return UIContainer.NAME;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean s) {
        if( scrollable == s) { return; }

        if(s) {
            if( scrollPane == null ) {
                scrollPane = new ScrollPane();
            }
            container.removeAll();
            container.add(scrollPane);
            for(UIComponent<Container> component : components) {
                scrollPane.add( component.getNativeComponent() );
            }
            updateContainerOrientation(scrollPane, orientation);
        } else {
            scrollPane.removeAll();
            scrollPane = null;
            for(UIComponent<Container> component : components) {
                container.add( component.getNativeComponent() );
            }
            updateContainerOrientation(container, orientation);
        }

        scrollable = s;
    }

    public UIPanel.Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(UIPanel.Orientation o) {
        if(orientation == o) { return; }

        if(o == null) {
            throw new NullPointerException();
        }

        if( scrollPane == null ) {
            updateContainerOrientation(container, o );
        } else {
            updateContainerOrientation(scrollPane, o);
        }
        
        orientation = o;
    }

    public void addComponent(UIComponent<Container> component) {
        components.add( component );
        container.add( component.getNativeComponent() );
    }

    public void removeComponent(UIComponent<Container> component) {
        container.remove( component.getNativeComponent() );
        components.remove( component );
    }

    public UIComponent[] getComponents() {
        return components.toArray( new UIComponent[ components.size() ] );
    }

    protected void updateContainerOrientation(Container c, Orientation o) {
        if( o.equals(UIPanel.Orientation.HORIZONTAL) ) {
            c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS) );
        } else if( o.equals(UIPanel.Orientation.VERTICAL) ) {
            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS) );
        } else {
            throw new IllegalArgumentException();
        }
    }
}
