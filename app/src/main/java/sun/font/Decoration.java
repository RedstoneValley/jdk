/*
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
 *
 */

/*
 * (C) Copyright IBM Corp. 1999-2003, All Rights Reserved
 *
 */

package sun.font;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Map;

/**
 * This class handles underlining, strikethrough, and foreground and background styles on text.
 * Clients simply acquire instances of this class and hand them off to ExtendedTextLabels or
 * GraphicComponents.
 */
public class Decoration {

  private Decoration() {
  }

  public static Decoration getDecoration(AttributeValues values) {
    if (values == null || !(values.hasLayoutAttributes())) {
      return PLAIN;
    }

    values = values.applyIMHighlight();

    return new DecorationImpl(values.getForeground(),
        values.getBackground(),
        values.getSwapColors(),
        values.getStrikethrough(),
        values.getUnderline(),
        values.getInputMethodUnderline());
  }

  /**
   * Return a Decoration appropriate for the the given Map.
   *
   * @param attributes the Map used to determine the Decoration
   */
  public static Decoration getDecoration(Map attributes) {
    if (attributes == null) {
      return PLAIN;
    }
    return getDecoration(AttributeValues.fromMap(attributes));
  }

  private static float getLowerDrawLimit(float underlineThickness) {
    // TODO
    return 0.0f;
  }

  private static void drawUnderline(Graphics2D g2d, float ulThickness, float x1, float x2,
      float y) {
    synchronized (g2d) {
      Stroke oldStroke = g2d.getStroke();
      g2d.setStroke(new BasicStroke(ulThickness));
      g2d.drawLine((int) x1, (int) y, (int) x2, (int) y);
      g2d.setStroke(oldStroke);
    }
  }

  private static final Decoration PLAIN = new Decoration();

  private static final class DecorationImpl extends Decoration {

    private Paint fgPaint;
    private Paint bgPaint;
    private boolean swapColors;
    private boolean strikethrough;
    private boolean stdUnderline; // underline from TextAttribute.UNDERLINE_ON
    private boolean imUnderline; // input method underline

    DecorationImpl(Paint foreground,
        Paint background,
        boolean swapColors,
        boolean strikethrough,
        boolean stdUnderline,
        boolean imUnderline) {

      fgPaint = (Paint) foreground;
      bgPaint = (Paint) background;

      this.swapColors = swapColors;
      this.strikethrough = strikethrough;

      this.stdUnderline = stdUnderline;
      this.imUnderline = imUnderline;
    }

    private static boolean areEqual(Object lhs, Object rhs) {

      if (lhs == null) {
        return rhs == null;
      } else {
        return lhs.equals(rhs);
      }
    }

    public boolean equals(Object rhs) {

      if (rhs == this) {
        return true;
      }
      if (rhs == null) {
        return false;
      }

      DecorationImpl other = null;
      try {
        other = (DecorationImpl) rhs;
      } catch (ClassCastException e) {
        return false;
      }

      if (!(swapColors == other.swapColors &&
          strikethrough == other.strikethrough)) {
        return false;
      }

      if (stdUnderline != other.stdUnderline) {
        return false;
      }
      if (!areEqual(fgPaint, other.fgPaint)) {
        return false;
      }
      return areEqual(bgPaint, other.bgPaint) && imUnderline == other.imUnderline;
    }

    public int hashCode() {

      int hc = 1;
      if (strikethrough) {
        hc |= 2;
      }
      if (swapColors) {
        hc |= 4;
      }
      if (stdUnderline) {
        hc |= 8;
      }
      if (imUnderline) {
        hc |= 16;
      }
      return hc;
    }

    /**
     * Return the bottom of the Rectangle which encloses pixels drawn by underlines.
     */
    private float getUnderlineMaxY(CoreMetrics cm) {

      float maxY = 0;
      if (stdUnderline) {
        float ulBottom = cm.underlineOffset;
        ulBottom += getLowerDrawLimit(cm.underlineThickness);
        maxY = Math.max(maxY, ulBottom);
      }

      if (imUnderline) {

        float ulBottom = cm.underlineOffset;
        ulBottom += getLowerDrawLimit(cm.underlineThickness);
        maxY = Math.max(maxY, ulBottom);
      }

      return maxY;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(super.toString());
      buf.append("[");
      if (fgPaint != null) {
        buf.append("fgPaint: ").append(fgPaint);
      }
      if (bgPaint != null) {
        buf.append(" bgPaint: ").append(bgPaint);
      }
      if (swapColors) {
        buf.append(" swapColors: true");
      }
      if (strikethrough) {
        buf.append(" strikethrough: true");
      }
      buf.append(" stdUnderline: ").append(stdUnderline);
      buf.append(" imUnderline: ").append(imUnderline);
      buf.append("]");
      return buf.toString();
    }
  }
}
