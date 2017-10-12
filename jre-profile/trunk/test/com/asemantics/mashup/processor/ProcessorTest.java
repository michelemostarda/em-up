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

import com.asemantics.lightparser.DefaultTerminal;
import com.asemantics.lightparser.TreeNode;
import junit.framework.TestCase;

/**
 * Test of {@link com.asemantics.mashup.processor.Processor} class.
 */
public class ProcessorTest extends TestCase {


    /**
     * Defines a test operation to verify execution sequences.
     */
    public class PrintOperation extends AbstractOperation {

        /**
         * Constructor.
         *
         * @param args
         */
        public PrintOperation(Argument[] args) {
            super(args);
        }

        public Value execute(ExecutionContext context, ExecutionStack stack)
                throws SequenceNotFoundException, ArgumentEvaluationException, InvocationException {

            Value value;
            StringBuilder result = new StringBuilder();
            StringValue sv;
            for (Argument arg : getArguments()) {
                try {
                    value = arg.getValue(context, stack);
                } catch (VariableNotFoundException vnfe) {
                    throw new ArgumentEvaluationException("Error while evaluating arg.", vnfe);
                }
                sv = value == null ? StringValue.NULL_STRING_VALUE : value.asString();
                context.print(sv);
                result.append(sv.getNativeValue());
            }
            return new StringValue(result.toString());
        }
    }

    /**
     * Test target.
     */
    private Processor target;

    protected void setUp() throws Exception {
        target = new Processor();
    }

    protected void tearDown() throws Exception {
        target = null;
    }

    /**
     * Executes a main cycle with invocation.
     * 
     * @throws ProcessorException
     */
    public void testMainCycle() throws ProcessorException {
        target.addPredicate("_main_", createHelloWorldSequence() );
        Value result = target.processPredicate("_main_", new Argument[]{ new ConstArgument( new StringValue("Hardest") ) });
        assertEquals( "unespected result type.", result.getClass(), StringValue.class);
        assertEquals( "unespected result value.", "Hardest", ((StringValue) result).getNativeValue() );
    }

    /**
     * Tests the processor listener.
     */
    public void testProcessorListener() {
        TestProcessorListener testProcessorListener = new TestProcessorListener();
        target.addListener( testProcessorListener );

        for(int i = 0; i < 5; i++) {
            target.addPredicate(
                    "native_" + i,
                    new ProgrammativeInvocable(
                            new OperationsSequence(),
                            new TreeNode("node1",
                                    new DefaultTerminal("terminal")
                            )
                    )
            ) ;
        }

        for(int i = 0; i < 3; i++) {
            target.addProgrammativePredicate(
                    "programmative_" + i,
                    new ProgrammativeInvocable(
                            new OperationsSequence(),
                            new TreeNode("nod1",
                                    new DefaultTerminal("terminal"))
                    )
            ) ;
        }

        for(int i = 0; i < 5; i++) {
            target.removePredicate("native_" + i);
        }

        for(int i = 0; i < 3; i++) {
            target.removePredicate("programmative_" + i);
        }

        testProcessorListener.checkAssertions();
    }

    private Invocable createHelloWorldSequence() {
        OperationsSequence os = new OperationsSequence();
        os.addOperation( new PrintOperation( new Argument[] { new ConstArgument( new StringValue("Hello") ) } ) );
        os.addOperation( new PrintOperation( new Argument[] { new ConstArgument( new StringValue(" ")   )   } ) );
        os.addOperation( new PrintOperation( new Argument[] { new ConstArgument( new StringValue("World") ) } ) );
        os.addOperation( new PrintOperation( new Argument[] { new ConstArgument( new StringValue(" ") ) } ) );
        os.addOperation( new PrintOperation( new Argument[] { new VariableArgument("name") } ) );
        os.complete();
        Invocable i = new ProgrammativeInvocable( new Signature( new String[]{"name"} ), os, new TreeNode("invocable_tree_node", new DefaultTerminal("terminal")) );
        return i;
    }

    /**
     * Processor listener used for test.
     */
    private class TestProcessorListener implements ProcessorListener {

        private int nativePredicatesCounter = 0;
        private int programmativePredicatesCounter = 0;
        private int deletedPredicatesCounter = 0;

        public void nativePredicateAdded(String name, Invocable invocable) {
            nativePredicatesCounter++;
        }

        public void addedProgrammativePredicate(String name, Invocable invocable) {
            programmativePredicatesCounter++;
        }

        public void predicateRemoved(String name, Invocable invocable) {
            deletedPredicatesCounter++;
        }

        void checkAssertions() {
            assertEquals("Unespected number of native predicates.", nativePredicatesCounter, 5);
            assertEquals("Unespected number of programmative predicates.", programmativePredicatesCounter, 3);
            assertEquals("Unespected number of deleted predicates.", deletedPredicatesCounter, 8);
        }
    }
}
