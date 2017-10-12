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


package com.asemantics.mashup.parser;

import com.asemantics.mashup.processor.Argument;
import com.asemantics.mashup.processor.Validable;
import com.asemantics.mashup.processor.Operation;

import java.util.Set;

/**
 * Defines a triple element <i>from</i>, <i>arc</i>, <i>to</i>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: Triple.java 444 2009-06-29 16:16:42Z michelemostarda $
 */
public class Triple implements Validable {

    private Operation from;
    private Operation arc;
    private Operation to;

    public Triple(Operation from, Operation arc, Operation to) {
        this.from = from;
        this.arc  = arc;
        this.to   = to;
    }

    protected Operation getFrom() {
        return from;
    }

    protected Operation getArc() {
        return arc;
    }

    protected Operation getTo() {
        return to;
    }

    public void validate(Set<String> context) throws ValidationException {
        from.validate(context);
        arc.validate(context);
        to.validate(context);
    }
}
