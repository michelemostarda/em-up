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
import com.asemantics.mashup.processor.unification.UnificationResult;

/**
 * Defines signature of an {@link OperationsSequence}.
 */
public class Signature {

    /**
     * Defines the <i>no arguments</i> signature.
     */
    public static final Signature NOARGS = new Signature();

    /**
     * Defines the varargs signature.
     */
    public static final Signature VARARGS = new VarargsSignature();

    /**
     * List of formal parameters.
     */
    private FormalParameter[] formalParameters;

    /**
     * Converts an array of strings in an array of formal paramters with <i>ANY</i> type.
     * 
     * @param fps array of formal parameters as string.
     * @return array of formal parameters as <i>FormalParameter</i>.
     */
    protected static FormalParameter[] convertAsFP(String[] fps) {
        FormalParameter[] formalParameters = new FormalParameter[ fps.length ];
        for(int i = 0; i < fps.length; i++) {
            formalParameters[i] = new FormalParameter(fps[i]);
        }
        return formalParameters;
    }

    protected static FormalParameter[] convertAsFP(ModelElement[] mes) {
        FormalParameter[] formalParameters = new FormalParameter[ mes.length ];
        for(int i = 0; i < mes.length; i++) {
            formalParameters[i] = new FormalParameter(mes[i]);
        }
        return formalParameters;

    }

    /**
     * Constructor.
     *
     * @param fps
     */
    public Signature(FormalParameter[] fps) {
        if( fps == null ) {
            throw new IllegalArgumentException();
        }
        formalParameters = fps;
    }

    /**
     * Constructor based on variables.
     *
     * @param fps list of signatures.
     */
    public Signature(String[] fps) {
        if( fps == null ) {
            throw new IllegalArgumentException();
        }
        formalParameters = convertAsFP(fps);
    }

    /**
     * Constructor based on model elements.
     * 
     * @param mes list of model elements.
     */
    public Signature(ModelElement[] mes) {
        formalParameters = convertAsFP(mes);
    }

    /**
     * Constructor for signature with no arguments.
     */
    public Signature() {
        this( new String[]{} );
    }

    /**
     * Returns the formal parameters.
     * 
     * @return list of formal parameters.
     */
    public FormalParameter[]  getFormalParameters() {
        return formalParameters;
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        for(FormalParameter fp : formalParameters) {
            sb.append( fp.asString() );
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Verifies compatibility between this signature and list
     * of gives <i>arguments</i>.
     *
     * @param values list of values to be unified.
     * @return Signature context map, if unification occurs.
     * @throws SequenceNotFoundException instance if signature in not compatible with given <i>arguments</i>
     */
    protected SignatureContextMap unify(Value[] values) throws SequenceNotFoundException {

        // Verify length.
        if(  formalParameters.length != values.length ) {
            throw new SequenceNotFoundException("Arguments length differs from parameters length.");
        }

        // Process unification.
        SignatureContextMap signatureContextMap = new SignatureContextMap(values.length);
        UnificationResult unificationResult;
        for(int i = 0; i < formalParameters.length; i++) {
            unificationResult = formalParameters[i].unify(values[i]);
            if( unificationResult.isFailed() ) {
                throw new SequenceNotFoundException( "Error in unification.", unificationResult.getUnificationException() );
            }
            // Dump unification result inside signature context map.
            for( String variable : unificationResult.getVariables() ) {
                signatureContextMap.add(
                        variable,
                        formalParameters[i].getType().castValue( (Value) unificationResult.getValue(variable) ),
                        false
                );
            }
        }
        return signatureContextMap;
    }

    @Override
    public int hashCode() {
        return formalParameters == null ? 1 : formalParameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if( obj == this ) {
            return true;
        }
        if( obj instanceof Signature ) {
            Signature other = (Signature) obj;
            return
                    formalParameters == null
                            ?
                    other.formalParameters == null
                            :
                    compareFormalParameters(formalParameters, other.formalParameters);
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Compares two lists of formal parameters.
     *
     * @return <code>true</code> if equals, <code>false</code> otherwise.
     */
    private boolean compareFormalParameters(FormalParameter[] a, FormalParameter[] b) {
        if(a.length != b.length) {
            return false;
        }
        for(int i = 0; i < a.length; i++) {
            if( ! a[i].equals(b[i]) ) {
                return false;
            }
        }
        return true;
    }

}
