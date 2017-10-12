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

import java.util.Stack;

/**
 * Defines the execution stack of a {@link com.asemantics.mashup.processor.Processor}.
 *
 * @see com.asemantics.mashup.processor.ExecutionContext
 */
public class ExecutionStack {

    /**
     * Defines a base stack level.
     */
    abstract class Level {

        /**
         * Index of current operation in invocation sequence.
         */
        int index;

        /**
         * Context in which invocation must be processed.
         */
        protected ExecutionContext context;

        /**
         * Constructor.
         *
         * @param ec
         */
        Level(ExecutionContext ec) {
            if( ec == null ) {
                throw new NullPointerException();
            }
            context = ec;
        }

        void increaseIndex() {
            index++;
        }

        /**
         * Prints the level description.
         *
         * @param sb string builder to write on description.
         */
        abstract void printDescription(StringBuilder sb);

        /**
         * Processes current level in context.
         *
         * @return  process result.
         * @throws InvocationException
         * @throws SequenceNotFoundException
         * @throws ArgumentEvaluationException
         */
        abstract Value process()
        throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException;
    }

    /**
     * Defines the main execution level.
     *
     * @see com.asemantics.mashup.parser.Evaluation
     */
    protected class MainLevel extends Level {

        /**
         * Sequence defining main level.
         */
        private OperationsSequence mainSequence;

        MainLevel(ExecutionContext ec, OperationsSequence os) {
            super(ec);
            if( os == null ) {
                throw new NullPointerException();
            }
            mainSequence = os;
        }

        public void printDescription(StringBuilder sb) {
            sb.append("<main:").append(index).append(">");
        }

        public Value process() throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
            return mainSequence.execute(context, ExecutionStack.this);
        }
    }

    /**
     * Defines a level of indirection in execution stack.
     */
    protected class StackLevel extends Level {

        /**
         * Invocation in current level.
         */
        protected InvokeOperation invocation;

        /**
         * Sequence associated to the operation.
         */
        protected Invocable invocable;

        /**
         * Constructor.
         *
         * @param ec
         * @param io
         * @param i
         */
        protected StackLevel(ExecutionContext ec, InvokeOperation io, Invocable i, int n) {
            super(ec);
            if(io == null) {
                throw new NullPointerException();
            }
            context    = ec;
            invocation = io;
            invocable  = i;
            index      = n;
        }

        /**
         * Prints the level description.
         *
         * @param sb string builder to write on description.
         */
         void printDescription(StringBuilder sb) {
            sb.append("<sequence:").append( invocation.getTargetSequence() ).append(":").append( index ).append(">");
        }

        /**
         * Processes current level in context.
         *
         * @return process result.
         * @throws InvocationException
         * @throws SequenceNotFoundException
         * @throws ArgumentEvaluationException
         */
        Value process()
        throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
            if( invocable == null) {
                return invocation.execute(context, ExecutionStack.this);
            } else {
                return invocable.execute( context, ExecutionStack.this);
            }
        }

    }

    /**
     * Sequence break flag.
     */
    private boolean breaked;
        
    /**
     * Mantains the last execution value.
     */
    private Value lastExecutionValue = NullValue.getInstance();

    /**
     * Stack of invocations.
     */
    private Stack<Level> stack;

    /**
     * constructor.
     */
    protected ExecutionStack() {
        stack = new Stack<Level>();
    }

    /**
     * Pushes a level to be executed into execution stack.
     *
     * @param level level under execution.
     * @return processed value.
     * @throws InvocationException
     * @throws ArgumentEvaluationException
     * @throws SequenceNotFoundException
     */
    protected Value pushLevel(Level level)
    throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
        stack.push(level);
        try {
            return level.process();
        } finally {
            stack.pop();
        }
    }

    /**
     * Pushes stack invocation level with index <i>i</i>.
     *
     * @param ec execution context.
     * @param invocable invocable operation.
     * @param invoke invoke operation
     * @param i invocable operation.
     * @throws InvocationException
     * @throws SequenceNotFoundException
     * @throws ArgumentEvaluationException
     * @return process result.
     */
    protected Value pushLevel(ExecutionContext ec, InvokeOperation invoke, Invocable invocable, int i)
    throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
        Level level = new StackLevel(ec, invoke, invocable, i);
        return pushLevel(level);
    }

    /**
     * Pushes stack invocation level with index <code>0</code>.
     *
     * @param ec
     * @param invoke
     * @param invocable
     * @throws InvocationException
     * @throws SequenceNotFoundException
     * @throws ArgumentEvaluationException
     * @return process result.
     */
    protected Value pushLevel(ExecutionContext ec, InvokeOperation invoke, Invocable invocable)
    throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
        return pushLevel(ec, invoke, invocable, 0);
    }

    /**
     * Pushes stack invocation level with index <code>0</code>.
     *
     * @param ec execution context used to process level.
     * @param invoke invoked operation.
     * @throws InvocationException
     * @throws SequenceNotFoundException
     * @throws VariableNotFoundException
     * @return process result.
     */
    protected Value pushLevel(ExecutionContext ec, InvokeOperation invoke)
    throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
        return pushLevel(ec, invoke, null, 0);
    }

     /**
     * Pushes main invocation level.
     *
     * @param ec  execution context.
     * @param mainSequence sequence to be executed.
     * @throws InvocationException
     * @throws SequenceNotFoundException
     * @throws ArgumentEvaluationException
     * @return process result.
     */
    protected Value pushLevel(ExecutionContext ec, OperationsSequence mainSequence)
    throws InvocationException, ArgumentEvaluationException, SequenceNotFoundException {
        return pushLevel( new MainLevel(ec, mainSequence) );
    }

    /**
     * This method is notified by {@link com.asemantics.mashup.processor.OperationsSequence}
     * that a sequence computation has been started.
     *
     * @param sequence notifying its own begin.
     * @see com.asemantics.mashup.processor.OperationsSequence
     */
    protected void beginSequence(OperationsSequence sequence) {
        breaked = false;
    }

    /**
     * This method is notified by {@link com.asemantics.mashup.processor.OperationsSequence}
     * that a sequence computation ended.
     * 
     * @param sequence motifying its own end.
     * @see com.asemantics.mashup.processor.OperationsSequence
     */
    protected void endSequence(OperationsSequence sequence) {
        // Empty.
    }

    /**
     * Returns <code>true</code> if sequence has been breaked, <code>false</code> otherwise.
     *
     * @return break status.
     */
    protected boolean isBreak() {
        return breaked;
    }

    /**
     * Breaks a sequence execution.
     */
    protected void breakSequence() {
        breaked = true;
    }

    /**
     * Increments the peek sequence operation index.
     *
     * @param lev
     */
    protected void nextOperation(Value lev) {
        assert lev != null : "Unespected null value for lev";
        lastExecutionValue = lev;
        stack.peek().increaseIndex();
    }

    /**
     * Returns the last execution value.
     *
     * @return last execution value if any, {@link com.asemantics.mashup.processor.NullValue} otherwise.
     */
    public Value getLastExecutionValue() {
        return lastExecutionValue;
    }

    /**
     * Notifies the execution stack that the executed operation
     * raised an exception. This implementation converts
     * the raised exception to a string value containing
     * the exception message.
     *
     * @param ie the exception throwns by the operation.
     */
    public void raisedException(InvocationException ie) {
        nextOperation( new StringValue( ie.getMessage() ) );
    }

    /**
     * Prints the stack trace in given string builder.
     *
     * @param sb
     */
    public void printStackTrace(StringBuilder sb) {
        for( int i  = stack.size() - 1; i >= 0; i-- ) {
            stack.get(i).printDescription(sb);
            sb.append("\n");
        }
    }

    /**
     * Returns the stack trace.
     *
     * @return stack trace description.
     */
    public String printStackTrace() {
        StringBuilder sb = new StringBuilder();
        printStackTrace(sb);
        return sb.toString();
    }

}
