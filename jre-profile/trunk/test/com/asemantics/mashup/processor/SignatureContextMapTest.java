package com.asemantics.mashup.processor;

import junit.framework.TestCase;/*
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

/**
 * Test class for {@link com.asemantics.mashup.processor.SignatureContextMap}.
 */
public class SignatureContextMapTest extends TestCase {

    private SignatureContextMap scm;

    protected void setUp() throws Exception {
        scm = new SignatureContextMap();
    }

    protected void tearDown() throws Exception {
        scm = null;
    }

    public void testMainUsageScenario() {

        // Loading data.
        for(int i = 0; i < scm.BLOCK_SIZE * 2; i++) {
            scm.add("name_" + i, new StringValue("value_" + i), false);
        }
        assertEquals( "Unespected size.", scm.BLOCK_SIZE * 2, scm.size() );

        // Reading data.
        for( int i = 0; i < scm.BLOCK_SIZE * 2; i++ ) {
            assertEquals("Cannot find expetected element.", "value_" + i ,scm.getValue("name_" + i).asString().getNativeValue() );
        }

        // Removing data.
        for( int i = 0; i < scm.BLOCK_SIZE * 2; i++ ) {
            assertTrue("Cannot find expetected element.", scm.remove("name_" + i) );
        }
        assertEquals( "Unespected size.", 0, scm.size() );

    }
}
