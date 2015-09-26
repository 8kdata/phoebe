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

import com.eightkdata.phoebe.common.message.Message;
import com.eightkdata.phoebe.common.message.MessageType;
import com.eightkdata.phoebe.common.messages.AuthenticationMD5Password;
import com.eightkdata.phoebe.common.messages.MessageEncoders;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Set;

import static com.eightkdata.phoebe.common.message.MessageType.*;

/**
 * Handler for the startup message flow.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/static/protocol-flow.html#AEN102761">PostgreSQL Documentation §49.2.1</a>
 */
public class StartupFlowHandler extends FlowHandler {

    private static final Set<MessageType> STARTUP_FLOW_MESSAGES = EnumSet.of(
            AuthenticationCleartextPassword, AuthenticationGSS, AuthenticationGSSContinue, AuthenticationKerberosV5,
            AuthenticationMD5Password, AuthenticationOk, AuthenticationSCMCredential, AuthenticationSSPI,
            BackendKeyData, ErrorResponse, ParameterStatus, ReadyForQuery
    );

    private final Callback callback;
    private final MessageEncoders messageEncoders;

    public StartupFlowHandler(Callback callback, ByteBufAllocator byteBufAllocator) {
        super(STARTUP_FLOW_MESSAGES);
        this.callback = callback;
        messageEncoders = new MessageEncoders(byteBufAllocator);
    }

    @Override
    public boolean handle(Channel channel, Message message, Charset encoding) {
        switch (message.getType()) {
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
            case ParameterStatus:
                onParameterStatus();
                break;
            case BackendKeyData:
                onBackendKeyData();
                break;
            case ReadyForQuery:
                onReadyForQuery();
                break;
            default:
                throw new UnsupportedOperationException(message.getType().name() + " is not part of the startup message flow");
        }
        return false;
    }

    public void onParameterStatus() {
        // Do nothing, needs no reply.
        // TODO: provide a callback to update session information with the value of these parameters
    }

    public void onBackendKeyData() {
        // Do nothing, needs no reply.
        // TODO: provide a callback to update session information with the value of these parameters
    }

    public void onReadyForQuery() {
        // TODO: report to the flow we're ready for user queries
    }

    /**
     * Called when the authentication exchange is successfully completed.
     */
    public void onAuthenticationOk() {
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
        String passwordHash = message.response(callback.getUsername(), callback.getPassword(), encoding);
        channel.writeAndFlush(messageEncoders.passwordMessage(encoding, passwordHash));
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


        // todo: onNoticeResponse
    }

}
