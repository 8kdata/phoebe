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

package com.eightkdata.phoebe.client.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A client represents a server configuration and manages any shared resources for the server.
 */
public class PGClient {
    private static final long DEFAULT_CONNECT_TIMEOUT = 30;
    private static final TimeUnit DEFAULT_CONNECT_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

    private final String host;
    private final int port;
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private PGSession currentSession;

    public PGClient(@Nonnull String host, int port) {
        checkArgument(!isNullOrEmpty(host), "host cannot be null or empty");
        checkArgument(port > 0, "port cannot be less than 1");
        this.host = host;
        this.port = port;
    }

    private PGSession clearAndSetCurrentSession(@Nullable  PGSession pgSession) {
        synchronized (this) {
            if(null != currentSession)
                currentSession.close();
            this.currentSession = pgSession;
        }

        return pgSession;
    }

    /**
     * Attempts to create a new session within a specified time limit.
     *
     * @return the session, or {@code null} if the time limit is reached.
     */
    public @Nullable PGSession connect(long timeout, TimeUnit unit) throws InterruptedException {
        checkArgument(timeout > 0, "timeout cannot be less than 1");
        checkNotNull(unit, "unit");

        final AtomicReference<Channel> channelRef = new AtomicReference<Channel>();
        new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ClientChannelHandlerInitializer(channelRef))
                .connect(host, port)
                .await(timeout, unit);

        Channel channel = channelRef.get();

        return clearAndSetCurrentSession(channel != null ? new PGSession(channel) : null);
    }

    /**
     * Attempts to create a new session using the default time limit of 30 seconds.
     *
     * @return the session, or {@code null} if the time limit is reached.
     */
    public PGSession connect() throws InterruptedException {
        return connect(DEFAULT_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT_TIMEUNIT);
    }

    private static class ClientChannelHandlerInitializer extends ChannelInitializer<Channel> {
        private final AtomicReference<Channel> channelRef;

        ClientChannelHandlerInitializer(AtomicReference<Channel> channelRef) {
            this.channelRef = channelRef;
        }

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channelRef.set(channel);
        }
    }

    public void disconnect() throws InterruptedException {
        clearAndSetCurrentSession(null);
        eventLoopGroup.shutdownGracefully().sync();
    }

}
