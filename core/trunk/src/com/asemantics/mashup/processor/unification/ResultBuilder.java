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

import com.asemantics.mashup.processor.Value;
import com.asemantics.mashup.processor.json.JsonBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to build unification result.
 */
public class ResultBuilder implements UnificationResult {

    /**
     * Failure flag.
     */
    private boolean failed = false;

    /**
     * Failure message.
     */
    private String failureMessage;

    /**
     * Unification map: variable -> JSON data.
     */
    private Map<String,JsonBase> unificationMap;

    /**
     * Constructor.
     */
    ResultBuilder() {
        unificationMap = new HashMap<String,JsonBase>();
    }

    /**
     * Adds a partial result.
     *
     * @param variable
     * @param jb
     * @throws UnificationException
     */
    void addUnification(String variable, JsonBase jb) throws UnificationException {
        if(unificationMap.containsKey(variable)) {
            abort("Variable '" + variable + "' has been already unified.");
        }
        unificationMap.put(variable, jb);
    }

    /**
     * Aborts unification process.
     * 
     * @param cause
     * @throws UnificationException
     */
    void abort(String cause) throws UnificationException {
        failed = true;
        failureMessage = cause;
        throw new UnificationException(cause);
    }

    /**
     * Aborts unification process.
     * 
     * @param cause
     * @throws UnificationException
     */
    void abort(Throwable cause) throws UnificationException {
        failed = true;
        failureMessage = cause.getMessage();
        throw new UnificationException(cause);
    }

    public boolean isFailed() {
        return failed;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public JsonBase getValue(String varname) {
        // TODO: FIX THIS.
        Value result = (Value) unificationMap.get(varname);
        return result == null ? null : (result.getNativeValue() instanceof JsonBase ? (JsonBase) result.getNativeValue() : result );
    }

    public String[] getVariables() {
        return unificationMap.keySet().toArray( new String[ unificationMap.keySet().size() ] );
    }

    public int size() {
        return unificationMap.keySet().size();
    }

    public UnificationException getUnificationException() {
        return new UnificationException( getFailureMessage() );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Unification status.
        sb.append("Unification ");
        sb.append( failed ? "failed" : "succeeded" );

        // Failure message.
        sb.append("\n");
        if( failed ) {
            sb.append(" with message '").append( failureMessage ).append("'\n");
        }

        // Unification map.
        sb.append("{\n");
        for( Map.Entry<String,JsonBase> entry : unificationMap.entrySet() ) {
            sb.append( entry.getKey() ).append(" =:= ").append( entry.getValue().asPrettyJSON() ).append("\n");
        }
        sb.append("}\n");

        return sb.toString();
    }
}
