/*
 * Copyright (c) 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software and its documentation for any purpose,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice and this
 * paragraph and the following two paragraphs appear in all copies.
 *
 * IN NO EVENT SHALL 8Kdata BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF 8Kdata HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 8Kdata SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND 8Kdata HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */


package com.eightkdata.phoebe.client.rs;


import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p>Holds only one instance out of two possible parametrized types.
 *
 * <p>The class is marked as @NotThreadSafe as the instance stored may have methods to mutate it,
 * but the references held by this class are themselves immutable.
 */
@NotThreadSafe
public class Either<T,U> {

    @Nullable final T t;
    @Nullable final U u;

    public Either(@Nullable T t, @Nullable U u) {
        checkArgument((null == t) ^ (null == u), "One arg must be null, the other not null");

        this.t = t;
        this.u = u;
    }

    public static <T,U> Either<T,U> left(T left) {
        return new Either<T, U>(left, null);
    }

    public static <T,U> Either<T,U> right(U right) {
        return new Either<T, U>(null, right);
    }

    public boolean isLeft() {
        return t != null;
    }

    public boolean isRight() {
        return u != null;
    }

    @Nullable public T getLeft() {
        return t;
    }

    @Nullable public U getRight() {
        return u;
    }

}
