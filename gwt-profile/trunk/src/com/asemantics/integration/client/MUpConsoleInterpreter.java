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


package com.asemantics.integration.client;

import com.asemantics.integration.client.proxy.PGetOperation;
import com.asemantics.integration.client.proxy.PPostOperation;
import com.asemantics.integration.client.proxy.PPutOperation;
import com.asemantics.integration.client.proxy.PDeleteOperation;
import com.asemantics.mashup.interpreter.Interpreter;
import com.asemantics.mashup.processor.Processor;

/**
 * Extends the {@link com.asemantics.mashup.interpreter.Interpreter}
 * class adding console specific operations.
 */
public class MUpConsoleInterpreter extends Interpreter {

    /**
     * Constructor.
     */
    public MUpConsoleInterpreter() {
        super();
    }

    @Override
    protected void initEnvironment(Processor processor) {

        // Loads basic predicates.
        super.initEnvironment(processor);

        // Loads GWT specific predicates.
        processor.addPredicate( "PGet"     , new PGetOperation ()     );
        processor.addPredicate( "PPost"    , new PPostOperation()     );
        processor.addPredicate( "PPut"     , new PPutOperation()      );
        processor.addPredicate( "PDelete"  , new PDeleteOperation()   );

    }
}
