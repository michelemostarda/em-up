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
import static com.asemantics.mashup.processor.ExecutionContext.*;
import com.asemantics.mashup.processor.nativeops.Print;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Tests the {@link com.asemantics.mashup.processor.ExecutionContext} class.
 */
public class ExecutionContextTest extends TestCase {

    /**
     * Simple value implementation for test puroposes.
     */
    private class TestValue extends Value {

        public TestValue(int i) {
            // Empty.
        }

        public StringValue getValueTypeName() {
            return null;
        }

        public StringValue asString() {
            return null;
        }

        public NumericValue asNumeric() {
            throw new UnsupportedOperationException();
        }

        public BooleanValue asBoolean() {
            throw new UnsupportedOperationException();
        }

        public ListValue asList() {
            throw new UnsupportedOperationException();
        }

        public MapValue asMap() {
            throw new UnsupportedOperationException();
        }

        public GraphValue asGraph() {
            throw new UnsupportedOperationException();
        }

        public JsonValue asJsonValue() {
            throw new UnsupportedOperationException();
        }

        public BooleanValue equalsTo(Value v) {
            throw new UnsupportedOperationException();
        }

        public NumericValue comparesTo(Value v) {
            throw new UnsupportedOperationException();
        }

        public Value cloneValue() {
            throw new UnsupportedOperationException();
        }

        public Object getNativeValue() {
            throw new UnsupportedOperationException();
        }

        public String getJsonType() {
            throw new UnsupportedOperationException();
        }

        public String asJSON() {
            throw new UnsupportedOperationException();
        }

        public String asPrettyJSON() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Target of test.
     */
    private ExecutionContext executionContext;

    private ExecutionStack executionStack;

    public void setUp() {
        executionContext = new ExecutionContext();
        executionStack   = new ExecutionStack();
    }

    public void tearDown() {
        executionContext = null;
        executionStack   = null;
    }

    /**
     * Loads sequences on execution context and verifies their consistency.
     */
    public void testSequencesHandling() throws InvocationException {

        final int SIZE = 100;

        // Adds sequences.
        for( int i = 0; i < SIZE; i++ ) {
            executionContext.addSequence(sequenceName(i), InvocationTest.createSequence(i), DELETABLE );
        }

        // Verifies sequences.
        String msg1 = "Expected sequence not found.";
        for(int i = 0; i < SIZE; i++) {
            assertTrue  ( msg1,    executionContext.containsSequence( sequenceName(i) )    );
            assertEquals( msg1, 1, executionContext.getSequences( sequenceName(i) ).length );
        }

        // Removes all.
         for(int i = 0; i < SIZE; i++) {
            executionContext.removeSequence( sequenceName(i) );
        }

        // Verifies removation.
        String msg2 = "Unexpected sequence found.";
        for(int i = 0; i < SIZE; i++) {
            assertFalse ( msg2, executionContext.containsSequence( sequenceName(i) ) );
            assertEquals( msg2, 0, executionContext.getSequences( sequenceName(i) ).length );
        }
    }

    /**
     * Adds variables to execution context and verifies their consistency.
     */
    public void testVariablesHandling() {

        final int SIZE = 100;

        // Adds sequences.
        for( int i = 0; i < SIZE; i++ ) {
            executionContext.addVariable(variableName(i), createValue(i) );
        }

        // Verifies sequences.
        String msg1 = "Expected value not found.";
        for(int i = 0; i < SIZE; i++) {
            assertTrue   (msg1, executionContext.containsVariable( variableName(i) ) );
            assertNotNull(msg1, executionContext.getValue( variableName(i) ) );
        }

        // Removes all.
         for(int i = 0; i < SIZE; i++) {
            executionContext.removeVariable( variableName(i) );
        }

        // Verifies removation.
        String msg2 = "Unexpected variable found.";
        for(int i = 0; i < SIZE; i++) {
            assertFalse( msg2, executionContext.containsVariable( variableName(i) ) );
            assertNull(  msg2, executionContext.getValue( variableName(i) ) );
        }
    }

    /**
     * Tests that flags work properly.
     */
    public void testFlags() {

        // Deletable.
        executionContext.addSequence("deletableSequence", InvocationTest.createSequence(1), DELETABLE);
        executionContext.removeSequence("deletableSequence");

        // Not deletable.
        executionContext.addSequence("nondeletableSequence", InvocationTest.createSequence(1), ExecutionContext.NONE);
        try {
            executionContext.removeSequence("nondeletableSequence", new Signature() );
            fail("Expected exc.");
        } catch (ExecutionContextException ece) {
            // OK.
        }

        // Overridable.
        executionContext.addSequence("overridableSequence", InvocationTest.createSequence(3), OVERRIDABLE);
        executionContext.addSequence("overridableSequence", InvocationTest.createSequence(4));

        // Not overridable.
        executionContext.addSequence("notOverridableSequence", InvocationTest.createSequence(5), NONE );
        try {
            executionContext.addSequence("notOverridableSequence", InvocationTest.createSequence(6) );
            fail("Expected exc.");
        } catch (ExecutionContextException ece) {
            // OK.
        }
    }

    /**
     * Test flag filter methods.
     */
    public void testFlagFilter() {
        executionContext.addSequence( "nativeSequence"     , InvocationTest.createSequence(1), NATIVE      );
        executionContext.addSequence( "deletableSequence"  , InvocationTest.createSequence(2), DELETABLE   );
        executionContext.addSequence( "overridableSequence", InvocationTest.createSequence(3), OVERRIDABLE );

        Map<String,Invocable> result1 = executionContext.getNativeInvocables();
        assertEquals( "Unespected result size.", 1, result1.size() );
        assertTrue("Unespected result content.", result1.get("nativeSequence") != null);

        Map<String,Invocable> result2 = executionContext.getDeletableInvocables();
        assertEquals( "Unespected result size.", 1, result2.size() );
        assertTrue("Unespected result content.", result2.get("deletableSequence") != null);

        Map<String,Invocable> result3 = executionContext.getOverridableInvocables();
        assertEquals( "Unespected result size.", 1, result3.size() );
        assertTrue("Unespected result content.", result3.get("overridableSequence") != null);
    }
    
    /**
     * Tests ExecutionContext#printProgrammativeContext()
     */
    public void testPrintProgrammativeContext() {
        final int SIZE = 10;

        for(int i = 0; i < SIZE; i++) {
            executionContext.addSequence(
                    "Sequence" + i,
                    new ProgrammativeInvocable( new Signature(), new Print(), new TreeNode("tree node " + i, new DefaultTerminal("terminal"))),
                    OVERRIDABLE
            );
        }
        String result = executionContext.printProgrammativeContext();
        System.out.println("Result:\n" + result );

        assertEquals(
                "Unespected programmative context.",
                "#0#  tree node 0\n" +
                "#1#  tree node 1\n" +
                "#2#  tree node 2\n" +
                "#3#  tree node 3\n" +
                "#4#  tree node 4\n" +
                "#5#  tree node 5\n" +
                "#6#  tree node 6\n" +
                "#7#  tree node 7\n" +
                "#8#  tree node 8\n" +
                "#9#  tree node 9\n",
                result
        );
    }

    private String sequenceName(int i) {
        return "sequence_" + i;
    }

    private String variableName(int i) {
        return "variable_" + i;
    }

    private Value createValue(int i) {
        return new TestValue(i);
    }

}
