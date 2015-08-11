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


package com.eightkdata.phoebe.common.util;

import com.eightkdata.phoebe.common.CharsetHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Enum class that holds several instances of Java {@link java.nio.charset.Charset}s.
 * Charsets are lazy-loaded and cached.
 */
public enum Charsets implements CharsetHolder {
    BIG5            {	@Nullable @Override public Charset getCharset() {   return Big5.INSTANCE;		}	},
    EUC_JP          {	@Nullable @Override public Charset getCharset() {	return EucJp.INSTANCE;		}	},
    EUC_KR          {	@Nullable @Override public Charset getCharset() {	return EucKr.INSTANCE;		}	},
    GB18030         {	@Nullable @Override public Charset getCharset() {	return Gb18030.INSTANCE;	}	},
    GB2312          {	@Nullable @Override public Charset getCharset() {	return Gb2312.INSTANCE;		}	},
    GBK             {	@Nullable @Override public Charset getCharset() {	return Gbk.INSTANCE;		}	},
    IBM866          {	@Nullable @Override public Charset getCharset() {	return Ibm866.INSTANCE;		}	},
    ISO_8859_1      {	@Nullable @Override public Charset getCharset() {	return Iso88591.INSTANCE;	}	},
    ISO_8859_2      {	@Nullable @Override public Charset getCharset() {	return Iso88592.INSTANCE;	}	},
    ISO_8859_3      {	@Nullable @Override public Charset getCharset() {	return Iso88593.INSTANCE;	}	},
    ISO_8859_4      {	@Nullable @Override public Charset getCharset() {	return Iso88594.INSTANCE;	}	},
    ISO_8859_5      {	@Nullable @Override public Charset getCharset() {	return Iso88595.INSTANCE;	}	},
    ISO_8859_6      {	@Nullable @Override public Charset getCharset() {	return Iso88596.INSTANCE;	}	},
    ISO_8859_7      {	@Nullable @Override public Charset getCharset() {	return Iso88597.INSTANCE;	}	},
    ISO_8859_8      {	@Nullable @Override public Charset getCharset() {	return Iso88598.INSTANCE;	}	},
    ISO_8859_9      {	@Nullable @Override public Charset getCharset() {	return Iso88599.INSTANCE;	}	},
    ISO_8859_13     {	@Nullable @Override public Charset getCharset() {	return Iso885913.INSTANCE;	}	},
    ISO_8859_15     {	@Nullable @Override public Charset getCharset() {	return Iso885915.INSTANCE;	}	},
    KOI8_R          {	@Nullable @Override public Charset getCharset() {	return Koi8R.INSTANCE;		}	},
    KOI8_U          {	@Nullable @Override public Charset getCharset() {	return Koi8U.INSTANCE;		}	},
    SHIFT_JIS       {	@Nullable @Override public Charset getCharset() {	return ShiftJis.INSTANCE;	}	},
    US_ASCII        {	@Nullable @Override public Charset getCharset() {	return USASCII.INSTANCE;	}	},
    UTF_8           {	@Nullable @Override public Charset getCharset() {	return UTF8.INSTANCE;		}	},
    WINDOWS_1250    {	@Nullable @Override public Charset getCharset() {	return Win1250.INSTANCE;	}	},
    WINDOWS_1251    {	@Nullable @Override public Charset getCharset() {	return Win1251.INSTANCE;	}	},
    WINDOWS_1252    {	@Nullable @Override public Charset getCharset() {	return Win1252.INSTANCE;	}	},
    WINDOWS_1253    {	@Nullable @Override public Charset getCharset() {	return Win1253.INSTANCE;	}	},
    WINDOWS_1254    {	@Nullable @Override public Charset getCharset() {	return Win1254.INSTANCE;	}	},
    WINDOWS_1255    {	@Nullable @Override public Charset getCharset() {	return Win1255.INSTANCE;	}	},
    WINDOWS_1256    {	@Nullable @Override public Charset getCharset() {	return Win1256.INSTANCE;	}	},
    WINDOWS_1257    {	@Nullable @Override public Charset getCharset() {	return Win1257.INSTANCE;	}	},
    WINDOWS_1258    {	@Nullable @Override public Charset getCharset() {	return Win1258.INSTANCE;	}	},
    X_EUC_TW        {	@Nullable @Override public Charset getCharset() {	return XEucTw.INSTANCE;		}	},
    X_IBM874        {	@Nullable @Override public Charset getCharset() {	return XIBM874.INSTANCE;	}	},
    X_JOHAB         {	@Nullable @Override public Charset getCharset() {	return XJohab.INSTANCE;		}	},
    X_SJIS_0213     {	@Nullable @Override public Charset getCharset() {	return XSjis0213.INSTANCE;	}   },
    X_WINDOWS_949   {	@Nullable @Override public Charset getCharset() {	return XWin949.INSTANCE;	}	}
    ;

    /**
     * Set of internal classes implementing the Holder Pattern to provide thread-safe, lazy loading of charsets
     */
    private static class Big5       {   private static final Charset INSTANCE = getCharset("Big5");             }
    private static class EucJp      {   private static final Charset INSTANCE = getCharset("EUC-JP");           }
    private static class EucKr      {   private static final Charset INSTANCE = getCharset("EUC-KR");           }
    private static class Gb18030    {   private static final Charset INSTANCE = getCharset("GB18030");          }
    private static class Gb2312     {   private static final Charset INSTANCE = getCharset("GB2312");           }
    private static class Gbk        {   private static final Charset INSTANCE = getCharset("GBK");              }
    private static class Ibm866     {   private static final Charset INSTANCE = getCharset("IBM866");           }
    private static class Iso88591   {   private static final Charset INSTANCE = getCharset("ISO-8859-1");       }
    private static class Iso88592   {   private static final Charset INSTANCE = getCharset("ISO-8859-2");       }
    private static class Iso88593   {   private static final Charset INSTANCE = getCharset("ISO-8859-3");       }
    private static class Iso88594   {   private static final Charset INSTANCE = getCharset("ISO-8859-4");       }
    private static class Iso88595   {   private static final Charset INSTANCE = getCharset("ISO-8859-5");       }
    private static class Iso88596   {   private static final Charset INSTANCE = getCharset("ISO-8859-6");       }
    private static class Iso88597   {   private static final Charset INSTANCE = getCharset("ISO-8859-7");       }
    private static class Iso88598   {   private static final Charset INSTANCE = getCharset("ISO-8859-8");       }
    private static class Iso88599   {   private static final Charset INSTANCE = getCharset("ISO-8859-9");       }
    private static class Iso885913  {   private static final Charset INSTANCE = getCharset("ISO-8859-13");      }
    private static class Iso885915  {   private static final Charset INSTANCE = getCharset("ISO-8859-15");      }
    private static class Koi8R      {   private static final Charset INSTANCE = getCharset("KOI8-R");           }
    private static class Koi8U      {   private static final Charset INSTANCE = getCharset("KOI8-U");           }
    private static class ShiftJis   {   private static final Charset INSTANCE = getCharset("Shift_JIS");        }
    private static class USASCII    {   private static final Charset INSTANCE = getCharset("US-ASCII");         }
    private static class UTF8       {   private static final Charset INSTANCE = getCharset("UTF-8");            }
    private static class Win1250    {   private static final Charset INSTANCE = getCharset("windows-1250");     }
    private static class Win1251    {   private static final Charset INSTANCE = getCharset("windows-1251");     }
    private static class Win1252    {   private static final Charset INSTANCE = getCharset("windows-1252");     }
    private static class Win1253    {   private static final Charset INSTANCE = getCharset("windows-1253");     }
    private static class Win1254    {   private static final Charset INSTANCE = getCharset("windows-1254");     }
    private static class Win1255    {   private static final Charset INSTANCE = getCharset("windows-1255");     }
    private static class Win1256    {   private static final Charset INSTANCE = getCharset("windows-1256");     }
    private static class Win1257    {   private static final Charset INSTANCE = getCharset("windows-1257");     }
    private static class Win1258    {   private static final Charset INSTANCE = getCharset("windows-1258");     }
    private static class XEucTw     {   private static final Charset INSTANCE = getCharset("x-EUC-TW");         }
    private static class XIBM874    {   private static final Charset INSTANCE = getCharset("x-IBM874");         }
    private static class XJohab     {   private static final Charset INSTANCE = getCharset("x-Johab");          }
    private static class XSjis0213  {   private static final Charset INSTANCE = getCharset("x-SJIS_0213");      }
    private static class XWin949    {   private static final Charset INSTANCE = getCharset("x-windows-949");    }

    private static Charset getCharset(@Nonnull String charsetName) {
        Preconditions.checkTextNotNullNotEmpty(charsetName, "Charset name cannot be empty");

        // The holder pattern is not safe if instance construction may fail. Make sure exception is not thrown
        if(Charset.isSupported(charsetName)) {
            try {
                return Charset.forName(charsetName);
            } catch (UnsupportedCharsetException ex) {
                assert false : "Charset '" + charsetName + "' should be supported, isSupported() returned true";
            }
        }
        
        return null;
    }
}
