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


package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;

import javax.annotation.concurrent.Immutable;

/**
 * A simple message that contains no variable data.
 *
 * Instances of this class are immutable and may be shared and reused across threads.
 */
@Immutable
public class HeaderOnlyMessage implements FeBeMessage {

    private final FeBeMessageType messageType;

    public HeaderOnlyMessage(FeBeMessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public FeBeMessageType getType() {
        return messageType;
    }

    @Override
    public int computePayloadLength() {
        return 0;
    }

    @Override
    public String toString() {
        return getType().name() + "()";
    }

}
