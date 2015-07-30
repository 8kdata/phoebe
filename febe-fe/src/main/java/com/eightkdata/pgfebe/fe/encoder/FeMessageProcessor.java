/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.encoder;

import com.eightkdata.pgfebe.common.FeBeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created: 30/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class FeMessageProcessor extends MessageToMessageEncoder<FeBeMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, FeBeMessage msg, List<Object> out) throws Exception {
        System.out.println(msg);
        out.add(msg);
    }
}
