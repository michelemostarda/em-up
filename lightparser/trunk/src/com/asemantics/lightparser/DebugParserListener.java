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

/**
 * Defines a debug implementation of {@link com.asemantics.lightparser.Parser}.
 */
public class DebugParserListener implements ParserListener {

    public void parsingStarted() {
        System.out.println("Parsing started.");
    }

    public void parsingEnded() {
        System.out.println("Parsing ended.");
    }

    public void parsingFailed(ParserException pe) {
        System.out.println("The parsing is failed.");
        pe.printStackTrace();
    }

    public void nextTokenAsked(Token t) {
        System.out.println("Next token retrieved: "+ t);
    }

    public void backtrackingStarted(ProductionStack ps) {
        System.out.println("Backtracking started.");
    }

    public void backtrackingConsumed(ProductionStack ps) {
        System.out.println("Backtracking consumed.");
    }

    public void backtrackingRejected(ProductionStack ps) {
        System.out.println("Backtracking rejected: " + ps);
    }

    public void productionStackIn(ProductionStack ps) {
        System.out.println("Production stack in");
        System.out.println(ps);
    }

    public void productionStackOut(ProductionStack ps) {
        System.out.println("Production stack out");
    }
}
