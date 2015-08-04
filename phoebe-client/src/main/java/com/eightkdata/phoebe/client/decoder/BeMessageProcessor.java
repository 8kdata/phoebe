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


package com.eightkdata.phoebe.client.decoder;

import com.eightkdata.phoebe.client.FlowHandler;
import com.eightkdata.phoebe.common.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.Deque;
import java.util.List;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class BeMessageProcessor extends MessageToMessageDecoder<Message> {

    private final Charset encoding;

    private final Deque<FlowHandler> handlers;

    public BeMessageProcessor(Deque<FlowHandler> handlers, Charset encoding) {
        this.handlers = handlers;
        this.encoding = encoding;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Message message, List<Object> out) throws Exception {
        FlowHandler handler = handlers.peekFirst();
        if (handler != null && handler.isHandled(message.getType())) {
            if (handler.handle(ctx.channel(), message, encoding)) {
                out.add(message);
            }
        } else {
            // always propagate the message if it is not handled
            out.add(message);
        }
        if (handler != null && handler.isComplete(message.getType())) {
            handlers.removeFirst();
        }

    }

}
