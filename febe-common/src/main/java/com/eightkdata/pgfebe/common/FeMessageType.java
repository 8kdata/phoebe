/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import com.eightkdata.pgfebe.common.message.HeaderOnlyMessage;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum FeMessageType {
    Bind,
    CancelRequest,
    Close,
    CopyData,
    CopyDone,
    CopyFail,
    Describe,
    Execute,
    Flush,
    FunctionCall,
    Parse,
    PasswordMessage,
    Query,
    SSLRequest,
    StartupMessage,
    Sync,
    Terminate
    ;

    private final FeBeMessageType febeMessageType;
    private final FeBeMessage headerOnlyInstance;

    FeMessageType() {
        this.febeMessageType = FeBeMessageType.valueOf(name());
        this.headerOnlyInstance = febeMessageType.hasPayload() ? null : new HeaderOnlyMessage(febeMessageType);
    }
}
