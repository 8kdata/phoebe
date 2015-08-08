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

import com.eightkdata.phoebe.common.BaseMessage;
import com.eightkdata.phoebe.common.Encoders;
import com.eightkdata.phoebe.common.FeBeMessageType;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightkdata.phoebe.common.util.Preconditions.checkTextNotNullNotEmpty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * The startup message that is sent to establish a new connection to a PostgreSQL server.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public class StartupMessage extends BaseMessage {
    /**
     * A map of Java character set names to Postgres names.
     */
    private static final Map<String, String> pgCharsetNames = new HashMap<String, String>();
    static {
        pgCharsetNames.put("US-ASCII", "SQL_ASCII");
        pgCharsetNames.put("ISO-8859-1", "LATIN1");
        pgCharsetNames.put("ISO-8859-15", "LATIN15");
        pgCharsetNames.put("UTF-8", "UTF8");
    }

    /**
     * Get a builder suitable for creating new startup messages.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    private final Map<String,String> parameters;

    StartupMessage(@Nonnull Map<String,String> parameters) {
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.StartupMessage;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = Byte.SIZE / 8;     // end '0' byte
        for (Map.Entry<String,String> entry : parameters.entrySet()) {
            length += Encoders.stringLength(entry.getKey(), encoding);
            length += Encoders.stringLength(entry.getValue(), encoding);
        }

        return length;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            toStringHelper.add(param.getKey(), param.getValue());
        }
    }

    public static class Builder implements MessageBuilder<StartupMessage> {
        private final Map<String,String> parameters = new LinkedHashMap<String, String>();

        public Builder user(@Nonnull String user) {
            return parameter("user", user);
        }

        public Builder database(@Nonnull String database) {
            return parameter("database", database);
        }

        public Builder clientEncoding(@Nonnull Charset encoding) {
            // todo: startup encoding seems to be a bit of a mess
            // what I have here and in MD5 works for the cases that I have tested but it probably
            // isn't a comprehensive solution. Some discussion about this issue on the lists here
            // http://www.postgresql.org/message-id/20081223212414.GD3894@merkur.hilbert.loc

            String pgCharsetName = pgCharsetNames.get(encoding.name());
            checkArgument(pgCharsetName != null, "unsupported client encoding: %s", encoding);

            return parameter("client_encoding", pgCharsetName);
        }

        public Builder parameter(@Nonnull String name, @Nonnull String value) {
            checkTextNotNullNotEmpty(name, name);
            checkTextNotNullNotEmpty(value, value);
            parameters.put(name, value);

            return this;
        }

        @Override
        public StartupMessage build() {
            checkState(parameters.get("user") != null, "no user specified");
            checkState(parameters.get("client_encoding") != null, "no encoding specified");

            return new StartupMessage(parameters);
        }
    }
}
