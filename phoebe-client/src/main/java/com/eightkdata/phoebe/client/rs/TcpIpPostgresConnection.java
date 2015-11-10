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


package com.eightkdata.phoebe.client.rs;

import com.eightkdata.phoebe.client.FlowHandler;
import com.eightkdata.phoebe.client.decoder.BeMessageDecoder;
import com.eightkdata.phoebe.client.decoder.BeMessageProcessor;
import com.eightkdata.phoebe.client.encoder.FeMessageEncoder;
import com.eightkdata.phoebe.client.encoder.FeMessageProcessor;
import com.eightkdata.phoebe.common.message.MessageHeaderEncoder;
import com.eightkdata.phoebe.common.util.Charsets;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

/**
 *
 */
public class TcpIpPostgresConnection implements PostgresConnection {

    private final Channel channel;

    public TcpIpPostgresConnection(
            @Nonnull EventLoopGroup eventLoopGroup, @Nonnull InetAddress host, @Nonnegative int port,
            long timeout, final TimeUnit unit
    ) throws FailedConnectionException {

        ChannelFuture channelFuture = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelHandlerInitializer())
                .connect(host, port)
        ;

        try {
            if(! channelFuture.await(timeout, unit))
                throw new FailedConnectionException("Connection timeout (timeout=" + timeout + " " + unit + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FailedConnectionException("Connection process was interrupted", e);
        }

        if(! channelFuture.isSuccess()) {
            throw new FailedConnectionException("Could not connect to " + host, channelFuture.cause());
        }

        channel = channelFuture.channel();
        assert channel != null;
    }

    private static final class ChannelHandlerInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            Deque<FlowHandler> handlers = new LinkedBlockingDeque<FlowHandler>();
            Charset encoding = Charsets.UTF_8.getCharset();

            MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder(channel.alloc());

            channel.pipeline()
                    .addLast("PGInboundMessageDecoder", new BeMessageDecoder(encoding))
                    .addLast("PGInboundMessageProcessor", new BeMessageProcessor(handlers, encoding))

                    .addLast("PGOutboundMessageEncoder", new FeMessageEncoder(messageHeaderEncoder))
                    .addLast("PGOutboundMessageProcessor", new FeMessageProcessor());
        }
    }

    @Override
    public void close() {
        if(null != channel && channel.isActive()) {
            channel.close();
        }
    }

    public InetSocketAddress remoteAddress() {
        checkState(null != channel, "Connection was not successful!");
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public String host() {
        return remoteAddress().getHostName();
    }

    public int remotePort() {
        return remoteAddress().getPort();
    }

    public int localPort() {
        return ((InetSocketAddress) channel.localAddress()).getPort();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ :" + localPort() + " -> " + host() + ":" + remotePort() + " }";
    }

}
