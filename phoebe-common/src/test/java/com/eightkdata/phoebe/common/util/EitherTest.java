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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EitherTest {

    @Test
    public void leftReturnsCorrectValue() {
        Either<Integer, String> either = Either.left(42);
        assertThat(either.getLeft(), is(42));
        assertThat(either.getRight(), is(nullValue()));
        assertThat(either.isLeft(), is(true));
        assertThat(either.isRight(), is(false));
    }

    @Test
    public void rightReturnsCorrectValue() {
        Either<Integer, String> either = Either.right("foo");
        assertThat(either.getLeft(), is(nullValue()));
        assertThat(either.getRight(), is("foo"));
        assertThat(either.isLeft(), is(false));
        assertThat(either.isRight(), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void leftValueCannotBeNull() {
        Either.<Integer,String>left(null);
    }

    @Test(expected = NullPointerException.class)
    public void rightValueCannotBeNull() {
        Either.<Integer,String>right(null);
    }

    @Test
    public void equalBehavesAsExpected() {
        Either leftInt = Either.left(42);
        Either leftString = Either.left("foo");
        Either rightInt = Either.right(42);
        Either rightString = Either.right("foo");

        assertThat(leftInt, is(not(rightInt)));
        assertThat(leftString, is(not(rightString)));
    }

    @Test
    public void hashCodeDoesNotCollide() {
        assertThat(Either.left(42).hashCode(), is(not(Either.right(42).hashCode())));
    }
}
