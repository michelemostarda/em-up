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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Test class of {@link Parser}.
 */
public class ParserTest extends TestCase {

    /**
     * Test SGML stream.
     */
    public static final String SGML_STREAM =
        " outside text 1" +
        "<node1 a11 = v11 a12 = v12 x11 x12 a13 = v13 > " +
        "   outside text 2" +
        "   <node2 a21 = v21 a22 = v22 x21 a23 = v23 x22 x23> " +
        "       outside text 3 " +
        "       <node3 x31 a31 = v31 x32 x33 a32 = v32 x34 a33 = v33 x35/> " +
        "       outside text 4" +
        "   </node2> " +
        "   outside text 5 " +
        "</node1> " +
        "outside text 6 ";

    /**
     * Test XML stream.
     */
    public static final String XML_STREAM =
        "<node1 attr11=\"attr 11 value\" attr12 = \"attr 12 value\" > " +
        "   <node2 attr21=\"attr 21 value\" attr22 = \"attr 22 value\"> " +
        "       <node3 attr31=\"attr 31 value\" attr32 = \"attr 32 value\" /> " +
        "   </node2> " +
        "</node1> ";



    /**
     * Exprected sequence of nodes.
     */
    private static final String[] EXPECTED_NODE_SEQUENCE = new String[] {"node1", "node2", "node3"};

    /**
     * Expected sequence of attributes for SGML node1.
     */
    private static final NodeAttribute[] EXPECTED_N1_ATTRS = new NodeAttribute[] {
            new NodeAttribute("a11", "v11"),
            new NodeAttribute("a12", "v12"),
            new NodeAttribute("a13", "v13"),
            new NodeAttribute("x11"),
            new NodeAttribute("x12"),
    };

    /**
     * Expected sequence of attributes for SGML node2.
     */
    private static final NodeAttribute[] EXPECTED_N2_ATTRS = new NodeAttribute[] {
            new NodeAttribute("a21", "v21"),
            new NodeAttribute("a22", "v22"),
            new NodeAttribute("a23", "v23"),
            new NodeAttribute("x21"),
            new NodeAttribute("x22"),
            new NodeAttribute("x23"),
    };

    /**
     * Expected sequence of attributes for SGML node3.
     */
    private static final NodeAttribute[] EXPECTED_N3_ATTRS = new NodeAttribute[] {
            new NodeAttribute("a31", "v31"),
            new NodeAttribute("x31"),
            new NodeAttribute("a32", "v32"),
            new NodeAttribute("x32"),
            new NodeAttribute("x33"),
            new NodeAttribute("a33", "v33"),
            new NodeAttribute("x34"),
            new NodeAttribute("x35"),
    };

    /**
     * Expected sequence of attributes for XML node1.
     */
    private static final NodeAttribute[] EXPECTED_N1X_ATTRS = new NodeAttribute[] {
            new NodeAttribute("attr11", "attr 11 value"),
            new NodeAttribute("attr12", "attr 12 value"),
    };

    /**
     * Expected sequence of attributes for XML node2.
     */
    private static final NodeAttribute[] EXPECTED_N2X_ATTRS = new NodeAttribute[] {
            new NodeAttribute("attr21", "attr 21 value"),
            new NodeAttribute("attr22", "attr 22 value"),
    };

    /**
     * Expected sequence of attributes for XML node3.
     */
    private static final NodeAttribute[] EXPECTED_N3X_ATTRS = new NodeAttribute[] {
            new NodeAttribute("attr31", "attr 31 value"),
            new NodeAttribute("attr32", "attr 32 value"),
    };

    /**
     * Expected sequence of node attributes.
     */
    private static final NodeAttribute[][] ALL_ATTRS = new NodeAttribute[][] {
            EXPECTED_N1_ATTRS,
            EXPECTED_N2_ATTRS,
            EXPECTED_N3_ATTRS
    };

   /**
     * Expected sequence of node attributes.
     */
    private static final NodeAttribute[][] ALL_XML_ATTRS = new NodeAttribute[][] {
            EXPECTED_N1X_ATTRS,
            EXPECTED_N2X_ATTRS,
            EXPECTED_N3X_ATTRS
    };

    /**
     * Context stack for test purposes.
     */
    class TestParserContextStack implements ContextStack {

        private  NodeAttribute[][] expectedAttributes;

        /**
         * Max reached depth.
         */
        private int maxDepth = 0;

        /**
         * current depth.
         */
        private int depth    = 0;

        /**
         * Constructor.
         */
        TestParserContextStack( NodeAttribute[][] nas) {
            expectedAttributes = nas;
        }

        public void pushNode(String node, NodeAttribute[] attributes) {

            assertEquals("Unespected node NAME.", EXPECTED_NODE_SEQUENCE[depth], node);

            checkAttributes( expectedAttributes[depth], attributes );

            depth++;
            if( depth > maxDepth ) {
                maxDepth = depth;
            }
        }

        /**
         * Checks the returned attributes, comparing them with expected.
         *  
         * @param expected
         * @param found
         */
        private void checkAttributes(NodeAttribute[] expected, NodeAttribute[] found) {
            assertEquals("Unespected attrs size.", expected.length, found.length );

            for( int i = 0; i < expected.length; i++ ) {
                assertEquals("Unespected attribute.", expected[i], found[i]);
            }
        }

        public void popNode(String nodeName) {
            depth++;
        }

        public void popNode() {
            depth--;
        }

        public int getDepth() {
            return depth;
        }

        public NodeContext[] getNodesStack() {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            maxDepth = depth = 0;
        }

        public void addLocationPath(LocationPath lp, LocationPathListener lpl) {
            throw new UnsupportedOperationException();
        }

        public void removeLocationPath(LocationPath lp) {
            throw new UnsupportedOperationException();
        }

        public void removeLocationPaths() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Defines a word reader for test purposes.
     */
    class TestWordReader implements WordReader {

        private StringBuilder sb = new StringBuilder();

        public void wordReceived(String w) {
            sb.append( w );
        }

        public void checkResult(String expected) {
            assertEquals("Unespected result collected by word reader.", expected, sb.toString() );
        }
    }

    /**
     * Test parser instance.
     */
    private TestParserContextStack tpcs;

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Tests the SGML parsing.
     */
    public void testSGMLParsing() throws ParserException {
        tpcs   = new TestParserContextStack(ALL_ATTRS);
        TestWordReader wordReader = new TestWordReader();
        Parser parser = new Parser(tpcs);
        parser.addWordReader( wordReader );
        parser.parse( SGML_STREAM );
        wordReader.checkResult( SGML_STREAM );
    }

    /**
     * Tests the XML parsing.
     */
    public void testXMLParsing() throws ParserException {
        tpcs   = new TestParserContextStack(ALL_XML_ATTRS);
        TestWordReader wordReader = new TestWordReader();
        Parser parser = new Parser(tpcs);
        parser.addWordReader( wordReader );
        parser.parse( XML_STREAM );
        wordReader.checkResult( XML_STREAM );
    }

    /**
     * Tests the parser with an expected sequence of nodes and related
     * attributes.
     *
     * @throws ParserException
     */
    public void testNodesSequence() throws ParserException {
        tpcs   = new TestParserContextStack(ALL_ATTRS);
        Parser parser = new Parser( tpcs );
        parser.parse( SGML_STREAM );
        assertEquals("Invalid max depth.", 3, tpcs.maxDepth);
    }

    /**
     * Tests the parser with real data.
     *
     * @throws ParserException
     */
    public void testRealData() throws ParserException {
        Parser parser = new Parser( new ContextStackImpl() );
        String massiveData = retrieveData();
        parser.parse( massiveData );
    }

    /**
     * Tests that a tag inside a comment tag cannot be detected.
     * 
     * @throws ParserException
     */
    public void testParsingCommentedData() throws ParserException {
        Parser parser = new Parser( new ContextStackImpl() {

            public void pushNode(String node, NodeAttribute[] attributes) {
                super.pushNode(node, attributes);
                System.out.println("node: " + node);
                fail("No nodes must be detected.");
            }
        });
        String in = "<!-- <no_detected/> -->";
        parser.parse( in );
    }

    /**
     * Tests that a tag inside another tag cannot be detected.
     *
     * @throws ParserException
     */
    public void testParsingNestedTags() throws ParserException {
        Parser parser = new Parser( new ContextStackImpl() {

            public void pushNode(String node, NodeAttribute[] attributes) {
                super.pushNode(node, attributes);
                System.out.println("node: " + node);
                assertEquals("Unespected node", "node", node);
            }
        });
        String in = "<node <no_detected/> > xxx </node>";
        parser.parse( in );
    }

    /**
     * Tests correct DCATA handling.
     *
     * @throws ParserException
     */
    public void testCDATAParsing() throws ParserException {
        Parser parser = new Parser( new ContextStackImpl() {

            public void pushNode(String node, NodeAttribute[] attributes) {
                super.pushNode(node, attributes);
                System.out.println("node: " + node);
                assertEquals("Unespected node", "node", node);
            }

            public void popNode() {
                 fail("Pop node must not be detected.");
            }
        });
        String in = "<node> <![CDATA[ This section contains a close tag that is ignored. </node> ]]>";
        parser.parse( in );
    }

    /**
     * Retrieves data from a real URL.
     * 
     * @return
     */
    private String retrieveData() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://www.bbc.co.uk/london/");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "";
        }
        return sb.toString();
    }

}
