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
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id: InMemoryResult.java 395 2009-04-29 13:08:49Z michelemostarda $
 */
public class InMemoryResult<T> implements Result<T> {

    public static final int CONTENT_PREVIEW_SIZE = 20;

    private String command;

    private long time;

    private T content;

    public InMemoryResult(String cmd, T value) {
        command = cmd;
        time = System.currentTimeMillis();
        content = value;
    }

    public String getOriginatingCommand() {
        return command;
    }

    public Date getGenerationTime() {
        return new Date(time);
    }

    public T getContent() {
        return content;
    }

    public String getContentPreview(int size) {
        String contentStr = content.toString();
        return contentStr.substring(0, size < contentStr.length() ? size : contentStr.length() );
    }

    public Utils.ContentType getContentType() {
        return Utils.induceType( content.toString() );
    }

    public String asStringPreview(boolean addContentPreview) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getGenerationTime()).append("]")
                .append("[").append(getContentType()).append("]")
                .append(" '").append(getOriginatingCommand()).append("'");
        if (addContentPreview) {
            sb.append("\n").append(getContentPreview(CONTENT_PREVIEW_SIZE)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return asStringPreview(true);
    }
}
