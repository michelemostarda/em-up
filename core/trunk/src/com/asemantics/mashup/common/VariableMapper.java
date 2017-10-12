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


package com.asemantics.mashup.common;

/**
 * Defines a map of variables.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: VariableMapper.java 394 2009-04-29 12:39:34Z michelemostarda $
 */
public interface VariableMapper {

    boolean defineVariable(String name);

    void setVariable(String name, String value);

    void unsetVariable(String name);

    public String getValueOf(String varName);
    
}
