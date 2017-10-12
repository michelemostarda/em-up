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


package com.asemantics.mashup.nativepkg;

import com.asemantics.mashup.gui.UIFactory;
import com.asemantics.mashup.processor.json.JsonBase;

import java.util.Map;

/**
 * Defines a list of methods which implementation is platform specific.
 * Implementation will be defined as {@link NativeImpl}.
 */
public abstract class Native {

    /**
     * Contains the unique instance of this class.
     */
    protected static Native instance;

    /**
     * Initializes native instance.
     *
     * @param i implementation instance.
     */
    protected static void initInstance(Native i) {
        if( instance != null ) {
            throw new IllegalStateException("Instance already initialized.");
        }
        instance = i;
    }

    /**
     * Returns the unique instance.
     * 
     * @return native instance.
     */
    public static Native getInstance() {
        if( instance == null ) {
            throw new IllegalStateException("Instance not yet initialized.");
        }
        return instance;
    }

    /**
     * Constructor.
     */
    protected Native() {
        // Empty.
    }

    /**
     * Sleeps current thread for the given time.
     *
     * @param timemillis time in milliseconds.
     */
     public abstract void sleep(int timemillis) throws NativeException;

    /**
     * Returns the simple class name of a given object.
     *
     * @param obj object to be inspected.
     * @return simple class name.
     */
    public abstract String getSimpleClassName(Object obj) throws NativeException;

    /**
     * Performs an <i>HTTP</i> GET request on given URL.
     *
     * @param url url to be retrieved.
     * @return URL content.
     * @throws NativeException
     */
    public abstract String httpGetRequest(String url) throws NativeException;

    /**
     * Performs an <i>HTTP</i> POST request on given URL.
     * 
     * @param url url to be retrieved.
     * @param params parameters to be added.
     * @return url content.
     * @throws NativeException
     */
    public abstract String httpPostRequest(String url, Map<String,String> params) throws NativeException;

    /**
     * Performs an <i>HTTP</i> PUT request on given URL.
     *
     * @param url url to be retrieved.
     * @param data data to be sent.
     * @return url content.
     * @throws NativeException
     */
    public abstract String httpPutRequest(String url, String data) throws NativeException;

    /**
     * Performs an <i>HTTP</i> DELETE request on given URL.
     *
     * @param url url to be deleted.
     */    
    public abstract String httpDeleteRequest(String url) throws NativeException;

    /**
     * Returns the platform specific UI factory.
     *
     * @return plaform specific UI Factory.
     */
    public abstract UIFactory getUIFactory();

    /**
     * Parses a JSON string returning the corresponding reprepresentation.
     *
     * @param jsonString JSON string.
     * @return JSON object representing input string.
     */
    public abstract JsonBase parseJSON(String jsonString) throws NativeException;

}
