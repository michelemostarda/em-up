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


package com.asemantics.mashup.nativepkg;

public class JSFunctions {

    /**
     * Mantains the result of disablng restriction.
     */
    protected static boolean restrictionsDisabled   = false;

    /**
     * Matains the result of first time operation.
     */
    protected static boolean tryDisableRestrictions = false;

    /**
     * Error on restriction in disabilitation.
     */
    protected static String restrictionDisabilitationError;

    /**
     * Disables browser restrictions.
     *
     * @return <code>true</code> if disabling worked, <code>false</code> otherwise.
     */
    public static boolean disableRestrictions() {
        if( tryDisableRestrictions ) {
            return restrictionsDisabled;
        }
        tryDisableRestrictions = true;
        restrictionDisabilitationError = tryDisableRestrictions();
        restrictionsDisabled = restrictionDisabilitationError == null;
        return restrictionsDisabled;
    }

    /**
     * Returns disabilitation error.
     * @return string error.
     */
    public static String getDisabilitationError() {
        return restrictionDisabilitationError;
    }

    /**
     * Performs a sleep of duration <i>timemillis</i>.
     *
     * @param timemillis
     */
    public static native void sleepThread(int timemillis) /*-{
        $wnd.alert("Waiting for " + timemillis + " millisec.");

//        try {
//            var loop = true;
//            var current = new Date();
//            var now;
//            var cTimestamp = current.getTime();
//            while(loop) {
//                now = new Date();
//                nTimestamp = now.getTime();
//                if(nTimestamp - cTimestamp > timemillis) {
//                    loop = false;
//                }
//            }
//        } catch(exc) {
//            $wnd.alert("Error while sleeping..." + exc);
//        }

    }-*/;

    /**
     * Tries disabling restrictions.
     *
     * @return operations result.
     */
    public static native String tryDisableRestrictions() /*-{
        try {
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalPreferencesRead" );
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalPreferencesWrite");
            $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead"     );  // Same origin check.
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserWrite"    );
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalFileRead"        );
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect"       );
            // $wnd.netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserAccess"   );
        } catch(exc) {
            return exc + "";
        }
        return null;
    }-*/;

}
