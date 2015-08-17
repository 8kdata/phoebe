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


package com.eightkdata.phoebe.common.message;

import com.eightkdata.phoebe.common.*;
import com.eightkdata.phoebe.common.util.ByteSize;
import com.eightkdata.phoebe.common.util.KeyValueIterator;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.HashMap;
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
public class StartupMessage extends BaseMessage {
    /**
     * Regardless of the user-requested encoding, the StartupMessage should always be encoded in UTF8.
     * The server knows how to encode/decode from any server encoding, so this is the way to go.
     */
    public static final Charset FIXED_CHARSET = PGEncoding.UTF8.getCharset();

    private final Map<String,String> parameters;

    /**
     * <p>Constructs a StartupMessage.
     * The parameters need at lest to contain compulsory parameters {@code user}.
     *
     * <p>If not set:
     * <ul>
     *     <li>{@code database}: defaults to the {@code user}</li>
     *     <li>{@code client_encoding}: defaults to UTF-8</li>
     *     <li>{@code lc_messages}: defauls to C, to guarantee messages are correctly encoded
     *     in the event of a server error when {@code client_encoding} is not set yet
     *     (see <a href="http://www.postgresql.org/message-id/4678.1438350389@sss.pgh.pa.us">
     *         Re: Encoding of early PG messages
     *         </a>
     *     )</li>
     * </ul>
     *
     * @param parameters the session parameters
     */
    public StartupMessage(@Nonnull SessionParameters parameters) {
        checkNotNull(parameters);
        checkNotNull(parameters.getParameter(SessionParameters.USER));
        this.parameters = new HashMap<String, String>(parameters.numberParameters());
        parameters.copyTo(this.parameters);

        if(! parameters.parameterIsSet(SessionParameters.DATABASE)) {
            this.parameters.put(SessionParameters.DATABASE, parameters.getParameter(SessionParameters.USER));
        }
        if(! parameters.parameterIsSet(SessionParameters.CLIENT_ENCODING)) {
            this.parameters.put(SessionParameters.CLIENT_ENCODING, PGEncoding.UTF8.name());
        }
        if(! parameters.parameterIsSet(SessionParameters.LC_MESSAGES)) {
            this.parameters.put(SessionParameters.LC_MESSAGES, "C");
        }
    }

    @Override
    public MessageType getType() {
        return MessageType.StartupMessage;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = ByteSize.BYTE;      // ending '0' byte

        for(Map.Entry<String, String> param : parameters.entrySet()) {
            length += Encoders.stringLength(param.getKey(), FIXED_CHARSET);
            length += Encoders.stringLength(param.getValue(), FIXED_CHARSET);
        }

        return length;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            toStringHelper.add(param.getKey(), param.getValue());
        }
    }

    public void iterateParameters(KeyValueIterator keyValueIterator) {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            keyValueIterator.doWith(param.getKey(), param.getValue());
        }
    }
}
