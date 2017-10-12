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

import com.asemantics.mashup.processor.BooleanValue;
import com.asemantics.mashup.processor.Value;

/**
 * Defines the condition <i>IsInfinite</i>.
 */
public class IsInfiniteMonoCondition extends MonoCondition {

    protected BooleanValue valuateCondition(Value v) {
        return new BooleanValue( v.asNumeric().isInfinite() );
    }

    public String getShortDescription() {
        return "Returns arg is infinite value";
    }

    public String getDescription() {
        return getShortDescription();
    }
}
