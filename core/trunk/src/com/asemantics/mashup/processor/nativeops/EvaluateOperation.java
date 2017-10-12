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


package com.asemantics.mashup.processor.nativeops;

import com.asemantics.mashup.interpreter.Interpretative;
import com.asemantics.mashup.interpreter.InterpreterException;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.NativeInvocable;
import com.asemantics.mashup.processor.Operation;
import com.asemantics.mashup.processor.Signature;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;

/**
 * Defines the <i>Evaluate</i> operation, that allows to
 * process a program string inside this interpreter.
 */
public class EvaluateOperation extends NativeInvocable {

    /**
     * Command parameter.
     */
    private final String COMMAND = "command";

    /**
     * Operation signature.
     */
    private final Signature SIGNATURE = new Signature( new String[] {COMMAND} );

    /**
     * Interpretative instance.
     */
    private Interpretative interpretative;

    /**
     * Constructor.
     *
     * @param i interpretative to be used to evaluate commands.
     */
    public EvaluateOperation(Interpretative i) {
        if(i == null) {
            throw new IllegalArgumentException();
        }
        interpretative = i;
    }

    public Signature getSignature() {
        return SIGNATURE;
    }

    public String getShortDescription() {
        return "Executes the given command.";
    }

    public String getDescription() {
        return getShortDescription();
    }

    public Value execute(ExecutionContext context, ExecutionStack stack)
    throws InvocationException {
        String command = context.getIthValueAsString(0).getNativeValue();
        try {
            return interpretative.process(command);
        } catch (InterpreterException ie) {
            return new StringValue( ie.getMessage() );
        }
    }

    public Operation[] getInnerOperations() {
        return new Operation[] {this};
    }

}