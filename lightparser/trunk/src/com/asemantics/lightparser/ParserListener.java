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

import java.util.EventListener;
import java.io.InputStream;

/**
 * Defines a listener of actions performed by  {@link com.asemantics.lightparser.Parser}.
 */
public interface  ParserListener {

    /**
     * Parsing has been started.
     */
    void parsingStarted();

    /**
     * Parser ended.
     */
    void parsingEnded();

    /**
     * The parsing is failed.
     *
     * @param pe exception raised by the parser.
     */
    void parsingFailed(ParserException pe);

    /**
     * Next token has been asked.
     * @param t
     */
    void nextTokenAsked(Token t);

    /**
     * Backtracking started.
     *
     * @param ps
     */
    void backtrackingStarted(ProductionStack ps);

    /**
     * Backtracking consumed.
     *
     * @param ps
     */
    void backtrackingConsumed(ProductionStack ps);

    /**
     * Backtracking rejected.
     *
     * @param ps
     */
    void backtrackingRejected(ProductionStack ps);

    /**
     * Production stack going deeper.
     *
     * @param ps
     */
    void productionStackIn(ProductionStack ps);

    /**
     * Production stact pop out.
     * 
     * @param ps
     */
    void productionStackOut(ProductionStack ps);

}
