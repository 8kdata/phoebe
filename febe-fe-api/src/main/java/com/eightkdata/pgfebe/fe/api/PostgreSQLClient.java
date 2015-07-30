/*
 * Copyright (c) 2015, 8Kdata Technology S.L.
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

import com.eightkdata.pgfebe.common.exception.FeBeException;
import com.eightkdata.pgfebe.common.exception.FeBeExceptionType;
import com.eightkdata.pgfebe.fe.decoder.BeMessageDecoder;
import com.eightkdata.pgfebe.fe.decoder.BeMessageProcessor;
import com.eightkdata.pgfebe.fe.encoder.FeMessageEncoder;
import com.eightkdata.pgfebe.fe.encoder.FeMessageProcessor;
import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created: 26/06/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@NotThreadSafe
public class PostgreSQLClient {
    private static final long DEFAULT_CONNECT_TIMEOUT = 30;
    private static final TimeUnit DEFAULT_CONNECT_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

    private final String host;
    private final int port;

    public PostgreSQLClient(@Nonnull String host, int port) {
        Preconditions.checkNotNull(host);
        Preconditions.checkArgument(port > 0);

        this.host = host;
        this.port = port;
    }

    private static final class LatchChannelFutureListener implements ChannelFutureListener {
        private final CountDownLatch connectLatch;

        public LatchChannelFutureListener(CountDownLatch connectLatch) {
            this.connectLatch = connectLatch;
        }

        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            try {
                if (! channelFuture.isSuccess()) {
                    throw new FeBeException(
                            FeBeExceptionType.CONNECT_EXCEPTION, "Unable to establish TCP connection"
                    );
                }
            } finally {
                connectLatch.countDown();
            }
        }
    }

    public PostgreSQLSession connect(long timeout, TimeUnit timeUnit)
    throws FeBeException {
        Preconditions.checkArgument(timeout > 0);
        Preconditions.checkNotNull(timeUnit);

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelHandlerInitializer())
        ;

        CountDownLatch connectLatch = new CountDownLatch(1);

        ChannelFuture channelFuture = bootstrap
                .connect(host, port)
                .addListener(new LatchChannelFutureListener(connectLatch))
        ;

        try {
            if(connectLatch.await(timeout, timeUnit)) {
                return new PostgreSQLSession(channelFuture.channel());
            }
        } catch (InterruptedException e) {
            return null;
        }

        return null;
    }

    public PostgreSQLSession connect() throws FeBeException {
        return connect(DEFAULT_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT_TIMEUNIT);
    }

    private static class ClientChannelHandlerInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline()

                    // inbound
                    .addLast(
                            new BeMessageDecoder(),
                            new BeMessageProcessor()
                    )

                    // outbound
                    .addLast(
                            new FeMessageEncoder(),
                            new FeMessageProcessor()
                    )
            ;
        }
    }
}
