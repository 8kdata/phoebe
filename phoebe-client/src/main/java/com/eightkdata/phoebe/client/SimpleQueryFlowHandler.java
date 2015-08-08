package com.eightkdata.phoebe.client;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.MessageType;
import com.eightkdata.phoebe.common.message.DataRow;
import com.eightkdata.phoebe.common.message.ErrorResponse;
import com.eightkdata.phoebe.common.message.ReadyForQuery;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Set;

import static com.eightkdata.phoebe.common.MessageType.*;

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
            case DataRow:
                assert message instanceof DataRow;
                onDataRow((DataRow) message);
                break;
            case EmptyQueryResponse:
                callback.onCompleted();
                break;
            case ErrorResponse:
                assert message instanceof ErrorResponse;
                callback.onFailed((ErrorResponse) message);
                break;
            case NoticeResponse:
                onNoticeResponse();
                break;
            case RowDescription:
                onRowDescription();
                break;
            case ReadyForQuery:
                assert message instanceof ReadyForQuery;
                callback.onCompleted((ReadyForQuery) message);
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

    /**
     * Called when rows are about to be returned.
     *
     * This will be followed by a call to {@link #onDataRow(DataRow)} for each row being returned.
     */
    public void onRowDescription() {
        // fixme: handle RowDescription
        // this should call a handler method with useful column information, probably not the raw message data
    }

    public void onDataRow(DataRow message) {
        // fixme: handle DataRow
        // this should call a handler method with useful column information, probably not the raw message data
    }

    public void onNoticeResponse() {
        throw new UnsupportedOperationException("NoticeResponse");
    }


    public interface Callback {
        void onCompleted(/* EmptyQueryResponse */);
        void onCompleted(ReadyForQuery message);
        void onFailed(ErrorResponse message);
    }

}
