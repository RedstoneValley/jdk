/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import android.util.Log;

import java.awt.Adjustable;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.MouseWheelEvent;

/*
 * ScrollPaneWheelScroller is a helper class for implmenenting mouse wheel
 * scrolling on a java.awt.ScrollPane.  It contains only static methods.
 * No objects of this class may be instantiated, thus it is declared abstract.
 */
public final class ScrollPaneWheelScroller {
  private static final String TAG = "ScrollPaneWheelScroller";

  private ScrollPaneWheelScroller() {
  }

  /*
   * Called from ScrollPane.processMouseWheelEvent()
   */
  public static void handleWheelScrolling(ScrollPane sp, MouseWheelEvent e) {
    Log.v(TAG, "x = " + e.getX() + ", y = " + e.getY() + ", src is " + e.getSource());
    int increment;

    if (sp != null && e.getScrollAmount() != 0) {
      Adjustable adj = getAdjustableToScroll(sp);
      if (adj != null) {
        increment = getIncrementFromAdjustable(adj, e);
        Log.v(TAG, "increment from adjustable(" + adj.getClass() + ") : " + increment);
        scrollAdjustable(adj, increment);
      }
    }
  }

  /*
   * Given a ScrollPane, determine which Scrollbar should be scrolled by the
   * mouse wheel, if any.
   */
  public static Adjustable getAdjustableToScroll(ScrollPane sp) {
    int policy = sp.getScrollbarDisplayPolicy();

    // if policy is display always or never, use vert
    if (policy == ScrollPane.SCROLLBARS_ALWAYS || policy == ScrollPane.SCROLLBARS_NEVER) {
      Log.v(TAG, "using vertical scrolling due to scrollbar policy");
      return sp.getVAdjustable();
    } else {

      Insets ins = sp.getInsets();
      int vertScrollWidth = sp.getVScrollbarWidth();

      Log.v(TAG, "insets: l = " + ins.left + ", r = " + ins.right +
          ", t = " + ins.top + ", b = " + ins.bottom);
      Log.v(TAG, "vertScrollWidth = " + vertScrollWidth);

      // Check if scrollbar is showing by examining insets of the
      // ScrollPane
      if (ins.right >= vertScrollWidth) {
        Log.v(TAG, "using vertical scrolling because scrollbar is present");
        return sp.getVAdjustable();
      } else {
        int horizScrollHeight = sp.getHScrollbarHeight();
        if (ins.bottom >= horizScrollHeight) {
          Log.v(TAG, "using horiz scrolling because scrollbar is present");
          return sp.getHAdjustable();
        } else {
          Log.v(TAG, "using NO scrollbar becsause neither is present");
          return null;
        }
      }
    }
  }

  /*
   * Given the info in a MouseWheelEvent and an Adjustable to scroll, return
   * the amount by which the Adjustable should be adjusted.  This value may
   * be positive or negative.
   */
  public static int getIncrementFromAdjustable(
      Adjustable adj, MouseWheelEvent e) {
    if (adj == null) {
      Log.d(TAG, "Assertion (adj != null) failed");
    }

    int increment = 0;

    if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
      increment = e.getUnitsToScroll() * adj.getUnitIncrement();
    } else if (e.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
      increment = adj.getBlockIncrement() * e.getWheelRotation();
    }
    return increment;
  }

  /*
   * Scroll the given Adjustable by the given amount.  Checks the Adjustable's
   * bounds and sets the new value to the Adjustable.
   */
  public static void scrollAdjustable(Adjustable adj, int amount) {
    if (adj == null) {
      Log.d(TAG, "Assertion (adj != null) failed");
    }
    if (amount == 0) {
      Log.d(TAG, "Assertion (amount != 0) failed");
    }

    int current = adj.getValue();
    int upperLimit = adj.getMaximum() - adj.getVisibleAmount();
    Log.v(TAG, "doScrolling by " + amount);

    if (amount > 0 && current < upperLimit) { // still some room to scroll
      // down
      if (current + amount < upperLimit) {
        adj.setValue(current + amount);
        return;
      } else {
        adj.setValue(upperLimit);
        return;
      }
    } else if (amount < 0 && current > adj.getMinimum()) { // still some room
      // to scroll up
      if (current + amount > adj.getMinimum()) {
        adj.setValue(current + amount);
        return;
      } else {
        adj.setValue(adj.getMinimum());
        return;
      }
    }
  }
}
