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

/**
 * Defines a generic location path.
 */
public interface LocationPath {

    /**
     * Allows to distinguish between relative and absolute paths.
     * 
     * @return <code>true</code> if relative, <code>false</code> otherwise.
     */
    boolean isRelative();

    /**
     * Returns all available steps.
     *
     * @return list of steps in location path from left to right.
     */
    Step[] getSteps();

    /**
     * Returns the <i>i-th</i> step.
     *
     * @param i step index to be returned.
     * @return step of this path.
     */
    Step getStep(int i);

    /**
     * Verifies that this location path matches on current stack context.
     *
     * @param cs context to be matched.
     * @return <code>true</code> if matches, <code>false</code> otherwise.
     */
    boolean matches(Context cs);
}
