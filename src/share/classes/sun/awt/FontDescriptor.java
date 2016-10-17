/*
 * Copyright (c) 1996, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;

public class FontDescriptor implements Cloneable {

  static final boolean isLE;

  static {
    NativeLibLoader.loadLibraries();
  }

  static {
    String enc = AccessController.doPrivileged(new sun.security.action.GetPropertyAction("sun.io"
        + ".unicode.encoding",
        "UnicodeBig"));
    isLE = !"UnicodeBig".equals(enc);
  }

  private final int[] exclusionRanges;
  public final CharsetEncoder encoder;
  public CharsetEncoder unicodeEncoder;
  final String nativeName;
  final String charsetName;
  final boolean useUnicode; // set to true from native code on Unicode-based systems

  public FontDescriptor(String nativeName, CharsetEncoder encoder, int[] exclusionRanges) {

    this.nativeName = nativeName;
    this.encoder = encoder;
    this.exclusionRanges = exclusionRanges;
    useUnicode = false;
    Charset cs = encoder.charset();
    charsetName = cs.name();
  }

  public String getNativeName() {
    return nativeName;
  }

  public CharsetEncoder getFontCharsetEncoder() {
    return encoder;
  }

  public String getFontCharsetName() {
    return charsetName;
  }

  public int[] getExclusionRanges() {
    return exclusionRanges;
  }

  /**
   * Return true if the character is exclusion character.
   */
  public boolean isExcluded(char ch) {
    for (int i = 0; i < exclusionRanges.length; ) {

      int lo = exclusionRanges[i];
      i++;
      int up = exclusionRanges[i];
      i++;

      if (ch >= lo && ch <= up) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    return super.toString() + " [" + nativeName + "|" + encoder + "]";
  }

  public boolean useUnicode() {
    if (useUnicode && unicodeEncoder == null) {
      try {
        unicodeEncoder = isLE ? StandardCharsets.UTF_16LE.newEncoder()
            : StandardCharsets.UTF_16BE.newEncoder();
      } catch (IllegalArgumentException x) {
      }
    }
    return useUnicode;
  }
}
