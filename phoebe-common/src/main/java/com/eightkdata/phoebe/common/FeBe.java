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


package com.eightkdata.phoebe.common;

/**
 * Container for some FeBe (FrontEnd-BackEnd) protocol definitions
 */
public final class FeBe {

    /**
     * PostgreSQL's current (and only supported in Phoebe) protocol major number
     */
    public static final int PROTOCOL_MAJOR = 3;

    /**
     * PostgreSQL's current (and only supported in Phoebe) protocol minor number
     */
    public static final int PROTOCOL_MINOR = 0;

    /**
     * IANA's assigned TCP/UDP port for PostgreSQL
     * @see <a href="http://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=92">
     *      Service Name and Transport Protocol Port Number Registry (page 92)
     *      </a>
     */
    public static final int IANA_TCP_PORT = 5432;

    private FeBe() {}

}
