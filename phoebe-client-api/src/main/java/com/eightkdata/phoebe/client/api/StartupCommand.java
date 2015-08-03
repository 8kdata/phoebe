package com.eightkdata.phoebe.client.api;

import com.eightkdata.phoebe.client.StartupFlowHandler;
import com.eightkdata.phoebe.common.message.*;
import io.netty.channel.Channel;

import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ianp on 2015-08-03.
 */
public class StartupCommand implements StartupFlowHandler.Callback {

    private final String user;
    private final String password;
    private final String database;
    private final Charset encoding;

    public StartupCommand(String username, String password, String database, Charset encoding) {
        this.user = checkNotNull(username, "username");
        this.password = password;
        this.database = database;
        this.encoding = encoding;
    }

    public Charset getEncoding() {
        return encoding;
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    void writeTo(Channel channel) {
        StartupMessage message = StartupMessage.builder()
                .user(user)
                .database(database)
                .clientEncoding(encoding)
                .build();
        channel.writeAndFlush(message);
    }

    public void onParameterStatus(ParameterStatus message) {
    }

    public void onBackendKeyData(BackendKeyData message) {
    }

    public void onCompleted(ReadyForQuery message) {
    }

    @Override
    public void onFailed(ErrorResponse message) {
        System.err.println("!!! " + message);
    }
}
