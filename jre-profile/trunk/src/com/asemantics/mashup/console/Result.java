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

import com.asemantics.mashup.common.Utils;

import java.util.Date;

/**
 * Defines a result entry.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: Result.java 395 2009-04-29 13:08:49Z michelemostarda $
 */
public interface Result<T> {

    /**
     * Returns the command origianting this result.
     * 
     * @return command string.
     */
    public String getOriginatingCommand();

    /**
     * Returns the time when the result was generated.
     *
     * @return date object.
     */
    public Date getGenerationTime();

    /**
     * Returns the result content.
     * 
     * @return content of this result entry.
     */
    public T getContent();

    /**
     *  Returns a short preview of the content.
     *
     * @param size size of the short preview.
     * @return string preview.
     */
    public String getContentPreview(int size);

    /**
     * Induces the content type of this entry.
     *
     * @return content type.
     */
    public Utils.ContentType getContentType();

    /**
     * Generates a result descriptive string, and optionally a content preview.
     *
     * @param addContentPreview if content preview shold be comprised.
     * @return result preview.
     */
    public String asStringPreview(boolean addContentPreview);

}
