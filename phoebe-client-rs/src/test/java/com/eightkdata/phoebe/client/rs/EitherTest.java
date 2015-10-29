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


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EitherTest {

    private final Integer left = 42;
    private final String right = "Right";

    @Test
    public void testConstructorAndGetters() {
        testLeftRight(
                new Either<Integer, String>(left, null),
                new Either<Integer, String>(null, right)
        );
    }

    @Test
    public void testStaticCreatorsAndGetters() {
        testLeftRight(
                Either.<Integer,String>left(left),
                Either.<Integer,String>right(right)
        );
    }

    private <T,U> void testLeftRight(Either<T,U> leftEither, Either<T,U> rightEither) {
        assertEquals(left, leftEither.getLeft());
        assertEquals(null, leftEither.getRight());
        assertTrue(leftEither.isLeft());
        assertTrue(! leftEither.isRight());

        assertEquals(right, rightEither.getRight());
        assertEquals(null, rightEither.getLeft());
        assertTrue(! rightEither.isLeft());
        assertTrue(rightEither.isRight());
    }

    @Test
    public void testIllegalArgumentException() {
        int nExceptions = 0;
        try {
            new Either<Integer,String>(left, right);
        } catch (IllegalArgumentException e) {
            nExceptions++;
        }
        try {
            new Either<Integer,String>(null, null);
        } catch (IllegalArgumentException e) {
            nExceptions++;
        }

        assertEquals("Wrong constructor arguments should throw IllegalArgumentExceptions", 2, nExceptions);
    }

}
