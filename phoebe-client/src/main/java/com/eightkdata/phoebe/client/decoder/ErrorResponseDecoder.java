package com.eightkdata.phoebe.client.decoder;

import com.eightkdata.phoebe.common.Decoders;
import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.message.ErrorResponse;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Decoder for {@link ErrorResponse} messages.
 */
public class ErrorResponseDecoder implements Message.Decoder<ErrorResponse> {

    @Override
    public ErrorResponse decode(@Nonnull ByteBuf in, @Nonnull Charset encoding) {
        // use a LinkedHashMap to retain the ordering
        Map<Byte, String> fields = new LinkedHashMap<Byte, String>();
        byte code;
        while ((code = in.readByte()) != 0) {
            fields.put(code, Decoders.readString(in, encoding));
        }
        return new ErrorResponse(fields);
    }

}
