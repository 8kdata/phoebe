// Copyright © 2015, 8Kdata Technologies, S.L.

package com.eightkdata.pgfebe.fe.api;

import com.eightkdata.pgfebe.common.exception.FeBeException;
import com.eightkdata.pgfebe.common.exception.FeBeExceptionType;
import com.eightkdata.pgfebe.fe.decoder.BeMessageDecoder;
import com.eightkdata.pgfebe.fe.decoder.BeMessageProcessor;
import com.eightkdata.pgfebe.fe.encoder.FeMessageEncoder;
import com.eightkdata.pgfebe.fe.encoder.FeMessageProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A client represents a server configuration and manages any shared resources for the server.
 */
@NotThreadSafe
public class PGClient {
    private static final long DEFAULT_CONNECT_TIMEOUT = 30;
    private static final TimeUnit DEFAULT_CONNECT_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

    private final String host;
    private final int port;

    public PGClient(@Nonnull String host, int port) {
        checkArgument(!isNullOrEmpty(host), "host cannot be null or empty");
        checkArgument(port > 0, "port cannot be less than 1");
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
                    throw new FeBeException(FeBeExceptionType.CONNECT_EXCEPTION, "Unable to establish TCP connection");
                }
            } finally {
                connectLatch.countDown();
            }
        }
    }

    public PGSession connect(long timeout, TimeUnit timeUnit) throws FeBeException {
        checkArgument(timeout > 0, "timeout cannot be less than 1");
        checkNotNull(timeUnit, "timeUnit");

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
                return new PGSession(channelFuture.channel());
            }
        } catch (InterruptedException e) {
            return null;
        }

        return null;
    }

    public PGSession connect() throws FeBeException {
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
