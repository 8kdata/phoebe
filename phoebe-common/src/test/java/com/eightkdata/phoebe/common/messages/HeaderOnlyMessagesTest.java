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


package com.eightkdata.phoebe.common.messages;

import com.eightkdata.phoebe.common.message.HeaderOnlyMessages;
import com.eightkdata.phoebe.common.message.MessageType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HeaderOnlyMessagesTest {
    @Test
    public void everyInstanceIsHeaderOnlyMessage() {
        for(HeaderOnlyMessages value : HeaderOnlyMessages.values()) {
            assertTrue(
                    "Header-only get '" + value.name() + "' has payload, when it shouldn't",
                    ! value.getInstance().getType().hasPayload()
            );
        }
    }

    @Test
    public void allHeaderOnlyMessagesArePresent() {
        for(MessageType messageType : MessageType.values()) {
            if(! messageType.hasPayload()) {
                assertNotNull(
                        "Header-only message '" + messageType.name() + "' is not added to the instances in "
                                + HeaderOnlyMessages.class.getSimpleName(),
                        HeaderOnlyMessages.getByMessageType(messageType)
                );
            }
        }
    }
}
