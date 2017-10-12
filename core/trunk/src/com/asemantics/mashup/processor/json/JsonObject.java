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


package com.asemantics.mashup.processor.json;

import java.util.Map;

/**
 * Defines the <i>JSON</i> object.
 */
public interface JsonObject extends JsonComplex<Map.Entry<String,JsonBase>>, Iterable<Map.Entry<String,JsonBase>> {

    /**
     * Checks that the obejct contains the specified <i>key</i>.
     *
     * @param key
     * @return <code>true</code> if contained, <code>false</code> otherwise.
     */
    public boolean containsKey(String key) ;

    /**
     * Returns the value associated to the specified <i>key</i>.
     *
     * @param key key to be found.
     * @return value associated to the key.
     */
    public JsonBase get(String key);

    /**
     * Puts an entry into this object.
     *
     * @param key key object.
     * @param value value object.
     */
    public void put(String key, JsonBase value);

    /**
     * Puts an entry into this object.
     *
     * @param key key object.
     * @param value value object.
     */
    public void put(String key, boolean value);

    /**
     * Puts an entry into this object.
     *
     * @param key key object.
     * @param value value object.
     */
    public void put(String key, String value);

    /**
     * Puts an entry into this object.
     *
     * @param key key object.
     * @param value value object.
     */
    public void put(String key, int value);

    /**
     * Puts an entry into this object.
     *
     * @param key key object.
     * @param value value object.
     */
    public void put(String key, double value);

    /**
     * Returns the keys of this object.
     *
     * @return list of keys of this object.
     */
    public String[] getKeys() ;

}