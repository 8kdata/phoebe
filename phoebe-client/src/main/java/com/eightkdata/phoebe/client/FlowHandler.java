package com.eightkdata.phoebe.client;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.MessageType;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.Set;

import static com.eightkdata.phoebe.common.MessageType.ErrorResponse;
import static com.eightkdata.phoebe.common.MessageType.ReadyForQuery;

public abstract class FlowHandler {

    /**
     * The set of message types that should be handled.
     */
    private final Set<MessageType> messageTypes;

    protected FlowHandler(Set<MessageType> messageTypes) {
        this.messageTypes = messageTypes;
    }

    public boolean isHandled(MessageType messageType) {
        return messageTypes.contains(messageType);
    }

    public boolean isComplete(MessageType messageType) {
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
