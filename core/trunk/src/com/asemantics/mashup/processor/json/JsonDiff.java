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


package com.asemantics.mashup.processor.json;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents the difference between two <i>JSON</i> objects.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: JsonDiff.java 434 2009-06-01 13:45:43Z michelemostarda $
 */
public class JsonDiff {

    /**
     * Defines a step into the visit of a JSON object.
     */
    class Step {

        /**
         * Step qualifier.
         */
        private String qualifier;

        /**
         * Constructor.
         *
         * @param q
         */
        Step(String q) {
            qualifier = q;
        }

        @Override
        public String toString() {
            return qualifier;
        }
    }

    /**
     * Defines an issue detected during the comparison of paraller elements
     * of two JSON objects.
     */
    public class Issue {

        /**
         * Location of the issue.
         */
        private Step[] location;

        /**
         * Description of the issue.
         */
        private String description;

        /**
         * Constructor.
         *
         * @param loc
         * @param desc
         */
        Issue(Step[] loc, String desc) {
            location    = loc;
            description = desc;
        }

        /**
         * @return description of the issue.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Prints out the location of this issue inside the given string builder.
         *
         * @param sb
         */
        public void getLocation(StringBuilder sb) {
            for(int i = 0; i < location.length; i++) {
                sb.append(location[i]);
            }
        }

        /**
         * @return the location of this issue.
         */
        public String getLocation() {
            StringBuilder sb = new StringBuilder();
            getLocation(sb);
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            getLocation(sb);
            sb.append(" - ");
            sb.append(description);
            return sb.toString();
        }
    }

    /**
     * Stack used to build the current context.
     */
    private Stack<Step> context;

    /**
     * List of detected issues.
     */
    private List<Issue> issues;

    /**
     * Constructor.
     */
    protected JsonDiff() {
        context = new Stack<Step>();
        issues  = new ArrayList<Issue>();
    }

    /**
     * @return <code>true</code> if there are no differences, <code>false</code>
     * otherwise.
     */
    public boolean noDifference() {
        return issues.isEmpty();
    }

    /**
     * @return the list of detected issues.
     */
    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    /**
     * Pushes a location in the context stack.
     *
     * @param path
     */
    protected void pushLocation(String path) {
        context.push( new Step(path) );
    }

    /**
     * Pops out the peek location from the context stack.
     */
    protected void popLocation() {
        context.pop();
    }

    /**
     * Adds an issue detected in the current context.
     * 
     * @param issue
     */
    protected void addIssue(String issue) {
        issues.add( new Issue( context.toArray( new Step[ context.size() ] ), issue ) );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Issue issue : issues) {
            sb.append(issue);
            sb.append("\n");
        }
        return sb.toString();
    }

}
