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


package com.asemantics.mashup.processor.jsonpath;

import com.asemantics.mashup.processor.ListValue;
import com.asemantics.mashup.processor.MapValue;
import com.asemantics.mashup.processor.json.JsonArray;
import com.asemantics.mashup.processor.json.JsonObject;
import com.asemantics.mashup.processor.json.JsonString;
import junit.framework.TestCase;

/**
 * Tests the default <i>JSONPath</i> extractor implementation.
 */
public class ExtractorTest extends TestCase {

    /**
     * Test target.
     */
    private Extractor extractor;

    @Override
    protected void setUp() throws Exception {
        extractor = new DefaultExtractorImpl();
    }

    @Override
    protected void tearDown() throws Exception {
        extractor = null;
    }

    /**
     * Tests the <i>[*]</i> accessor.
     */
    public void testStarAccessor() throws ExtractorException {
        Context testContext = createTestContext();
        Context result = extractor.extractPath(".array[*]", testContext);
        assertEquals("Unspected context size.", 6, result.getContextSize() );
        assertTrue("Unespected content.", ((JsonString) result.getElement(0)).stringValue().equals("a") );
        assertTrue("Unespected content.", ((JsonString) result.getElement(1)).stringValue().equals("b") );
        assertTrue("Unespected content.", ((JsonString) result.getElement(2)).stringValue().equals("c") );
        assertTrue("Unespected content.", ((JsonString) result.getElement(3)).stringValue().equals("d") );
        assertTrue("Unespected content.", ((JsonString) result.getElement(4)).stringValue().equals("e") );
        assertTrue("Unespected content.", ((JsonString) result.getElement(5)).stringValue().equals("f") );
    }

    /**
     * Tests the range accessor scenario.
     *
     * @throws ExtractorException
     */
    public void testRangeAccessor() throws ExtractorException {
        Context testContext = createTestContext();
        Context result1 = extractor.extractPath( ".array[1:2]", testContext );
        assertEquals("Unespected context size.", 2, result1.getContextSize());
        assertTrue("Unespected content.", ((JsonString) result1.getElement(0)).stringValue().equals("b") );
        assertTrue("Unespected content.", ((JsonString) result1.getElement(1)).stringValue().equals("c") );

        Context result2 = extractor.extractPath( ".array[1:1]", testContext );
        assertEquals("Unespected context size.", 1, result2.getContextSize());
        assertTrue("Unespected content.", ((JsonString) result2.getElement(0)).stringValue().equals("b") );

        Context result3 = extractor.extractPath( ".array[:2]", testContext );
        assertEquals("Unespected context size.", 3, result3.getContextSize());
        assertTrue("Unespected content.", ((JsonString) result3.getElement(0)).stringValue().equals("a") );
        assertTrue("Unespected content.", ((JsonString) result3.getElement(1)).stringValue().equals("b") );
        assertTrue("Unespected content.", ((JsonString) result3.getElement(2)).stringValue().equals("c") );

        Context result4 = extractor.extractPath( ".array[0:5:2]", testContext );
        assertEquals("Unespected context size.", 3, result4.getContextSize());
        assertTrue("Unespected content.", ((JsonString) result4.getElement(0)).stringValue().equals("a") );
        assertTrue("Unespected content.", ((JsonString) result4.getElement(1)).stringValue().equals("c") );
        assertTrue("Unespected content.", ((JsonString) result4.getElement(2)).stringValue().equals("e") );

        try {
            extractor.extractPath( ".array[2:1]", testContext );
            fail("Expected exception here.");
        } catch (IllegalArgumentException iae) {}
    }

    /**
     * Tests the relative range accessor.
     *
     * @throws ExtractorException
     */
    public void testRelativeRangeAccessor() throws ExtractorException {
        Context testContext = createTestContext();
        Context result = extractor.extractPath( ".array[-1:]", testContext );
        assertEquals("Unespected context size.", 1, result.getContextSize());
        assertTrue("Unespected content.", ((JsonString) result.getElement(0)).stringValue().equals("f") );
    }

    /**
     * Tests a complex extraction scenario.
     *
     * @throws ExtractorException
     */
    public void testComplexExtraction() throws ExtractorException {
        DefaultPathImpl path = new DefaultPathImpl();
        path.add( new ObjectAccessor("array") );
        path.add( new ArrayAccessor(1) );

        Context result = extractor.extractPath(path, createTestContext()       );
        assertTrue("Unespected result size.", result.getContextSize() == 1     );
        assertEquals("Unespected value.", "b", result.getElement(0).toString() );
    }

    /**
     * Tests a complex extraction scenario.
     *
     * @throws ExtractorException
     */
    public void testComplexExtractionWithPathStr() throws ExtractorException {
        Context result = extractor.extractPath(".array[1:1:1]", createTestContext() );
        assertTrue("Unespected result size.", result.getContextSize() == 1     );
        assertEquals("Unespected value.", "b", result.getElement(0).toString() );
    }

    private Context createTestContext() throws ExtractorException {
        JsonArray jsonArray   = new ListValue();
        jsonArray.add("a");
        jsonArray.add("b");
        jsonArray.add("c");
        jsonArray.add("d");
        jsonArray.add("e");
        jsonArray.add("f");

        JsonObject jsonObject = new MapValue();
        jsonObject.put("array", jsonArray);

        DefaultContextImpl context = new DefaultContextImpl();
        context.addElement(jsonObject);
        return context;
    }

/*
 * TODO: add support for these scenarios.
 *
 * <table>
 * <tr>
 * <ti>/store/book/author</ti>  <ti>$.store.book[*].author</ti> 	        <ti>the authors of all books in the store.</ti>
 * </tr>
 * <tr>
 * <ti>//author</ti> 	        <ti>$..author</ti> 	                        <ti>all authors.</ti>
 * </tr>
 * <tr>
 * <ti>/store/*</ti> 	        <ti>$.store.*</ti> 	                        <ti>all things in store, which are some books and a red bicycle.</ti>
 * </tr>
 * <tr>
 * <ti>/store//price</ti> 	    <ti>$.store..price</ti> 	                <ti>the price of everything in the store.</ti>
 * </tr>
 * <tr>
 * <ti>//book[3]</ti> 	        <ti>$..book[2]</ti> 	                    <ti>the third book.</ti>
 * </tr>
 * <tr>
 * <ti>//book[last()]</ti> 	    <ti>$..book[-1:] $..book[(@.length-1)]</ti> <ti>the last book in order.</ti>
 * </tr>
 * <tr>
 * <ti>//book[position()<3]</ti><ti>$..book[0,1] $..book[:2]</ti>           <ti>the first two books.</ti>
 * </tr>
 * <tr>
 * <ti>//book[isbn]</ti> 	    <ti>$..book[?(@.isbn)]</ti> 	            <ti>filter all books with isbn number.</ti>
 * </tr>
 * <tr>
 * <ti>//book[price<10]</ti> 	<ti>$..book[?(@.price<10)]</ti> 	        <ti>filter all books cheapier than 10.</ti>
 * </tr>
 * <tr>
 * <ti>//*</ti>	                <ti>$..*</ti> 	                            <t1>all members of JSON structure.</ti>
 * </tr>
 * </table>
 */
}
