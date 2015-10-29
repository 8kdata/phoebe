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

import com.eightkdata.phoebe.common.PostgresEncoding;
import com.eightkdata.phoebe.common.SessionParameters;
import com.eightkdata.phoebe.common.message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The startup message that is sent to establish a new connection to a PostgreSQL server.
 * StartupMessages are always sent in SQL_ASCII encoding, as the database server may not have initialized yet the
 * {@code client_encoding}.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public class StartupMessage extends AbstractSetKeyValueMessage {

    /**
     * The {@code StartupMessage} sends the server the desired client's encoding.
     * But before the server has set this parameter's value, it needs to exchange message(s) with a given encoding.
     * The most sensible value is {@code UTF-8}, as it is the only encoding which the server can convert to/from
     * any other encoding.
     */
    public static final Charset FIXED_CHARSET = PostgresEncoding.UTF8.getCharset();

    /**
     * Unless otherwise specified, default client encoding is {@code UTF-8}.
     */
    public static final PostgresEncoding DEFAULT_ENCODING = PostgresEncoding.UTF8;

    /**
     * Unless otherwise specified, default lc_messages is {@code C}, to guarantee messages are correctly encoded
     * in the event of a server error when {@code client_encoding} is not set yet
     * (see
     *      <a href="http://www.postgresql.org/message-id/4678.1438350389@sss.pgh.pa.us">
     *          Re: Encoding of early PG messages
     *      </a>
     * )
     */
    public static final String DEFAULT_LC_MESSAGES = "C";

    public StartupMessage(@Nonnull ByteBuf byteBuf, @Nonnull Charset charset) {
        super(byteBuf, charset);
    }

    static StartupMessage encode(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnull Charset charset,
            @Nonnull SessionParameters sessionParameters
    ) {
        // Validate input arguments
        checkNotNull(sessionParameters, "sessionParameters");
        checkNotNull(sessionParameters.getParameter(SessionParameters.USER), "sessionParameters.user");

        Map<String,String> parameters = sessionParameters.parametersMap();
        if(! sessionParameters.parameterIsSet(SessionParameters.DATABASE)) {
            parameters.put(SessionParameters.DATABASE, sessionParameters.getParameter(SessionParameters.USER));
        }
        if(! sessionParameters.parameterIsSet(SessionParameters.CLIENT_ENCODING)) {
            parameters.put(SessionParameters.CLIENT_ENCODING, DEFAULT_ENCODING.name());
        }
        if(! sessionParameters.parameterIsSet(SessionParameters.LC_MESSAGES)) {
            parameters.put(SessionParameters.LC_MESSAGES, DEFAULT_LC_MESSAGES);
        }

        return new StartupMessage(
                encodeToByteBuf(byteBufAllocator, FIXED_CHARSET, parameters),
                FIXED_CHARSET
        );
    }

    @Override @Nonnull
    public MessageType getType() {
        return MessageType.StartupMessage;
    }

}
