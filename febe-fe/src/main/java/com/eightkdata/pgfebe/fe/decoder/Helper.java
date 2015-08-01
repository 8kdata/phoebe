package com.eightkdata.pgfebe.fe.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

import java.nio.charset.Charset;

/**
 * Static helper methods, needed because Java 6 does not support extension methods.
 */
final class Helper {

    static <T> T checkNotNull(T value, String name) {
        if (value == null) {
            throw new NullPointerException(name);
        }
        return value;
    }

    static String checkNotNullOrEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            throw new NullPointerException(name);
        }
        return value;
    }

    static int checkLength(int expected, int actual) {
        if (actual != expected) {
            throw new CorruptedFrameException("expected length " + expected + ", found " + actual);
        }
        return actual;
    }

    static String readString(ByteBuf in, Charset encoding) {
        String s = in.readSlice(in.bytesBefore((byte) 0)).toString(encoding);
        in.readByte(); // discard the trailing zero
        return s;
    }

    static void writeString(ByteBuf buf, Charset encoding, String s) {
        buf.writeBytes(s.getBytes(encoding)).writeByte(0);
    }

    private Helper() {}

}
