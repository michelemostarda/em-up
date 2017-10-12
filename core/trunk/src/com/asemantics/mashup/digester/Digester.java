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

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.ParseTreeVisitor;
import com.asemantics.mashup.digester.syntax.XPParser;
import com.asemantics.mashup.digester.syntax.XPParserException;

import java.util.LinkedList;
import java.util.List;

public class Digester {

    /**
     * A bufferes word reader is a word reader able to collect
     * notified words into a buffer.
     */
    protected class BufferedWordReader implements WordReader {

        /**
         * Internal buffer.
         */
        private StringBuilder sb = new StringBuilder();

        /**
         * Constructor.
         */
        BufferedWordReader() {}

        public void wordReceived(String word) {
            sb.append( word );
        }

        /**
         * Clears buffer content.
         */
        void clearBuffer() {
            sb.delete(0, sb.length());
        }

        /**
         * Flushes buffer content.
         * 
         * @return the buffer content.
         */
        String flushBuffer() {
            String result = sb.toString();
            clearBuffer();
            return result;
        }
    }

    /**
     * Digester path listener.
     */
    protected class DigesterLocationPathListener implements LocationPathListener {

        /**
         * Internal buffered word reader.
         */
        private BufferedWordReader wordReader;

        /**
         * Result collector list.
         */
        private List<String> resultCollector;

        /**
         * Constructor.
         */
        DigesterLocationPathListener() {
            wordReader      = new BufferedWordReader();
            resultCollector = new LinkedList<String>();
        }

        public void matchBegins(Context cs, LocationPath lp) {
            wordReader.clearBuffer();
            sgmlParser.addWordReader(wordReader);
        }

        public void matchEnds(Context cs, LocationPath lp) {
            sgmlParser.removeWordReader(wordReader);
            resultCollector.add( wordReader.flushBuffer() );
        }

        /**
         * Clears collector content.
         */
        void clearCollector() {
            resultCollector.clear();
        }

        /**
         * Flushes collector list.
         * @return list of strings inside digester.
         */
        public String[] flushCollector() {
            String[] result = resultCollector.toArray( new String[ resultCollector.size() ] );
            clearCollector();
            return result;
        }
    }

    /**
     * Internal <i>XPath</i> grammar parser.
     */
    private XPParser xpParser;

    /**
     * Internal context stack.
     */
    private ContextStackImpl contextStack = new ContextStackImpl();

    /**
     * Internal <i>SGML</i> parser.
     */
    private Parser sgmlParser;

    /**
     * Single instance of digester location path listener.
     */
    private final DigesterLocationPathListener digesterLocationPathListener;

    /**
     * Constructor.
     */
    public Digester() {
        xpParser     = new XPParser();
        contextStack = new ContextStackImpl();
        sgmlParser   = new Parser(contextStack);

        digesterLocationPathListener = new DigesterLocationPathListener();
    }

    /**
     * Creates a location path from an expression string.
     *
     * @param expression expression to be parsed.
     * @return the parsed location path.
     * @throws DigesterException if an error occurs in parsing <i>expression</i>.
     */
    public LocationPath createLocationPath(String expression) throws DigesterException {

        // Generated parse tree.
        ParseTree xpParseTree;

        // Parsing expression.
        try {
            xpParseTree = xpParser.parse( expression );
        } catch (XPParserException xppe) {
            throw new DigesterException("error while parsing expression '" + expression + "'.", xppe);
        }

        // Compiling expression.
        ParseTreeVisitor ptv = new ParseTreeVisitor(xpParseTree);
        return (LocationPath) ((Object[]) ptv.compile())[0];
    }

    /**
     * Finds matches of <i>locationPath</i> in <i>input</i> string.
     *
     * @param locationPath location path to be applied.
     * @param input the input string.
     * @return list of matched strings for the location path.
     * @throws DigesterException
     */
    public String[] findMatches(LocationPath locationPath, String input) throws DigesterException {

        // Clearing data.
        sgmlParser.reset();
        digesterLocationPathListener.clearCollector();

        // Registering path.
        contextStack.addLocationPath(locationPath, digesterLocationPathListener);

        // Parsing.
        try {
            sgmlParser.parse(input);
        } catch (ParserException pe) {
            throw new DigesterException("Error while parsing in string.", pe);
        } finally {
            contextStack.removeLocationPath(locationPath);
        }

        // Flushing result.
        return digesterLocationPathListener.flushCollector();
    }

    /**
     * Finds matches of <i>locationPath</i> in <i>input</i> string.
     *
     * @param locationPath location path expressed as string.
     * @param input input string.
     * @return list of matches.
     * @throws DigesterException
     */
    public String[] findMatches(String locationPath, String input) throws DigesterException {
        LocationPath lp = createLocationPath(locationPath);
        return findMatches(lp, input);
    }

}
