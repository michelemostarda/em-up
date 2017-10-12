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

/**
 * Represents a generic size of a rectangular object.
 */
public class Size {

    private int width;

    private int height;

    Size(int w, int h) {
        width  = w;
        height = h;
    }

    public Size mergeTo(Size other) {
        width  = width  < other.width  ?  other.width  : width;
        height = height < other.height ?  other.height : height;
        return this;
    }

    public Size mergeTo(int w, int h) {
        width  = width  < w ?  w : width;
        height = height < h ?  h : height;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int hashCode() {
        return width * height;
    }

    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof Size) {
            Size other = (Size) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    public String toString() {
        return "size(w:" + width + ",h:" + height + ")";
    }
}
