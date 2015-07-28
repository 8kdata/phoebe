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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created: 26/06/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class OutboundMessageHandler extends MessageToByteEncoder<Object> {
    private final short POSTGRESQL_PROTOCOL_MAJOR = 3;
    private final short POSTGRESQL_PROTOCOL_MINOR = 0;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(POSTGRESQL_PROTOCOL_MAJOR);
        byteBuf.writeShort(POSTGRESQL_PROTOCOL_MINOR);

    // Validate possible startup params
        StringBuffer stringBuffer = new StringBuffer();
        writeParam(stringBuffer, "user", "aht");

        byteBuf.writeBytes(stringBuffer.toString().getBytes());

        byteBuf.writeByte(0);
    }

    private void writeParam(StringBuffer stringBuffer, String key, String value) {
        stringBuffer
                .append(key)
                .append("\0")
                .append(value)
                .append("\0")
        ;
    }
}
