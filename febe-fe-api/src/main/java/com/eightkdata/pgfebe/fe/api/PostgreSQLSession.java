/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.api;

import com.eightkdata.pgfebe.common.message.StartupMessage;
import io.netty.channel.Channel;

/**
 * Created: 30/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class PostgreSQLSession {
    private final Channel channel;

    public PostgreSQLSession(Channel channel) {
        this.channel = channel;
    }

    public void sendStartupMessage() {
        StartupMessage.Builder builder = new StartupMessage.Builder("aht", "template1");
        channel.writeAndFlush(builder.build());
    }
}
