/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
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
