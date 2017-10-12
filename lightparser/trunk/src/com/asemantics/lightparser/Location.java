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


package com.asemantics.lightparser;

/**
 * Defines the location of a tree node. 
 */
public class Location {

    /**
     * Defines a native location.
     */
    public static final Location NATIVE_LOCATION = new Location(0,0) {

        @Override
        public String toString() {
            return "<native location>";
        }

    };

    /**
     * Row location.
     */
    private int row;

    /**
     * Col location.
     */
    private int col;

    /**
     * Constructor.
     *
     * @param r row location.
     * @param c col location.
     */
    public Location(int r, int c) {
        row = r;
        col = c;
    }

    /**
     *
     * @return row location.
     */
    public int getRow() {
        return row;
    }

    /**
     *
     * @return column location.
     */
    public int getCol() {
        return col;
    }

    @Override
    public int hashCode() {
        return row * col;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof Location ) {
            Location other = (Location) obj;
            return row == other.row && col == other.col;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + row + "," + col + "]";
    }
}