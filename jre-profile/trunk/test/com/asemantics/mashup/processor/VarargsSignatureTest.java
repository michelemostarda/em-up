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

/**
 * Tests the {@link com.asemantics.mashup.processor.VarargsSignature} class.
 */
public class VarargsSignatureTest extends TestCase {

    private VarargsSignature target;

    @Override
    protected void setUp() throws Exception {
        target = new VarargsSignature(
                new FormalParameter[] {
                        new FormalParameter(FormalParameter.Type.BOOLEAN, "boolArg"),
                        new FormalParameter(FormalParameter.Type.STRING,  "stringArg"),
                        new FormalParameter(FormalParameter.Type.NUMERIC, "numericArg"),
                        new FormalParameter(FormalParameter.Type.ANY,     "anyArg"),
                }
        );
    }

    @Override
    protected void tearDown() throws Exception {
        target = null;
    }

    /**
     * Tests varargs signature with actual parameters size == formal parameters size.
     *
     * @throws SequenceNotFoundException
     */
    public void testSignatureWithNoRest() throws SequenceNotFoundException {
        Value[] values = new Value[] {
            new StringValue("true"),
            new StringValue("stringValue"),
            new StringValue("100"),
            new ListValue( new StringValue("list_element") )
        };

        SignatureContextMap signatureContextMap = target.unify(values);
        assertEquals("Unespected size.", 4, signatureContextMap.size() );
        assertFalse("Unespected varargs.", signatureContextMap.containsValueName(VarargsSignature.VARARGS_KEY));
    }

    /**
     * Tests varargs signature with actual parameters size > formal parameters size.
     *
     * @throws SequenceNotFoundException
     */
    public void testSignatureWithRest() throws SequenceNotFoundException {
        Value[] values = new Value[] {
            new StringValue("true"),
            new StringValue("stringValue"),
            new StringValue("100"),
            new ListValue( new StringValue("list_element") ),
            new StringValue("rest1"),
            new StringValue("rest2")
        };

        SignatureContextMap signatureContextMap = target.unify(values);
        assertEquals("Unespected size.", 5, signatureContextMap.size() );
        assertTrue("Unespected varargs.", signatureContextMap.containsValueName(VarargsSignature.VARARGS_KEY));

        VarargsSignature.VarargsValue varargsValue =
                (VarargsSignature.VarargsValue) signatureContextMap.getValue(VarargsSignature.VARARGS_KEY);
        assertEquals("Unespected rest size.", 2, varargsValue.getValues().length);
    }

    /**
     * Tests varargs signature with actual parameters size < formal parameters size.
     */
    public void testSignatureWithInsufficientActualParamters() {
        Value[] values = new Value[] {
            new StringValue("true"),
            new StringValue("stringValue"),
            new StringValue("100"),
        };

        try {
            target.unify(values);
            fail("Unespected unification.");
        } catch (SequenceNotFoundException snfe) {
            // OK.
            System.out.println("message:" + snfe.getMessage());
        }
    }

}
