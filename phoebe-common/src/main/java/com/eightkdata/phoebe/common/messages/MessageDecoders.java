/*
 * Copyright © 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for any purpose, without fee, and without
 * a written agreement is hereby granted, provided that the above
 * copyright notice and this paragraph and the following two
 * paragraphs appear in all copies.
 *
 * In no event shall 8Kdata Technology S.L. be liable to any party
 * for direct, indirect, special, incidental, or consequential
 * damages, including lost profits, arising out of the use of this
 * software and its documentation, even if 8Kdata Technology S.L.
 * has been advised of the possibility of such damage.
 *
 * 8Kdata Technology S.L. specifically disclaims any warranties,
 * including, but not limited to, the implied warranties of
 * merchantability and fitness for a particular purpose. the
 * software provided hereunder is on an “as is” basis, and
 * 8Kdata Technology S.L. has no obligations to provide
 * maintenance, support, updates, enhancements, or modifications.
 */


package com.eightkdata.phoebe.common.messages;

import com.eightkdata.phoebe.common.message.Message;
import com.eightkdata.phoebe.common.message.MessageType;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

/**
 *
 */
public class MessageDecoders {

    public static Message decode(@Nonnull MessageType messageType, @Nonnull ByteBuf byteBuf, @Nonnull Charset charset) {
        switch (messageType) {
            case AuthenticationMD5Password:     return new AuthenticationMD5Password(byteBuf);
            case PasswordMessage:               return new PasswordMessage(byteBuf, charset);
            case StartupMessage:                return new StartupMessage(byteBuf, charset);
            case ParameterStatus:               return new ParameterStatus(byteBuf, charset);
            case BackendKeyData:                return new BackendKeyData(byteBuf);
            case ReadyForQuery:                 return new ReadyForQuery(byteBuf);

            default:    throw new UnsupportedOperationException("No decoder for message type " + messageType);
        }
    }

}
