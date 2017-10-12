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
import java.util.List;

/**
 * Tests the {@link com.asemantics.lightparser.Util} class. 
 */
public class UtilTest extends TestCase {

    /**
     * Tests the sublist method.
     */
    public void testSubList() {

        List in = Arrays.asList( new Integer[] {1, 2, 3, 4, 5, 6} );

        checkSize(1, Util.subList( in , 0 ,1 ));
        checkSize(2, Util.subList( in , 3 ,5 ));
        checkSize(1, Util.subList( in , 5 ,6 ));

        try {
            Util.subList(in, 6, 7);
            fail();
        } catch (Exception e){}

       try {
            Util.subList(in, -3, -2);
            fail();
        } catch (Exception e){}
    }
    
    /**
     * Auxiliary.
     *
     * @param exp
     * @param list
     */
    private void checkSize(int exp, List list) {
        assertEquals("Unespected result size", exp, list.size() );
    }

}
