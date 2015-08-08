/*
 * Copyright © 2015, 8Kdata Technologies, S.L.
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


package com.eightkdata.phoebe.common.message;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static methods for working with MD5 passwords.
 */
class MD5 {

    private static final Charset ASCII = Charset.forName("US-ASCII");

    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    private static String toHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    /**
     * Encode a suitable MD5 password response.
     *
     * This generates the equivalent to the SQL {@code concat('md5', md5(concat(md5(concat(password, username)), salt)))}.
     *
     * @param username the username to respond with
     * @param password the password to respond with
     * @param salt the salt to respond with
     * @return the response string, suitable for using with a PasswordMessage
     */
    public static String encode(String username, String password, byte[] salt, Charset encoding) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            md5.update(password.getBytes(encoding));
            md5.update(username.getBytes(encoding));
            byte[] userpass = md5.digest();

            md5.update(toHexBinary(userpass).getBytes(ASCII));
            md5.update(salt);
            return "md5" + toHexBinary(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("could not load MD5 message digest", e);
        }

    }

    private MD5() {}

}
