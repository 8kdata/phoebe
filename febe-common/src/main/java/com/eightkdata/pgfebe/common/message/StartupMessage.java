/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.BaseFeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.encoder.EncoderUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 29/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class StartupMessage extends BaseFeBeMessage {
    public static final Charset MESSAGE_ENCODING = Charsets.US_ASCII;

    private static final String USER_PARAMETER_NAME = "user";
    private static final String DATABASE_PARAMETER_NAME = "database";
    private static final String CLIENT_ENCODING_PARAMETER_NAME = "client_encoding";

    private final Map<String,String> parameters;

    public StartupMessage(@Nonnull Map<String,String> parameters) {
        super(FeBeMessageType.StartupMessage);

        Preconditions.checkNotNull(parameters);
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public interface ParametersIterator {
        void doWithParameter(@Nonnull final String name, @Nonnull final String value);
    }

    public void iterateParameters(ParametersIterator iterator) {
        for(Map.Entry<String,String> entry : parameters.entrySet()) {
            iterator.doWithParameter(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int computePayloadLength() {
        int length = 1;     // end '0' byte
        for(Map.Entry<String,String> entry : parameters.entrySet()) {
            length += EncoderUtils.computeCStringLength(entry.getKey(), MESSAGE_ENCODING);
            length += EncoderUtils.computeCStringLength(entry.getValue(), MESSAGE_ENCODING);
        }

        return length;
    }

    private static class ToStringMessagePayloadParamsIterator implements ParametersIterator {
        private final StringBuilder sb;

        public ToStringMessagePayloadParamsIterator(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void doWithParameter(final @Nonnull String name, final @Nonnull String value) {
            sb.append(name).append("=").append(value).append(" ");
        }
    }

    @Nonnull
    @Override
    protected StringBuilder toStringMessagePayload(@Nonnull StringBuilder sb, @Nonnull String separator) {
        sb.append(separator);
        sb.append("params={").append(" ");

        iterateParameters(new ToStringMessagePayloadParamsIterator(sb));

        sb.append("}");

        return sb;
    }

    private static final Map<Charset,String> java2PostgresEncodingNames = new HashMap<Charset, String>();
    static {
        java2PostgresEncodingNames.put(Charsets.UTF_8, "UTF8");
    }

    public static class Builder implements MessageBuilder<StartupMessage> {
        private final Map<String,String> parameters = new HashMap<String, String>();

        public Builder(@Nonnull String user, @Nonnull String database, @Nullable Charset clientEncoding) {
            addStartupParameter(USER_PARAMETER_NAME, user);
            addStartupParameter(DATABASE_PARAMETER_NAME, database);

            if(null != clientEncoding) {
                String postgresEncoding = java2PostgresEncodingNames.get(clientEncoding);
                if(null == postgresEncoding) {
                    throw new IllegalArgumentException("Encoding " + clientEncoding + " not supported by the driver");
                }
                addStartupParameter(CLIENT_ENCODING_PARAMETER_NAME, postgresEncoding);
            }
        }

        public Builder(@Nonnull String user, @Nonnull String database) {
            this(user, database, null);
        }

        public Builder(@Nonnull String user, @Nullable Charset clientEncoding) {
            this(user, user, clientEncoding);
        }

        public Builder(@Nonnull String user) {
            this(user, user, null);
        }

        public Builder addStartupParameter(@Nonnull String name, @Nonnull String value) {
            Preconditions.checkArgument(null != name && ! name.isEmpty());
            Preconditions.checkArgument(null != value && ! value.isEmpty());

            parameters.put(name, value);

            return this;
        }

        @Override
        public StartupMessage build() {
            return new StartupMessage(parameters);
        }
    }
}
