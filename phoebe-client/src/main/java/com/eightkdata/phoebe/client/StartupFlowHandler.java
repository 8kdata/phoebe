/*
 * Copyright © 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for any purpose, without fee, and without
 * a written agreement is hereby granted, provided that the above
 * copyright notice and this paragraph and the following two
 * paragraphs appear in all copies.
 *
 * In no event shall 8Kdata Technology S.L. be liable to any party
 * for direct, indirect, special, incidental, or consequential
 * damages, including lost profits, arising out of the use of this
 * software and its documentation, even if 8Kdata Technology S.L.
 * has been advised of the possibility of such damage.
 *
 * 8Kdata Technology S.L. specifically disclaims any warranties,
 * including, but not limited to, the implied warranties of
 * merchantability and fitness for a particular purpose. the
 * software provided hereunder is on an “as is” basis, and
 * 8Kdata Technology S.L. has no obligations to provide
 * maintenance, support, updates, enhancements, or modifications.
 */


package com.eightkdata.phoebe.client;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.FeBeMessageType;
import com.eightkdata.phoebe.common.message.*;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Set;

import static com.eightkdata.phoebe.common.FeBeMessageType.*;

/**
 * Handler for the startup message flow.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/static/protocol-flow.html#AEN102761">PostgreSQL Documentation §49.2.1</a>
 */
public class StartupFlowHandler extends FlowHandler {

    private static final Set<FeBeMessageType> STARTUP_FLOW_MESSAGES = EnumSet.of(
            AuthenticationCleartextPassword, AuthenticationGSS, AuthenticationGSSContinue, AuthenticationKerberosV5,
            AuthenticationMD5Password, AuthenticationOk, AuthenticationSCMCredential, AuthenticationSSPI,
            BackendKeyData, ErrorResponse, ParameterStatus, ReadyForQuery
    );

    private final Callback callback;

    public StartupFlowHandler(Callback callback) {
        super(STARTUP_FLOW_MESSAGES);
        this.callback = callback;
    }

    @Override
    public boolean handle(Channel channel, Message message, Charset encoding) {
        switch (message.getType()) {
            case AuthenticationCleartextPassword:
                onAuthenticationCleartextPassword(channel);
                break;
            case AuthenticationGSS:
                onAuthenticationGSS();
                break;
            case AuthenticationGSSContinue:
                onAuthenticationGSSContinue();
                break;
            case AuthenticationKerberosV5:
                onAuthenticationKerberosV5();
                break;
            case AuthenticationMD5Password:
                assert message instanceof AuthenticationMD5Password;
                onAuthenticationMD5Password(channel, (AuthenticationMD5Password) message, encoding);
                break;
            case AuthenticationOk:
                onAuthenticationOk();
                break;
            case AuthenticationSCMCredential:
                onAuthenticationSCMCredential();
                break;
            case AuthenticationSSPI:
                onAuthenticationSSPI();
                break;
            case BackendKeyData:
                assert message instanceof BackendKeyData;
                callback.onBackendKeyData((BackendKeyData) message);
                break;
            case ErrorResponse:
                assert message instanceof ErrorResponse;
                callback.onFailed((ErrorResponse) message);
                break;
            case ParameterStatus:
                assert message instanceof ParameterStatus;
                callback.onParameterStatus((ParameterStatus) message);
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
     * Called when the authentication exchange is successfully completed.
     */
    public void onAuthenticationOk() {
    }

    public void onAuthenticationCleartextPassword(Channel channel) {
        channel.writeAndFlush(new PasswordMessage(callback.getPassword()));
    }

    public void onAuthenticationGSS() {
        throw new UnsupportedOperationException("AuthenticationGSS");
    }

    public void onAuthenticationGSSContinue() {
        throw new UnsupportedOperationException("AuthenticationGSSContinue");
    }

    public void onAuthenticationKerberosV5() {
        throw new UnsupportedOperationException("KerberosV5 authentication is no longer supported");
    }

    public void onAuthenticationMD5Password(Channel channel, AuthenticationMD5Password message, Charset encoding) {
        System.out.println(">>> " + message);
        String passwordHash = message.response(callback.getUsername(), callback.getPassword(), encoding);
        channel.writeAndFlush(new PasswordMessage(passwordHash));
    }

    public void onAuthenticationSCMCredential() {
        throw new UnsupportedOperationException("AuthenticationSCMCredential");
    }

    public void onAuthenticationSSPI() {
        throw new UnsupportedOperationException("AuthenticationSSPI");
    }

    public interface Callback {
        String getUsername();
        String getPassword();

        void onBackendKeyData(BackendKeyData message);
        void onParameterStatus(ParameterStatus message);
        void onCompleted(ReadyForQuery message);
        void onFailed(ErrorResponse message);
        // todo: onNoticeResponse
    }

}
