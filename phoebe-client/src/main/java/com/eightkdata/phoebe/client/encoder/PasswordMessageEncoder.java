package com.eightkdata.phoebe.client.encoder;

import com.eightkdata.phoebe.common.Encoders;
import com.eightkdata.phoebe.common.MessageEncoder;
import com.eightkdata.phoebe.common.message.PasswordMessage;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

/**
 * Encoder for {@link PasswordMessage}s.
 */
public class PasswordMessageEncoder implements MessageEncoder<PasswordMessage> {

    @Override
    public void encode(@Nonnull PasswordMessage message, @Nonnull ByteBuf out, @Nonnull Charset encoding) {
        Encoders.writeString(out, message.getPassword(), encoding);
    }

}
