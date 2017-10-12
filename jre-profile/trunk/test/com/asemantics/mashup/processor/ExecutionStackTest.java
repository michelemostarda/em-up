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

import junit.framework.TestCase;

/**
 * Tests the {@link com.asemantics.mashup.processor.ExecutionStack} class.
 */
public class ExecutionStackTest extends TestCase {

    /**
     * Test target.
     */
    private ExecutionStack stack;

    /**
     * Auxiliary context.
     */
    private ExecutionContext context;

    protected void setUp() throws Exception {
        stack   = new ExecutionStack();
        context = new ExecutionContext();
    }

    protected void tearDown() throws Exception {
        stack   = null;
        context = null;
    }

    /**
     * Simulates normal execution operations.
     * @throws InvocationException
     * @throws SequenceNotFoundException
     * @throws VariableNotFoundException
     */
    public void testExecutionSession()
   throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {

        final int SIZE = 200;

        // Pushes stack.
        for( int i = 0; i < SIZE; i++ ) {
            stack.pushLevel(context, InvocationTest.createOperationsSequence(i) );
        }

        //
        int invocationsCounter = context.getValue(InvocationTest.VARCOUNTER).asNumeric().integer();
        assertEquals("Unespected number of invocations.", computeResult(SIZE), invocationsCounter);
    }

    /**
     * Computes test result.
     *
     * @param i
     * @return test result.
     */
    private int computeResult(int i) {
        if(i == 0) {
            return 0;
        }
        return i + computeResult(i - 1);
    }
}
