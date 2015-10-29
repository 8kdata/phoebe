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

public class TryTest {

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    final Exception e = new Exception("Failure");

    @Test
    public void leftReturnsCorrectValue() {
        Try<Integer, Exception> t = Try.success(42);
        assertThat(t.getSuccess(), is(42));
        assertThat(t.getFailure(), is(nullValue()));
        assertThat(t.isSuccess(), is(true));
        assertThat(t.isFailure(), is(false));
    }

    @Test
    public void rightReturnsCorrectValue() {
        Try<Integer, Exception> t = Try.failure(e);
        assertThat(t.getSuccess(), is(nullValue()));
        assertThat(t.getFailure(), is(e));
        assertThat(t.isSuccess(), is(false));
        assertThat(t.isFailure(), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void leftValueCannotBeNull() {
        Try.<Integer, Exception>success(null);
    }

    @Test(expected = NullPointerException.class)
    public void rightValueCannotBeNull() {
        Try.<Integer, Exception>failure(null);
    }

    @Test
    public void equalBehavesAsExpected() {
        Try s = Try.success(42);
        Try f = Try.failure(e);
        assertThat(s, is(not(f)));
    }

    @Test
    public void hashCodeDoesNotCollide() {
        assertThat(Try.success(42).hashCode(), is(not(Try.failure(e).hashCode())));
    }
}
