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

package com.eightkdata.phoebe.common.util;


import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A value which represents the result of an operation.
 *
 * This is a (Java 8 style) value-based class; use of identity-sensitive operations
 * (including reference equality (==), identity hash code, or synchronization) on
 * instances of Optional may have unpredictable results and should be avoided.
 */
@Immutable
public final class Try<S, T extends Throwable> {

    @SuppressWarnings("unchecked")
    public static <S, T extends Throwable> Try<S, T> success(@Nonnull S result) {
        return (Try<S, T>) new Try(checkNotNull(result, "result"), false);
    }

    @SuppressWarnings("unchecked")
    public static <S, T extends Throwable> Try<S, T> failure(@Nonnull T cause) {
        return (Try<S, T>) new Try(checkNotNull(cause, "cause"), true);
    }

    private final Object value;
    private final boolean failed;

    private Try(Object value, boolean failed) {
        this.value = value;
        this.failed = failed;
    }

    public boolean isSuccess() {
        return !failed;
    }

    public boolean isFailure() {
        return failed;
    }

    @SuppressWarnings("unchecked")
    public S getSuccess() {
        return failed ? null : (S) value;
    }

    @SuppressWarnings("unchecked")
    public T getFailure() {
        return failed ? (T) value : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Try)) { return false; }
        Try that = (Try) obj;
        return this.failed == that.failed && this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * (failed ? 1 : 17);
    }

    @Override
    public String toString() {
        return (failed ? "Failure(" : "Success(") + value + ")";
    }
}
