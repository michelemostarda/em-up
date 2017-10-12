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

import junit.framework.TestCase;
import com.asemantics.mashup.processor.unification.UnificationResult;

/**
 * Test class for {@link com.asemantics.mashup.processor.Signature}.
 */
public class SignatureTest extends TestCase {

    private Signature signature;

    private Value[] values = new Value[] {
            new StringValue("true"),
            new StringValue("stringValue"),
            new StringValue("100"),
            new ListValue( new StringValue("list_element") ),
    };

    protected void setUp() throws Exception {
        signature = new Signature(
                new FormalParameter[] {
                        new FormalParameter(FormalParameter.Type.BOOLEAN, "boolArg"),
                        new FormalParameter(FormalParameter.Type.STRING,  "stringArg"),
                        new FormalParameter(FormalParameter.Type.NUMERIC, "numericArg"),
                        new FormalParameter(FormalParameter.Type.ANY,     "anyArg"),
                }
        );
    }

    protected void tearDown() throws Exception {
        signature = null;
    }

    /**
     * Tests the ability of the signature of casting actual parameters.
     *
     * @throws ArgumentEvaluationException
     */
    public void testArgumentCastingInContextMap() throws SequenceNotFoundException {

        SignatureContextMap scm = signature.unify(values);
        assertEquals( "Unespected scm size.", 4, scm.size() );

        assertTrue("Unespected type.", scm.getIthArgumentValue(0) instanceof BooleanValue);
        BooleanValue bv = (BooleanValue) scm.getIthArgumentValue(0);
        assertEquals("Unespected value.", true, bv.getNativeValue().booleanValue() );

        assertTrue("Unespected type.", scm.getIthArgumentValue(1) instanceof StringValue);
        StringValue sv = (StringValue) scm.getIthArgumentValue(1);
        assertEquals("Unespected value.", "stringValue", sv.getNativeValue() );

        assertTrue("Unespected type.", scm.getIthArgumentValue(2) instanceof NumericValue);
        NumericValue nv = (NumericValue) scm.getIthArgumentValue(2);
        assertEquals("Unespected value.", 100, nv.integer() );

        assertTrue("Unespected type.", scm.getIthArgumentValue(3) instanceof ListValue);
        ListValue lv = (ListValue) scm.getIthArgumentValue(3);
        assertEquals("Unespected value.", "list_element", lv.getElementAt(0).asString().getNativeValue() );
    }

    /**
     * Tests the #unify() with right number of arguments.
     */
    public void testPositiveVerifyInvocation() throws SequenceNotFoundException {
        try {
            signature.unify(
                    new Value[] {
                            new StringValue(),
                            new StringValue(),
                            new StringValue(),
                            new StringValue()
                    }
            );
        } catch (SequenceNotFoundException snfe) {
            fail("Expected unification here.");
        }
    }

    /**
     * Tests the #unify() with wrong number of arguments.
     */
    public void testNegativeVerifyInvocation() throws SequenceNotFoundException {
        try {
            signature.unify(
                    new Value[]{
                        new StringValue(),
                    }
            );
            fail("Expected unification failure here.");
        } catch (SequenceNotFoundException snfe) {
            // OK.
        }
    }

    /**
     * Tests the FormalParameter#unify()
     */
    public void testUnify() {
        FormalParameter formalParameter = new FormalParameter(FormalParameter.Type.ANY, "fp1");
        UnificationResult unificationResult = formalParameter.unify( new StringValue("value") );
        assertNotNull("Expected result here.", unificationResult);
        assertFalse("Expected unification here.", unificationResult.isFailed());
    }

}
