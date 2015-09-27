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

package com.eightkdata.phoebe.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.eightkdata.phoebe.common.util.Preconditions.checkTextNotNullNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class manages the state of the session parameters,
 * which are reported by PostgreSQL server via {@code ParameterStatus} messages.
 * These ParameterStatus messages are asynchronous, so this class is implemented as thread safe.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/static/protocol-flow.html#PROTOCOL-ASYNC">
 *      Asynchronous Operations
 *      </a>
 */
@ThreadSafe
public class SessionParameters {
    // Define constants for some frequently used parameter names
    public static final String USER = "user";
    public static final String DATABASE = "database";
    public static final String CLIENT_ENCODING = "client_encoding";
    public static final String LC_MESSAGES = "lc_messages";
    
    // Initialize the ConcurrentHashMap with as low memory footprint as possible:
    //      16   initial capacity, should be enough for most typical sessions, which have ~ 10 parameters
    //      0.9f load factor, so that it's a compact map
    //      1    concurrency level, as writes are infrequent and concurrency very low
    private final ConcurrentHashMap<String,String> parameters = new ConcurrentHashMap<String, String>(16, 0.9f, 1);
    
    public SessionParameters setParameter(@Nonnull String name, @Nonnull String value) {
        checkTextNotNullNotEmpty(name, "Parameter name cannot be empty");
        checkNotNull(value, "Parameter value cannot be null");
        parameters.putIfAbsent(name, value);

        return this;
    }

    public SessionParameters user(@Nonnull String user) {
        return setParameter(USER, user);
    }

    public SessionParameters database(@Nonnull String database) {
        return setParameter(DATABASE, database);
    }

    @Nullable
    public String getParameter(@Nonnull String name) {
        checkTextNotNullNotEmpty(name, "Parameter name cannot be empty");

        return parameters.get(name);
    }

    public boolean parameterIsSet(@Nonnull String name) {
        checkTextNotNullNotEmpty(name, "Parameter name cannot be empty");

        return parameters.containsKey(name);
    }

    public int numberParameters() {
        return parameters.size();
    }

    /**
     * Returns a deep copy of the session parameters
     * @return
     */
    public Map<String,String> parametersMap() {
        Map<String,String> result = new HashMap<String, String>(parameters.size());
        result.putAll(parameters);

        return result;
    }
}
