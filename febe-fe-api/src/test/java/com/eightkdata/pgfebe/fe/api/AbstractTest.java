// Copyright © 2015, 8Kdata Technologies, S.L.

package com.eightkdata.pgfebe.fe.api;

import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Properties;

public class AbstractTest {

    static Properties props;

    PGClient client;

    @BeforeClass
    public static void setUpClass() throws Exception {
        props = new Properties();
        props.load(AbstractTest.class.getResourceAsStream("/default.properties"));
        props.load(AbstractTest.class.getResourceAsStream("/local.properties"));
    }

    @Before
    public void setUp() {
        client = new PGClient(
                props.getProperty("db.host"),
                Integer.parseInt(props.getProperty("db.port")));
    }

}
