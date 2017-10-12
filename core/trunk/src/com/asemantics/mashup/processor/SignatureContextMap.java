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


package com.asemantics.mashup.processor;


/**
 *  This data structure represents a map <i>variableName : variableValue</i> and 
 *  is optimized to contain variables defined by
 *  a <i>Signature</i> applied on a given <i>ExecutionContext</i>.
 *
 * @see com.asemantics.mashup.processor.Signature
 * @see com.asemantics.mashup.processor.ExecutionContext
 */
public class SignatureContextMap {

    /**
     * Signature Context Map block size.
     */
    public static final int BLOCK_SIZE = 5;

    /**
     * List of names.
     */
    private String[] valueNames;

    /**
     * List of values.
     */
    private Value[]  values;

    /**
     * Next useful index.
     */
    private int nextIndex;

    /**
     * Constructor, allow specification of initial size.
     * @param initialSize
     */
    protected SignatureContextMap(final int initialSize) {
        valueNames = new String[initialSize];
        values     = new Value [initialSize];
        nextIndex  = 0;
    }

    /**
     * Constructor.
     */
    protected SignatureContextMap() {
        this(BLOCK_SIZE);
    }

    /**
     * Constructor.
     *
     * @param vns
     * @param vs
     */
    protected SignatureContextMap(String[] vns, Value[] vs) {
        if( vns.length != vs.length ) {
            throw new IllegalArgumentException();
        }
        valueNames = vns;
        values     = vs;
    }

    /**
     * Adds a value into this map.
     * @param name
     * @param value
     * @param override
     */
    public void add(String name, Value value, boolean override) {
        checkSpace();
        int i = findName(name);
        if( i != -1 ) {
            if( ! override ) {
                throw new IllegalArgumentException("NAME '" + name + "' already present in map.");
            }
            values[i] = value;
        } else {
            valueNames[nextIndex] = name;
            values    [nextIndex] = value;
        }
        nextIndex++;
    }

    /**
     * Removes a value name.
     *
     * @param name
     */
    boolean remove(String name) {
        for(int i = 0; i < valueNames.length; i++ ) {
            if( valueNames[i] != null && valueNames[i].equals(name) ) {
                valueNames[i] = null;
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param name name of variable found.
     * @return <code>true</i> if found, <code>false</i> otherwise.
     */
    boolean containsValueName(String name) {
        for (String valueName : valueNames) {
            if(valueName != null && valueName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param name name of value to find.
     * @return value found.
     */
    Value getValue( String name ) {
        for( int i = 0; i < valueNames.length; i++ ) {
            if( valueNames[i] != null && valueNames[i].equals( name ) ) {
                return values[i];
            }
        }
        return null;
    }

    /**
     *
     * @param i
     * @return the <i>i-th</i> argument value.
     */
    Value getIthArgumentValue(int i) {
        return values[i];
    }

    /**
     *
     * @return returns the map size.
     */
    int size() {
        int s = 0;
        for(String name : valueNames) {
            if( name != null ) {
                s++;
            }
        }
        return s;
    }


    /**
     * Checks if an array swap is needed.
     */
    private void checkSpace() {
        if( nextIndex < valueNames.length) {
            return;
        }
        String[] newValueNames = new String[nextIndex + BLOCK_SIZE];
        Value [] newValues     = new Value [nextIndex + BLOCK_SIZE];
        System.arraycopy( valueNames, 0 , newValueNames, 0, nextIndex);
        System.arraycopy( values    , 0 , newValues    , 0, nextIndex);
        valueNames = newValueNames;
        values     = newValues;
    }

    /**
     * Finds a given variable name.
     *
     * @param n
     * @return index of found name, <i>-1</i> if not found.
     */
    private int findName(String n) {
        for(int i = 0; i < valueNames.length; i++) {
            if( valueNames[i] != null && valueNames[i].equals(n) ) {
                return i;
            }
        }
        return -1;
    }

}
