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


package com.asemantics.lightparser;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Defines a test for {@link com.asemantics.lightparser.ParseTreeVisitor}.
 */
public class ParseTreeVisitorTest extends TestCase {

    /**
     * Target.
     */
     ParseTreeVisitor ptv;

    /**
     * Auxiliary term implementation.
     */
    class TestTerm extends Term {

        boolean precompiled  = false;

        boolean postcompiled = false;

        Object[] childrenResult;

        protected TestTerm(String c) {
            super(c);
        }

        public String getSimpleName() {
            return "TestTerm";
        }

        public void precompile() {
            precompiled = true;
            System.out.println("precompile: "+ getContent() );
        }

        public Object postcompile(TreeNode associatedNode, Object[] cr) {
            postcompiled = true;
            childrenResult = cr;
            System.out.println("postcompile " + getContent() + "{");
            for(Object o : cr) {
                System.out.println(o);
            }
            System.out.println("}");
            return getContent();
        }
    }

    protected void setUp() throws Exception {
        ptv = new ParseTreeVisitor( createTestTree() );
    }

    protected void tearDown() throws Exception {
        ptv = null;
    }

    /**
     * Tests just run.
     */
    public void testRun() {
        while( ptv.hasNext() ) {
            TreeNode tn = ptv.next();
            System.out.println("TreeNode: " + tn.getName());
        }
    }

    /**
     * Tests a visit sequence.
     */
    public void testVisit() {
       verifyExpected("root", false, false, null );
       verifyExpected("1"   , true , false, null );
       verifyExpected("2"   , true , false, null );
       verifyExpected("3"   , true , false, null );
       verifyExpected("4"   , true , true, new Object[]{}               );
       verifyExpected("3"   , true , true, new Object[]{"4"}            );
       verifyExpected("5"   , true , true, new Object[]{}               );
       verifyExpected("6"   , true , true, new Object[]{}               );
       verifyExpected("2"   , true , true, new Object[]{"3", "5", "6"}  );
       verifyExpected("7"   , true , true, new Object[]{}               );
       verifyExpected("1"   , true , true, new Object[]{"2", "7"}       );
       verifyExpected("root", true , true, new Object[]{"1"}            );

       assertFalse("Unespected element.", ptv.hasNext());

        Object[] result = (Object[]) ptv.getResult();
        System.out.println( "Result: "+ Arrays.asList(result).toString() );
        assertEquals("Unespected result value", "1", result[0]);

    }

    /**
     * Verifies exptected step.
     *
     * @param expected
     * @param precmp
     * @param postcmp
     * @param cr
     */
    private void verifyExpected(String expected, boolean precmp, boolean postcmp, Object[] cr) {
        TreeNode next = ptv.next();
        assertEquals("Unespected sequence.", expected, next.getName() );
        Term t = next.getTerm();
        if( ! (t instanceof TestTerm ) ) {
            return;
        }
        TestTerm tt = (TestTerm) t;
        assertEquals("Unespected pre-compilation  status.", precmp , tt.precompiled  );
        assertEquals("Unespected post-compilation status.", postcmp, tt.postcompiled );
        if( postcmp ) {
            for(int i = 0; i< cr.length; i++) {
                assertEquals("Unespected children result.", cr[i], tt.childrenResult[i]);
            }
        }
    }

    /**
     * Creates the test tree.
     * 
     * @return
     */
    private ParseTree createTestTree() {
        ParseTree pt = new ParseTree("root");

        TreeNode tn1 = new TreeNode("1", new TestTerm("1") );
        TreeNode tn2 = new TreeNode("2", new TestTerm("2") );
        TreeNode tn3 = new TreeNode("3", new TestTerm("3") );
        TreeNode tn4 = new TreeNode("4", new TestTerm("4") );
        TreeNode tn5 = new TreeNode("5", new TestTerm("5") );
        TreeNode tn6 = new TreeNode("6", new TestTerm("6") );
        TreeNode tn7 = new TreeNode("7", new TestTerm("7") );

        tn1.addChild(tn2);
        tn1.addChild(tn7);

        tn2.addChild(tn3);
        tn2.addChild(tn5);
        tn2.addChild(tn6);

        tn3.addChild(tn4);

        pt.getRoot().addChild( tn1 );

        return pt;
    }
}
