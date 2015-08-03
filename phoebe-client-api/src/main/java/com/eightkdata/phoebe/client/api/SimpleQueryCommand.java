package com.eightkdata.phoebe.client.api;

import com.eightkdata.phoebe.client.SimpleQueryFlowHandler;
import com.eightkdata.phoebe.common.message.ErrorResponse;
import com.eightkdata.phoebe.common.message.Query;
import com.eightkdata.phoebe.common.message.ReadyForQuery;
import io.netty.channel.Channel;

public class SimpleQueryCommand implements SimpleQueryFlowHandler.Callback {

    private final String query;

    public SimpleQueryCommand(String query) {
        this.query = query;
    }

    void writeTo(Channel channel) {
        channel.writeAndFlush(new Query(query));
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onCompleted(ReadyForQuery message) {
    }

    @Override
    public void onFailed(ErrorResponse message) {
    }

}
