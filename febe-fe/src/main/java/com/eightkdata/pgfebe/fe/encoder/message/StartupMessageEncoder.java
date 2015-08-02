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


package com.eightkdata.pgfebe.fe.encoder.message;

import com.eightkdata.pgfebe.common.encoder.MessageEncoder;
import com.eightkdata.pgfebe.common.message.StartupMessage;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import static com.eightkdata.pgfebe.common.encoder.EncoderUtils.encodeToCString;

/**
 * Encoder for {@link StartupMessage}s.
 */
@Immutable
public class StartupMessageEncoder implements MessageEncoder<StartupMessage> {

    @Override
    public void encode(@Nonnull StartupMessage message, @Nonnull final ByteBuffer byteBuffer) {
        Charset encoding = message.getEncoding();
        for (Map.Entry<String, String> param : message.getParameters().entrySet()) {
            encodeToCString(param.getKey(), byteBuffer, encoding);
            encodeToCString(param.getValue(), byteBuffer, encoding);

        }
        byteBuffer.put((byte) 0);
    }

}
