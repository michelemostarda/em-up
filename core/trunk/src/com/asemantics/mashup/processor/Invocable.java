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

import com.asemantics.lightparser.TreeNode;
import com.asemantics.mashup.parser.ValidationException;

import java.util.Set;

/**
 * Defines any invocable operation.
 *
 * @see com.asemantics.mashup.processor.OperationsSequence
 * @see com.asemantics.mashup.processor.Signature
 */
public abstract class Invocable implements Operation {

    /**
     * Returns the invocable signature.
     * 
     * @return signature of this invocable.
     */
     public abstract Signature getSignature();

    /**
     * Returns the TreeNode generating of current invocable.
     *
     * @return tree node from which this invocable has been defined.
     */
     public abstract TreeNode getTreeNode();

    /**
     * Returns a short human readable description for this invocable.
     * @return short description.
     */
     public abstract String getShortDescription();

    /**
     * Returns a complete human readable description for this invocable.
     * @return complete description.
     */
     public abstract String getDescription();

    /**
     * Invocable operation will be not validated.
     *
     * @param context
     * @throws ValidationException
     */
     public void validate(Set<String> context) throws ValidationException {
        // Empty.
     }

    /**
     * Two invocables are equivalent if their signatures are equal.
     *
     * @param obj
     * @return the euquality condition.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if( obj instanceof Invocable) {
            Invocable other = (Invocable) obj;
            return getSignature().equals( other.getSignature() );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }
}
