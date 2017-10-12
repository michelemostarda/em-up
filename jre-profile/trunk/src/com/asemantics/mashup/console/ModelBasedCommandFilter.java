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

/**
 * This filter accepts a command if it respects a given <i>regexp</i> pattern
 * and the expressed command in defined inside the <i>model</i>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: ModelBasedCommandFilter.java 400 2009-05-14 22:52:07Z michelemostarda $
 */
public final class ModelBasedCommandFilter extends RegexpCommandFilter {

    private CommandModel model;

    public ModelBasedCommandFilter(CommandModel model, String regexp, int nameGroup, int strGroup) {
        super(regexp, nameGroup, strGroup);
        this.model = model;
    }

    public ModelBasedCommandFilter(CommandModel model, String regexp) {
        super(regexp);
        this.model = model;
    }

    @Override
    public boolean acceptCommand(String commandStr) {
        if( super.acceptCommand(commandStr) ) {
            String cmd  = super.filteredCommandName(commandStr);
            return model.containCommand(cmd);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return super.getDescription()  + "(" + model.getModelDescription()  + ")";
    }
}
