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
 * This argumnet provides support for nested sequence invocations.
 *
 * @see com.asemantics.mashup.processor.EnqueueArgumentOperation
 */
public class DequeueArgument implements Argument {

    /**
     * Singleton instance.
     */
    private static final DequeueArgument INSTANCE = new DequeueArgument();

    /**
     * Returns singleton instance.
     *
     * @return instance.
     */
    public static DequeueArgument getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton constructor.
     */
    private DequeueArgument() {
        // Empty.
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es) {
        return ec.dequeueArgument();
    }

}
