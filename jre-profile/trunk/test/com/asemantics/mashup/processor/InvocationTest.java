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
import com.asemantics.lightparser.DefaultTerminal;
import com.asemantics.mashup.parser.ValidationException;
import junit.framework.TestCase;

import java.util.Set;

/**
 * Tests classes {@link com.asemantics.mashup.processor.Invocable}
 * and {@link com.asemantics.mashup.processor.OperationsSequence}.
 */
public class InvocationTest extends TestCase {

    /**
     * Auxilary operation.
     */
    static class TestOperation implements Operation {

        public void validate(Set<String> context) throws ValidationException {
            throw new UnsupportedOperationException();
        }

        public Value execute(ExecutionContext context, ExecutionStack stack)
                throws SequenceNotFoundException, ArgumentEvaluationException {

            NumericValue nv = (NumericValue) context.getValue(VARCOUNTER);
            if(nv == null) {
                nv = new NumericValue(0);
                context.addVariable( VARCOUNTER, nv );
            }
            nv.increment();
            return nv;

        }

        public Operation[] getInnerOperations() {
            return new Operation[] {this};
        }
    }

    /**
     * Variable name.
     */
    protected static final String VARCOUNTER = "varcounter";

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     *  Tests invokation creation.
     *
     * @throws ArgumentEvaluationException
     * @throws SequenceNotFoundException
     * @throws InvocationException
     */
    public void testOperationsSequence()
    throws ArgumentEvaluationException, SequenceNotFoundException, InvocationException {

        final int SIZE = 10000;

        ExecutionContext ec = new ExecutionContext();
        ExecutionStack   es = new ExecutionStack();

        ec.addVariable(VARCOUNTER, new NumericValue() );

        OperationsSequence i = createOperationsSequence(SIZE);

        es.pushLevel(ec, i);

        int value = (int) ((NumericValue) ec.getValue(VARCOUNTER)).getNativeValue().doubleValue();
        assertEquals( "Unespected variable value.", SIZE + 1, value );
    }

    protected static OperationsSequence createOperationsSequence(int i) {
        OperationsSequence operationsSequence = new OperationsSequence();
        for(int j = 0; j < i + 1; j++) {
            operationsSequence.addOperation( new TestOperation() );
        }
        operationsSequence.complete();
        return operationsSequence;
    }

    protected static Invocable createSequence(int i) {
        return new ProgrammativeInvocable( createOperationsSequence(i), new TreeNode("sequence_tree_node", new DefaultTerminal("terminal")) );
    }

}
