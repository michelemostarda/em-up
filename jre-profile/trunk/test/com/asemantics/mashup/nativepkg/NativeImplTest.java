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

import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonBase;
import com.asemantics.mashup.processor.json.JsonDouble;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonString;
import junit.framework.TestCase;

import java.util.HashMap;

/**
 * Test class for {@link com.asemantics.mashup.nativepkg.NativeImpl}
 * <i>Java Runtime Environment</i> profile.
 *
 * @see com.asemantics.mashup.nativepkg.Native
 */
public class NativeImplTest extends TestCase {

    /**
     * Native implementation.
     */
    private Native nativeImpl;

    protected void setUp() throws Exception {
        nativeImpl = NativeImpl.getInstance();
    }

    protected void tearDown() throws Exception {
        nativeImpl = null;
    }

    /**
     * Tests the sleep method.
     * 
     * @throws NativeException
     */
    public void testSleep() throws NativeException {
        int time = 200;
        long begin = System.currentTimeMillis();
        nativeImpl.sleep(time);
        long end   = System.currentTimeMillis();
        long diff = end - begin;
        assertTrue("Unespected time: " + diff, diff >= time);
    }

    /**
     * Tests the JSON parsing.
     * 
     * @throws NativeException
     */
    public void testParseJSON() throws NativeException {

        final String jsonString = "{ \"k1\" : \"v1\" , \"k2\" : [ 10.7 ] }";

        JsonBase json = nativeImpl.parseJSON(jsonString);
        assertNotNull("Expected object here.", json);

        assertTrue("Espected JSON Object here.", json instanceof JsonObject );
        JsonObject jsonObject = (JsonObject) json;

        assertTrue( "Espected key", jsonObject.containsKey("k1") );
        assertTrue( "Espected key", jsonObject.containsKey("k2") );

        assertTrue  ("Unespected value type.", jsonObject.get("k1") instanceof JsonString );
        assertEquals("Unespected value.", "v1", ((JsonString) jsonObject.get("k1")).getNativeValue() );

        assertTrue("Unespected value type.", jsonObject.get("k2") instanceof JsonArray);
        assertTrue("Unespected value type.", ((JsonArray) jsonObject.get("k2")).get(0) instanceof JsonDouble );

        double d = ((JsonDouble) ((JsonArray) jsonObject.get("k2")).get(0)).getNativeValue();
        assertEquals("Unespected value.", 10.7 , d);
        
    }

    /**
     * Tests the NativeImpl#testHttpGetRequest method.
     *
     * @throws NativeException
     */
    public void testHttpGetRequest() throws NativeException {
        String response = nativeImpl.httpGetRequest( "http://www.repubblica.it" );
        assertNotNull("Invalid response.", response);
        assertTrue("Invalid response size.", response.length() > 0);
    }

    /**
     * Tests the NativeImpl#testHttpPostRequest method.
     *
     * @throws NativeException
     */
    public void testHttpPostRequest() throws NativeException {
        String response = nativeImpl.httpPostRequest( "http://www.repubblica.it", new HashMap<String,String>() );
        assertNotNull("Invalid response.", response);
        assertTrue("Invalid response size.", response.length() > 0);
    }

}
