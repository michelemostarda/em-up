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


package com.asemantics.mashup.digester;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Tests the {@link com.asemantics.mashup.digester.Digester} class.
 */
public class DigesterTest extends TestCase {

    /**
     * Test input stream.
     */
    public static final String SGML_STREAM =
            "<a>            " +
            "   <b>         " +
            "       <c>     " +
            "           <f/>" +
            "       </c>    " +
            "   </b>        " +
            "   <c>         " +
            "       <d/>    " +
            "   </c>        " +
            "   <b>         " +
            "       <e/>    " +
            "   </b>        " +
            "</a>           ";

    /**
     * Test target.
     */
    private Digester digester;

    /**
     * Input RSS loaded from classpath.
     */
    private final String INPUT_RSS;

    /**
     * Test constructor.
     *
     * @throws IOException
     */
    public DigesterTest() throws IOException {
        INPUT_RSS = loadRealInput();
    }

    protected void setUp() throws Exception {
        digester = new Digester();
    }

    protected void tearDown() throws Exception {
        digester = null;
    }

    /**
     * Tests the creation of a location path.
     *
     * @throws DigesterException
     */
    public void testCreateLocationPath() throws DigesterException {

        LocationPath lp = digester.createLocationPath( "/a/b/c[1]" );

        assertNotNull("Expected location path.", lp);
        assertEquals("Unexpected length.", 3, lp.getSteps().length );

        assertEquals("Unespected step.", "a", lp.getStep(0).getIdentifier() );
        assertEquals("Unespected step.", "b", lp.getStep(1).getIdentifier() );
        assertEquals("Unespected step.", "c", lp.getStep(2).getIdentifier() );

        assertNull("Unespected condition."  , lp.getStep(0).getCondition() );
        assertNull("Unespected condition."  , lp.getStep(1).getCondition() );
        assertTrue("Unespected condition."  , lp.getStep(2).getCondition() instanceof IndexCondition );
    }

    /**
     * Tests finding of a single match.
     *
     * @throws DigesterException
     */
    public void testFindSingleMatch() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "/a/b/c" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 1, result.length);
        checkContent( "<f/>", result[0].trim() );
    }

    /**
     * Tests finding of a multi match.
     *
     * @throws DigesterException
     */
    public void testFindMultiMatch() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "/a/b" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 2, result.length);
        checkContent( "<c> <f/> </c>", result[0].trim() );
        checkContent( "<e/>",        result[1].trim() );
    }

    /**
     * Tests finding of a multi match restriction.
     *
     * @throws DigesterException
     */
    public void testFindMultiMatch0() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "/a/b[0]" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 1, result.length);
        checkContent("<c> <f/> </c>", result[0].trim() );
    }

    /**
     * Tests finding of a multi match restriction.
     *
     * @throws DigesterException
     */
    public void testFindMultiMatch1() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "/a/b[1]" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 1, result.length);
        checkContent( "<e/>", result[0].trim() );
    }

    /**
     * Tests finding of a relative match.
     *
     * @throws DigesterException
     */
    public void testFindRelativeMatch1() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "b/c" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 1, result.length);
        checkContent( "<f/>", result[0].trim() );
    }

    /**
     * Tests finding of a relative match.
     *
     * @throws DigesterException
     */
    public void testFindRelativeMatch2() throws DigesterException {
        LocationPath lp = digester.createLocationPath( "c" );
        System.out.println("Location path: " + lp);
        String[] result = digester.findMatches(lp, SGML_STREAM);
        assertEquals("Unespected result size.", 2, result.length);
        checkContent( "<f/>", result[0].trim() );
        checkContent( "<d/>", result[1].trim() );
    }

    /**
     * Tests that the evaluation of CDATA context is done correctly.
     *
     * @throws DigesterException
     */
    public void testReadCDATA()
    throws DigesterException {
        String in = "<container><![CDATA[Content of > escaped section]]></container>";
        String[] items = digester.findMatches("container", in);
        System.out.println( "items: " + Arrays.asList(items) );

        assertEquals("Unespected items length.", 1, items.length);
        assertEquals("Unespected result content.", "Content of > escaped section", items[0]);
    }

    /**
     * Tests the Digester with a real RSS feed, reading all the items.
     *
     * @throws IOException
     * @throws DigesterException
     */
    public void testRealRSSItems() throws IOException, DigesterException {
        String[] items = digester.findMatches("item", INPUT_RSS);
        printList("items", items);
        assertEquals("Unespected number of items.", 11, items.length);
    }

    /**
     * Tests the Digester with a real RSS feed, reading all the links.
     *
     * @throws IOException
     * @throws DigesterException
     */
    public void testRealRSSLinks() throws IOException, DigesterException {
        String[] links = digester.findMatches("link", INPUT_RSS);
        printList("link", links);
        assertEquals("Items should be as links minus 2.", 13, links.length);

    }

    /**
     * Tests the Digester with a real RSS feed, reading all the enclosures.
     *
     * @throws IOException
     * @throws DigesterException
     */
    public void testRealRSSEnclosures() throws IOException, DigesterException {
        String[] enclosures = digester.findMatches("enclosure", INPUT_RSS);
        printList("enclosures", enclosures);
        assertEquals("Expected enclosures not found.", 3, enclosures.length);
    }


    /**
     * Tests the Digester with a real RSS feed, reading all the titles.
     *
     * @throws IOException
     * @throws DigesterException
     */
    public void testRealRSSTitles() throws IOException, DigesterException {
        String[] titles = digester.findMatches("title", INPUT_RSS);
        printList("titles", titles);
        assertEquals("Unespected number of titles.", 13, titles.length);
        // Check title content.
        for(String title : titles) {
            assertFalse("Unespected content in title.", title.contains("!CDATA"));
            assertFalse("Unespected content in title.", title.contains("]]"));
        }
    }

    /**
     * Tests the Digester with a real RSS feed, reading all the descriptions.
     *
     * @throws IOException
     * @throws DigesterException
     */
    public void testRealRSSDescriptions() throws IOException, DigesterException {
        String[] descriptions = digester.findMatches("description", INPUT_RSS);
        printList("descriptions", descriptions);
        assertEquals("Expected mandatory descriptions.", 12, descriptions.length);
        for(String description : descriptions) {
            assertTrue("Error in description: \"" + description + "\".", checkTagBalancingInString(description));
        }

    }

    /**
     * Checks that the given string open and closes tags regurally.
     *
     * @param description
     * @return <code>true</code> if if balanced, <code>false</code> otherwise.
     */
    protected boolean checkTagBalancingInString(String description) {
        boolean tagOpen = false;
        for(int i = 0; i < description.length(); i++) {
            if(description.charAt(i) == '<') {
                if(tagOpen) {
                    return false;
                }
                tagOpen = true;
            } else if(description.charAt(i) == '>') {
                tagOpen = false;
            }
        }
        return true;
    }

    protected void printList(String collectionName, String[] list) {
        System.out.printf("Collection %s {", collectionName);
        for(String element : list) {
            System.out.println(element);
            System.out.println("-----------------------------------------------------------------------------------");
        }
        System.out.println("}");
    }

    /**
     * Loads a real input stream.
     *
     * @return content of real input file.
     * @throws IOException if an error occurs in reading.
     */
    protected String loadRealInput() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/resources/digester/digester-real-input.xml");
        InputStreamReader isr = new InputStreamReader(is);
        StringBuilder sb = new StringBuilder();
        int c;
        while ( (c = isr.read()) != -1 ) {
            sb.append((char) c);
        }
        is.close();
        return sb.toString();
    }

    /**
     * Normalizes string removing unuseful spaces.
     * 
     * @param s
     * @return
     */
    private String normalize(String s) {
        boolean firstSpaceFound = false;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            if( s.charAt(i) == ' ' ) {
                if( firstSpaceFound ) {
                    continue;
                }
                firstSpaceFound = true;
            } else {
                firstSpaceFound = false;
            }
            sb.append( s.charAt(i) );
        }
        return sb.toString().trim();
    }

    /**
     * Checkes normalized contents.
     *
     * @param expected
     * @param in
     */
    private void checkContent(String expected, String in) {
        assertEquals("Unespected content.", normalize(expected), normalize(in) );
    }

}
