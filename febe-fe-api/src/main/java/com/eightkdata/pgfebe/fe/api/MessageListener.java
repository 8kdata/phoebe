/*
 * Copyright © 2015, 8Kdata Technologies, S.L.
 *
 * Permission to use, copy, modify, and distribute this software and its documentation for any purpose,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice and this
 * paragraph and the following two paragraphs appear in all copies.
 *
 * IN NO EVENT SHALL 8Kdata BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF 8Kdata HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 8Kdata SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND 8Kdata HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */

package com.eightkdata.pgfebe.fe.api;

import com.eightkdata.pgfebe.common.FeBeMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Callback interface to allow clients to process individual messages.
 */
public abstract class MessageListener<T extends FeBeMessage> {

    private final Class<T> type;

    protected MessageListener(Class<T> type) {
        this.type = checkNotNull(type, "type");
    }

    /**
     * Does this listener understand {@code message}.
     * @param message the message to test.
     * @return {@code true} if the message can be understood, {@code false} otherwise.
     */
    public final boolean accepts(FeBeMessage message) {
        return message != null && type.isAssignableFrom(message.getClass());
    }

    /**
     * Called when a new message is available for processing.
     *
     * This will be called during pipeline processing and so should return promptly.
     * In particular, if a large amount of work needs to be done, or any blocking calls made,
     * then this methid should hand the work off to a different thread for processing.
     */
    public abstract void onMessage(T message) throws Exception;
}
