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

/**
 * Defines an invocation of another sequence.
 *
 * @see com.asemantics.mashup.processor.Invocable
 */
public class InvokeOperation extends AbstractOperation {

    /**
     * Name of target sequence to invoke.
     */
    private String targetSequence;

    /**
     * Name of variable to return back.
     */
    private String result;

    /**
     * Constructor.
     *
     * @param targetSequence
     * @param args
     * @param result
     */
    public InvokeOperation(String targetSequence, Argument[] args, String result) {
        super(args);
        if(targetSequence == null) {
            throw new IllegalArgumentException("targetSequence cannot be null.");
        }
        this.targetSequence = targetSequence;
        this.result         = result;
    }

    /**
     * Constructor.
     *
     * @param targetSequence
     * @param args
     */
    public InvokeOperation(String targetSequence, Argument[] args) {
        this(targetSequence, args, null);
    }

    /**
     *
     * @return returns the target sequence name.
     */
    public String getTargetSequence() {
        return targetSequence;
    }

    /**
     * Invokes the referenced sequence.
     *
     * @param context
     * @param stack
     * @return result of execution.
     * @throws SequenceNotFoundException
     * @throws VariableNotFoundException
     * @throws InvocationException
     */

    public Value execute(ExecutionContext context, ExecutionStack stack)
            throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

        // Retrieves candidate invocables.
        Invocable[] invocables = context.getSequences( targetSequence );
        if(invocables.length == 0) {
            throw new SequenceNotFoundException("Cannot find sequence '" + targetSequence + "' in context.");
        }

        // Evaluate arguments just once.
        Argument[] args = getArguments();
        Value[] values = new Value[args.length];
        try {
            for(int i = 0; i < args.length; i++) {
                values[i] = args[i].getValue(context, stack);
            }
        } catch (VariableNotFoundException vnfe) {
            throw new ArgumentEvaluationException("Cannot evaluate arguments.", vnfe);
        }

        // Find target invocable.
        InvocableTarget invocableTarget = findCompatibleInvocable(invocables, values);

        // Creates the invocation context.
        ExecutionContext newContext = context.createContext(invocableTarget.signatureContextMap);

        // Invokes invocable.
        Value resultValue = stack.pushLevel(newContext, this, invocableTarget.invocable);

        // Sets result value.
        if( result != null ) {
            context.addVariable(result, resultValue);
        }
        return resultValue;
    }

    /**
     * Returns a compatible invocable for the given arguments.
     *
     * @param invocables list of candidate invocables.
     * @param values list of values used to match the invocable.
     * @return candidate invocable.
     * @throws InvocationException
     */
    InvocableTarget findCompatibleInvocable(
            Invocable[] invocables,
            Value[] values
    ) throws SequenceNotFoundException {
        SequenceNotFoundException root = null;
        SignatureContextMap scm;
        for(Invocable invocable : invocables) {
            try {
                 scm = invocable.getSignature().unify(values);
                return new InvocableTarget(invocable, scm);
            } catch(SequenceNotFoundException snfe) {
                if( root == null ) {
                    root = new SequenceNotFoundException("Cannot find an invocable object compatible with given arguments.");
                    root.setSequenceName(targetSequence);
                }
                root.addCause(snfe);
            }

        }
        throw root;
    }

    /**
     * Container class for #findCompatibleInvocable() result.
     */
    private class InvocableTarget {

        private SignatureContextMap signatureContextMap;
        private Invocable invocable;

        InvocableTarget( Invocable i, SignatureContextMap scm ) {
            invocable           = i;
            signatureContextMap = scm;
        }
    }

}
