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

package com.eightkdata.pgfebe.common;

/**
 * All of the single byte codes used to identify messages on the wire.
 *
 * Note that some messages (SSL Request, Startup Message, and Cancel Request) do not have identifying codes,
 * these sre represented by {@link #NONE}.
 */
public interface MessageId {

    // Backend messages
    byte AUTHENTICATION = 'R';
    byte BACKEND_KEY_DATA = 'K';
    byte BIND_COMPLETE = '2';
    byte CLOSE_COMPLETE = '3';
    byte COMMAND_COMPLETE = 'C';
    byte DATA_ROW = 'D';
    byte EMPTY_QUERY_RESPONSE = 'I';
    byte ERROR_RESPONSE = 'E';
    byte FUNCTION_CALL_RESPONSE = 'V';
    byte NO_DATA = 'n';
    byte NOTICE_RESPONSE = 'N';
    byte NOTIFICATION_RESPONSE = 'A';
    byte PARAMETER_DESCRIPTION = 't';
    byte PARAMETER_STATUS = 'S';
    byte PARSE_COMPLETE = '1';
    byte PORTAL_SUSPENDED = 's';
    byte READY_FOR_QUERY = 'Z';
    byte ROW_DESCRIPTION = 'T';

    // Copy Messages
    byte COPY_DATA = 'd';
    byte COPY_DONE = 'c';
    byte COPY_FAIL = 'f';
    byte COPY_IN_RESPONSE = 'G';
    byte COPY_OUT_RESPONSE = 'H';
    byte COPY_BOTH_RESPONSE = 'W';

    // Frontend Messages
    byte BIND = 'B';
    byte CLOSE = 'C';
    byte DESCRIBE = 'D';
    byte EXECUTE = 'E';
    byte FLUSH = 'H';
    byte FUNCTION_CALL = 'F';
    byte PARSE = 'P';
    byte PASSWORD_MESSAGE = 'p';
    byte QUERY = 'Q';
    byte SYNC = 'S';
    byte TERMINATE = 'X';

    byte NONE = -1;

}
