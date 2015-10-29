/*
 * Copyright (c) 2015, 8Kdata Technology S.L.
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


package com.eightkdata.phoebe.client.rs.rxjava;


import com.eightkdata.phoebe.client.rs.FailedConnectionException;
import com.eightkdata.phoebe.client.rs.PostgresClient;
import com.eightkdata.phoebe.client.rs.PostgresConnection;
import com.eightkdata.phoebe.client.rs.TcpIpPostgresConnection;
import com.eightkdata.phoebe.common.FeBe;
import com.eightkdata.phoebe.common.util.Try;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.reactivestreams.Publisher;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.reactivestreams.PublisherAdapter;
import rx.schedulers.Schedulers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.eightkdata.phoebe.common.util.Preconditions.checkTextNotNullNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 */
public class RxPostgresClient implements PostgresClient {
    public static final long DEFAULT_TIMEOUT = 10;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final EventLoopGroup eventLoopGroup;

    private final Observable<Try<PostgresConnection,FailedConnectionException>> connections;

    private RxPostgresClient(
            final Observable<Map.Entry<InetAddress,Integer>> postgresHosts,
            final boolean onlyOneHost, final boolean errorsAsFailedConnections,
            @Nonnegative final long timeout, @Nonnull final TimeUnit unit
    ) {
        // Init event loop and make sure daemon connections will be closed properly
        eventLoopGroup = new NioEventLoopGroup(
                0,                                              // use default number of threads (queries system CPUs)
                new DefaultThreadFactory("netty", true)         // Use daemon threads, JVM will always exit
        );

        // Make sure proper cleanup is performed even if the user does not explicitly call .close()
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(null != connections)
                    close();
            }
        });

        // Establish the connections
        final int nConnections = postgresHosts.count().toBlocking().single();

        connections = Observable.create(
        new Observable.OnSubscribe<Try<PostgresConnection,FailedConnectionException>>() {
            @Override
            public void call(final Subscriber<? super Try<PostgresConnection,FailedConnectionException>> subs) {
                final CountDownLatch latch = new CountDownLatch(nConnections);
                final AtomicBoolean someConnected = new AtomicBoolean(false);
                final AtomicReference<Throwable> error = new AtomicReference<Throwable>(null);
                final Collection<PostgresConnection> connections = new Vector<PostgresConnection>(nConnections);

                postgresHosts.forEach(new Action1<Map.Entry<InetAddress,Integer>>() {
                    @Override
                    public void call(final Map.Entry<InetAddress,Integer> host) {
                        if(subs.isUnsubscribed() || null != error.get())
                            return;

                        Schedulers
                                .newThread()
                                .createWorker()
                                .schedule(new Action0() {
                                    @Override
                                    public void call() {
                                        if (! onlyOneHost || ! someConnected.get()) {
                                            try {
                                                TcpIpPostgresConnection c = new TcpIpPostgresConnection(
                                                        eventLoopGroup, host.getKey(), host.getValue(), timeout, unit
                                                );
                                                if(onlyOneHost) {
                                                    if(someConnected.compareAndSet(false, true))
                                                        subs.onNext(
                                                            Try.<PostgresConnection,FailedConnectionException>success(c)
                                                        );
                                                    else
                                                        c.close();   // We lost the race to another connection!
                                                } else
                                                    subs.onNext(
                                                        Try.<PostgresConnection,FailedConnectionException>success(c)
                                                );
                                                connections.add(c);
                                            } catch (FailedConnectionException e) {
                                                if(errorsAsFailedConnections) {
                                                    subs.onNext(
                                                        Try.<PostgresConnection,FailedConnectionException>failure(e)
                                                    );
                                                } else {
                                                    error.compareAndSet(null, e.getCause());
                                                }
                                            }
                                        }

                                        latch.countDown();
                                    }
                                })
                        ;
                    }
                });

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    subs.onError(e);
                    return;
                }

                if(null == error.get()) {
                    subs.onCompleted();
                } else {
                    for(PostgresConnection connection : connections)
                        connection.close();

                    subs.onError(error.get());
                }
            }
        })
        .replay(nConnections)
        .autoConnect()
        ;
    }

    public Observable<Try<PostgresConnection,FailedConnectionException>> connectionsObservable() {
        return connections;
    }

    @Override
    public Publisher<Try<PostgresConnection,FailedConnectionException>> connections() {
        return new PublisherAdapter<Try<PostgresConnection,FailedConnectionException>>(connectionsObservable());
    }

    private static class FilterConnections
    implements Func1<Try<PostgresConnection, FailedConnectionException>, Boolean> {
        @Override
        public Boolean call(Try<PostgresConnection, FailedConnectionException> t) {
            return t.isSuccess();
        }
    }

    private static class MapConnections
    implements Func1<Try<PostgresConnection, FailedConnectionException>, PostgresConnection> {
        @Override
        public PostgresConnection call(Try<PostgresConnection, FailedConnectionException> t) {
            return t.getSuccess();
        }
    }

    private static final FilterConnections FILTER_CONNECTIONS = new FilterConnections();
    private static final MapConnections MAP_CONNECTIONS = new MapConnections();

    public Observable<PostgresConnection> onConnectedObservable() {
        return connections
                .filter(FILTER_CONNECTIONS)
                .map(MAP_CONNECTIONS);
    }

    @Override
    public Publisher<PostgresConnection> onConnected() {
        return new PublisherAdapter<PostgresConnection>(onConnectedObservable());
    }

    private static class FilterFailedConnections
    implements Func1<Try<PostgresConnection, FailedConnectionException>, Boolean> {
        @Override
        public Boolean call(Try<PostgresConnection, FailedConnectionException> t) {
            return t.isFailure();
        }
    }


    private static class MapFailedConnections
    implements Func1<Try<PostgresConnection, FailedConnectionException>, FailedConnectionException> {
        // TODO: fix the findbug complain, if it's finally fair
        @SuppressFBWarnings(
                value="BC_UNCONFIRMED_CAST",
                justification="Shouldn't happen here, Try#getFailure returns a T. But TODO: check it"
        )
        @Override
        public FailedConnectionException call(Try<PostgresConnection, FailedConnectionException> t) {
            return t.getFailure();
        }
    }

    private static final FilterFailedConnections FILTER_FAILED_CONNECTIONS = new FilterFailedConnections();
    private static final MapFailedConnections MAP_FAILED_CONNECTIONS = new MapFailedConnections();

    public Observable<FailedConnectionException> onFailedObservable() {
        return connections
                .filter(FILTER_FAILED_CONNECTIONS)
                .map(MAP_FAILED_CONNECTIONS);
    }

    @Override
    public Publisher<FailedConnectionException> onFailed() {
        return new PublisherAdapter<FailedConnectionException>(onFailedObservable());
    }

    private static class ConnectionCloser implements Action1<PostgresConnection> {
        @Override
        public void call(PostgresConnection postgresConnection) {
            postgresConnection.close();
        }
    }

    private static class OnErrorCloser implements Action1<Throwable> {
        @Override
        public void call(Throwable throwable) {
            // Do nothing. But method needs to be implemented to make sure we have an onError handler
        }
    }

    private static final ConnectionCloser CONNECTION_CLOSER = new ConnectionCloser();
    private static final OnErrorCloser ON_ERROR_CLOSER = new OnErrorCloser();

    @Override
    public void close() {
        onConnectedObservable().subscribe(CONNECTION_CLOSER, ON_ERROR_CLOSER);
        eventLoopGroup.shutdownGracefully();
    }

    public static Builder newClient() {
        return new Builder();
    }

    public static class Builder {
        private static class HostPort {
            private static final String LOCALHOST = "localhost";

            @Nonnull String host;
            @Nonnegative int port;

            HostPort(@Nonnull String host, @Nonnegative int port) {
                this.host = host;
                this.port = port;
            }

            HostPort(@Nonnull String host) {
                this(host, FeBe.IANA_TCP_PORT);
            }

            HostPort() {
                this(LOCALHOST, FeBe.IANA_TCP_PORT);
            }
        }

        public enum ConnectionsSelector {
            /** Select the first connection that gets established **/
            FIRST_CONNECTED,
            /** Select all the connections **/
            ALL;
        }

        private final ArrayList<HostPort> hostPorts = new ArrayList<HostPort>();
        private boolean onlyOneHost = true;
        private boolean abortOnError = true;

        public Builder tcpIp(@Nonnull String host, @Nonnegative int port) {
            checkTextNotNullNotEmpty(host, "host");
            hostPorts.add(new HostPort(host, port));
            return this;
        }

        public Builder tcpIp(@Nonnull String host) {
            checkTextNotNullNotEmpty(host, "host");
            hostPorts.add(new HostPort(host));
            return this;
        }

        public Builder tcpIp() {
            hostPorts.add(new HostPort());
            return this;
        }

        /**
         * Select how many connections to return once the client is created.
         * This allows to select whether to return all the connections or the first successful one.
         * Failed connections are returned nonetheless.
         *
         * @param connectionsSelector The selector to use to select which connections to return
         * @return The Builder, to allow keep chaining calls
         */
        public Builder selectConnections(@Nonnull ConnectionsSelector connectionsSelector) {
            checkNotNull(connectionsSelector, "connectionsSelector");

            switch (connectionsSelector) {
                case FIRST_CONNECTED:       onlyOneHost = true;     break;
                case ALL:                   onlyOneHost = false;    break;

                default:                    onlyOneHost = false;
            }

            return this;
        }

        /**
         * <p>
         * By default, failed connections are still returned by the Observable.
         * Call {@link Try#isSuccess()} on the {@link PostgresClient#onConnected()} items
         * to check whether the connection succeeded.
         * Use the {@link RxPostgresClient#onConnected()} and {@link RxPostgresClient#onFailed()} methods
         * to return the Observables corresponding to the successful and failed connections, respectively.
         *
         * <p>
         * If {@link #abortOnError()} is called,
         * Subscriber's {@code onError()} handler will be called instead.
         * Any previosuly stablished connection will be closed, and the Subscriber will be forced to provide
         * an implementation of the {@code onError()} handler. In this case, it's better to use the
         * {@link RxPostgresClient#connections()} method to subscribe to (and implement the {@code onError()} handler).
         *
         * @return
         */
        public Builder abortOnError() {
            abortOnError = false;
            return this;
        }

        public RxPostgresClient create(@Nonnegative long timeout, @Nonnull TimeUnit unit) {
            checkState(timeout > 0, "timeout");
            checkNotNull(unit, "unit");

            if(hostPorts.isEmpty())
                tcpIp();

            // Resolve hosts into IP address(es)
            Observable<Map.Entry<InetAddress,Integer>> hosts = Observable.from(hostPorts)
                    .flatMap(new Func1<HostPort, Observable<Map.Entry<InetAddress,Integer>>>() {
                        @Override
                        public Observable<Map.Entry<InetAddress,Integer>> call(final HostPort hostPort) {
                            InetAddress[] addresses;
                            try {
                                addresses = InetAddress.getAllByName(hostPort.host);
                            } catch (UnknownHostException e) {
                                return abortOnError ?
                                        Observable.<Map.Entry<InetAddress,Integer>>empty()
                                        : Observable.<Map.Entry<InetAddress,Integer>>error(e);
                            }

                            return Observable.from(addresses)
                                    .map(new Func1<InetAddress, Map.Entry<InetAddress,Integer>>() {
                                        @Override
                                        public Map.Entry<InetAddress,Integer> call(InetAddress inetAddress) {
                                            return new AbstractMap.SimpleImmutableEntry<InetAddress,Integer>(
                                                    inetAddress, hostPort.port
                                            );
                                        }
                                    });
                        }
                    })
            ;

            return new RxPostgresClient(hosts, onlyOneHost, abortOnError, timeout, unit);
        }

        public RxPostgresClient create() {
            return create(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
        }
    }

}
