/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import com.eightkdata.pgfebe.common.exception.FeBeException;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public interface MessageDecoder<T extends FeBeMessage> {
    /**
     * Decodes a febe message from the contents of a ByteBuffer.
     * MessageDecoder implementations must be stateless, working only on byteBuffer argument and method local variables.
     * @param byteBuffer the buffer from where to decode the bytes
     * @return a message T, decoded
     * @throws FeBeException if the remaining bytes in the ByteBuffer do not match the expected message format
     */
    T decode(@Nonnull ByteBuffer byteBuffer) throws FeBeException;
}
