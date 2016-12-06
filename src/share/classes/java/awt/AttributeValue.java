/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.awt;

import android.util.Log;

import java.util.Arrays;

abstract class AttributeValue {
  private static final String TAG = "java.awt.AttributeValue";
  private final int value;
  private final String[] names;

  protected AttributeValue(int value, String[] names) {
    Log.v(TAG, "value = " + value + ", names = " + Arrays.toString(names));
    if (value < 0 || names == null || value >= names.length) {
      Log.d(TAG, "Assertion failed");
    }
    this.value = value;
    this.names = names;
  }

  // This hashCode is used by the sun.awt implementation as an array
  // index.
  public int hashCode() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AttributeValue)) {
      return false;
    }

    AttributeValue that = (AttributeValue) o;

    return value == that.value && Arrays.equals(names, that.names);
  }

  public String toString() {
    return names[value];
  }
}
