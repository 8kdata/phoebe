/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.decoder;

import com.eightkdata.pgfebe.common.BeMessageType;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.MessageDecoder;
import com.eightkdata.pgfebe.fe.decoder.message.AuthenticationMD5PasswordDecoder;

import javax.annotation.Nullable;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum BeMessageTypeDecoder {
    AuthenticationOk                (   null                                    ),
    AuthenticationKerberosV5        (   null                                    ),
    AuthenticationCleartextPassword (   null                                    ),
    AuthenticationMD5Password       (   new AuthenticationMD5PasswordDecoder()  ),
    AuthenticationSCMCredential     (   null                                    ),
    AuthenticationGSS               (   null                                    ),
    AuthenticationSSPI              (   null                                    ),
    AuthenticationGSSContinue       (   null                                    ),  // TODO: needs a decoder
    BackendKeyData                  (   null                                    ),  // TODO: needs a decoder
    BindComplete                    (   null                                    ),
    CloseComplete                   (   null                                    ),
    CommandComplete                 (   null                                    ),  // TODO: needs a decoder
    CopyData                        (   null                                    ),  // TODO: needs a decoder
    CopyDone                        (   null                                    ),  // TODO: needs a decoder
    CopyInResponse                  (   null                                    ),  // TODO: needs a decoder
    CopyOutResponse                 (   null                                    ),  // TODO: needs a decoder
    CopyBothResponse                (   null                                    ),  // TODO: needs a decoder
    DataRow                         (   null                                    ),  // TODO: needs a decoder
    EmptyQueryResponse              (   null                                    ),
    ErrorResponse                   (   null                                    ),  // TODO: needs a decoder
    FunctionCallResponse            (   null                                    ),  // TODO: needs a decoder
    NoData                          (   null                                    ),
    NoticeResponse                  (   null                                    ),  // TODO: needs a decoder
    NotificationResponse            (   null                                    ),  // TODO: needs a decoder
    ParameterDescription            (   null                                    ),  // TODO: needs a decoder
    ParameterStatus                 (   null                                    ),  // TODO: needs a decoder
    ParseComplete                   (   null                                    ),
    PortalSuspended                 (   null                                    ),
    ReadyForQuery                   (   null                                    ),  // TODO: needs a decoder
    RowDescription                  (   null                                    )   // TODO: needs a decoder
    ;

    private final BeMessageType beMessageType;
    private final MessageDecoder<?> decoder;

    BeMessageTypeDecoder(@Nullable MessageDecoder<?> decoder) {
        this.beMessageType = BeMessageType.valueOf(name());
        this.decoder = decoder;
    }

    public FeBeMessageType getFeBeMessageType() {
        return beMessageType.getFeBeMessageType();
    }

    public MessageDecoder<?> getDecoder() {
        return decoder;
    }
}
