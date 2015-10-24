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


import com.eightkdata.phoebe.client.rs.PostgresClient;
import com.eightkdata.phoebe.client.rs.PostgresConnection;
import com.eightkdata.phoebe.client.rs.TcpIpPostgresConnection;
import com.eightkdata.phoebe.common.FeBe;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 */
public class RxPostgresClient implements PostgresClient {
    public static final long DEFAULT_TIMEOUT = 10;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final Observable<PostgresConnection> connections;

    private RxPostgresClient(
            final Observable<Map.Entry<InetAddress,Integer>> postgresHosts,
            final boolean onlyOneHost, final boolean errorsAsFailedConnections,
            @Nonnegative final long timeout, @Nonnull final TimeUnit unit
    ) {
        final int nConnections = postgresHosts.count().toBlocking().single();

        connections = Observable.create(new Observable.OnSubscribe<PostgresConnection>() {
            @Override
            public void call(final Subscriber<? super PostgresConnection> subscriber) {
                if (subscriber.isUnsubscribed())
                    return;

                final CountDownLatch latch = new CountDownLatch(nConnections);
                final AtomicBoolean someConnected = new AtomicBoolean(false);
                final AtomicReference<Throwable> error = new AtomicReference<Throwable>(null);
                final Collection<PostgresConnection> connections = new Vector<PostgresConnection>(nConnections);

                postgresHosts.forEach(new Action1<Map.Entry<InetAddress,Integer>>() {
                    @Override
                    public void call(final Map.Entry<InetAddress,Integer> host) {
                        if(null != error.get())
                            return;

                        Schedulers
                                .newThread()
                                .createWorker()
                                .schedule(new Action0() {
                                    @Override
                                    public void call() {
                                        if (! onlyOneHost || ! someConnected.get()) {
                                            TcpIpPostgresConnection conn = new TcpIpPostgresConnection(
                                                    eventLoopGroup, host.getKey(), host.getValue(), timeout, unit
                                            );
                                            if(conn.isSuccessful()) {
                                                if(onlyOneHost) {
                                                    if(someConnected.compareAndSet(false, true))
                                                        subscriber.onNext(conn);
                                                    else
                                                        conn.close();   // We lost the race to another connection!
                                                } else
                                                    subscriber.onNext(conn);
                                                connections.add(conn);
                                            } else {
                                                if(errorsAsFailedConnections) {
                                                    subscriber.onNext(conn);
                                                } else {
                                                    error.compareAndSet(null, conn.failedConnection().getCause());
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
                    subscriber.onError(e);
                    return;
                }

                if(null == error.get()) {
                    subscriber.onCompleted();
                } else {
                    for(PostgresConnection connection : connections)
                        connection.close();

                    subscriber.onError(error.get());
                }
            }
        })
        .replay(nConnections)
        .autoConnect()
        ;
    }

    public Observable<PostgresConnection> connectionsObservable() {
        return connections;
    }

    @Override
    public Publisher<PostgresConnection> connections() {
        return new PublisherAdapter<PostgresConnection>(connectionsObservable());
    }

    private static class FilterSuccessful implements Func1<PostgresConnection, Boolean> {
        @Override
        public Boolean call(PostgresConnection postgresConnection) {
            return postgresConnection.isSuccessful();
        }
    }
    private static final FilterSuccessful FILTER_SUCCESSFUL = new FilterSuccessful();

    public Observable<PostgresConnection> onConnectedObservable() {
        return connections.filter(FILTER_SUCCESSFUL);
    }

    @Override
    public Publisher<PostgresConnection> onConnected() {
        return new PublisherAdapter<PostgresConnection>(onConnectedObservable());
    }

    private static class FilterFailed implements Func1<PostgresConnection, Boolean> {
        @Override
        public Boolean call(PostgresConnection postgresConnection) {
            return ! postgresConnection.isSuccessful();
        }
    }
    private static final FilterFailed FILTER_FAILED = new FilterFailed();

    public Observable<PostgresConnection> onFailedObservable() {
        return connections.filter(FILTER_FAILED);
    }

    @Override
    public Publisher<PostgresConnection> onFailed() {
        return new PublisherAdapter<PostgresConnection>(onFailedObservable());
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
        connections.subscribe(CONNECTION_CLOSER, ON_ERROR_CLOSER);
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
            hostPorts.add(new HostPort(host, port));
            return this;
        }

        public Builder tcpIp(@Nonnull String host) {
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
         * Call {@link TcpIpPostgresConnection#isSuccessful()} to check whether the connection succeeded.
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
            checkNotNull(unit);

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
