package com.eightkdata.pgfebe.main;/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

/**
 * Created: 26/06/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        PostgreSQLClient postgreSQLClient = new PostgreSQLClient("localhost", 5432, "aht", "aht", "aht");
        postgreSQLClient.start();
        Thread.sleep(1000);
        postgreSQLClient.sendStartupMessage();
    }
}
