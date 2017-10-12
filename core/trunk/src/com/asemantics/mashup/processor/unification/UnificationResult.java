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


package com.asemantics.mashup.processor.unification;

import com.asemantics.mashup.processor.json.JsonBase;


/**
 * Defines unification result data.
 */
public interface UnificationResult {

    /**
     * @return <code>true</code> if unification failed,
     *         <code>false</code> otherwise.
     */
    public boolean isFailed();

    /**
     * @return the failure message if unification failed.
     */
    public String getFailureMessage();

    /**
     * Returns the <i>JSON</i> data unified with variable name.
     *
     * @param varname name of variable.
     * @return value associated to variable.
     */
    public JsonBase getValue(String varname);

    /**
     * @return list of variable names.
     */
    public String[] getVariables();

    /**
     * Number of variables.
     * 
     * @return  number of variables.
     */
    public int size();

    /**
     * Returns the specific unification exception.
     * 
     * @return the exception.
     */
    UnificationException getUnificationException();
}
