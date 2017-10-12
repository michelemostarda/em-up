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

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Contains some utility methods.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: ErrorReporter.java 395 2009-04-29 13:08:49Z michelemostarda $
 */
public class ErrorReporter {

    public enum ErrorLevel {
        ERROR("Error"),
        SEVERE_ERROR("Severe Error");

        String description;

        ErrorLevel(String d) {
            description = d;
        }
    }

    /**
     * Prevents instantiation.
     */
    private ErrorReporter(){}

       /**
     * Handles an {@link Exception} raised by a command transforming it
     * in a error message.
     *
     * @param t
     * @param cmd
     */
    public static void handleException(ErrorLevel errorLevel, CommandContext context, Throwable t, String cmd) {
        PrintStream errStream = context.getSysErr();
        errStream.println( errorLevel.description + " '" + t.getMessage() + "'");
        if( context.isDebug() ) { t.printStackTrace(); }
        Throwable cause = getCause(t);
        int causeLevel = 0;
        while(cause != null) {
            errStream.println(
                "[" + (causeLevel++) + "] with cause: '" + cause.getClass() + " " + getMessage(cause) + " " + getLine(cause)
            );
            if( context.isDebug() ) { cause.printStackTrace(); }
            cause = getCause(cause);
        }
    }

    /**
     * Generates a formatted message for the throwable.
     *
     * @param t
     * @return
     */
    private static String getMessage(Throwable t) {
        return t.getMessage() != null ? "with message: '" + t.getMessage() + "'" : "<with no message>";
    }

    /**
     * Generates a proper location string for the throwable.
     *
     * @param t
     * @return
     */
    private static String getLine(Throwable t) {
        StackTraceElement ste = t.getStackTrace()[0];
        return "[" + ste.getFileName() + "]" +  ( ste.getLineNumber() > 0 ? "(" + ste.getLineNumber() + ")" : "" );
    }

    /**
     * Extracts the right exception cause.
     * 
     * @param t
     * @return
     */
    private static Throwable getCause(Throwable t) {
        if(t instanceof InvocationTargetException) {
            return ((InvocationTargetException) t).getTargetException();
        }
        return t.getCause();
    }

}
