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


package com.asemantics.mashup.parser;

import com.asemantics.lightparser.Location;
import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.processor.*;
import com.asemantics.mashup.processor.unification.ModelElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines a definition entity.
 */
public class Definition implements Processable {

    /**
     * Predicate name.
     */
    private String predicateName;

    /**
     * List of arguments.
     */
    private ModelElement[] arguments;

    /**
     * Varargs element present.
     */
    private boolean varargs;

    /**
     * Invocable object.
     */
    private OperationsSequence operations;

    /**
     * {@link com.asemantics.lightparser.TreeNode}  associated to this Definition.
     */
    private TreeNode treeNode;

    /**
     * Constructor.
     *
     * @param pn predicate name.
     * @param args list of predicate arguments.
     * @param vas varargs operator present.
     * @param os operations sequence.
     * @param tn tree node generating this Definition.
     */
    protected Definition(String pn, ModelElement[] args, boolean vas, OperationsSequence os, TreeNode tn) {
        predicateName = pn;
        arguments     = args;
        varargs       = vas;
        operations    = os;
        treeNode      = tn;
    }

    public void validate() throws ValidationException {
        // Set of defined arguments.
        Set<String> definedVariables = new HashSet<String>();
        for(ModelElement me : arguments) {
            me.validate(definedVariables);
        }
        // Calls validation.
        operations.validate(definedVariables);
    }

    public Value process(Processor processor) {
        Signature signature = varargs ? new VarargsSignature(arguments) : new Signature(arguments); 
        Invocable invocable = new ProgrammativeInvocable(signature , getSequence(), getTreeNode() );
        return processor.addProgrammativePredicate( getPredicateName(), invocable );
    }

    /**
     * Returns the definition tree node.
     *
     * @return a tree node.
     */
    protected TreeNode getTreeNode() {
        return treeNode;
    }

    /**
     * Returns the definition location.
     *
     * @return a location.
     */
    protected Location getLocation() {
        return treeNode.getLocation();
    }

    /**
     * Returns the predicate name.
     *
     * @return name of predicate.
     */
    protected String getPredicateName() {
        return predicateName;
    }

    /**
     * Returns predicate arguments.
     *
     * @return list of arguments.
     */
    protected ModelElement[] getArguments() {
        return arguments;
    }

    protected OperationsSequence getSequence() {
        return operations;
    }

}
