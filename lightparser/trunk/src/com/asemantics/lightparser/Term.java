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

import java.lang.annotation.Annotation;

/**
 * Defines a production {@link com.asemantics.lightparser.Body} element.
 */
public abstract class Term implements Compilable {

    /**
     * Term name.
     */
    private String content;

    /**
     * Constructor.
     *
     * @param c term name.
     */
    protected Term(String c) {
        if( c == null ) {
            throw new IllegalArgumentException("Term content cannot be null");
        }
        content = c;
    }

    /**
     * Returns the descriptive name of this term.
     *
     * @return descriptive name.
     */
    public abstract String getSimpleName();

    /**
     * Returns the term name.
     *
     * @return content value.
     */
    public String getContent() {
        return content;
    }

    public int hashCode() {
        return content.hashCode();
    }

    public boolean equals(Object obj) {
        if( obj == null ) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if( ! (obj instanceof Term ) ) {
            return false;
        }
        Term other = (Term) obj;
        return content.equals( other.content);
    }

    public String toString() {
        return "Term" + "(" + content + ")";
    }
}
