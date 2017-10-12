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

/**
 * Provides some general utility methods.
 */
public class Utils {

    public static final String EXPANSION_PREFIX = "${";
    public static final String EXPANSION_SUFFIX = "}";

    /**
     * Stimated char width in pixels.
     */
    protected static final int CHAR_PIXEL_WIDTH  = 8;

    /**
     * Stimated char height in pixels.
     */
    protected static final int CHAR_PIXEL_HEIGHT = 16;

    /**
     * Margin size.
     */
    protected static final int MARGIN            = 30;

    /**
     * Prevents instantiation.
     */
    private Utils() {}

    /**
     * Expannds a string with a map of variables.
     * The string will be in the form of:
     * <pre>
     * a bb ${1} ccc ${2} dddd eeeee ${3}
     * </pre>
     * where the values for ${<i>N</i>} are defined in <i>varMap</i>.
     *
     *
     * @param variableMapper map of variables.
     * @param in input string.
     * @return expanded string.
     * @throws ExpansionException if an error occurs.
     */
    public static String expandString(final VariableMapper variableMapper, final String in)
    throws ExpansionException {
        int lastBeginIndex = 0;
        int beginIndex, endIndex;
        String varName, varValue;
        StringBuilder sb = new StringBuilder();
        while( (beginIndex = in.indexOf(EXPANSION_PREFIX, lastBeginIndex)) != -1 ) {
            if( in.length() <= beginIndex ) {
                throw new ExpansionException("Expected varName after " + EXPANSION_PREFIX + ".");
            }
            endIndex = in.indexOf(EXPANSION_SUFFIX, beginIndex);
            if(endIndex == -1) {
                throw new ExpansionException(
                        "Expected closure (" + EXPANSION_SUFFIX + ") for expansion section started at " + beginIndex
                );
            }
            varName = in.substring(beginIndex + EXPANSION_PREFIX.length(), endIndex);
            if( !variableMapper.defineVariable(varName) ) {
                throw new ExpansionException("Unknown variable '" + varName + "' in section starting at " + beginIndex);
            }
            varValue = variableMapper.getValueOf(varName);
            sb.append( in.substring(lastBeginIndex, beginIndex) );
            sb.append( varValue );
            lastBeginIndex = endIndex + 1;
        }
        if( lastBeginIndex == 0 ) {
            return in;
        } else {
            sb.append( in.substring(lastBeginIndex) );
        }
        return sb.toString();
    }

    /**
     * Returns the preferred height for the given number of lines.
     *
     * @param lines number of lines.
     * @return expected height.
     */
    public static int getLinesHeight(int lines) {
        return CHAR_PIXEL_HEIGHT * lines + MARGIN * 2;
    }

    /**
     * Returns the preferred height of a stirng contained in a box
     * of given width.
     *
     * @param in input string.
     * @param boxWidth width of box containing <i>in</i> string.
     * @return expected height of the box.
     */
    public static int getStringHeight(String in, int boxWidth) {
        int stringLines = in.length() * CHAR_PIXEL_WIDTH / boxWidth;
        return ( CHAR_PIXEL_HEIGHT * stringLines + MARGIN );
    }

    /**
     * Returns the textbox size for the given string.
     *
     * @param in input string.
     * @return text box size.
     */
    public static TextBoxSize getTextBoxSize(String in) {
        int index, lineLength,lastIndex = 0, maxLength = 0, lines = 1;
        while( ( index = in.indexOf("\n", lastIndex) ) != -1 ) {
            lines++;
            lineLength = index - lastIndex;
            lastIndex  = index + 1;
            maxLength  = (maxLength < lineLength) ? lineLength : maxLength;
        }
        return new TextBoxSize(maxLength, lines);
    }

    /**
     * Encodes given Unicode char in HTML.
     *
     * @param c char to be encoded.
     * @param full if full, SGML nodes are escaped.
     * @return result.
     */
    public static String encodeChar(char c, boolean full) {
        if( c == '"' ) {
            return "&#34;";
        }
        if(full) {
            if( c == '\'' ) {
                return "&#39;";
            }
            if( c == '(' ) {
                return "&#40;";
            }
            if( c == ')' ) {
                return "&#41;";
            }
            if( c == '[' ) {
                return "&#91;";
            }
            if( c == ']' ) {
                return "&#93;";
            }
            if( c == '{' ) {
                return "&#123;";
            }
            if( c == '}' ) {
                return "&#125;";
            }
            if( c == '>' ) {
                return "&gt;";
            }
            if( c == '<' ) {
                return "&lt;";
            }
        }
        return Character.toString(c);
    }

    /**
     * Encodes <i>in</i> string as HTML.
     *
     * @param in input string.
     * @param full if also SGML special chars must be encoded.
     * @return encoded string.
     */
    public static String encodeStringAsHTML(String in, boolean full) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < in.length(); i++ ) {
            sb.append( encodeChar( in.charAt(i), full ) );
        }
        return sb.toString();
    }

    /**
     * Encodes <i>in</i> string as HTML.
     *
     * @param in input string to be encoded.
     * @return encoded string.
     */
    public static String encodeStringAsHTML(String in) {
        return encodeStringAsHTML(in, false);
    }

    /**
     * Encodes the carriage returns of a string with the
     * &lt;br/&gt; element.
     *
     * @param in the string to be encoded.
     * @return the encoded string.
     */
    public static String encodeCarrigeReturn(String in) {
        return in.replaceAll("\n", "<br/>");
    }

    /**
     * Decodes HTML encoded char in Unicode.
     *
     * @param charStr character to be encoded.
     * @param full if <code>true</code>, then also SGML special chars must be encoded.
     * @return encoded char.
     */
    public static String decodeChar(String charStr, boolean full) {
        int last = charStr.length() - 1;
        if( charStr.charAt(0) != '&' || charStr.charAt(last) != ';' ) {
            return charStr;
        }
        String code = charStr.substring(1, last);
        if( "#34".equals(code) ) {
            return "\"";
        }
        if( full ) {
            if( "#39".equals(code) ) {
                return "\'";
            }
            if( "#40".equals(code) ) {
                return "(";
            }
            if( "#41".equals(code) ) {
                return ")";
            }
            if( "#91".equals(code) ) {
                return "[";
            }
            if( "#93".equals(code) ) {
                return "]";
            }
            if( "#123".equals(code) ) {
                return "{";
            }
            if( "#125".equals(code) ) {
                return "}";
            }
            if( "lt".equals(code) ) {
                return "<";
            }
            if( "gt".equals(code) ) {
                return ">";
            }
        }
        return charStr;
    }

    /**
     * Decodes HTML encoded string in Unicode.
     *
     * @param in input string to be decoded.
     * @param full decode must be applied also for SGML special chars.
     * @return the decoded string.
     */
    public static String decodeStringAsUnicode(String in, boolean full) {
        StringBuilder sb = new StringBuilder();
        int begin;
        begin = -1;
        char c;
        for( int i = 0; i < in.length(); i++ ) {
            c = in.charAt(i);
            if( c == '&' ) {
                begin = i;
            } else if( begin != -1 && c == ';' ) {
                sb.append( decodeChar( in.substring(begin, i + 1), full) );
                begin = -1;
            } else if( begin == -1 ) { // Outside notation.
                sb.append( c );
            }
        }
        if( begin != -1 ) {
            sb.append( decodeChar( in.substring(begin), full) );
        }
        return sb.toString();
    }

    public static String decodeStringAsUnicode(String in) {
        return decodeStringAsUnicode(in, false);
    }

    /**
     * Defines a generic source content type.
     */
    public enum ContentType {
        JSON,
        XML,
        HTML,
        UNKNOWN
    }

    /**
     * Given a string containing some kind of data, this method induces
     * the possible content type.
     *
     * @param in input.
     * @return possible content type.
     */
    public static ContentType induceType(String in) {
        char c;
        for(int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if( c == ' ' || c == '\n' || c == '\t' ) {
                continue;
            }
            if( c == '[' || c == '{') {
                return ContentType.JSON;
            }
            if( c == '<' ) {
                return induceSGMLType(in, i + 1);
            }
            break;
        }
        return ContentType.UNKNOWN;
    }

    /**
     * Given a string containing some kind of SGML data and a begin index to start scanning,
     *  this method induces the possible type.
     *
     * @param in input.
     * @param begin begin index.
     * @return possible content type.
     */
    protected static ContentType induceSGMLType(String in, int begin) {
        boolean foundStartNode = true;
        char c;
        int k;
        for( int i = begin; i < in.length(); i++) {

            c = in.charAt(i);

            // Skip meta nodes.
            if( foundStartNode && (c == '!' || c == '?') ) {
                // Skip node.
                do {
                    c = in.charAt(i++);
                } while (c != '>');
                foundStartNode = false;
                continue;
            }

            if( ! foundStartNode && c == '<' ) {
                foundStartNode = true;
                continue;
            }

            // Extract node name.
            k = i+1;
            while( k < in.length() ) {
                c = in.charAt(k);
                if( c== '>' || c == ' ' || c == '\t' ) {
                    break;
                }
                k++;
            }
            String nodeName = in.substring(i, k);

            if( "html".equalsIgnoreCase(nodeName) || "body".equalsIgnoreCase(nodeName) ) {
                return ContentType.HTML;
            }

            break;
        }
        return ContentType.XML;
    }

    /**
     * Represents the generic size of a rectangulare text box.
     */
    public static class TextBoxSize {

        private int maxLineLen;
        private int numOfLines;

        TextBoxSize(int mll, int nol) {
            maxLineLen = mll;
            numOfLines = nol;
        }

        public int getMaxLineLen() {
            return maxLineLen;
        }

        public int getNumOfLines() {
            return numOfLines;
        }

        public int getHeightPx() {
            return numOfLines * CHAR_PIXEL_HEIGHT + MARGIN * 2 ;
        }

        public int getWidthPx() {
            return maxLineLen * CHAR_PIXEL_WIDTH + MARGIN * 2 ;
        }
    }

}
