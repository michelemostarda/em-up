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


package com.asemantics.mashup.processor;

import com.asemantics.mashup.processor.unification.ModelElement;
import com.asemantics.mashup.processor.unification.VariableModelElement;
import com.asemantics.mashup.processor.unification.UnificationResult;
import com.asemantics.mashup.processor.unification.Unifier;

/**
 * Defines the positional formal paramerter of a signature.
 *
 * @see com.asemantics.mashup.processor.Signature
 * @see com.asemantics.mashup.processor.InvokeOperation
 */
public class FormalParameter {

    /**
     * Formal parameter type.
     */
    private Type type;

    /**
     * Formal parameter name.
     */
    private ModelElement modelElement;

    /**
     * Internal unifier of formal parameter.
     */
    private static final Unifier unifier = new Unifier();

    /**
     * Constructor.
     *
     * @param t parameter type.
     * @param p parameter name.
     */
    public FormalParameter(Type t, String p) {
        if (t == null || p == null) {
            throw new NullPointerException();
        }
        type = t;
        modelElement = new VariableModelElement(p);
    }

    /**
     * Constructor. Default type is any.
     *
     * @param p paramter name.
     */
    public FormalParameter(String p) {
        this(Type.ANY, p);
    }

    /**
     * Constructor. Implicit model type <i>JSON</i>.
     *
     * @param me
     */
    public FormalParameter(ModelElement me) {
        if (me == null) {
            throw new NullPointerException();
        }
        type = Type.ANY;
        modelElement = me;
    }

    /**
     * Returns the type of this formal parameter.
     *
     * @return type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Verifies that  this formal parameter unifies whit the
     * given <i>argument</i>.
     *
     * @param value value to be unified.
     * @return unification result object.
     */
    public UnificationResult unify(Value value) {
        if( value instanceof JsonValue) {
            return unifier.unify(modelElement, (JsonValue) value);
        }
        return unifier.unify(modelElement, new JsonValue(value));
    }

    /**
     * Returns a human readable description of this parameter.
     * @return string description.
     */
    public String asString() {
         return modelElement.asString() + "(" + type.toString() + ")";
    }

    @Override
    public int hashCode() {
        return modelElement.hashCode();
    }

    /**
     * The model element is the discrimination in formal parameter's equality.
     *
     * @return equality condition.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FormalParameter)) {
            return false;
        }
        FormalParameter other = (FormalParameter) obj;
        return modelElement.equals(other.modelElement);
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Defines a formal parameter type.
     */
    public enum Type {
        ANY {  // Any type -- DEFAULT.
            public Value castValue(Value in) {
                return in;
            }
        },
        BOOLEAN {
            public Value castValue(Value in) {
                return in.asBoolean();
            }
        },
        NUMERIC {
            public Value castValue(Value in) {
                return in.asNumeric();
            }
        },
        STRING {
            public Value castValue(Value in) {
                return in.asString();
            }
        },
        LIST {
            public Value castValue(Value in) {
                return in.asList();
            }
        },
        MAP{
            public Value castValue(Value in) {
                return in.asMap();
            }
        },
        JSON{
            public Value castValue(Value in) {
                return in.asJsonValue();
            }
        },
        GRAPH{

            public Value castValue(Value in) {
                return in.asGraph();
            }};

        /**
         * Forces casting of <i>in</i> value to type.
         *
         * @param in input value.
         * @return casted output.
         */
        public abstract Value castValue(Value in);
    }
}
