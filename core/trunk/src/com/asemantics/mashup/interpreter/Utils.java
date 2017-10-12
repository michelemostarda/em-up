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

import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Provides some general utility methods.
 */
public class Utils {

    /**
    * Enumeration of all available values.
    */
    public enum Values {
      Boolean {
          char getCharIdentifier() {
              return 'b';
          }
          String asJSON(Value v) {
              return v.asBoolean().asJSON();
          }
      },
      String {
          char getCharIdentifier() {
              return 's';
          }
          String asJSON(Value v) {
              return  v.asString().asJSON();
          }
      },
      NakedString {
          char getCharIdentifier() {
              return 'S';
          }
          String asJSON(Value v) {
              return  v.asString().getNativeValue();
          }
      },
      Numeric {
          char getCharIdentifier() {
              return 'n';
          }
          String asJSON(Value v) {
              return v.asNumeric().asJSON();
          }
      },
      Array {
          char getCharIdentifier() {
              return 'a';
          }
          String asJSON(Value v) {
              return v.asList().asJSON();
          }
      },
      Object {
          char getCharIdentifier() {
              return 'o';
          }
          String asJSON(Value v) {
              return v.asMap().asJSON();
          }
      },
      Json {
          char getCharIdentifier() {
              return 'j';
          }
          String asJSON(Value v) {
              return v.asJsonValue().asJSON();
          }
      },
      None {
          char getCharIdentifier() {
              return 'u';
          }
          String asJSON(Value v) {
              return v.asJSON();
          }
      }
       ;

      /**
       * @return the char identifying the given type.
       */
      abstract char getCharIdentifier();

      /**
       * Converts given value in type identified by char and returns relative JSON string.
       * @param v value to be converted as type.
       * @return type representing v.
       */
      abstract String asJSON(Value v);

      public static String asJSON(char c, Value v) {
          for( Values values : Values.values() ) {
              if( c == values.getCharIdentifier() ) {
                  return values.asJSON(v);
              }
          }
          throw new RuntimeException("Unknown type: '" + c + "'");
      }
    }

    /**
     * Prefix used to defines markes in expanded strings.
     */
    public static final String EXPANSION_PREFIX = "%";

    /**
     * Prevents instantiation. 
     */
    private Utils() {}

    /**
     *  Replaces string markers expanding string.
     *
     * @param string
     * @param parameters
     * @return
     */
    public static String expandsString(final String string, String[] parameters) {
        StringValue[] stringValues = new StringValue[ parameters.length ];
        for(int i = 0; i < parameters.length; i++) {
            stringValues[i] = new StringValue( parameters[i] );
        }
        return expandsString( string, stringValues );
    }

    /**
     * Expands a given string with list of {@link com.asemantics.mashup.processor.Value} parameters.
     * The string is of form:
     * <pre>
     * lore ipsum digit %D1 datum que ls %D2 aste %D3 risque
     * </pre>
     * where D<i>N</i> is one of the discriminators defined in {com.asemantics.mashup.interpreter.Utils.Values}. 
     *
     * @param string string to be sxpanded.
     * @param parameters list of parameters.
     * @return expanded string.
     */
    public static String expandsString(final String string, Value[] parameters) {
        int lastBeginIndex = 0;
        int beginIndex;
        int arrayIndex     = 0;
        StringBuilder sb = new StringBuilder();
        while( (beginIndex = string.indexOf(EXPANSION_PREFIX, lastBeginIndex)) != -1 ) {
            if( string.length() <= beginIndex ) {
                throw new RuntimeException("Expected type char identifier after " + EXPANSION_PREFIX + ".");
            }
            if( arrayIndex == parameters.length ) {
                throw new RuntimeException("found less parameters than markers: " + arrayIndex);
            }
            sb.append( string.substring(lastBeginIndex, beginIndex) );
            sb.append( Values.asJSON( string.charAt(beginIndex + 1), parameters[arrayIndex++] ) ); 
            lastBeginIndex = beginIndex + 2;
        }
        if( lastBeginIndex == 0 ) {
            return string;
        } else {
            sb.append( string.substring(lastBeginIndex) );
        }
        return sb.toString();
    }

}
