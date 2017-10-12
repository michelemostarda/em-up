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


package com.asemantics.mashup.common;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for class {@link com.asemantics.mashup.interpreter.Utils}.
 */
public class UtilsTest extends TestCase {

    /**
     * Utility implementation of var map.
     */
    class FakeVariableMapper implements VariableMapper {

        private Map<String,String> values;

        FakeVariableMapper() {
            values = new HashMap<String,String>();
            values.put("0", "value0");
            values.put("1", "value1");
            values.put("2", "value2");
            values.put("3", "value3");
        }

        public boolean defineVariable(String varName) {
            return values.containsKey(varName);
        }

        public void setVariable(String name, String value) {
            throw new UnsupportedOperationException();
        }

        public void unsetVariable(String name) {
            throw new UnsupportedOperationException();
        }

        public String getValueOf(String varName) {
            return values.get(varName);
        }
    }

    /**
     * Tests Utils#expandString()
     */
    public void testExpandString() throws ExpansionException {
        FakeVariableMapper fakeVarMap = new FakeVariableMapper();
        String result = Utils.expandString(fakeVarMap, "a bb ${0} ccc dddd ${1} eeeee ffffff ${2} ggggggg ${3}");
        assertEquals("Unespected result.", "a bb value0 ccc dddd value1 eeeee ffffff value2 ggggggg value3", result);
    }

    /**
     * Tests Utils#expandString()
     */
    public void testExpandStringNoClosure() {
        FakeVariableMapper fakeVarMap = new FakeVariableMapper();
        try {
            Utils.expandString(fakeVarMap, "a bb ${0} ccc dddd ${1X");
            fail("Expected exception");
        } catch (ExpansionException ee) {
            assertTrue("Wrong exception message.", ee.getMessage().indexOf("Expected closure") != -1);
        }
    }

    /**
     * Tests Utils#expandString()
     */
    public void testExpandStringNoVariable() {
        FakeVariableMapper fakeVarMap = new FakeVariableMapper();
        try {
            Utils.expandString(fakeVarMap, "a bb ${100} ccc");
            fail("Expected exception");
        } catch (ExpansionException ee) {
            assertTrue("Wrong exception message.", ee.getMessage().indexOf("Unknown variable") != -1);
        }
    }

    /**
     * Tests Utils#getStringHeight()
     */
    public void testGetStringHeight() {
        int height = Utils.getStringHeight("123456789012345678901234", 3 * Utils.CHAR_PIXEL_WIDTH );
        assertEquals("Unespected result.", 158, height);
    }

    /**
     * Tests Utils#getTextBoxSize()
     */
    public void testGetBoxSize() {
        Utils.TextBoxSize size = Utils.getTextBoxSize("1234\n12345\n12345678\n1224\n123456");
        assertEquals("Unespected max line length.", 8, size.getMaxLineLen() );
        assertEquals("Unespected number of lines.", 5, size.getNumOfLines() );
    }

    /**
     * Tests Utils#encodeStringAsHTML()
     */
    public void testEncodeStringAsHTML() {
        String result = Utils.encodeStringAsHTML("a\"bàcèdìeòfù");
        assertEquals("Unespected result.", "a&#34;b&agrave;c&egrave;d&igrave;e&ograve;f&ugrave;", result);
    }

    /**
     * Tests Utils#decodeStringAsUnicode()
     */
    public void testDecodeStringAsUnicode() {
        String result = Utils.decodeStringAsUnicode("a&#34;b&agrave;c&egrave;d&igrave;e&ograve;f&ugrave;");
        assertEquals("Unespected result.", "a\"bàcèdìeòfù", result);
    }

    public void testDecodeStringWithNoSymbols() {
        String result = Utils.decodeStringAsUnicode("This is plain text.");
        assertEquals("Unespected result.", "This is plain text.", result);
    }

    /**
     * Tests Utils#induceType() with JSON.
     */
    public void testInduceTypeJSONObj() {
        String in = "{ \"x\"  : \"y\" }";
        assertEquals("Unespected type.", Utils.ContentType.JSON, Utils.induceType(in) );
    }

     /**
     * Tests Utils#induceType() with JSON.
     */
    public void testInduceTypeJSONArr() {
        String in = "[ 1, 2 ,3]";
        assertEquals("Unespected type.", Utils.ContentType.JSON, Utils.induceType(in) );
    }

    /**
     * Tests Utils#induceType() with HTML.
     */
    public void testInduceTypeHTML1() {
        String in = "  <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head> ...";
        assertEquals("Unespected type.", Utils.ContentType.HTML, Utils.induceType(in) );
    }

    /**
     * Tests Utils#induceType() with HTML.
     */
    public void testInduceTypeHTML2() {
        String in = "  <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                "<body>...\n";
        assertEquals("Unespected type.", Utils.ContentType.HTML, Utils.induceType(in) );
    }

    /**
     * Tests Utils#induceType() with XML.
     */
    public void testInduceTypeXML() {
        String in = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?><?xml-stylesheet title=\"XSL_formatting\" type=\"text/xsl\" href=\"/shared/bsp/xsl/rss/nolsol.xsl\"?><rss version=\"2.0\" xmlns:media=\"http://search.xxx-yyy.com/rss\">";
        assertEquals("Unespected type.", Utils.ContentType.XML, Utils.induceType(in) );
    }

    /**
     * Tests Utils#induceType() with unknown type.
     */
    public void testInduceTypeUnknown() {
        String in = "bla bla bla";
        assertEquals("Unespected type.", Utils.ContentType.UNKNOWN, Utils.induceType(in) );
    }

}
