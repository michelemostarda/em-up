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


package com.asemantics.mashup.interpreter;

import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.nativepkg.NativeImpl;
import com.asemantics.mashup.nativepkg.NativeException;
import junit.framework.TestCase;

/**
 * Base class for <i>interpreter</i> tests.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: AbstractInterpreterTest.java 429 2009-06-01 13:27:59Z michelemostarda $
 */
public abstract class AbstractInterpreterTest extends TestCase {

    /**
     * Test target.
     */
    protected Interpreter interpreter;

    protected void setUp() throws Exception {
        interpreter = new Interpreter();
    }

    protected void tearDown() throws Exception {
        interpreter = null;
    }

    protected void assertContains(String msg, String str, String actual) {
        assertTrue(msg, actual.indexOf(str) != -1);
    }

    protected void checkResult( boolean expected, Value actual ) {
        assertEquals( "Unespected result.", expected, actual.asBoolean().getNativeValue().booleanValue() );
    }

    protected void checkResult( int expected, Value actual ) {
        assertEquals( "Unespected result.", expected, actual.asNumeric().integerValue().integer() );
    }

    protected void checkResult( double expected, Value actual ) {
        assertEquals( "Unespected result.", expected, actual.asNumeric().getNativeValue() );
    }

    protected void checkResult( String expected, Value actual ) {
        assertEquals( "Unespected result.", expected, actual.asString().getNativeValue() );
    }

    protected void compareJsonObjects(String message, String expected, String actual) {
        try {
            assertEquals(message, NativeImpl.getInstance().parseJSON(expected), NativeImpl.getInstance().parseJSON(actual));
        } catch (NativeException ne) {
            throw new RuntimeException("Error while parsing a string expected to represent JSON data.", ne);
        }
    }

}
