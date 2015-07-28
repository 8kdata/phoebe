/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.exception;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class FeBeException extends Exception {
    private final FeBeExceptionType exceptionType;

    public FeBeException(FeBeExceptionType exceptionType, String message) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public FeBeException(FeBeExceptionType exceptionType, String message, Throwable cause) {
        super(message, cause);
        this.exceptionType = exceptionType;
    }

    public FeBeExceptionType getExceptionType() {
        return exceptionType;
    }
}
