/*
 * Copyright (c) 1997, 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class Symbol extends Charset {
  public Symbol() {
    super("Symbol", null);
  }

  @Override
  public boolean contains(Charset cs) {
    return cs instanceof Symbol;
  }

  /* Seems like supporting a decoder is required, but we aren't going
   * to be publically exposing this class, so no need to waste work
   */
  @Override
  public CharsetDecoder newDecoder() {
    throw new Error("Decoder is not implemented for Symbol Charset");
  }

  @Override
  public CharsetEncoder newEncoder() {
    return new Encoder(this);
  }

  private static class Encoder extends CharsetEncoder {
    private static final byte[] table_math = {
        (byte) 34, (byte) 0, (byte) 100, (byte) 36, (byte) 0, (byte) 198, (byte) 68, (byte) 209,
        // 00
        (byte) 206, (byte) 207, (byte) 0, (byte) 0, (byte) 0, (byte) 39, (byte) 0, (byte) 80,
        (byte) 0, (byte) 229, (byte) 45, (byte) 0, (byte) 0, (byte) 164, (byte) 0, (byte) 42,
        // 10
        (byte) 176, (byte) 183, (byte) 214, (byte) 0, (byte) 0, (byte) 181, (byte) 165, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 189, (byte) 0, (byte) 0, (byte) 0, (byte) 217,    // 20
        (byte) 218, (byte) 199, (byte) 200, (byte) 242, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 92, (byte) 0, (byte) 0, (byte) 0,    // 30
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 126, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 0, (byte) 0,    // 40
        (byte) 187, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,    // 50
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 185,
        (byte) 186, (byte) 0, (byte) 0, (byte) 163, (byte) 179, (byte) 0, (byte) 0,    // 60
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,    // 70
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 204, (byte) 201, (byte) 203, (byte) 0, (byte) 205, (byte) 202,    // 80
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 197, (byte) 0, (byte) 196,    // 90
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 94, (byte) 0, (byte) 0,    // a0
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,    // b0
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 224, (byte) 215, (byte) 0, (byte) 0,    // c0
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,    // d0
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,    // e0
        (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 188,};
    private static final byte[] table_greek = {
        (byte) 65, (byte) 66, (byte) 71, (byte) 68, (byte) 69, (byte) 90, (byte) 72,
        // 90
        (byte) 81, (byte) 73, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 88, (byte) 79,
        (byte) 80, (byte) 82, (byte) 0, (byte) 83, (byte) 84, (byte) 85, (byte) 70, (byte) 67,
        // a0
        (byte) 89, (byte) 87, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 97, (byte) 98, (byte) 103, (byte) 100, (byte) 101, (byte) 122, (byte) 104,    // b0
        (byte) 113, (byte) 105, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 120,
        (byte) 111, (byte) 112, (byte) 114, (byte) 86, (byte) 115, (byte) 116, (byte) 117,
        (byte) 102, (byte) 99,    // c0
        (byte) 121, (byte) 119, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        (byte) 0, (byte) 74, (byte) 161, (byte) 0, (byte) 0, (byte) 106, (byte) 118,
        // d0
    };

    public Encoder(Charset cs) {
      super(cs, 1.0f, 1.0f);
    }

    /* The default implementation creates a decoder and we don't have one */
    @Override
    public boolean isLegalReplacement(byte[] repl) {
      return true;
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer src, ByteBuffer dst) {
      char[] sa = src.array();
      int sp = src.arrayOffset() + src.position();
      int sl = src.arrayOffset() + src.limit();
      assert sp <= sl;
      sp = sp <= sl ? sp : sl;
      byte[] da = dst.array();
      int dp = dst.arrayOffset() + dst.position();
      int dl = dst.arrayOffset() + dst.limit();
      assert dp <= dl;
      dp = dp <= dl ? dp : dl;

      try {
        while (sp < sl) {
          char c = sa[sp];
          if (dl - dp < 1) {
            return CoderResult.OVERFLOW;
          }
          if (!canEncode(c)) {
            return CoderResult.unmappableForLength(1);
          }
          sp++;
          if (c >= 0x2200 && c <= 0x22ef) {
            da[dp] = table_math[c - 0x2200];
            dp++;
          } else if (c >= 0x0391 && c <= 0x03d6) {
            da[dp] = table_greek[c - 0x0391];
            dp++;
          }
        }
        return CoderResult.UNDERFLOW;
      } finally {
        src.position(sp - src.arrayOffset());
        dst.position(dp - dst.arrayOffset());
      }
    }

    @Override
    public boolean canEncode(char c) {
      if (c >= 0x2200 && c <= 0x22ef) {
        if (table_math[c - 0x2200] != 0x00) {
          return true;
        }
      } else if (c >= 0x0391 && c <= 0x03d6) {
        if (table_greek[c - 0x0391] != 0x00) {
          return true;
        }
      }
      return false;
    }
  }
}
