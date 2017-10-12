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

/**
 * In this class are tested the transpositions of of some <i>Erlang</i>
 * code samples in <i>MU</i> language.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: ErlangScenariosTest.java 432 2009-06-01 13:31:44Z michelemostarda $
 */
public class ErlangScenariosTest extends AbstractInterpreterTest {

    /**
     * Tests the transposition of following <i>Erlang</i> code:
     *
     * <pre>
     *   list_lenght([]) -> 0;
     *   list_lenght([First|Rest]) -> 1 + list_lenght(Rest);
     * </pre>
     *
     * @throws InterpreterException
     */
    public void testSample1() throws InterpreterException {
        interpreter.process("ListLenght([]) : 0; ListLenght([head|tail]) : Sum(1,ListLenght(tail));");
        Value result = interpreter.process("ListLenght( List(1,2,3,4,5,6,7,8,9,10) );");
        System.out.println("Result: " + result);
        assertEquals("Unespected Result.", 10, result.asNumeric().integer() );
    }

    /**
     * Tests the transposition of following <i>Erlang</i> code:
     *
     * <pre>
     *   list_max([Head|Rest]) -> list_max(Rest,Head);
     *   list_max([], Rest)    -> Rest;
     *   list_max([Head|Rest], Result_so_far) when Head > Result_so_far -> list_max(Rest, Head);
     *   list_max([Head|Rest], Result_so_far) -> list_max(Rest, Result_so_far);
     * </pre>
     *
     * @throws InterpreterException
     */
    // TODO: introduce alternative support.
    public void testSample2() throws InterpreterException {
        String program =
                "ListMax([head|rest]) : ListMax(head, rest);" +
                "ListMax([],rest) : rest;" +
                "ListMax([head|rest],rsf) : if( Gt(head,rsf), ListMax(rest,head), ListMax(rest,rsf) );" +
                "ListMax(List(1,2,3,4,5,7,4,3,2,1));";
        Value result = interpreter.process(program);
        System.out.println("Result: " + result);
    }

}
