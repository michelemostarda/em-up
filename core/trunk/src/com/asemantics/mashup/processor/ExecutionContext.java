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

import com.asemantics.mashup.parser.MUGrammarFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Defines the execution context for a process.
 *
 * @see com.asemantics.mashup.processor.ExecutionStack
 */
public class ExecutionContext {

    /**
     * No flags specified.
     */
    public static final byte NONE           = 0x0;

    /**
     * The invocable is native.
     */
    protected static final byte NATIVE      = 0x1;

    /**
     * The invocable is deletable.
     */
    protected static final byte DELETABLE   = 0x2;

    /**
     * The invocable is overridable.
     */
    protected static final byte OVERRIDABLE = 0x4;

    /**
     * The invocable is overloadable.
     */
    protected static final byte OVERLOADABLE = 0x8;

    /**
     * Provides unique indexes for every invocable object.
     */
    private static int invocableObjectCounter = 0;

    /**
     * Static empty invocable.
     */
    private static final Invocable[] INVOCABLE = new Invocable[0];

    /**
     * Returns next invocable counter.
     *
     * @return index of next invocable counter.
     */
    protected static int nextInvocableCounter() {
        return invocableObjectCounter++;
    }

    /**
     * Defines an {@link com.asemantics.mashup.processor.Invocable}
     * senquence and a set of modifiers.
     */
    class InvocableObject implements Comparable<InvocableObject> {

        /**
         * Defines an univocal index for every invocable.
         */
        private int index;

        /**
         * Invocable internal.
         */
        Invocable invocable;

        /**
         * Modifiers associated to invocable.
         */
        byte modifiers = NONE;

        /**
         * Constructor.
         *
         * @param i invocable.
         * @param m modifiers.
         */
        InvocableObject(Invocable i, byte m) {
            if(i == null) {
                throw new ExecutionContextException("Invocable cannot be null.");
            }
            index = nextInvocableCounter();
            invocable = i;
            modifiers = m;
        }

        /**
         * Constructor.
         * Sets as defualt the only native flag.
         *
         * @param i
         */
        InvocableObject(Invocable i) {
            this(i, NATIVE);
        }

        /**
         * @return the invocable object.
         */
        Invocable getInvocable() {
            return invocable;
        }

        /**
         * @return invocable modifiers.
         */
        byte getModifiers() {
            return modifiers;
        }

        /**
         * Returns the native flag.
         *
         * @return <code>true</code> if this invocable has the native flag,
         *         <code>false</code> otherwise.
         */
        boolean isNative() {
            return (NATIVE & modifiers) == NATIVE;
        }

        /**
         * Returns the deletable flag.
         *
         * @return <code>true</code> if this invocable has the deletable flag,
         *         <code>false</code> otherwise.
         */
        boolean isDeletable() {
            return (DELETABLE & modifiers) == DELETABLE;
        }

        /**
         * Returns the overridable flag.
         *
         * @return <code>true</code> if this invocable has the overridable flag,
         *         <code>false</code> otherwise.
         */
        boolean isOverridable() {
            return (OVERRIDABLE & modifiers) == OVERRIDABLE;
        }

        /**
         * Returns the overloadable flag.
         *
         * @return <code>true</code> if this invocable has the overloadable flag,
         *         <code>false</code> otherwise.
         */
        boolean isOverloadable() {
            return (OVERLOADABLE & modifiers) == OVERLOADABLE;
        }

        public int compareTo(InvocableObject other) {
            return index - other.index;
        }

        @Override
        public boolean equals(Object obj) {
            if( obj == null ) {
                return false;
            }
            if( obj == this ) {
                return true;
            }
            if( obj instanceof InvocableObject ) {
                InvocableObject other = (InvocableObject) obj;
                return invocable.equals(other.invocable)
                        &&
                modifiers == other.modifiers;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return invocable.hashCode() * modifiers;
        }
    }

    /**
     * Defines a list of possible alternatives for an overload.
     */
    // TODO: integrate alternatives.
    class AlternativeList {

        /**
         * Common signature for all alternatives.
         */
        private final Signature signature;

        /**
         * List of alternatives.
         */
        private List<InvocableObject> alternatives;

        /**
         * Constructor.
         *
         * @param signature common signature of this list of alternatives.
         * @param alternative the first alternative.
         */
        AlternativeList(Signature signature, InvocableObject alternative) {
            assert signature.equals( alternative.getInvocable().getSignature() ) : "Expected same signature";
            this.signature = signature;
            alternatives = new ArrayList<InvocableObject>();
            alternatives.add(alternative);
        }

        /**
         * Adds a new alternative to the last place.
         * 
         * @param alternative
         */
        void addAlternative(InvocableObject alternative) {
            assert signature.equals( alternative.getInvocable().getSignature() ) : "Expected same signature";
            alternatives.add(alternative);
        }

        /**
         * Adds a new alternative to the specified index.
         *
         * @param alternative
         * @param i
         */
        void addAlternative(InvocableObject alternative, int i) {
            assert signature.equals( alternative.getInvocable().getSignature() ) : "Expected same signature";
            alternatives.add(i, alternative);
        }

        /**
         * Removes the i-th alternative.
         *
         * @param i index of alternative to remove.
         * @return the removed alternative.
         */
        InvocableObject removeAlternative(int i) {
            return alternatives.remove(i);
        }

        /**
         * @return the signature of this alternative.
         */
        Signature getSignature() {
            return signature;
        }

        /**
         * @return list of invocable objects.
         */
        List<InvocableObject> getAlternatives() {
            return Collections.unmodifiableList(alternatives);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if(obj == this) {
                return true;
            }
            if(obj instanceof AlternativeList) {
                AlternativeList other = (AlternativeList) obj;
                return this.signature.equals( other.signature );
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return signature.hashCode();
        }

    }

    /**
     * Defines a set of invocable objects with the same name.
     */
    class Overload {

        /**
         * Name of senquence owning this overload.
         */
        private String ownerSequence;

        /**
         * Overload list.
         */
        private Set<InvocableObject> invocableObjects;

        /**
         * Constructor.
         *
         * @param owner the owner sequence name.
         * @param io
         */
        Overload(String owner, InvocableObject io) {
            ownerSequence    = owner;
            invocableObjects = new HashSet<InvocableObject>();
            invocableObjects.add(io);
        }

        /**
         * Returns the list of invocables inside this overload.
         *
         * @return
         */
        public Invocable[] getInvocables() {
            Invocable[] result = new Invocable[ invocableObjects.size() ];
            int i = 0;
            for( InvocableObject invocableObject : invocableObjects ) {
                result[i++] = invocableObject.getInvocable();
            }
            return result;
        }

        /**
         * Returns the list of invocable objects inside this overload.
         *
         * @return
         */
        public InvocableObject[] getInvocableObjects() {
            return invocableObjects.toArray( new InvocableObject[ invocableObjects.size() ] );
        }

        /**
         * Returns the specific invocable of this overload that defines the given signature.
         *
         * @param signature signature to be compared.
         * @return
         */
        Invocable getInvocable(Signature signature) {
            Invocable invocable;
            for(InvocableObject invocableObject : invocableObjects) {
                invocable = invocableObject.getInvocable();
                if( invocable.getSignature().equals(signature) ) {
                    return invocable;
                }
            }
            return null;
        }

        /**
         * Adds a specific invocable object into this overload.
         *
         * @param toBeAdded invocable object to be added.
         * @throws ExecutionContextException if invocable oject already exists and it is not overridable.
         */
        public void add(InvocableObject toBeAdded) {
            final Signature toBeAddedSignature = toBeAdded.getInvocable().getSignature();
            for(InvocableObject io :  invocableObjects) {
                // Check whether the invocable has the same signature of the existing one.
                if( io.getInvocable().getSignature().equals(toBeAddedSignature) ) {
                    if( ! io.isOverridable() ) {
                        throw new ExecutionContextException(
                                "invocable '" + io + "' in sequence '" + ownerSequence  +
                                "' aready exists and is not overridable."
                        );
                    }
                }
            }
            // Add the new one.
            boolean added = invocableObjects.add(toBeAdded);
            assert added : "toBeAdded is already present.";
        }

        /**
         * Removes a specific overaload entry matching with given signature.
         * 
         * @param signature
         * @return
         */
        public InvocableObject delete(Signature signature) {
            for(InvocableObject invocableObject : invocableObjects) {
                if( invocableObject.getInvocable().getSignature().equals(signature) ) {
                    if(invocableObject.isDeletable()) {
                        invocableObjects.remove(invocableObject);
                        return invocableObject;
                    }
                    throw new ExecutionContextException("Overload '" + ownerSequence + "' is flagged as non deletable.");
                }
            }
            return null;
        }

        /**
         * Removes the content of this overload returning all invocable objects.
         *
         * @return
         */
        public InvocableObject[] delete() {
            InvocableObject[] result = getInvocableObjects();
            invocableObjects.clear();
            return result;
        }

    }

    /**
     * Map of callable sequences.
     */
    private Map<String,Overload> sequenceMap;

    /**
     * Map of variables.
     */
    private SignatureContextMap signatureContextMap;

    /**
     * Queue of nested arguments.
     */
    private Queue<Value> argumentsQueue;

    /**
     * The execution context print stream.
     */
    private PrintStream printStream;

    /**
     * Constructor used to create restricted contexts.
     *
     * @param ps
     * @param sm
     * @param scm
     */
    private ExecutionContext(PrintStream ps, Map<String, Overload> sm, SignatureContextMap scm) {
        if( ps == null ) {
            throw new IllegalArgumentException();
        }
        if(sm == null) {
            throw new IllegalArgumentException();
        }
        printStream         = ps;
        sequenceMap         = sm;
        signatureContextMap = scm;

        argumentsQueue = new LinkedList<Value>();
    }

    /**
     * Constructor.
     *
     * @param ps allows to specify the print stream.
     */
    protected ExecutionContext(PrintStream ps) {
        this(ps, new HashMap<String,Overload>(), new SignatureContextMap() );
    }

    /**
     * Constructor
     */
    protected ExecutionContext() {
        this(System.out);
    }

    /**
     * Returns the print stream.
     *
     * @return runtime print stream.
     */
    public PrintStream getPrintStream() {
        return printStream;
    }

    /**
     * Returns <code>true</code> if this context contains the sequence name,
     * <code>false</code> otherwise.
     *
     * @param sequenceName name of the sequence to be found.
     * @return result of check.
     */
    public boolean containsSequence(String sequenceName) {
        return sequenceMap.containsKey(sequenceName);
    }

    /**
     * Returns <code>true</code> if this context contains the sequence name
     * whith the specified signature, <code>false</code> otherwise.
     *
     * @param sequenceName name of the sequence to be found.
     * @param signature signature to be matched.
     * @return the sequence matching with specified criteria, <code>null</code> otherwise.
     */
    Invocable getSequence(String sequenceName, Signature signature) {
        Overload overload = sequenceMap.get(sequenceName);
        return overload == null ? null : overload.getInvocable(signature);
    }

    /**
     * Returns a sequence by name.
     *
     * @param sequenceName name of sequence.
     * @return required invocable, or <code>null</code> if sequence name doesn't match.
     * @throws InvocationException if sequence name is found but arguments don't match any overload.
     */
    public Invocable[] getSequences(String sequenceName) throws InvocationException {
        Overload overload = sequenceMap.get(sequenceName);
        return overload == null ? INVOCABLE : overload.getInvocables();
    }

    /**
     * Adds a sequence to this context with specified flags.
     *
     * @param sequenceName
     * @param flags
     * @param sequence
     */
    public void addSequence(String sequenceName, Invocable sequence, byte flags) {
        Overload overload = sequenceMap.get(sequenceName);
        if( overload == null ) {
            sequenceMap.put( sequenceName, new Overload( sequenceName, new InvocableObject(sequence, flags) ) );
        } else {
            overload.add( new InvocableObject(sequence, flags) );
        }
    }

    /**
     * Adds a sequence to this context that is deletable and overridable.
     *
     * @param sequenceName
     * @param sequence
     * @see #addSequence(String, Invocable, byte)
     */
    public void addSequence(String sequenceName, Invocable sequence) {
        addSequence(sequenceName, sequence, (byte)(DELETABLE | OVERRIDABLE) );
    }

    /**
     * Removes a sequence from this context.
     *
     * @param sequenceName name of senquence to be removed.
     * @return the removed invocable if found, <code>null</code> otherwise.
     */
    public InvocableObject[] removeSequence(String sequenceName) {
        Overload overload = sequenceMap.remove(sequenceName);
        return overload == null ? new InvocableObject[0] : overload.delete();
    }

    /**
     * Removes a specific signature overload from this context.
     *
     * @param sequenceName
     * @param signature
     * @return the removed invocable.
     */
    public InvocableObject removeSequence(String sequenceName, Signature signature) {
        Overload overload = sequenceMap.get(sequenceName);
        return overload.delete(signature);
    }

    /**
     * Returns <code>true</code> if this context contains a variable name,
     * <code>false</code> otherwise.
     *
     * @param var
     * @return result of check.
     */
    public boolean containsVariable(String var) {
        return signatureContextMap.containsValueName(var);
    }

    /**
     * Returns a variable value.
     *
     * @param var
     * @return required value.
     */
    public Value getValue(String var) {
        return signatureContextMap.getValue(var);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}.
     *
     * @param i i-th index.
     * @return internal value.
     */
    public Value getIthValue(int i) {
        return signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.BooleanValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public BooleanValue getIthValueAsBoolean(int i) {
        return (BooleanValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.NumericValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public NumericValue getIthValueAsNumeric(int i) {
        return (NumericValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.StringValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public StringValue getIthValueAsString(int i) {
        return (StringValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.ListValue}.
     *
     * @param i i-th  index.
     * @return casted value.
     */
    public ListValue getIthValueAsList(int i) {
        return (ListValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.MapValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public MapValue getIthValueAsMap(int i) {
        return (MapValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.JsonValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public JsonValue getIthValueAsJson(int i) {
        return (JsonValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Returns the i-th argument value as defined in {@link com.asemantics.mashup.processor.Signature}
     * casted as {@link com.asemantics.mashup.processor.GraphValue}.
     *
     * @param i i-th index.
     * @return casted value.
     */
    public GraphValue getIthValueAsGraph(int i) {
        return (GraphValue) signatureContextMap.getIthArgumentValue(i);
    }

    /**
     * Sets a variable value.
     *
     * @param var variable name.
     * @param value variable value.
     */
    public void addVariable(String var, Value value) {
        signatureContextMap.add(var, value, true);
    }

    /**
     * Removes a variable from this context.
     *
     * @param var variable name.
     */
    public void removeVariable(String var) {
        signatureContextMap.remove(var);
    }

    /**
     * Prints a string on the context print stream.
     *
     * @param stringValue
     */
    public void print(StringValue stringValue) {
        getPrintStream().print( stringValue.asString().getNativeValue() );
    }

    /**
     * Returs the sequence map.
     *
     * @return internal sequence map.
     */
    protected Map<String,Overload> getSequenceMap() {
        return sequenceMap;
    }

    /**
     * Creates a new context on this current context trasferring the content of the given signature context map.
     *
     * @return created context.
     * @throws InvocationException
     */
    protected ExecutionContext createContext(SignatureContextMap scm)
    throws InvocationException {
        return new ExecutionContext(getPrintStream(), getSequenceMap(), scm);
    }

    /**
     * Adds value to queue.
     *
     * @param value
     * @see com.asemantics.mashup.processor.EnqueueArgumentOperation
     */
    protected void enqueueArgument(Value value) {
        argumentsQueue.add(value);
    }

    /**
     * Polls value from queue.
     *
     * @return dequeued argument.
     * @see com.asemantics.mashup.processor.EnqueueArgumentOperation
     */
    protected Value dequeueArgument() {
        return argumentsQueue.poll();
    }

    /**
     * Returns a string listing the invocable objects in context.
     *
     * @return list of invocable sequences.
     */
    public String getContextSequencesList() {
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String,Overload> entry :  sequenceMap.entrySet() ) {
            sb.append( entry.getKey() ).append( " : {" );
            for(Invocable invocable : entry.getValue().getInvocables() ) {
                sb.append( invocable.getSignature() ).append(" ");
            }
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     * Returns a sequence short description.
     *
     * @param sequenceName
     * @return  short description.
     */
    public String getSequenceShortDescription(String sequenceName) {
        Overload overload = sequenceMap.get(sequenceName);
        if( overload == null ) {
            return "Unkown sequence";
        }
        Invocable[] invocables = overload.getInvocables();
        StringBuilder sb = new StringBuilder();
        for(Invocable invocable : invocables) {
            fillSequenceShortDescription(sb, sequenceName, invocable);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns complete description of given sequence.
     *
     * @param sequenceName
     * @return  complete description.
     */
    public String getSequenceDescription(String sequenceName) {
        Overload overload = sequenceMap.get(sequenceName);
        if( overload == null ) {
            return "Unkown sequence";
        }
        Invocable[] invocables = overload.getInvocables();
        StringBuilder sb = new StringBuilder();
        for(Invocable invocable : invocables) {
            fillSequenceDescription(sb, sequenceName, invocable);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a string listening the invocable objects in
     * context with a short description.
     *
     * @return short description of context elements.
     */
    public String getContextSequencesShortDescription() {
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String,Overload> entry :  sequenceMap.entrySet() ) {
            for( Invocable invocable : entry.getValue().getInvocables() ) {
                fillSequenceShortDescription(sb, entry.getKey(), invocable );
            }
        }
        return sb.toString();
    }

    /**
     * Returns a string listing the invocable objects in
     * context with complete description.
     *
     * @return textual descriptions.
     */
    public String getContextSequencesLongDescription() {
        StringBuilder sb = new StringBuilder();
        for( Map.Entry<String,Overload> entry :  sequenceMap.entrySet() ) {
            for( Invocable invocable : entry.getValue().getInvocables() ) {
                fillSequenceDescription(sb, entry.getKey(), invocable );
            }
        }
        return sb.toString();
    }

    /**
     * Returns native invocables.
     *
     * @return  map of native invocables.
     */
    public Map<String,Invocable> getNativeInvocables() {
        return filterInvocables( NATIVE );
    }

    /**
     * Returns not native invocables.
     *
     * @return map of non native invocables.
     */
    public Map<String,Invocable> getNotNativeInvocables() {
        return filterInvocables( (byte) ~NATIVE );
    }

    /**
     * Returns overridable invocables.
     *
     * @return map of native overridables.
     */
    public Map<String,Invocable> getOverridableInvocables() {
        return filterInvocables( OVERRIDABLE );
    }

    /**
     * Returns not overridable invocables.
     *
     * @return map of non overridable invocables.
     */
    public Map<String,Invocable> getNotOverridableInvocables() {
        return filterInvocables( (byte) ~OVERRIDABLE );
    }

    /**
     * Returns deletable invocables.
     *
     * @return  list of deletable invocables.
     */
    public Map<String,Invocable> getDeletableInvocables() {
        return filterInvocables( DELETABLE );
    }

    /**
     * Returns not deletable invocables.
     *
     * @return map of non deletable invocables.
     */
    public Map<String,Invocable> getNotDeletableInvocables() {
        return filterInvocables( (byte) ~DELETABLE );
    }

    /**
     * Returns list of all programmative invocables defined inside this context
     * sorted by addition.
     *
     * @return list of programmative invocables.
     */
    public ProgrammativeInvocable[] getProgrammativeInvocables() {
        List<InvocableObject> programmatives = new ArrayList<InvocableObject>(
            filterInvocableObjects((byte) ~NATIVE).values()
        );
        Collections.sort(programmatives);
        ProgrammativeInvocable[] result = new ProgrammativeInvocable[ programmatives.size() ];
        int i = 0;
        for(InvocableObject io : programmatives) {
            result[i++] = (ProgrammativeInvocable) io.getInvocable();
        }
        return result;
    }

    /**
     * Prints out the list of programmative invocables.
     *
     * @param sb string builder to use as output.
     */
    public void printProgrammativeContext(StringBuilder sb) {
        ProgrammativeInvocable[] pis = getProgrammativeInvocables();
        int size = pis.length;
        int row = 0;
        for(ProgrammativeInvocable pi : pis) {
            generateRowComment(sb, row++, size);
            sb.append("  ");
            sb.append( pi.getTreeNode().getRoot().getName() );
            sb.append("\n");
        }
    }

    /**
     * Prints out the list of programmative context.
     * 
     * @return programmative context.
     */
    public String printProgrammativeContext() {
        StringBuilder sb = new StringBuilder();
        printProgrammativeContext(sb);
        return sb.toString();
    }

    /**
     * Fills given string builder with info of given sequence with a short description.
     *
     * @param sb string builder to be filled.
     * @param sequenceName name of sequence.
     * @param sequence sequence definition.
     */
    protected void fillSequenceShortDescription(StringBuilder sb, String sequenceName, Invocable sequence) {
        sb
            .append( sequenceName )
            .append( ":" )
            .append( sequence.getSignature().toString() )
            .append(" - ")
            .append( sequence.getShortDescription() )
            .append("\n");
    }
    
    /**
     * Fills given string builder with info of given sequence with a complete description.
     *
     * @param sb string builder to be filled.
     * @param sequenceName name of sequence.
     * @param sequence sequence definition.
     */
    protected void fillSequenceDescription(StringBuilder sb, String sequenceName, Invocable sequence) {
        sb
            .append( sequenceName )
            .append( ":" )
            .append( sequence.getSignature() )
            .append(" - ")
            .append( sequence.getDescription() )
            .append("\n");
    }

    /**
     * Returns complete description of given sequence.
     *
     * @param sequenceName
     * @param sequence
     * @return short descripiton.
     */
    protected String getSequenceDescription(String sequenceName, Invocable sequence) {
        StringBuilder sb = new StringBuilder();
        fillSequenceDescription(sb, sequenceName, sequence);
        return sb.toString();
    }

    /**
     * Returns a string representing row count as comment.
     *
     * @param sb string builder used to generate comment.
     * @param row current row.
     * @param rows number of rows.
     */
    protected void generateRowComment(StringBuilder sb, int row, int rows) {
        int i = (int) Math.log10(rows);
        int j = (int) Math.log10(row);
        sb.append(MUGrammarFactory.BEGIN_COMMENT_BLOCK_STR);
        for( int k = 1; k < i - j; k++) {
            sb.append("0");
        }
        sb.append( Integer.toString(row) );
        sb.append(MUGrammarFactory.END_COMMENT_BLOCK_STR);
    }

    /**
     * Filters the invocables in the given overload using the flags as filter criteria.
     *
     * @param overload overload on which perform filtering.
     * @param flags flags used as filter criteria.
     * @return list of invocables matching given flags.
     */
    protected List<Invocable> filterInvocables(Overload overload, byte flags) {
        List<Invocable> result = new ArrayList<Invocable>();
        byte modifiers;
        for( InvocableObject invocableObject : overload.getInvocableObjects() ) {
            modifiers = invocableObject.getModifiers();
            if( (modifiers & flags) == modifiers) {
                result.add( invocableObject.getInvocable() );
            }
        }
        return result;
    }

    /**
     * Filters invocables in sequence map using given flags as criteria.
     * 
     * @param flags
     * @return map of invocable objects matching given flags.
     */
    protected Map<String,Invocable> filterInvocables(byte flags) {
        Map<String,Invocable> result = new HashMap<String,Invocable>();
        List<Invocable> selectedOverloadInvocables;
        for( Map.Entry<String,Overload> entry : sequenceMap.entrySet() ) {
            selectedOverloadInvocables = filterInvocables(entry.getValue(), flags);
            for(Invocable selectedInvocable : selectedOverloadInvocables) {
                result.put(entry.getKey(), selectedInvocable);
            }
        }
        return result;
    }

    /**
     * Filters the invocable objects in the given overload using the flags as filter criteria.
     *
     * @param overload overload on which perform filtering.
     * @param flags flags used as filter criteria.
     * @return list of invocable objects matching given flags.
     */
    protected List<InvocableObject> filterInvocableObjects(Overload overload, byte flags) {
        List<InvocableObject> result = new ArrayList<InvocableObject>();
        byte modifiers;
        for( InvocableObject invocableObject : overload.getInvocableObjects() ) {
            modifiers = invocableObject.getModifiers();
            if( (modifiers & flags) == modifiers) {
                result.add( invocableObject );
            }
        }
        return result;
    }

    /**
     * Filters {@link com.asemantics.mashup.processor.ExecutionContext.InvocableObject}s
     * in sequence map using given flags as criteria.
     *
     * @param flags
     * @return map of invocable objects matching given flags.
     */
    protected Map<String,InvocableObject> filterInvocableObjects(byte flags) {
        Map<String,InvocableObject> result = new HashMap<String,InvocableObject>();
        List<InvocableObject> selectedOverloadInvocableObjects;
        for( Map.Entry<String,Overload> entry : sequenceMap.entrySet() ) {
            selectedOverloadInvocableObjects = filterInvocableObjects(entry.getValue(), flags);
            for(InvocableObject selectedInvocableObject : selectedOverloadInvocableObjects) {
                result.put(entry.getKey(), selectedInvocableObject);
            }
        }
        return result;
    }

}
