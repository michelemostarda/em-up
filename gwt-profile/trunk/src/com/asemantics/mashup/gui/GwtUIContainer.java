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

import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.*;

import java.util.ArrayList;

/**
 * GWT abstract container implementation.
 */
public abstract class GwtUIContainer extends GwtUIComponent<Panel> implements UIContainer<Widget> {

    /**
     * List of components in this container.
     */
    protected java.util.List<UIComponent> components;

    /**
     * Container orientation.
     */
    private Orientation orientation = Orientation.HORIZONTAL;

    /**
     * Internal panel implementing this container.
     */
    private Panel panel;

    /**
     * Constructor.
     */
    GwtUIContainer() {
        panel = new Panel();
        panel.setLayout( getVerticalLayout() );
        setNativeComponent(panel);
        components = new ArrayList<UIComponent>();
    }

    /**
     * Constuctor with internal panel.
     *
     * @param c
     */
    GwtUIContainer(Panel c) {
        super( c );
        components = new ArrayList<UIComponent>();
    }

    public String getComponentName() {
        return UIContainer.NAME;
    }

    /**
     * Returns the preferred size for this container on the basis of
     * the sizes of internal components.
     * 
     * @return a rectangular size.
     */
    @Override
    public Size getSize() {
        Size size = new Size(0,0);
        for(UIComponent<Widget> component : components) {
            if( component instanceof UIContainer ) {

                // Merging children container size.
                size.mergeTo( component.getSize() );

            } else {

                // Merging children compoment sizes.
                Widget w = component.getNativeComponent();
                Size componentSize = component.getSize();
                size.mergeTo(
                    w.getAbsoluteLeft() + componentSize.getWidth(),
                    w.getAbsoluteTop()  + componentSize.getHeight()
                );

            }
        }
        return size;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation o) {
        if(orientation == o) { return; }

        if( o == Orientation.HORIZONTAL) {
            panel.setLayout( getHorizontalLayout() );
        } else if( o == Orientation.VERTICAL) {
            panel.setLayout( getVerticalLayout() );
        } else {
            throw new IllegalArgumentException("Unknown o: " + o);
        }
        orientation = o;
    }

    public boolean isScrollable() {
        return panel.isAutoScroll();
    }

    public void setScrollable(boolean s) {
        panel.setAutoScroll(s);
    }

    public void addComponent(UIComponent<Widget> component) {
        components.add( component );
        panel.add( component.getNativeComponent() );
    }

    public void removeComponent(UIComponent<Widget> component) {
        panel.remove( component.getNativeComponent() );
        components.remove( component );
    }

    public UIComponent[] getComponents() {
        return components.toArray( new UIComponent[ components.size() ] );
    }

    protected HorizontalLayout getHorizontalLayout() {
        return new HorizontalLayout(2);
    }

    protected VerticalLayout getVerticalLayout() {
        return new VerticalLayout(2);
    }

}
