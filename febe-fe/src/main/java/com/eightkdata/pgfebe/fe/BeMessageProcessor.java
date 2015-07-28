/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe;

import com.eightkdata.pgfebe.common.FeBeMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class BeMessageProcessor extends MessageToMessageDecoder<FeBeMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, FeBeMessage msg, List<Object> out)
    throws Exception {
        System.out.println(msg);
    }
}
