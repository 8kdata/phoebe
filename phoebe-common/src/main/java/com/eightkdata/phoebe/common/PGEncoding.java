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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.eightkdata.phoebe.common.util.Preconditions.checkTextNotNullNotEmpty;

/**
 * <p>Enum class to map between PostgreSQL encodings and Java encodings.
 * See PostgreSQL's source file {@code src/backend/utils/mb/encnames.c} for more information.
 *
 * <p>Please note that the following PostgreSQL encodings are not supported here
 * (there are no equivalent Java charsets):
 * {@code LATIN6}, {@code LATIN8}, {@code LATIN10} and {@code MULE_INTERNAL}.
 * Not all of the encodings here may be supported by a given JVM (provider and version),
 * and as such methods will throw exceptions if an unsupported encoding is requested.
 *
 * <p>This encoding only affects the client/server communication.
 * The PostgreSQL server will perform any required conversion to satisfy the {@code client_encoding},
 * independently of the server encoding, although some conversions are not allowed
 * (see "Automatic Character Set Conversion Between Server and Client").
 * UTF8 should be used almost always, as conversion to UTF8 is guaranteed from every server encoding.
 * Use different encodings only if you really know that you are doing.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/static/multibyte.html">Character Set Support</a>
 * @see <a href="http://www.postgresql.org/docs/9.4/static/multibyte.html#MULTIBYTE-TRANSLATION-TABLE">
 *      Automatic Character Set Conversion Between Server and Client
 *      </a>
 */
@ThreadSafe
public enum PGEncoding {
    /** Alias for WIN1258 **/
    ABC("windows-1258"),
    /** Alias for IBM866 **/
    ALT("IBM866"),
    /** Big5; Chinese for Taiwan multibyte set **/
    BIG5("Big5"),
    /** EUC-CN; Extended Unix Code for simplified Chinese **/
    EUC_CN("GB2312"),
    /** EUC-JIS-2004; Extended UNIX Code fixed Width for Japanese, standard JIS X 0213 **/
    EUC_JIS_2004("x-SJIS_0213"),
    /** EUC-JP; Extended UNIX Code fixed Width for Japanese, standard OSF **/
    EUC_JP("EUC-JP"),
    /** EUC-KR; Extended Unix Code for Korean , KS X 1001 standard **/
    EUC_KR("EUC-KR"),
    /** EUC-TW; Extended Unix Code for traditional Chinese **/
    EUC_TW("x-EUC-TW"),
    /** GB18030;GB18030 **/
    GB18030("GB18030"),
    /** GBK; Chinese Windows CodePage 936 simplified Chinese **/
    GBK("GBK"),
    /** ISO-8859-1; RFC1345,KXS2 **/
    ISO88591("ISO-8859-1"),
    /** ISO-8859-2; RFC1345,KXS2 **/
    ISO88592("ISO-8859-2"),
    /** ISO-8859-3; RFC1345,KXS2 **/
    ISO88593("ISO-8859-3"),
    /** ISO-8859-4; RFC1345,KXS2 **/
    ISO88594("ISO-8859-4"),
    /** ISO-8859-5; RFC1345,KXS2 **/
    ISO_8859_5("ISO-8859-5"),
    /** ISO-8859-6; RFC1345,KXS2 **/
    ISO_8859_6("ISO-8859-6"),
    /** ISO-8859-7; RFC1345,KXS2 **/
    ISO_8859_7("ISO-8859-7"),
    /** ISO-8859-8; RFC1345,KXS2 **/
    ISO_8859_8("ISO-8859-8"),
    /** ISO-8859-9; RFC1345,KXS2 **/
    ISO88599("ISO-8859-9"),
    /** ISO-8859-13; RFC1345,KXS2 **/
    ISO885913("ISO-8859-13"),
    /** ISO-8859-15; RFC1345,KXS2 **/
    ISO885915("ISO-8859-15"),
    /** JOHAB; Extended Unix Code for simplified Chinese **/
    JOHAB("x-Johab"),
    /** Alias for KOI8-R **/
    KOI8("KOI8-R"),
    /** KOI8-R; RFC1489 **/
    KOI8R("KOI8-R"),
    /** KOI8-U; RFC2319 **/
    KOI8U("KOI8-U"),
    /** Alias for ISO-8859-1 **/
    LATIN1("ISO-8859-1"),
    /** Alias for ISO-8859-2 **/
    LATIN2("ISO-8859-2"),
    /** Alias for ISO-8859-3 **/
    LATIN3("ISO-8859-3"),
    /** Alias for ISO-8859-4 **/
    LATIN4("ISO-8859-4"),
    /** Alias for ISO-8859-9 **/
    LATIN5("ISO-8859-9"),
    /** Alias for ISO-8859-13 **/
    LATIN7("ISO-8859-13"),
    /** Alias for ISO-8859-15 **/
    LATIN9("ISO-8859-15"),
    /** Alias for Shift_JIS **/
    Mskanji("Shift_JIS"),
    /** SHIFT-JIS-2004; Shift JIS for Japanese, standard JIS X 0213 **/
    SHIFT_JIS_2004("x-SJIS_0213"),
    /** Shift_JIS; JIS X 0202-1991 **/
    ShiftJIS("Shift_JIS"),
    /** Alias for Shift_JIS **/
    SJIS("Shift_JIS"),
    /** Declaration of ignorance about the encoding.
     * Server interprets byte values 0-127 according to the ASCII standard,
     * while byte values 128-255 are taken as uninterpreted characters.
     * No encoding conversion will be done when the setting is SQL_ASCII.
     * Thus, this setting is not so much a declaration that a specific encoding is in use.
     **/
    SQL_ASCII("US-ASCII"),
    /** Alias for WIN1258 **/
    TCVN("windows-1258"),
    /** Alias for WIN1258 **/
    TCVN5712("windows-1258"),
    /** UHC; Korean Windows CodePage 949 **/
    UHC("x-windows-949"),
    /** Alias for UTF8 **/
    Unicode("UTF-8"),
    /** UTF8 **/
    UTF8("UTF-8"),
    /** Alias for WIN1258 **/
    VSCII("windows-1258"),
    /** Alias for Windows-1251 **/
    WIN("windows-1251"),
    /** Windows-1250; Microsoft **/
    WIN1250("windows-1250"),
    /** Windows-1251; Microsoft **/
    WIN1251("windows-1251"),
    /** Windows-1252; Microsoft **/
    WIN1252("windows-1252"),
    /** Windows-1253; Microsoft **/
    WIN1253("windows-1253"),
    /** Windows-1254; Microsoft **/
    WIN1254("windows-1254"),
    /** Windows-1255; Microsoft **/
    WIN1255("windows-1255"),
    /** Windows-1256; Microsoft **/
    WIN1256("windows-1256"),
    /** Windows-1257; Microsoft **/
    WIN1257("windows-1257"),
    /** Windows-1258; Microsoft **/
    WIN1258("windows-1258"),
    /** IBM866 **/
    WIN866("IBM866"),
    /** Windows-874; Microsoft **/
    WIN874("x-IBM874"),
    /** Alias for Shift_JIS **/
    WIN932("Shift_JIS"),
    /** Alias for GBK **/
    WIN936("GBK"),
    /** Alias for UHC **/
    WIN949("x-windows-949"),
    /** Alias for BIG5 **/
    WIN950("Big5")
    ;

    private final String charsetName;

    PGEncoding(@Nonnull String charsetName) {
        this.charsetName = checkTextNotNullNotEmpty(charsetName, "Invalid Java charset name '" + charsetName + "'");
    }

    private static final Lock charsetMappingLock = new ReentrantLock();

    @GuardedBy(value = "charsetMappingLock")
    private static final Map<String,Charset> charsetMapping;
    static {
        // Compute the maximum size of the mapping
        TreeSet<String> uniqueCharsets = new TreeSet<String>();
        for(PGEncoding pgEncoding : values()) {
            uniqueCharsets.add(pgEncoding.charsetName);
        }

        charsetMapping = new HashMap<String, Charset>(uniqueCharsets.size(), 0.9f);     // Dense packing
    }

    /**
     * Gets the Java Charset associated with this PGEncoding.
     *
     * @return the associated Java Charset
     * @throws UnsupportedCharsetException if this PGEncoding is associated with a Java Charset
     *                                     not supported by this JVM
     */
    public Charset getCharset() throws UnsupportedCharsetException {
        Charset charset = charsetMapping.get(charsetName);
        if(null != charset) {
            return charset;
        }

        return initializeCharset(charsetName);
    }

    /**
     * Performs lazy initialization of requested Charset.
     */
    private static Charset initializeCharset(String charsetName) throws UnsupportedCharsetException {
        charsetMappingLock.lock();
        try {
            Charset charset;

            charset = charsetMapping.get(charsetName);
            if(null != charset) {
                return charset;
            }

            charset = Charset.forName(charsetName);
            charsetMapping.put(charsetName, charset);
            return charset;

        } finally {
            charsetMappingLock.unlock();
        }
    }
}
