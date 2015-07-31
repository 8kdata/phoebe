// Copyright © 2015, 8Kdata Technologies, S.L.

package com.eightkdata.pgfebe.fe.api;

import com.eightkdata.pgfebe.common.message.StartupMessage;
import io.netty.channel.Channel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A session represents a useable connection to the database, it is used to send and receive messages.
 */
public class PGSession {
    private final Channel channel;

    public PGSession(Channel channel) {
        this.channel = checkNotNull(channel, "channel");
    }

    /**
     * Send the start message to the server.
     */
    public void start(String user, String database) {
        StartupMessage message = new StartupMessage.Builder(user, database).build();
        channel.writeAndFlush(message);
    }

}
