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


package com.asemantics.mashup.console;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.asemantics.mashup.interpreter.Interpreter;
import com.asemantics.mashup.interpreter.InterpreterException;
import com.asemantics.mashup.processor.ExecutionContext;

/**
 * Test class for the {@link com.asemantics.mashup.console.CommandLine} class.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MUCommandLineTest.java 425 2009-05-27 22:45:12Z michelemostarda $
 */
public class MUCommandLineTest extends TestCase {

    /**
     * Traces the characted sent to an output stream
     * and allows to perform assetions on it.
     */
    class TracePrintStream extends PrintStream {

        private StringBuilder buffer = new StringBuilder();

        public TracePrintStream(OutputStream outputStream) {
            super(outputStream);
        }

        void clear() {
            if(buffer.length() == 0) {
                return;
            }
            buffer.delete(0, buffer.length() - 1);
        }

        String dump() {
            String result =  buffer.toString();
            clear();
            return result;
        }

        void assertContains(String ... content) {
            for(String c : content) {
                assertTrue("Cannot find content '" + c + "' in buffer.", buffer.indexOf(c) != -1 );
            }
        }

        void assertNotContains(String ... content) {
            for(String c : content) {
                assertFalse("Found unexpected content '" + c + "' in buffer.", buffer.indexOf(c) != -1 );
            }
        }

        void assertEmpty() {
            assertTrue("Espected empty buffer.", buffer.length() == 0);
        }

        void assertNotEmpty() {
            assertTrue("Espected empty buffer.", buffer.length() > 0);
        }

        int countLines() {
            int i = 0, count = 0;
            while( i < buffer.length() ) {
                i = buffer.indexOf("\n", i) + 1;
                count++;
            }
            return count;
        }

        @Override
        public void print(String s) {
            super.print(s);
            buffer.append(s);
        }

        @Override
        public void print(char c) {
            super.print(c);
            buffer.append(c);
        }

        @Override
        public void println(String s) {
            super.println(s);
            buffer.append(s);
        }
    }

    private static final int NUMBER_OF_VARS = 10;

    private static final int NUMBER_OF_RESULTS = 10;

    static final int FAKE_PREDICATES_COUNT = 10;

    static final String FAKE_PREDICATE_PREFIX = "Predicate";

    static final String FAKE_PREDICATE_CONTENT = "This is the predicate ";

    private CommandLine commandLine;

    private TracePrintStream traceOut;
    private TracePrintStream traceErr;

    public void setUp()
    throws IllegalAccessException, IOException, InvocationTargetException {
        traceOut = new TracePrintStream( System.out );
        traceErr = new TracePrintStream( System.err );
        commandLine = MUCommandLine.createCommandLine(traceOut, traceErr );
    }

    public void tearDown() {
        commandLine = null;
    }

    /**
     * Tests the management of unknown commands.
     */
    public void testUnknownCommand() {
        final String wrongCommand = ":bla";
        commandLine.processCommand(wrongCommand);
        traceErr.assertNotEmpty();
        traceErr.assertContains("unknown", wrongCommand);
    }

    /**
     * Tests the <i>help</i> command.
     */
    public void testHelpCommand() {
        commandLine.processCommand(":help");
        traceOut.assertContains( "Usage" );
        traceOut.assertContains( commandLine.getCommandModel().getCommandNames() );
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>help</i> command with argument.
     */
    public void testHelpCommandWithArg() {
        commandLine.processCommand(":help help");
        traceOut.assertContains( "help" );
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>ls</i> command.
     */
    public void testLsCommand() {
        commandLine.processCommand(":ls");

        int filesCount = retrieveMUCommands().getCurrentLocation().listFiles().length;
        assertEquals( "Unespected number of lines.", filesCount * 2, traceOut.countLines() );
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>cd</i> command.
     */
    public void testCdCommand() throws IOException {
        commandLine.processCommand(":pwd");
        traceErr.assertEmpty();
        File currLocation  = retrieveMUCommands().getCurrentLocation();
        traceOut.assertContains( currLocation.getCanonicalPath() );
        traceOut.clear();

        commandLine.processCommand(":cd ..");

        commandLine.processCommand(":pwd");
        traceErr.assertEmpty();
        traceOut.assertContains( currLocation.getCanonicalFile().getParent() );

        commandLine.processCommand(":cd ..");

        commandLine.processCommand(":pwd");
        traceErr.assertEmpty();
        traceOut.assertContains( currLocation.getCanonicalFile().getParentFile().getParent() );
    }

    /**
     * Tests the <i>exit</i> command.
     */
    public void testExit() {
        commandLine.processCommand(":exit");
        assertTrue( "Expected exit.", commandLine.isExit() );
    }

    /**
     * Tests the <i>results</i> command.
     */
    public void testResults() {
        DefaultCommands defaultCommands = retrieveMUCommands();
        addResults(defaultCommands);
        commandLine.processCommand(":results");
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>result</i> command.
     */
    public void testResult() {
        DefaultCommands defaultCommands = retrieveMUCommands();
        addResults(defaultCommands);
        commandLine.processCommand(":result 0");
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>resultclear</i> command.
     */
    public void testResultClear() {
        commandLine.processCommand(":resultsclear");
        traceErr.assertEmpty();
    }

    /**
     * Tests the <i>echo</i> command.
     */
    public void testEcho() {
        commandLine.processCommand(":echo    1     2     3");
        traceOut.assertContains("1 2 3");
    }

    public void testExpansionFailure() {
        try {
            commandLine.processCommand(":echo ${X}");
            fail("Expected exception here.");
        } catch (Exception e) {
             // OK.
        }

    }

    /**
     * Tests the <i>echo</i> command with expansion.
     */
    public void testEchoWithExpansion() {
        DefaultCommands defaultCommands = retrieveMUCommands();
        addResults(defaultCommands);
        commandLine.processCommand(":echo ${1} ${2} ${3}");
        traceOut.assertContains("Result1 Result2 Result3");
    }

    /**
     * Tests the expansion feature.
     */
    public void testExpandResults() {
        DefaultCommands defaultCommands = retrieveMUCommands();
        addResults(defaultCommands);
        commandLine.processCommand( "Print('${1} - ${2} - ${3}');" );
        traceOut.assertContains("Result1 - Result2 - Result3");
    }

    /**
     * Tests the set and uset commands.
     */
    public void testSetUnset() {
        DefaultCommands defaultCommands = retrieveMUCommands();

        commandLine.processCommand( ":set" );
        traceOut.assertContains("no variables");
        traceOut.clear();

        addVariables(defaultCommands);
        commandLine.processCommand( ":set" );
        for(int i = 0; i < NUMBER_OF_VARS; i++) {
            traceOut.assertContains("var" + i);
        }

        commandLine.processCommand( ":unset var9" );
        traceOut.clear();
        commandLine.processCommand( ":set" );
        for(int i = 0; i < NUMBER_OF_VARS - 1; i++) {
            traceOut.assertContains("var" + i);
        }
        traceOut.assertNotContains("var9");
    }

    /**
     * Tests the usage of composite variable names and values.
     */
    public void testSetCompositeVariable() {
        final String varName  = "the composite name";
        final String varValue = "and the composite value...";
        commandLine.processCommand( String.format(":set \"%s\" \"%s\"", varName, varValue) );
        assertEquals("Unespected variable value.", varValue, retrieveMUCommands().getValueOf(varName) );
    }

    /**
     * Tests the store and load result.
     */
    public void testStoringAndLoadingResults() {
        final DefaultCommands defaultCommands = retrieveMUCommands();
        addResults(defaultCommands);

        final String RESULT_NAME_PREFIX = "result_store_";

        // Going to temporary dir.
        final String tmpDir = System.getProperty("java.io.tmpdir");
        defaultCommands.setCurrentLocation( new File(tmpDir) );

        // Storing first.
        for(int i = 0; i < NUMBER_OF_RESULTS; i++) {
            commandLine.processCommand( String.format(":storeresult %d %s%d", i, RESULT_NAME_PREFIX, i) );
        }
        for(int i = 0; i < NUMBER_OF_RESULTS; i++) {
            assertTrue("Espected stored result.",
                    new File(
                            defaultCommands.getCurrentLocation(),
                            RESULT_NAME_PREFIX + i + DefaultCommands.RESULT_EXT).exists()
            );
        }

        // Loading results.
        for(int i = 0; i < NUMBER_OF_RESULTS; i++) {
            commandLine.processCommand( String.format(":loadresult %s%d", RESULT_NAME_PREFIX, i) );
        }

        // Checking content.
        final List<Result> results = defaultCommands.getResults();
        assertEquals("Unespected number of results.", NUMBER_OF_RESULTS * 2, results.size() );
        for(int i = 0; i < NUMBER_OF_RESULTS; i++) {
            assertEquals("Unpespected result value.",
                    results.get(i).getContent(),
                    results.get(i + NUMBER_OF_RESULTS).getContent()
            );
        }
    }

    /**
     * Tests the <i>storepcontext</i> and <i>loadpcontext</i> command.
     */
    public void testStoreAndLoadProgrammativeContext() {
        final MUCommands muCommands = retrieveMUCommands();

        // Going to temporary dir.
        final String tmp = System.getProperty("java.io.tmpdir");
        final File tmpDir = new File(tmp);
        muCommands.setCurrentLocation( tmpDir );

        // Loading fake Programmative Context.
        createFakePContext( muCommands.getInterpreter() );

        final String pcontext = "pcontext";

        // Removing old files.
        final File ctxFile = new File(tmpDir, pcontext + MUCommands.CONTEXT_EXT);
        if(ctxFile.exists()) {
            ctxFile.delete();
        }

        // Storing context.
        commandLine.processCommand(":storecontext " + pcontext);

        // Checking result.
        assertTrue("Cannot find stored file.", ctxFile.exists());

        // Removing predicates.
        removeFakePContext( muCommands.getInterpreter() );

        // Loading context.
        commandLine.processCommand(":loadcontext " + pcontext);

        // Checking context.
        for(int i = 0; i < FAKE_PREDICATES_COUNT; i++) {
            commandLine.processCommand(FAKE_PREDICATE_PREFIX + i  + "();");
            traceOut.assertContains( FAKE_PREDICATE_CONTENT + i);
            traceOut.clear();
        }
    }

    /**
     * Tests the <i>shell</i> command.
     */
    public void testShell() {
        commandLine.processCommand(":shell ls");
    }

    /**
     * Adds syntetic results to the command buffer.
     *
     * @param defaultCommands
     */
    private void addResults(DefaultCommands defaultCommands) {
        for(int i = 0; i < NUMBER_OF_RESULTS; i++) {
            defaultCommands.addResult( new InMemoryResult<String>("Command " + i, "Result" + i) );
        }
    }

    /**
     * Adds test variables to the command handler.
     * 
     * @param defaultCommands
     */
    private void addVariables(DefaultCommands defaultCommands) {
        for(int i = 0; i < NUMBER_OF_VARS; i++) {
            defaultCommands.setVariable("var" + i, "value" + i);
        }
    }

    /**
     * Creates a fake programmative context.
     * @param interpreter
     */
    private void createFakePContext(Interpreter interpreter) {
        for(int i = 0; i < FAKE_PREDICATES_COUNT; i++) {
            try {
                interpreter.process(
                        String.format("%s%d() : Print('%s%d');", FAKE_PREDICATE_PREFIX, i , FAKE_PREDICATE_CONTENT, i)
                );
            } catch (InterpreterException ie) {
                throw new RuntimeException("Error while creating fake programmative context.", ie);
            }
        }
    }

    private void removeFakePContext(Interpreter interpreter) {
        ExecutionContext ec =  interpreter.getProcessor().getExecutionContext();
        for(int i = 0; i < FAKE_PREDICATES_COUNT; i++) {
            ec.removeSequence("Predicate" + i);
        }
    }

    /**
     * Retrieves the default commands instance.
     * 
     * @return
     */
    private MUCommands retrieveMUCommands() {
        CommandDispatcherModel cmd = (CommandDispatcherModel) commandLine.getCommandModel();
        IntrospectionCommandModel icm = (IntrospectionCommandModel) cmd.getCommandHandlers().get(0).getModel();
        return (MUCommands) icm.getInstance();
    }

}
