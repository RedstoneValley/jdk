/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java.awt.font;

import sun.font.CoreMetrics;

/**
 * The {@code LineMetrics} class allows access to the metrics needed to layout characters along a
 * line and to layout of a set of lines.  A {@code LineMetrics} object encapsulates the measurement
 * information associated with a run of text.
 * <p>
 * Fonts can have different metrics for different ranges of characters.  The {@code getLineMetrics}
 * methods of {@link java.awt.Font Font} take some text as an argument and return a {@code
 * LineMetrics} object describing the metrics of the initial number of characters in that text, as
 * returned by {@link #getNumChars}.
 */

public abstract class LineMetrics extends CoreMetrics {

  public LineMetrics(
      float ascent, float descent, float leading, float height, int baselineIndex,
      float[] baselineOffsets, float strikethroughOffset, float strikethroughThickness,
      float underlineOffset, float underlineThickness, float ssOffset, float italicAngle) {
    super(
        ascent,
        descent,
        leading,
        height,
        baselineIndex,
        baselineOffsets,
        strikethroughOffset,
        strikethroughThickness,
        underlineOffset,
        underlineThickness,
        ssOffset,
        italicAngle);
  }

  public LineMetrics(CoreMetrics coreMetrics) {
    super(coreMetrics);
  }

  /**
   * Returns the number of characters ({@code char} values) in the text whose metrics are
   * encapsulated by this {@code LineMetrics} object.
   *
   * @return the number of characters ({@code char} values) in the text with which this {@code
   * LineMetrics} was created.
   */
  public abstract int getNumChars();
}
