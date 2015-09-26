package com.eightkdata.phoebe.client;

import com.eightkdata.phoebe.common.message.Message;
import com.eightkdata.phoebe.common.message.MessageType;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Set;

import static com.eightkdata.phoebe.common.message.MessageType.*;

/**
 * Handler for the simple query message flow.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/static/protocol-flow.html#AEN102836">PostgreSQL Documentation §49.2.2</a>
 */
public class SimpleQueryFlowHandler extends FlowHandler {

    private static final Set<MessageType> MESSAGE_TYPES = EnumSet.of(
            CommandComplete, CopyInResponse, CopyOutResponse, DataRow, EmptyQueryResponse,
            ErrorResponse, NoticeResponse, RowDescription, ReadyForQuery
    );
    private final Callback callback;

    public SimpleQueryFlowHandler(Callback callback) {
        super(MESSAGE_TYPES);
        this.callback = callback;
    }

    @Override
    public boolean handle(Channel channel, Message message, Charset encoding) {
        switch (message.getType()) {
            case CommandComplete:
                onCommandComplete();
                break;
            case CopyInResponse:
                onCopyInResponse();
                break;
            case CopyOutResponse:
                onCopyOutResponse();
                break;
            case EmptyQueryResponse:
                callback.onCompleted();
                break;
            case NoticeResponse:
                onNoticeResponse();
                break;
            default:
                throw new UnsupportedOperationException(message.getType().name() + " is not part of the startup message flow");
        }
        return false;
    }

    /**
     * Called when a SQL command completed normally.
     */
    public void onCommandComplete() {
    }


    /**
     * Called when the server is ready to copy data from the client.
     */
    public void onCopyInResponse() {
        throw new UnsupportedOperationException("");
    }

    /**
     * Called when the server is ready to copy data to the client.
     */
    public void onCopyOutResponse() {
        throw new UnsupportedOperationException("");
    }



    public void onNoticeResponse() {
        throw new UnsupportedOperationException("NoticeResponse");
    }


    public interface Callback {
        void onCompleted(/* EmptyQueryResponse */);
    }

}
