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

import java.util.Arrays;

/**
 * Defines the <i>varargs</i> signature extension.
 * A varargs signature can define also a list of fixed arguments.
 *
 * <p>
 * An example of fully varargs signature is:
 * <pre>
 * Predicate(_) : <predicate body>;
 * </pre>
 * while an example of varargs signature eith fixed arguments is:
 * <pre>
 * Predicate(a1,a2,_) : <predicate body>;
 * </pre>
 * </p>
 *
 */
public class VarargsSignature extends Signature {

    /**
     * Defines key in context to retrieve a varargs value.
     */
    public static final String VARARGS_KEY = "__varargs";

    /**
     * Empty varargs array.
     */
    public static final Value[] EMPTY_VARARGS = new Value[0];

    /**
     * Helps in tetrieving varargs from execution context.
     *
     * @param ec
     * @return list of varargs
     */
    public static Value[] getVarargs(ExecutionContext ec) {
        VarargsValue varargsValue = (VarargsValue) ec.getValue( VARARGS_KEY );
        return varargsValue == null ? EMPTY_VARARGS : varargsValue.getValues();
    }

    /**
     * Constructor with no fixed arguments.
     */
    public VarargsSignature() {
        super();
    }

    /**
     * Constructor with fixed arguments.
     *
     * @param fixedArguments list of fixed arguments.
     */
    public VarargsSignature(FormalParameter[] fixedArguments) {
        super(fixedArguments);
    }

    /**
     * Constructor with model elements.
     *
     * @param modelElements list of model elements.
     */
    public VarargsSignature(ModelElement[] modelElements) {
        super(modelElements);
    }

    @Override
    public String asString() {
        return "_";
    }

    /**
     * Constext map with list of arguments.
     *
     * @param values values used to create the contect map.
     * @return context map created with list of <i>arguments</i>.
     */
    @Override
    public SignatureContextMap unify(Value[] values)
    throws SequenceNotFoundException {

        // Checks arugments size.
        if( getFormalParameters().length > values.length ) {
            throw new SequenceNotFoundException("varags: found a number of actual parameters < of the number of formal parameters");
        }

        int i = 0;

        // Unifies fixed parameters first.
        Value[] fixedValues = new Value[getFormalParameters().length];
        for(; i < fixedValues.length; i++) {
            fixedValues[i] = values[i];
        }
        // Super unification.
        SignatureContextMap resultSignature = super.unify(fixedValues);

        // If there is a rest.
        if( i < values.length ) {
            Value[] varargsValues = new Value[values.length - i];
            for(int k = 0; i < values.length; i++,k++) {
                varargsValues[k] = values[i];
            }
            resultSignature.add(VARARGS_KEY,  new VarargsValue(varargsValues), false );
        }

        return resultSignature;
    }

    /**
     * Defines a list of arguments.
     * Used to return varargs arguments.
     */
    protected class VarargsValue extends Value<Value[]> {

        /**
         * List of values.
         */
        private Value[] values;

        /**
         * Constructor.
         *
         * @param vs
         */
        VarargsValue(Value[] vs) {
            values = vs;
        }

        /**
         * Returns values.
         *
         * @return list of values.
         */
        public Value[] getValues() {
            return values;
        }

        public StringValue getValueTypeName() {
            return new StringValue("varargs");
        }

        public StringValue asString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[...");
            for(Value value : values) {
                sb.append(value.asString());
                sb.append(" ");
            }
            sb.append("]");
            return new StringValue(sb.toString());
        }

        public NumericValue asNumeric() {
            return new NumericValue( values.length );
        }

        public BooleanValue asBoolean() {
            return new BooleanValue( values.length > 0 );
        }

        public ListValue asList() {
            return new ListValue( this );
        }

        public MapValue asMap() {
            return new MapValue( Arrays.asList(values) );
        }

        public GraphValue asGraph() {
            return asMap().asGraph();
        }

        public BooleanValue equalsTo(Value v) {
            if (v!= null && v instanceof VarargsValue) {
                return new BooleanValue( values.length == ((VarargsValue) v).values.length );
            }
            return BooleanValue.FALSE_VALUE;
        }

        public NumericValue comparesTo(Value v) {
            return new NumericValue( v.asNumeric().getNativeValue() - asNumeric().getNativeValue() );
        }

        public Value cloneValue() {
            throw new UnsupportedOperationException();
        }

        public Value[] getNativeValue() {
            return values;
        }

        public String getJsonType() {
            throw new UnsupportedOperationException();
        }

        public String asJSON() {
            return ListValue.asJSONArray( Arrays.asList(values) );
        }

        public String asPrettyJSON() {
            return asJSON();
        }
    }

}
