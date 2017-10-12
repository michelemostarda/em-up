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

/**
 * Implements a condition to verify a comparison matching.
 */
public class ComparisonCondition implements Condition {

    /**
     * Comparison operators.
     */
    public enum Operator {

        /**
         * <
         */
        MINUS {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return asDouble( i.getValue(nc) ) < asDouble( v );
            }
        },

        /**
         * <=
         */
        MINUS_EQUAL {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return asDouble( i.getValue(nc) ) <= asDouble( v );
            }
        },

        /**
         * >
         */
        MAJOR {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return asDouble( i.getValue(nc) ) > asDouble( v );
            }
        },

        /**
         * >=
         */
        MAJOR_EQUAL {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return asDouble( i.getValue(nc) ) >= asDouble( v );
            }
        },

        /**
         * =
         */
        EQUAL {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return i.getValue(nc).equals( v );
            }
        },

        /**
         * !=
         */
        NOT_EQUAL {
            boolean compare(NodeContext nc, Identifier i, String v) {
                return ! i.getValue(nc).equals( v );
            }
        };

        /**
         * Compares a given identifier <i>i</i> defined in node context <i>nc</i>
         * with string value <i>v</i>.
         *
         * @param nc
         * @param i
         * @param v
         * @return <code>true</code> if comparison succeds, <code>false</code> otherwise.
         */
        abstract boolean compare(NodeContext nc, Identifier i, String v);

        double asDouble(String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    /**
     * Defines an entity identifier.
     */
    public static abstract class Identifier {

        /**
         * identifier sequence of chars.
         */
        String identifier;

        /**
         * Returns the value of this identifier in given node context.
         *
         * @param nc node context to be used in evaluation.
         * @return value.
         */
        abstract String getValue(NodeContext nc);

        /**
         * Constructor.
         *
         * @param id
         */
        Identifier(String id) {
            identifier = id;
        }

        /**
         *
         * @return identifier sequence.
         */
        String getIdentifier() {
            return identifier;
        }

    }

    /**
     * Defines an <i>SGML</i> attribute.
     */
    public static class Attribute extends Identifier {

        /**
         * Constructor.
         *
         * @param a attrite name.
         */
        public Attribute(String a) {
            super(a);
        }

        String getValue(NodeContext nc) {
            return nc.getValue(identifier);
        }

    }

    /**
     * Defines an <i>SGML</i> node.
     */
    public static class Node extends Identifier {

        /**
         * Constructor.
         *
         * @param n node name.
         */
        public Node(String n) {
            super(n);
        }

        String getValue(NodeContext nc) {
            throw new UnsupportedOperationException("Cannot retrieve internal nodes of a node from intenal context.");
        }

    }

    /**
     * Identifier of entity to be confronted.
     */
    private Identifier identifier;

    /**
     * Operator to be applied.
     */
    private Operator operator;

    /**
     * Value to be confronted.
     */
    private String value;

    public ComparisonCondition(Identifier i, Operator o, String v) {
        if( i == null || o == null || v == null ) {
            throw new NullPointerException();
        }
        identifier = i;
        operator   = o;
        value      = v;
    }


    public boolean matches(NodeContext nc) {
        return operator.compare(nc, identifier, value);
    }

    public String toString() {
        return "ComparisonCondition{" + identifier + "," + operator + "," + value + "}";
    }
}
