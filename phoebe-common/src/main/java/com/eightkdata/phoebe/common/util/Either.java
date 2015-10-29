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
 * A value which can be one of two possible types.
 *
 * This is a (Java 8 style) value-based class; use of identity-sensitive operations
 * (including reference equality (==), identity hash code, or synchronization) on
 * instances of Optional may have unpredictable results and should be avoided.
 */
@Immutable
public final class Either<Left, Right> {

    @SuppressWarnings("unchecked")
    public static <Left, Right> Either<Left, Right> left(@Nonnull Left left) {
        return (Either<Left, Right>) new Either(checkNotNull(left, "left"), false);
    }

    @SuppressWarnings("unchecked")
    public static <Left, Right> Either<Left, Right> right(@Nonnull Right right) {
        return (Either<Left, Right>) new Either(checkNotNull(right, "right"), true);
    }

    private final Object value;
    private final boolean right;

    private Either(Object value, boolean right) {
        this.value = value;
        this.right = right;
    }

    public boolean isLeft() {
        return !right;
    }

    public boolean isRight() {
        return right;
    }

    @SuppressWarnings("unchecked")
    public Left getLeft() {
        return right ? null : (Left) value;
    }

    @SuppressWarnings("unchecked")
    public Right getRight() {
        return right ? (Right) value : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Either)) { return false; }
        Either that = (Either) obj;
        return this.right == that.right && this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * (right ? 1 : 17);
    }

    @Override
    public String toString() {
        return (right ? "Right(" : "Left(") + value + ")";
    }
}
