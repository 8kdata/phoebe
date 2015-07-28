/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.FeBeMessage;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public interface MessageBuilder<T extends FeBeMessage> {
    T build();
}
