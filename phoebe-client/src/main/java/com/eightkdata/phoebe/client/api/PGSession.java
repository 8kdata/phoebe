/*
 * Copyright © 2015, 8Kdata Technologies, S.L.
 *
 * Permission to use, copy, modify, and distribute this software and its documentation for any purpose,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice and this
 * paragraph and the following two paragraphs appear in all copies.
 *
 * IN NO EVENT SHALL 8Kdata BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF 8Kdata HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 8Kdata SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND 8Kdata HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */


package com.eightkdata.phoebe.client.api;

import com.eightkdata.phoebe.client.FlowHandler;
import com.eightkdata.phoebe.client.StartupFlowHandler;
import com.eightkdata.phoebe.client.decoder.BeMessageDecoder;
import com.eightkdata.phoebe.client.decoder.BeMessageProcessor;
import com.eightkdata.phoebe.client.encoder.FeMessageEncoder;
import com.eightkdata.phoebe.client.encoder.FeMessageProcessor;
import com.eightkdata.phoebe.common.SessionParameters;
import com.eightkdata.phoebe.common.message.MessageHeaderEncoder;
import com.eightkdata.phoebe.common.messages.MessageEncoders;
import com.eightkdata.phoebe.common.messages.StartupMessage;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A session represents a useable connection to the database, it is used to send and receive messages.
 */
public class PGSession {

    private final Channel channel;
    private final MessageEncoders messageEncoders;
    private final MessageHeaderEncoder messageHeaderEncoder;

    // todo: use ConcurrentLinkedDeque once we switch to Java 1.7 or later
    private final Deque<FlowHandler> handlers = new LinkedBlockingDeque<FlowHandler>();

    public PGSession(Channel channel) {
        this.channel = checkNotNull(channel, "channel");
        messageEncoders = new MessageEncoders(channel.alloc());
        messageHeaderEncoder = new MessageHeaderEncoder(channel.alloc());
    }

    private void initChannel(Charset encoding) {
        channel.pipeline()
                .addLast("PGInboundMessageDecoder", new BeMessageDecoder(encoding))
                .addLast("PGInboundMessageProcessor", new BeMessageProcessor(handlers, encoding))

                .addLast("PGOutboundMessageEncoder", new FeMessageEncoder(messageHeaderEncoder))
                .addLast("PGOutboundMessageProcessor", new FeMessageProcessor());
    }

    public void start(StartupCommand command) throws InterruptedException {
        initChannel(command.getCharset());
        handlers.addLast(new StartupFlowHandler(command, channel.alloc()));
        SessionParameters sessionParameters = new SessionParameters()
                .user(command.getUsername())
                .database(command.getDatabase());

        StartupMessage startupMessage = messageEncoders.startupMessage(command.getCharset(), sessionParameters);
        channel.writeAndFlush(startupMessage);
    }

    public void close() {
        // Release any acquired resources
        messageHeaderEncoder.releaseByteBufs();
    }

}
