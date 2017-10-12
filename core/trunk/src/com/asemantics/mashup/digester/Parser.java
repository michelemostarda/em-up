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

import com.asemantics.lightparser.Token;
import com.asemantics.lightparser.Tokenizer;
import static com.asemantics.lightparser.Tokenizer.TokenSeparator;
import com.asemantics.lightparser.TokenizerException;
import com.asemantics.lightparser.TokenizerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a digester for any <i>SGML</i> derived formats.
 */
public class Parser {

    /**
     * Debug flag.
     */
    public static final boolean DEBUG = false;

    /** Open tag marker. */
    public static final char OPEN_TAG             = '<';

    /** Close tag marker. */
    public static final char CLOSE_TAG            = '>';

    /** Close node marker. */
    public static final char CLOSE_NODE           = '/';

    /** Attribute value associator. */
    public static final char ATTR_VALUE_SEPARATOR = '=';

    /** Word separator (space). */
    public static final char WORD_SEPARATOR       = ' ';

    /** Word separator (return). */
    public static final char WORD_SEPARATOR2      = '\n';

    /**
     * CDATA begin sequence.
     */
    public static final String CDATA_BEGIN = "![CDATA[";

    /**
     * CDATA end sequence.
     */
    public static final String CDATA_END = "]]";


    // String versions of separator tokens.

    public static final String OPEN_TAG_STR             = Character.toString(OPEN_TAG);
    public static final String CLOSE_TAG_STR            = Character.toString(CLOSE_TAG);
    public static final String CLOSE_NODE_STR           = Character.toString(CLOSE_NODE);
    public static final String ATTR_VALUE_SEPARATOR_STR = Character.toString(ATTR_VALUE_SEPARATOR);
    public static final String WORD_SEPARATOR_STR       = Character.toString(WORD_SEPARATOR);
    public static final String WORD_SEPARATOR2_STR      = Character.toString(WORD_SEPARATOR2);

    /**
     * SGML token separators.
     */
    TokenSeparator[] SGML_TOKENS = new TokenSeparator[] {
        new TokenSeparator(OPEN_TAG             , true ), //Token, passthrough.
        new TokenSeparator(CLOSE_TAG            , true ),
        new TokenSeparator(CLOSE_NODE           , true ),
        new TokenSeparator(ATTR_VALUE_SEPARATOR , true ),
        new TokenSeparator(WORD_SEPARATOR       , true ),
        new TokenSeparator(WORD_SEPARATOR2      , true )
    };

    /**
     * Tokenizer unique instance.
     */
    private Tokenizer sgmlTokenizer;

    /**
     * If <code>true</code>, the next token is inside a SGML tag,
     * otherwise the next token is outside.
     * <pre>
     *      <this_is_inside> this is outside </this_is_inside>
     *      <this_is_inside/>
     *      <this_is_inside> this is outside  <this_is_inside> this is outside </this_is_inside> </this_is_inside>
     * </pre>
     */
    private boolean insideTag;

    /**
     * Close node "/" marker was found inside node.
     */
    private boolean closeNodeFound;

    /**
     * Close node "/" marker was found inside node as last token.
     */
    private boolean inlineNodeFound;

    /**
     * if <code>true</code> we're parsing in escape mode (i.e. &lt; and &gt; are interpreted as chars), otherwise
     * we're parsing in normal way.
     */
    private boolean escaped;

    /**
     * if <code>true</code> we're inside a CDATA section.
     */
    private boolean insideCDATA;

    
    /**
     * Content buffer for inside / outside sections.
     */
    private List<String> contentBuffer;

    /**
     * Content buffer activity buffer.
     * Content buffer is always active inside a node, can be activated outside
     * when outside section want be collected.
     */
    private boolean contentBufferOn;

    /**
     * Underlying context stack.
     */
    private ContextStack contextStack;

    /**
     * List of active char readers.
     */
    private WordReader[] readers;

    /**
     * Constructor.
     */
    public Parser(ContextStack cs) {
        if( cs == null ) {
            throw new NullPointerException();
        }
        contextStack  = cs;
        sgmlTokenizer = new TokenizerImpl(SGML_TOKENS);
        contentBuffer = new ArrayList<String>();
        readers       = new WordReader[0];
        setStringQuoting(false);
    }

    /**
     * Resets the digester data structures, setting it ready
     * for a new digestion.
     */
    public void reset() {
        insideTag       = false;
        contentBufferOn = false;
        inlineNodeFound = false;
        escaped         = false;
        insideCDATA     = false;
        contentBuffer.clear();
        contextStack .clear();
    }

    /**
     * Adds a word reader to the parser.
     *
     * @param wr word reader to be added.
     * @throws NullPointerException if cr is <code>null</code>.
     */
    public void addWordReader(WordReader wr) {
        if( wr == null ) {
            throw new NullPointerException();
        }

        WordReader[] newReaders = new WordReader[ readers.length + 1 ];
        System.arraycopy( readers, 0, newReaders, 0, readers.length );
        newReaders[ readers.length ] = wr;

        readers = newReaders;
    }

    /**
     * Removes a word reader from parser.
     *
     * @param wr word reader to remove.
     * @throws NullPointerException if cr is <code>null</code>.
     * @throws IllegalArgumentException if cr is not in list.
     */
    public void removeWordReader(WordReader wr) {
        if( wr == null ) {
            throw new NullPointerException();
        }

        int location = -1;
        for( int i = 0; i < readers.length; i++) {
            if( readers[i].equals(wr) ) {
                location = i;
                break;
            }
        }
        if( location == -1 ) {
            throw new IllegalArgumentException("Cannot find reader " + wr + " in list of readers.");
        }

        WordReader[] newReaders = new WordReader[ readers.length - 1 ];
        System.arraycopy( readers, 0, newReaders, 0, location );
        System.arraycopy( readers, location + 1, newReaders, location, newReaders.length - location );

        readers = newReaders;
    }

    /**
     * Removes all word readers.
     */
    public void removeWordReaders() {
        readers = new WordReader[0];
    }

    /**
     * Returns all word readers.
     *
     * @return list of registered word readers.
     */
    public WordReader[] getWordReaders() {
        return readers;
    }

    /**
     * Digests content of <i>in</i> string.
     * @param in
     * @throws ParserException
     */
    public void parse(String in) throws ParserException {

        // Reset first !
        reset();

        // Init tokenizer.
        try {
            // TODO: deactivate backtracking support.
            sgmlTokenizer.tokenize(in);
        } catch (TokenizerException te) {
            throw new ParserException("Error in initializing tokenizer.", te);
        }

        // Token being processed.
        Token token;
        try {
            while( (token = sgmlTokenizer.nextToken()) != null ) {

                String tokenStr = token.getValue();
                //log("TOKEN: '" + token + "'");

                // Tag opened.
                if( !escaped && OPEN_TAG_STR.equals( tokenStr ) ) {

                    // BEGIN !CDATA handling.
                    sgmlTokenizer.beginBacktrack();
                    Token t = sgmlTokenizer.nextToken();
                    if( isBeginCDATA( t.getValue() ) ) {
                        insideCDATA = true;
                        sgmlTokenizer.consumeBacktrack();
                        setStringQuoting(false);
                        setEscape(true);
                        startCollect();
                        stripBeginCDATA(t.getValue());
                        continue;
                    }
                    sgmlTokenizer.rejectBacktrack();
                    // END !CDATA handling.


                    /**
                     * Activate quoting inside tag.
                     */
                    setStringQuoting(true);

                    /**
                     * Avoid parsing tags inside other tags like "<!-- <no_detect/> -->"
                     */
                    setEscape(true);

                    // Flush outiside content.
                    if(DEBUG) {
                        log("FLUSH OUTSIDE: " + flushBuffer() );
                    } else {
                        clearBuffer();
                    }

                    insideTag = true;

                    startCollect();

                    continue;

                }

                // Notifies word readers.
               if( ! insideTag && ! insideCDATA) {
                    notifyWordReader( tokenStr );
               }

                // Tag closed. 
                if( CLOSE_TAG_STR.equals( tokenStr ) ) {

                    // BEGIN !CDATA handling.
                    if( insideCDATA ) {
                        if( isEndCDATAinContentBuffer() ) {
                            insideCDATA = false;
                            setEscape(false);
                            stripEndCDATA();
                            stopCollect();
                            notifyWordReader( flushBuffer() );
                        } else {
                            collect(CLOSE_TAG_STR);
                        }
                        continue;
                    }
                    // END !CATA handling.

                    /**
                     * Avoid considering quotes outside tags.
                     */
                    setStringQuoting(false);

                    /**
                     * Activating tags detection.
                     */
                    setEscape(false);

                    if( insideTag ) {

                        String nodeName = getNodeName();
                        log("Node name: " + nodeName);

                        // If meta node found skip it.
                        if( isCommentNode(nodeName) || isDirectiveNode(nodeName) || isInstructionNode(nodeName) ) {

                            log("SKIPPING meta node: " + nodeName);

                            // Reset all flags and data.
                            insideTag       = false;
                            inlineNodeFound = false;
                            clearBuffer();

                            continue;
                        }

                        // Is node CLOSURE tag.
                        if(closeNodeFound) {

                            // Inline node found.
                            if(inlineNodeFound) {

                                //inlineNodeFound = false;

                                NodeAttribute[] nodeAttributes = getNodeAttributes();
                                log("INLINE NODE FOUND: " + nodeName + " " + Arrays.asList(nodeAttributes) );

                                // If valid node name found.
                                if( nodeName != null ) {
                                    pushNode(nodeName, nodeAttributes);
                                    createTag(closeNodeFound, inlineNodeFound, true);
                                    popNode();
                                }

                            } else { // Node closure.

                                // Node closure found.
                                log("----- NODE CLOSURE: " + nodeName);
                                popNode( nodeName );

                                // Il close tag first evaluate then flush.
                                createTag(closeNodeFound, inlineNodeFound, true);

                            }

                        } else { // Is node OPENING tag.

                            log("----- NODE OPENING: " + nodeName);

                            // If open tag first flush then evaluate nel listeners.
                            createTag(closeNodeFound, inlineNodeFound, false);

                            // Node opening found.
                            NodeAttribute[] nodeAttributes = getNodeAttributes();
                            log("NODE ATTRIBUTES: " + Arrays.asList(nodeAttributes) );
                            pushNode( nodeName, nodeAttributes );

                            clearBuffer();

                        }

                        insideTag       = false;
                        closeNodeFound  = false;
                        inlineNodeFound = false;
                    }

                    stopCollect();

                    continue;

                }

                // Handling closure token "/".
                if( insideTag && CLOSE_NODE_STR.equals( tokenStr ) ) {
                    closeNodeFound  = true;
                    inlineNodeFound = ! contentBuffer.isEmpty();
                    continue;
                }

                // Collect word.
                collect(tokenStr);

            }

            // Flush tail outside content.
            if(DEBUG) {
                log("FLUSH OUTSIDE: " + flushBuffer() );
            } else {
                clearBuffer();
            }

        } catch (TokenizerException te) {
            throw new ParserException("Error in retrieving token.", te);
        }
    }

    /**
     * Sets the escape flag.
     *
     * @param b
     */
    protected void setEscape(boolean b) {
        escaped = b;
    }

    /**
     *
     * @return the escape flag.
     */
    protected boolean isEscape() {
        return escaped;
    }

    /**
     * Activates / deactivates string quoting.
     * 
     * @param b
     */
    protected void setStringQuoting(boolean b) {
        sgmlTokenizer.setSingleQuoteEnabled(b);
        sgmlTokenizer.setDoubleQuoteEnabled(b);
    }

    /**
     * Notifies all word readers that a word has been received.
     *
     * @param word  word received
     */
    protected void notifyWordReader(String word) {
        for(int i = 0; i < readers.length; i++) {
            readers[i].wordReceived(word);
        }
    }

    /**
     * Collects the given string.
     *
     * @param s
     * @see #contentBufferOn
     */
    private void collect(String s) {
        if( contentBufferOn ) {
            contentBuffer.add(s);
        }
    }

    /**
     * Activates collection of strings.
     */
    private void startCollect() {
        contentBufferOn = true;
    }

    /**
     * Deactivates collection of strings.
     */
    private void stopCollect() {
        contentBufferOn = false;
    }

    /**
     * Checks if given node name is a directive.
     *
     * @param nodeName
     * @return
     */
    private boolean isDirectiveNode(String nodeName) {
        return nodeName != null && nodeName.charAt(0) == '?';
    }

    /**
     * Checks if given node name is a comment.
     *
     * @param nodeName
     * @return
     */
    private boolean isCommentNode(String nodeName) {
        return nodeName != null && nodeName.equals("!--");
    }

    /**
     * Checks if given node name is an instruction.
     *
     * @param nodeName
     * @return
     */
    private boolean isInstructionNode(String nodeName) {
        return nodeName != null && nodeName.length() > 1 && '!' == ( nodeName.charAt(0) )  && '-' != ( nodeName.charAt(1) );
    }

    /**
     * Checks if given node name is a begin CDATA section.
     *
     * @param nodeName
     * @return
     */
    private boolean isBeginCDATA(String nodeName) {

        return
                nodeName != null
                        &&
                nodeName.length() >= CDATA_BEGIN.length()
                        &&
                nodeName.substring(0, CDATA_BEGIN.length()).equals(CDATA_BEGIN);
    }

    /**
     * Checks if content buffer contains !CDATA closing sequence.
     *
     * @return <code>true</code>if buffer contains closing sequence, <code>false</code> otherwise.
     */
    private boolean isEndCDATAinContentBuffer() {
        if(contentBuffer.size() == 0) {
            return false;
        }
        String last = contentBuffer.get( contentBuffer.size() - 1 );
        return
                last.length() >= CDATA_END.length()
                        &&
                last.substring( last.length() - CDATA_END.length() ).equals(CDATA_END);
    }

    /**
     * Strips a given node name from the CDATA begin prefix.
     *
     * @param nodeName
     * @return
     */
    private void stripBeginCDATA(String nodeName) {
        collect( nodeName.substring(CDATA_BEGIN.length()) );
    }

    /**
     * Strips the last string in content buffer from CDATA end.
     */
    private void stripEndCDATA() {
        int lastIndex = contentBuffer.size() - 1;
        String last = contentBuffer.get( lastIndex );
        contentBuffer.set(lastIndex, last.substring( 0, last.length() - CDATA_END.length() ) );
    }

    /**
     * Returns the candidate node name if any, <code>null</code> if content
     * buffer for some reason is empty.
     *
     * @return
     */
    private String getNodeName() {
        return contentBuffer.size() > 0 ? contentBuffer.get(0) : null;
    }

    /**
     * Returns the list of node attributes parsed inside #contentBuffer.
     *
     * @return list of {@link NodeAttribute}s.
     */
    private NodeAttribute[] getNodeAttributes() {

        // Current token.
        String token;
        // true if next token found will be considered as an attribute value,
        // false if next token found will be considere an an attribute name.
        boolean expectedValue = false;

        // List of attribute name found.
        LinkedList<String> attributeNames = new LinkedList<String>();

        // Collected result.
        List<NodeAttribute> result = new ArrayList<NodeAttribute>();

        // Starts from 1 to skip node name.
        for( int i = 1; i < contentBuffer.size(); i++ ) {

            token = contentBuffer.get(i);

            // Space skipped.
            if( WORD_SEPARATOR_STR.equals( token ) ) {
                continue;
            }

            // = activates expected value flag.
            if( ATTR_VALUE_SEPARATOR_STR.equals( token ) ) {
                expectedValue = true;
                continue;
            }

            // Other words are either attributes or values.
            if( expectedValue ) {

                // Collected both attribute name and value.
                if( ! attributeNames.isEmpty() ) {
                    String last = attributeNames.removeLast();
                    result.add( new NodeAttribute(last, stripValueToken(token) ) );
                }

                // Dumps eventual attribute names not associated with values.
                dumpAttributes( attributeNames, result );
                expectedValue = false;

            } else {

               attributeNames.add(token);

            }
        }

        // Dump eventual trailing names.
        dumpAttributes( attributeNames, result );

        return result.toArray( new NodeAttribute[ result.size() ] );
    }

    private String stripValueToken(String token) {
        int tokenLength = token.length();
        char first = token.charAt(0);
        char last  = token.charAt( tokenLength - 1 );
        boolean beginWithQuote =
                 first == Tokenizer.SINGLE_QUOTE_STRING_DELIMITER
                        ||
                 first == Tokenizer.DOUBLE_QUOTE_STRING_DELIMITER;
        boolean endWithQuote =
                 last == Tokenizer.SINGLE_QUOTE_STRING_DELIMITER
                        ||
                 last == Tokenizer.DOUBLE_QUOTE_STRING_DELIMITER;
        return token.substring( beginWithQuote ? 1 : 0, endWithQuote ? tokenLength - 1 : tokenLength);
    }

    // Populates result list with node attributes without values. 
    private void dumpAttributes(List<String> an, List<NodeAttribute> rs) {
        for( String nodeName : an ) {
            rs.add( new NodeAttribute(nodeName, null) );
        }
        an.clear();
    }

    /**
     * Pushes a node inside stack.
     *
     * @param nodeName name of node.
     * @param attributes list of attributes.
     */
    private void pushNode(String nodeName, NodeAttribute[] attributes) {
        if( nodeName == null ) {
            return;
        }

        log("PUSH node: " + nodeName);

        contextStack.pushNode(nodeName, attributes);
    }

    /**
     * Pops the first occurrence of <code>nodeName</code> from stack
     * popping out unespected nodes.
     *
     * @param nodeName name of node to pop out.
     */
    private void popNode(String nodeName) {
        if( nodeName == null ) {
            return;
        }

        log("POP node: " + nodeName);

        contextStack.popNode(nodeName);
    }

    /**
     * Pops out the first entry of stack.
     */
    private void popNode() {
        contextStack.popNode();
    }

    /**
     * Flushes the contentBuffer, and empty it.
     *
     * @return concatenation string of contentBuffer.
     * @see #contentBuffer
     */
    private String flushBuffer() {
        StringBuilder sb = new StringBuilder();
        for( String s : contentBuffer ) {
            sb.append(s);
        }
        contentBuffer.clear();
        return sb.toString();
    }

    /**
     * Returns the contentBuffer.
     *
     * @return concatenation string of contentBuffer.
     * @see #contentBuffer
     */
    private String getBuffer() {
        StringBuilder sb = new StringBuilder();
        for( String s : contentBuffer ) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Flushes the content of the buffer as a tag.
     */
    private void createTag(boolean close, boolean inline, boolean flush) {
        String buffer = flush ? flushBuffer() : getBuffer();
        if (close) {
            if (inline) {
                notifyWordReader("<" + buffer + "/>");
            } else {
                notifyWordReader("</" + buffer + ">");
            }
        } else {
            notifyWordReader("<" + buffer + ">");
        }
    }

    /**
     * Clears the buffer content.
     */
    private void clearBuffer() {
        contentBuffer.clear();
    }

    private void log(String msg) {
        if(DEBUG) {
            System.out.println("Parser: " + msg);
        }
    }

}
