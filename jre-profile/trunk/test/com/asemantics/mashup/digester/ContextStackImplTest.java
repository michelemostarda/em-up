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


package com.asemantics.mashup.digester;

import junit.framework.TestCase;

/**
 * Test class for {@link com.asemantics.mashup.digester.ContextStackImpl}.
 */
public class ContextStackImplTest extends TestCase {

    /**
     * Test location listener.
     */
    class TestLocationPathListener implements LocationPathListener {

        private boolean found = false;

        private int expectedDepth;

        private int opened = 0;

        TestLocationPathListener(int ed) {
            expectedDepth = ed;
        }

        public void matchBegins(Context cs, LocationPath lp) {
            assertEquals( "Unespected depth.", expectedDepth, cs.getNodesStack().length );
            found = true;
            opened++;
        }

        public void matchEnds(Context cs, LocationPath lp) {
            opened--;
        }

        void checkFound() {
            assertTrue( "Location path not satisfied.", found );
            found = false;
        }

        void checkNotFound() {
            assertFalse( "Location cannot be satisfied.", found );
        }

        void checkOpened() {
            assertEquals("Not opened nodes expected.", 0, opened);
        }
    }

    private ContextStack csi;

    protected void setUp() throws Exception {
        csi  = new ContextStackImpl();
    }

    protected void tearDown() throws Exception {
        csi  = null;
    }

    public void testConditions() {

        LocationPathImpl lpi1 = new LocationPathImpl();
        lpi1.addStep( new Step("a") );
        lpi1.addStep( new Step("b") );
        lpi1.addStep( new Step("c") );
        TestLocationPathListener pathListener1 = new TestLocationPathListener(3);
        csi.addLocationPath( lpi1, pathListener1);

        LocationPathImpl lpi2 = new LocationPathImpl();
        lpi2.addStep( new Step("a") );
        lpi2.addStep( new Step("x") );
        lpi2.addStep( new Step("x") );
        lpi2.addStep( new Step("y", new AttributeNameCondition("ny1") ) );
        TestLocationPathListener pathListener2 = new TestLocationPathListener(4);
        csi.addLocationPath( lpi2, pathListener2);

        /*
           <a>
                <x>
                    <x>
                        <y ny1=nvy1 />
                    </x>
                </x>

                <b>
                    <c>
                    </c>
                </b>
           </a>
         */

        pathListener1.checkNotFound();

        csi.pushNode("a", new NodeAttribute[]{} );
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.pushNode("x", new NodeAttribute[]{} );
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.pushNode("x", new NodeAttribute[]{} );
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.pushNode("y", new NodeAttribute[]{ new NodeAttribute("ny1", "nvy1") } );
        pathListener1.checkNotFound();
        pathListener2.checkFound(); // -- FOUND

        csi.popNode();
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.popNode("x");
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.popNode("x");
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.pushNode("b", new NodeAttribute[]{} );
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.pushNode("c", new NodeAttribute[]{} );
        pathListener1.checkFound();  // -- FOUND
        pathListener2.checkNotFound();

        csi.popNode("c");
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.popNode("b");
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        csi.popNode("a");
        pathListener1.checkNotFound();
        pathListener2.checkNotFound();

        pathListener1.checkOpened();
        pathListener2.checkOpened();
    }


}
