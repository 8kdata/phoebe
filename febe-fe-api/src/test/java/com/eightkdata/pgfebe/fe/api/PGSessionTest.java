// Copyright © 2015, 8Kdata Technologies, S.L.

package com.eightkdata.pgfebe.fe.api;

import org.junit.Test;

/**
 * Unit tests for {@link }
 */
public class PGSessionTest extends AbstractTest {

    @Test
    public void testStart() throws Exception {
        String dbuser = props.getProperty("db.user");
        String dbname = props.getProperty("db.name");
        client.connect().start(dbuser, dbname);
    }

}
