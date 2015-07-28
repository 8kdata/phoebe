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


package com.eightkdata.pgfebe.main;

import com.eightkdata.pgfebe.fe.BeMessageProcessor;
import com.eightkdata.pgfebe.fe.decoder.BeMessageHeaderDecoder;
import com.eightkdata.pgfebe.fe.decoder.BeMessagePayloadDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created: 26/06/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class PostgreSQLClient {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private Channel channel;

    public PostgreSQLClient(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelHandlerInitializer())
        ;

        bootstrap.connect(host, port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(channelFuture.isSuccess()) {
                            System.out.println("Connect succesful");
                            channel = channelFuture.channel();
                        } else {
                            System.err.println("Connect failed");
                        }
                    }
                })
        ;

    }

    private class ClientChannelHandlerInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline()

                    // inbound
                    .addLast(
                            new BeMessageHeaderDecoder(),
                            new BeMessagePayloadDecoder(),
                            new BeMessageProcessor()
                      )

                    // outbound
                    .addLast(new LengthFieldPrepender(Integer.BYTES, true), new OutboundMessageHandler())
            ;
        }
    }

    public void sendStartupMessage() {
        channel.writeAndFlush(new Object());
    }
}
