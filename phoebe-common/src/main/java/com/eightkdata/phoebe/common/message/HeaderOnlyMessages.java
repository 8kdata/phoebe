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


package com.eightkdata.phoebe.common.message;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
@Immutable
public enum HeaderOnlyMessages {

	AuthenticationOk	            (	MessageType.AuthenticationOk	            ),
	AuthenticationCleartextPassword (	MessageType.AuthenticationCleartextPassword ),
	AuthenticationKerberosV5	    (	MessageType.AuthenticationKerberosV5	    ),
	AuthenticationSCMCredential	    (	MessageType.AuthenticationSCMCredential	    ),
	AuthenticationGSS	            (	MessageType.AuthenticationGSS	            ),
	AuthenticationSSPI	            (	MessageType.AuthenticationSSPI	            ),
	BindComplete	                (	MessageType.BindComplete	                ),
	CloseComplete	                (	MessageType.CloseComplete	                ),
	CopyDone	                    (	MessageType.CopyDone	                    ),
	EmptyQueryResponse	            (	MessageType.EmptyQueryResponse	            ),
	Flush	                        (	MessageType.Flush	                        ),
	NoData	                        (	MessageType.NoData	                        ),
	ParseComplete	                (	MessageType.ParseComplete	                ),
	PortalSuspended	                (	MessageType.PortalSuspended	                ),
	SSLRequest	                    (	MessageType.SSLRequest	                    ),
	Sync	                        (	MessageType.Sync	                        ),
	Terminate	                    (	MessageType.Terminate	                    )
    ;

    private final HeaderOnlyMessage headerOnlyMessage;

    HeaderOnlyMessages(@Nonnull MessageType messageType) {
        checkMessageTypeIsHeaderOnly(messageType);
        headerOnlyMessage = new HeaderOnlyMessage(messageType);
    }

    private static void checkMessageTypeIsHeaderOnly(@Nonnull MessageType messageType) {
        checkNotNull(messageType);
        checkArgument(messageType.isFixedLengthMessage() && ! messageType.hasPayload());
    }

    public @Nonnull HeaderOnlyMessage getInstance() {
        return headerOnlyMessage;
    }

    private static final Map<MessageType,HeaderOnlyMessages> instances =
            new HashMap<MessageType, HeaderOnlyMessages>(values().length);

    static {
        for(HeaderOnlyMessages value : values())
            instances.put(value.headerOnlyMessage.getType(), value);
    }

    public static @Nonnull HeaderOnlyMessages getByMessageType(@Nonnull MessageType messageType) {
        checkMessageTypeIsHeaderOnly(messageType);

        return instances.get(messageType);
    }

}
