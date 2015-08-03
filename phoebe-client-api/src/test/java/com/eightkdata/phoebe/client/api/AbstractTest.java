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

package com.eightkdata.phoebe.client.api;

import net.jodah.concurrentunit.Waiter;
import org.junit.Before;
import org.junit.BeforeClass;

import java.nio.charset.Charset;
import java.util.Properties;

public class AbstractTest {

    static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Add a file named {@code local.properties} to override the defaults for your database connection.
     */
    static Properties props;

    PGClient client;
    PGSession session;
    Waiter waiter;


    @BeforeClass
    public static void setUpClass() throws Exception {
        props = new Properties();
        props.load(AbstractTest.class.getResourceAsStream("/default.properties"));
        props.load(AbstractTest.class.getResourceAsStream("/local.properties"));
    }

    @Before
    public void setUp() throws Exception {
        client = new PGClient(
                props.getProperty("db.host"),
                Integer.parseInt(props.getProperty("db.port")));
        waiter = new Waiter();
        session = client.connect();
    }

}
