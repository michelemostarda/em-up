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


package com.asemantics.mashup.console;

import java.lang.reflect.Method;

/**
 * Defines a command associated to a method.
 *
 * @see com.asemantics.mashup.console.annotations.CommandMethod
 * @see com.asemantics.mashup.console.IntrospectionCommandModel
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: IntrospectionCommand.java 384 2009-04-16 20:51:17Z michelemostarda $
 */
public class IntrospectionCommand implements Command {

    /**
     * Class instance accessed by introspection.
     */
    private Object instance;

    /**
     * Target method.
     */
    private Method target;

    /**
     * Command name.
     */
    private String name;

    /**
     * Short description.
     */
    private String shortDescr;

    /**
     * Long description.
     */
    private String longDescr;

    /**
     * Defines an introspection command.
     *
     * @param instance object instance.
     * @param target target method.
     * @param name command name.
     * @param shortDescription short description.
     * @param longDescription long description.
     */
    public IntrospectionCommand(
            Object instance,  Method target, String name, String shortDescription, String longDescription
    ) {
        if(instance == null) {
            throw new IllegalArgumentException("Invalid object instance.");
        }
        if(target == null) {
            throw new IllegalArgumentException("Invalid method target.");
        }
        if(name == null || shortDescription == null || longDescription == null) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        this.instance = instance;
        this.name = name;
        shortDescr = shortDescription;
        longDescr  = longDescription;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescr;
    }

    public String getLongDescription() {
        return longDescr;
    }

    public void execute(CommandContext commandContext, String[] args)
    throws CommandException {
        try {
            // CommandContext commandContext, String[] args
            String result = (String) target.invoke(instance, commandContext, (Object) args);
            if(result != null) {
                commandContext.getSysOut().println(result);
            }
        } catch (Exception e) {
            throw new CommandException("Error while executing command.", e);
        }
    }

}
