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

import com.eightkdata.phoebe.common.util.Charsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * All PostgreSQL character encodings, and their Java equivalents.
 *
 * <p>See PostgreSQL's source file {@code src/backend/utils/mb/encnames.c} for more information.
 * The following PostgreSQL encodings are not supported here as there are no equivalent Java charsets:
 * <ul>
 * <li>{@code LATIN6}
 * <li>{@code LATIN8}
 * <li>{@code LATIN10}
 * <li>{@code MULE_INTERNAL}
 * </ul>
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
 *      Automatic Character Set Conversion Between Server and Client</a>
 */
@ThreadSafe
public enum PostgresEncoding implements CharsetHolder {

    /** Alias for WIN1258 **/
    ABC(Charsets.WINDOWS_1258),
    /** Alias for IBM866 **/
    ALT(Charsets.IBM866),
    /** Big5; Chinese for Taiwan multibyte set **/
    BIG5(Charsets.BIG5),
    /** EUC-CN; Extended Unix Code for simplified Chinese **/
    EUC_CN(Charsets.GB2312),
    /** EUC-JIS-2004; Extended UNIX Code fixed Width for Japanese, standard JIS X 0213 **/
    EUC_JIS_2004(Charsets.X_SJIS_0213),
    /** EUC-JP; Extended UNIX Code fixed Width for Japanese, standard OSF **/
    EUC_JP(Charsets.EUC_JP),
    /** EUC-KR; Extended Unix Code for Korean , KS X 1001 standard **/
    EUC_KR(Charsets.EUC_KR),
    /** EUC-TW; Extended Unix Code for traditional Chinese **/
    EUC_TW(Charsets.X_EUC_TW),
    /** GB18030;GB18030 **/
    GB18030(Charsets.GB18030),
    /** GBK; Chinese Windows CodePage 936 simplified Chinese **/
    GBK(Charsets.GBK),
    /** ISO-8859-1; RFC1345,KXS2 **/
    ISO88591(Charsets.ISO_8859_1),
    /** ISO-8859-2; RFC1345,KXS2 **/
    ISO88592(Charsets.ISO_8859_2),
    /** ISO-8859-3; RFC1345,KXS2 **/
    ISO88593(Charsets.ISO_8859_3),
    /** ISO-8859-4; RFC1345,KXS2 **/
    ISO88594(Charsets.ISO_8859_4),
    /** ISO-8859-5; RFC1345,KXS2 **/
    ISO_8859_5(Charsets.ISO_8859_5),
    /** ISO-8859-6; RFC1345,KXS2 **/
    ISO_8859_6(Charsets.ISO_8859_6),
    /** ISO-8859-7; RFC1345,KXS2 **/
    ISO_8859_7(Charsets.ISO_8859_7),
    /** ISO-8859-8; RFC1345,KXS2 **/
    ISO_8859_8(Charsets.ISO_8859_8),
    /** ISO-8859-9; RFC1345,KXS2 **/
    ISO88599(Charsets.ISO_8859_9),
    /** ISO-8859-13; RFC1345,KXS2 **/
    ISO885913(Charsets.ISO_8859_13),
    /** ISO-8859-15; RFC1345,KXS2 **/
    ISO885915(Charsets.ISO_8859_15),
    /** JOHAB; Extended Unix Code for simplified Chinese **/
    JOHAB(Charsets.X_JOHAB),
    /** Alias for KOI8-R **/
    KOI8(Charsets.KOI8_R),
    /** KOI8-R; RFC1489 **/
    KOI8R(Charsets.KOI8_R),
    /** KOI8-U; RFC2319 **/
    KOI8U(Charsets.KOI8_U),
    /** Alias for ISO-8859-1 **/
    LATIN1(Charsets.ISO_8859_1),
    /** Alias for ISO-8859-2 **/
    LATIN2(Charsets.ISO_8859_2),
    /** Alias for ISO-8859-3 **/
    LATIN3(Charsets.ISO_8859_3),
    /** Alias for ISO-8859-4 **/
    LATIN4(Charsets.ISO_8859_4),
    /** Alias for ISO-8859-9 **/
    LATIN5(Charsets.ISO_8859_9),
    /** Alias for ISO-8859-13 **/
    LATIN7(Charsets.ISO_8859_13),
    /** Alias for ISO-8859-15 **/
    LATIN9(Charsets.ISO_8859_15),
    /** Alias for Shift_JIS **/
    Mskanji(Charsets.SHIFT_JIS),
    /** SHIFT-JIS-2004; Shift JIS for Japanese, standard JIS X 0213 **/
    SHIFT_JIS_2004(Charsets.X_SJIS_0213),
    /** Shift_JIS; JIS X 0202-1991 **/
    ShiftJIS(Charsets.SHIFT_JIS),
    /** Alias for Shift_JIS **/
    SJIS(Charsets.SHIFT_JIS),
    /** Declaration of ignorance about the encoding.
     * Server interprets byte values 0-127 according to the ASCII standard,
     * while byte values 128-255 are taken as uninterpreted characters.
     * No encoding conversion will be done when the setting is SQL_ASCII.
     * Thus, this setting is not so much a declaration that a specific encoding is in use.
     **/
    SQL_ASCII(Charsets.US_ASCII),
    /** Alias for WIN1258 **/
    TCVN(Charsets.WINDOWS_1258),
    /** Alias for WIN1258 **/
    TCVN5712(Charsets.WINDOWS_1258),
    /** UHC; Korean Windows CodePage 949 **/
    UHC(Charsets.X_WINDOWS_949),
    /** Alias for UTF8 **/
    Unicode(Charsets.UTF_8),
    /** UTF8 **/
    UTF8(Charsets.UTF_8),
    /** Alias for WIN1258 **/
    VSCII(Charsets.WINDOWS_1258),
    /** Alias for Windows-1251 **/
    WIN(Charsets.WINDOWS_1251),
    /** Windows-1250; Microsoft **/
    WIN1250(Charsets.WINDOWS_1250),
    /** Windows-1251; Microsoft **/
    WIN1251(Charsets.WINDOWS_1251),
    /** Windows-1252; Microsoft **/
    WIN1252(Charsets.WINDOWS_1252),
    /** Windows-1253; Microsoft **/
    WIN1253(Charsets.WINDOWS_1253),
    /** Windows-1254; Microsoft **/
    WIN1254(Charsets.WINDOWS_1254),
    /** Windows-1255; Microsoft **/
    WIN1255(Charsets.WINDOWS_1255),
    /** Windows-1256; Microsoft **/
    WIN1256(Charsets.WINDOWS_1256),
    /** Windows-1257; Microsoft **/
    WIN1257(Charsets.WINDOWS_1257),
    /** Windows-1258; Microsoft **/
    WIN1258(Charsets.WINDOWS_1258),
    /** IBM866 **/
    WIN866(Charsets.IBM866),
    /** Windows-874; Microsoft **/
    WIN874(Charsets.X_IBM874),
    /** Alias for Shift_JIS **/
    WIN932(Charsets.SHIFT_JIS),
    /** Alias for GBK **/
    WIN936(Charsets.GBK),
    /** Alias for UHC **/
    WIN949(Charsets.X_WINDOWS_949),
    /** Alias for BIG5 **/
    WIN950(Charsets.BIG5)
    ;

    private final Charsets charset;

    PostgresEncoding(@Nonnull Charsets charset) {
        this.charset = checkNotNull(charset);
    }

    @Nullable
    @Override
    public Charset getCharset() {
        return charset.getCharset();
    }

}
