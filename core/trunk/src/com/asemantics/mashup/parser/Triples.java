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
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.ExecutionContext;
import com.asemantics.mashup.processor.ExecutionStack;
import com.asemantics.mashup.processor.VariableNotFoundException;
import com.asemantics.mashup.processor.GraphValue;
import com.asemantics.mashup.processor.Validable;
import com.asemantics.mashup.processor.InvocationException;
import com.asemantics.mashup.processor.ArgumentEvaluationException;
import com.asemantics.mashup.processor.SequenceNotFoundException;

import java.util.List;
import java.util.Set;

/**
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: Triples.java 444 2009-06-29 16:16:42Z michelemostarda $
 */
public class Triples implements Argument, Validable {

    private List<Triple> triples;

    Triples(List<Triple> triples) {
        this.triples = triples;
    }

    public Value getValue(ExecutionContext ec, ExecutionStack es)
        throws VariableNotFoundException {
        GraphValue graph = new GraphValue();
        try {

            for( Triple triple : triples ) {
            graph.addArc(
                triple.getFrom().execute(ec,es),
                triple.getArc().execute(ec,es),
                triple.getTo().execute(ec,es)
            );
            }
        } catch (Exception e) {
            throw new VariableNotFoundException("Error while evaluating triple.", e);  
        }
        return graph;
    }

    public void validate(Set<String> context) throws ValidationException {
        for(Triple triple : triples) {
            triple.validate(context);
        }
    }
}
