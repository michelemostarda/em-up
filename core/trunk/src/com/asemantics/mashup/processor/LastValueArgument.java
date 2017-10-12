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
 * This argument provides the result returned by last operation executed
 * by processor.
 *
 * @see com.asemantics.mashup.processor.Processor
 * @see com.asemantics.mashup.processor.Operation
 * @see ExecutionStack#getLastExecutionValue() 
 */
public class LastValueArgument implements Argument {

    private static final LastValueArgument INSTANCE = new LastValueArgument();

    /**
     * Returns the singleton instance.
     *
     * @return singleton instance.
     */
    public static LastValueArgument getInstance() {
        return INSTANCE;
    }

    /**
     * private constructor. Singleton.
     */
    private LastValueArgument() {
        // Empty.
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es)
    throws VariableNotFoundException {
        return es.getLastExecutionValue();
    }

}
