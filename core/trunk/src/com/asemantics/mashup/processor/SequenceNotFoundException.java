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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This exception is raised when an invoked sequence
 * cannot be found.
 *
 * @see com.asemantics.mashup.processor.InvokeOperation 
 */
public class SequenceNotFoundException extends Exception {

    /**
     * Name of sequence raining exception.
     */
    private String sequenceName;

    /**
     * List of sub causes.
     */
    private List<SequenceNotFoundException> causes = new ArrayList<SequenceNotFoundException>();

    /**
     * Constructor.
     *
     * @param message error message.
     * @param cause error cause.
     */
    public SequenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Contructor.
     *
     * @param msg error message.
     */
    public SequenceNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Sets the sequence name raising exception.
     *
     * @param sn
     */
    protected void setSequenceName(String sn) {
        if( sn == null ) {
            throw new IllegalArgumentException();
        }
        sequenceName = sn;
    }

    /**
     * Allows to add a sub cause for this exception.
     *
     * @param cause
     */
    public void addCause(SequenceNotFoundException cause) {
        causes.add(cause);
    }

    /**
     * Returns the list of internal causes.
     * @return a list of causes.
     */
    public SequenceNotFoundException[] getCauses() {
        return causes.toArray( new SequenceNotFoundException[ causes.size() ] );
    }

    @Override
    public String getMessage() {
        return "Error while invoking sequence" +
                ( sequenceName != null ?  "'" + sequenceName + "'" : "" ) +
                super.getMessage() +
                ( causes.size() > 0 ? " with causes: {" + Arrays.asList(causes) + "}" : "");
    }

    @Override
    public String toString() {
        return getMessage();
    }

}
