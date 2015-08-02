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

package com.eightkdata.pgfebe.fe.api;

import com.eightkdata.pgfebe.common.FeBeMessage;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import static com.eightkdata.pgfebe.common.MessageId.AUTHENTICATION;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Unit tests for {@link }
 */
public class PGSessionTest extends AbstractTest {

    @Test
    public void testStart() throws Throwable {
        final Waiter waiter = new Waiter();

        PGSession session = client.connect();
        session.addListener(new MessageListener<FeBeMessage>(FeBeMessage.class) {
            @Override
            public void onMessage(FeBeMessage message) {
                System.out.println(">>> " + message);
                if (message.getType().getId() == AUTHENTICATION) {
                    waiter.assertTrue(true);
                    waiter.resume();
                }
            }
        });
        session.start(props.getProperty("db.user"), props.getProperty("db.name"));

        waiter.await(5, SECONDS);
    }

}
