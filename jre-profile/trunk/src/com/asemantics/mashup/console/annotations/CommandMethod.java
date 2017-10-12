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


package com.asemantics.mashup.console.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method canditate to be a command.
 *
 * @see com.asemantics.mashup.console.IntrospectionCommandModel
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: CommandMethod.java 384 2009-04-16 20:51:17Z michelemostarda $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandMethod {

    /**
     * Defines the command name.
     *
     * @return
     */
    String name();

    /**
     * Defines the short description.
     *
     * @return
     */
    String shortDescription() default "<no short description>";

    /**
     * Defines the long description.
     * 
     * @return
     */
    String longDescription() default "<no long description>";

}
