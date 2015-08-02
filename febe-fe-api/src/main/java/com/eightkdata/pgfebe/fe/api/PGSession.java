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
import com.eightkdata.pgfebe.common.message.StartupMessage;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A session represents a useable connection to the database, it is used to send and receive messages.
 */
public class PGSession {
    private final Channel channel;
    private final List<MessageListener> listeners;
    private final Charset encoding;

    public PGSession(Channel channel, List<MessageListener> listeners, Charset encoding) {
        this.listeners = listeners;
        this.encoding = encoding;
        this.channel = checkNotNull(channel, "channel");
    }

    /**
     * Send the start message to the server.
     */
    public void start(String user, String database) {
        StartupMessage message = StartupMessage.builder()
                .user(user)
                .database(database)
                .clientEncoding(encoding)
                .build();
        channel.writeAndFlush(message);
    }

    public void send(FeBeMessage message) {
        channel.writeAndFlush(message);
    }

    public void send(FeBeMessage... messages) {
        FeBeMessage lastMessage = messages[messages.length - 1];
        for (FeBeMessage message : messages) {
            if (message == lastMessage) {
                channel.writeAndFlush(message);
            } else {
                channel.write(message);
            }
        }
    }

    /**
     * Adds a listener which will be notified of any messages passing over this session.
     */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a previously added listener.
     */
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }
}
