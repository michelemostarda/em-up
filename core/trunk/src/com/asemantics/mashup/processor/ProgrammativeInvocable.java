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

import com.asemantics.lightparser.TreeNode;

/**
 * Defines any invocable defined by programmatic way.
 */
public class ProgrammativeInvocable extends Invocable {

    /**
     * Invocable signature.
     */
    private Signature signature;

    /**
     * Internal invocable operation.
     */
    private Operation operation;

    /**
     * Preserves the tree node extrated during parsing of this programmative.
     */
    private TreeNode treeNode;

    /**
     * Constructor.
     *
     * @param s signature.
     * @param o operation body.
     * @param tn tree node which parsing generated this programmative.
     * @throws IllegalArgumentException if parameters are <i>null</i>.
     */
    public ProgrammativeInvocable(Signature s, Operation o, TreeNode tn) {
        if(s == null) {
            throw new IllegalArgumentException();
        }
        if(o == null) {
            throw new IllegalArgumentException();
        }
        signature = s;
        operation = o;
        treeNode  = tn;
    }

    /**
     * Constructor with default signature (no arguments).
     *
     * @param o operation body.
     * @param tn tree node which parsing generated this programmative.
     */
    public ProgrammativeInvocable(Operation o, TreeNode tn) {
        this( new Signature(), o, tn );
    }

    /**
     * Returns the invocable signature.
     *
     * @return signature of this invocable.
     */
    public Signature getSignature() {
        return signature;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public String getShortDescription() {
        return "programmative invocable";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {
        return operation.execute(context, stack);
    }

    public Operation[] getInnerOperations() {
        return operation.getInnerOperations();
    }


}
