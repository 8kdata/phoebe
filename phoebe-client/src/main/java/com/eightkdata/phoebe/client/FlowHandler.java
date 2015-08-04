package com.eightkdata.phoebe.client;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.FeBeMessageType;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.Set;

import static com.eightkdata.phoebe.common.FeBeMessageType.ErrorResponse;
import static com.eightkdata.phoebe.common.FeBeMessageType.ReadyForQuery;

public abstract class FlowHandler {

    /**
     * The set of message types that should be handled.
     */
    private final Set<FeBeMessageType> messageTypes;

    protected FlowHandler(Set<FeBeMessageType> messageTypes) {
        this.messageTypes = messageTypes;
    }

    public boolean isHandled(FeBeMessageType messageType) {
        return messageTypes.contains(messageType);
    }

    public boolean isComplete(FeBeMessageType messageType) {
        return messageType == ReadyForQuery || messageType == ErrorResponse;
    }

    /**
     * Handle a message.
     * @param channel the current channel.
     * @param message the message to handle, guaranteed to be one of {@link #messageTypes}.
     * @param encoding the current encoding.
     * @return {@code true} to keep propgating the message along the pipeline, {@code false} to consume it.
     */
    public abstract boolean handle(Channel channel, Message message, Charset encoding);

}
