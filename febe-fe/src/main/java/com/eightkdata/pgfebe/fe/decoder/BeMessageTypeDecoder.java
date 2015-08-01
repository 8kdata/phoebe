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


package com.eightkdata.pgfebe.fe.decoder;

import com.eightkdata.pgfebe.common.BeMessageType;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.decoder.MessageDecoder;

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
    ParameterStatus                 (   new ParameterStatusDecoder()            ),
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
