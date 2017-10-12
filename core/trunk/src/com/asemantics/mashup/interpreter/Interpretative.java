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

package com.asemantics.mashup.interpreter;

import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.InvokeOperation;
import com.asemantics.mashup.processor.ProcessorException;

/**
 * Defines any class able to interpretate a program string.
 *
 * @see com.asemantics.mashup.interpreter.Interpreter
 */
public interface Interpretative {

    /**
     * Activates / deactivates validation of processed strings.
     *
     * @param v
     */
    void setValidating(boolean v);

    /**
     * Processes a program string.
     *
     * @param program program to be processed.
     * @return value resulted in processing the program string.
     * @throws InterpreterException in an error occurs during interpretation
     *         or execution of <i>program</i>.
     */
    Value process(String program) throws InterpreterException;

    /**
     * Processes the given invocation operation in this processor.
     *
     * @param invoke the operation to be invoked.
     * @return last computed value.
     * @throws ProcessorException if any exception occurs;
     */
    Value processOperation(InvokeOperation invoke) throws ProcessorException; 
}
