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

import com.asemantics.mashup.processor.NumericValue;
import com.asemantics.mashup.processor.Value;

/**
 * Defines the <i>Mult (Multiply)</i> expression.
 */
public class MultExpression extends Expression {

    protected NumericValue valuateExpression(Value op1, Value op2) {
        return new NumericValue( op1.asNumeric().getNativeValue() * op2.asNumeric().getNativeValue() );
    }

    public String getShortDescription() {
        return "Returns arg1 x arg2";
    }

    public String getDescription() {
        return getShortDescription();
    }
}