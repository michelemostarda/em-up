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


package com.asemantics.mashup;

/**
 * This class defines version and properties of <i>MU</i> language.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: MU.java 385 2009-04-17 17:07:10Z michelemostarda $
 */
public final class MU {

    private static final MU INSTANCE = new MU();

    public static MU instance() {
        return INSTANCE;
    }

    private MU() {}

    public String getLanguageName() {
        return "MU";
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 7;
    }

    @Override
    public String toString() {
        return  getLanguageName() + " language v" + getMajorVersion() + "." + getMinorVersion();
    }
}
