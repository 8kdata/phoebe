/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.BaseFeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;

import javax.annotation.concurrent.Immutable;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class HeaderOnlyMessage extends BaseFeBeMessage {
    public HeaderOnlyMessage(FeBeMessageType febeMessageType) {
        super(febeMessageType);
    }
}
