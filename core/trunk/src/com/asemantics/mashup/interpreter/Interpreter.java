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

import com.asemantics.lightparser.ParseTree;
import com.asemantics.lightparser.ParseTreeVisitor;
import com.asemantics.mashup.parser.MUGrammarFactory;
import com.asemantics.mashup.parser.MUParser;
import com.asemantics.mashup.parser.MUParserException;
import com.asemantics.mashup.parser.Processable;
import com.asemantics.mashup.parser.ValidationException;
import com.asemantics.mashup.processor.InvokeOperation;
import com.asemantics.mashup.processor.Processor;
import com.asemantics.mashup.processor.ProcessorException;
import com.asemantics.mashup.processor.ProcessorListener;
import com.asemantics.mashup.processor.StringValue;
import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.nativeops.AndCondition;
import com.asemantics.mashup.processor.nativeops.ApplyOperation;
import com.asemantics.mashup.processor.nativeops.AsBooleanCastOperator;
import com.asemantics.mashup.processor.nativeops.AsJSONCastOperator;
import com.asemantics.mashup.processor.nativeops.AsListCastOperator;
import com.asemantics.mashup.processor.nativeops.AsMapCastOperator;
import com.asemantics.mashup.processor.nativeops.AsNumericCastOperator;
import com.asemantics.mashup.processor.nativeops.AsStringCastOperator;
import com.asemantics.mashup.processor.nativeops.CloneOperation;
import com.asemantics.mashup.processor.nativeops.ConcretizeOperation;
import com.asemantics.mashup.processor.nativeops.ContentOperation;
import com.asemantics.mashup.processor.nativeops.ContextOperation;
import com.asemantics.mashup.processor.nativeops.DiffExpression;
import com.asemantics.mashup.processor.nativeops.DivExpression;
import com.asemantics.mashup.processor.nativeops.EqCondition;
import com.asemantics.mashup.processor.nativeops.EvaluateOperation;
import com.asemantics.mashup.processor.nativeops.GetOperation;
import com.asemantics.mashup.processor.nativeops.Graph;
import com.asemantics.mashup.processor.nativeops.GraphAddArc;
import com.asemantics.mashup.processor.nativeops.GraphAddNode;
import com.asemantics.mashup.processor.nativeops.GraphRemoveArc;
import com.asemantics.mashup.processor.nativeops.GraphRemoveNode;
import com.asemantics.mashup.processor.nativeops.GtCondition;
import com.asemantics.mashup.processor.nativeops.GteCondition;
import com.asemantics.mashup.processor.nativeops.IsInfiniteMonoCondition;
import com.asemantics.mashup.processor.nativeops.IsNanMonoCondition;
import com.asemantics.mashup.processor.nativeops.JPathOperation;
import com.asemantics.mashup.processor.nativeops.JsonizeOperation;
import com.asemantics.mashup.processor.nativeops.List;
import com.asemantics.mashup.processor.nativeops.ListAddAll;
import com.asemantics.mashup.processor.nativeops.ListAddElem;
import com.asemantics.mashup.processor.nativeops.ListAddFirst;
import com.asemantics.mashup.processor.nativeops.ListAddLast;
import com.asemantics.mashup.processor.nativeops.ListGetElem;
import com.asemantics.mashup.processor.nativeops.ListIndexOf;
import com.asemantics.mashup.processor.nativeops.ListRemove;
import com.asemantics.mashup.processor.nativeops.ListSizeOperation;
import com.asemantics.mashup.processor.nativeops.LtCondition;
import com.asemantics.mashup.processor.nativeops.LteCondition;
import com.asemantics.mashup.processor.nativeops.Map;
import com.asemantics.mashup.processor.nativeops.MapGetKey;
import com.asemantics.mashup.processor.nativeops.MapPut;
import com.asemantics.mashup.processor.nativeops.MapRemove;
import com.asemantics.mashup.processor.nativeops.MapSizeOperation;
import com.asemantics.mashup.processor.nativeops.ModExpression;
import com.asemantics.mashup.processor.nativeops.ModelizeOperation;
import com.asemantics.mashup.processor.nativeops.MultExpression;
import com.asemantics.mashup.processor.nativeops.NeqCondition;
import com.asemantics.mashup.processor.nativeops.NotMonoCondition;
import com.asemantics.mashup.processor.nativeops.OrCondition;
import com.asemantics.mashup.processor.nativeops.PostOperation;
import com.asemantics.mashup.processor.nativeops.Print;
import com.asemantics.mashup.processor.nativeops.ProgrammativeContextOperation;
import com.asemantics.mashup.processor.nativeops.RangeOperation;
import com.asemantics.mashup.processor.nativeops.RenderizeOperation;
import com.asemantics.mashup.processor.nativeops.StackTraceOperation;
import com.asemantics.mashup.processor.nativeops.StringIndexOf;
import com.asemantics.mashup.processor.nativeops.StringSubString;
import com.asemantics.mashup.processor.nativeops.SumExpression;
import com.asemantics.mashup.processor.nativeops.TypeOperation;
import com.asemantics.mashup.processor.nativeops.UnifyOperation;
import com.asemantics.mashup.processor.nativeops.XPathOperation;
import com.asemantics.mashup.processor.nativeops.XorCondition;
import com.asemantics.mashup.processor.nativeops.PutOperation;
import com.asemantics.mashup.processor.nativeops.DeleteOperation;

import java.util.ArrayList;


/**
 * Defines the <i>MUp</i> language interpreter.
 *
 * @see InterpreterException
 */
public class Interpreter implements Interpretative {

    /**
     * The defualt result of the interpreter.
     */
    private static final StringValue DEFAULT_RESULT;

    static {
        DEFAULT_RESULT = new StringValue("<Nothing to parse: a statement must end with semi-colon.>");
    }

    /**
     * Internal <i>Mash Up</i> parser.
     */
    private MUParser parser;

    /**
     * Internal processor.
     */
    private Processor processor;

    /**
     * Validation flag.
     */
    private boolean validating = true;

    /**
     * Constructor.
     */
    public Interpreter() {
        parser    = new MUParser();
        processor = new Processor();
        initEnvironment(processor);
    }

    /**
     *
     * @return validation flag value.
     */
    public boolean isValidating() {
        return validating;
    }

    /**
     * Sets validation flag.
     *
     * @param v <code>true</code> to enable validation,
     * <code>false</code> otherwise.
     */
    public void setValidating(boolean v) {
        validating = v;
    }

    /**
     * Processes a program string, defined as a sequence of prepositions separated by semi quote.
     *
     * @param program
     * @return value returned by last operation.
     * @throws InterpreterException
     * @see MUGrammarFactory
     */
    public Value process(String program) throws InterpreterException {

        // Parsing phase.
        ParseTree pt;
        try {
            pt = parser.parse(program);
        } catch (MUParserException mupe) {
            throw new InterpreterException("Error while parsing program.", mupe);
        }

        // Compilation phase.
        ParseTreeVisitor ptv = new ParseTreeVisitor(pt);
        ptv.compile();

        // Peeling result.
        Object[] root =  (Object[]) ptv.getResult();
        assert root.length == 1;

        ArrayList<Processable> processables = (ArrayList<Processable>) root[0];

        // Processes all processables.
        Value result = DEFAULT_RESULT;
        for(Processable processable : processables) {
            result = process(processable);
        }

        // Returns the last processed value.
        return result;
    }

    public Value processOperation(InvokeOperation invoke)
    throws ProcessorException {
        return processor.processOperation(invoke);
    }

    /**
     * Returns the programmative context of this interpreter.
     *
     * @return the programmative context.
     */
    public String getProgrammativeContext() {
        return processor.getExecutionContext().printProgrammativeContext();
    }

    /**
     * Adds a processor listener to the interpreter processor.
     *
     * @param pl processor to be added.
     */
    public void addProcessorListener(ProcessorListener pl) {
        getProcessor().addListener(pl);
    }

    /**
     * Removes a processor listener from the interpreter processor.
     *
     * @param pl processor to be removed.
     */
    public void removeProcessorListener(ProcessorListener pl) {
        getProcessor().removeListener(pl);
    }
    
    /**
     *
     * @return the internal processor.
     */
    public Processor getProcessor() {
        return processor;
    }

    /**
     * Processes a single {@link com.asemantics.mashup.parser.Processable}.
     *
     * @param processable
     * @return the value returned by the processing.
     * @throws InterpreterException
     */
    protected Value process(Processable processable)
    throws InterpreterException {
        // Performs validation of processable.
        if(validating) {
            try {
                processable.validate();
            } catch (ValidationException ve) {
                throw new InterpreterException("Error while validating processable.", ve);
            }
        }

        // Processing result.
        try {
            return processable.process( processor );
        } catch (ProcessorException pe) {
            throw new InterpreterException("Error while processing preposition.", pe);
        }
    }

    /**
     * Initialized languae environment.
     * 
     * @param p
     */
    protected void initEnvironment(Processor p) {

        // Evaluate operation.
        p.addPredicate( "Evaluate", new EvaluateOperation(this));

        // Print operation.
        p.addPredicate( "Print", new Print() );

        // Context operations.
        p.addPredicate( "Context"   , new ContextOperation()              );
        p.addPredicate( "PContext"  , new ProgrammativeContextOperation() );
        p.addPredicate( "StackTrace", new StackTraceOperation()           );

        // Unification support.
        p.addPredicate( "Unify", new UnifyOperation()    );
        p.addPredicate( "Apply", new ApplyOperation(this));

        // Conditional operations.
        p.addPredicate( "Eq" , new EqCondition()  );
        p.addPredicate( "Neq", new NeqCondition() );
        p.addPredicate( "Lt" , new LtCondition()  );
        p.addPredicate( "Gt" , new GtCondition()  );
        p.addPredicate( "Lte", new LteCondition() );
        p.addPredicate( "Gte", new GteCondition() );

        // Boolean operators.
        p.addPredicate("And", new AndCondition()     );
        p.addPredicate("Or" , new OrCondition()      );
        p.addPredicate("Xor", new XorCondition()     );
        p.addPredicate("Not", new NotMonoCondition() );

        // Math operators.
        p.addPredicate("Sum",        new SumExpression());
        p.addPredicate("Diff",       new DiffExpression());
        p.addPredicate("Mult",       new MultExpression());
        p.addPredicate("Div",        new DivExpression());
        p.addPredicate("Mod",        new ModExpression());
        p.addPredicate("IsInfinite", new IsInfiniteMonoCondition());
        p.addPredicate("IsNan",      new IsNanMonoCondition());

        // List support.
        p.addPredicate("List"    , new List() );
        p.addPredicate("AddElem" , new ListAddElem()  );
        p.addPredicate("AddAll"  , new ListAddAll()   );
        p.addPredicate("AddFirst", new ListAddFirst() );
        p.addPredicate("AddLast" , new ListAddLast()  );
        p.addPredicate("GetElem" , new ListGetElem()  );
        p.addPredicate("LIndexOf", new ListIndexOf()  );
        p.addPredicate("LRemove" , new ListRemove()   );
        p.addPredicate("LSize"   , new ListSizeOperation() );

        // Range.
        p.addPredicate("Range"   , new RangeOperation() );

        // Map support.
        p.addPredicate("Map"    , new Map()       );
        p.addPredicate("MPut"   , new MapPut()    );
        p.addPredicate("GetKey" , new MapGetKey() );
        p.addPredicate("MRemove", new MapRemove() );
        p.addPredicate("MSize"  , new MapSizeOperation() );

        // Graph support.
        p.addPredicate("Graph"      , new Graph()           );
        p.addPredicate("GAddNode"   , new GraphAddNode()    );
        p.addPredicate("GRemoveNode", new GraphRemoveNode() );
        p.addPredicate("GAddArc"    , new GraphAddArc()     );
        p.addPredicate("GRemoveArc" , new GraphRemoveArc()  );

        // String support.
        p.addPredicate("SubString", new StringSubString() );
        p.addPredicate("SIndexOf" , new StringIndexOf()   );

        // Type handling.
        p.addPredicate("Type"     , new TypeOperation()         );
        p.addPredicate("AsBoolean", new AsBooleanCastOperator() );
        p.addPredicate("AsString" , new AsStringCastOperator()  );
        p.addPredicate("AsNumeric", new AsNumericCastOperator() );
        p.addPredicate("AsList"   , new AsListCastOperator()    );
        p.addPredicate("AsMap"    , new AsMapCastOperator()     );
        p.addPredicate("AsJSON"   , new AsJSONCastOperator()    );

        // Reference handling.
        p.addPredicate("Clone", new CloneOperation() );

        // Source operations.
        p.addPredicate("Get"    , new GetOperation()     );
        p.addPredicate("Post"   , new PostOperation()    );
        p.addPredicate("Put"    , new PutOperation()     );
        p.addPredicate("Delete" , new DeleteOperation()  );

        // Content inspection operations.
        p.addPredicate("Content", new ContentOperation() );

        // Inspection operations.
        p.addPredicate("XPath"   , new XPathOperation() );
        p.addPredicate("JPath"   , new JPathOperation() );

        // JSON_TEMPLATE operations.
        p.addPredicate("Jsonize", new JsonizeOperation() );

        // Renderization operations.
        p.addPredicate( "Renderize" , new RenderizeOperation()  );
        p.addPredicate( "Modelize"  , new ModelizeOperation()   );
        p.addPredicate( "Concretize", new ConcretizeOperation() );

    }

}
