/*
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
 * @test
 * @bug 6752638
 * @summary Test no NPE calling preferLocaleFonts() on custom GE.
 * @run main PreferLocaleFonts
 */

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Locale;
import sun.font.FontManager;

public class PreferLocaleFonts extends GraphicsEnvironment {

    public static void main(String[] args) {
new PreferLocaleFonts().preferLocaleFonts();
    }
    public PreferLocaleFonts() {
    }
    @Override
    public Graphics2D createGraphics(BufferedImage image) {
        return null;
    }
    public String[] getAvailableFontFamilyNames(Locale locale) {
        return null;
    }
    public String[] getAvailableFontFamilyNames() {
        return null;
    }
    @Override
    public Font[] getAllFonts() {
        return null;
    }
    @Override
    public GraphicsDevice getDefaultScreenDevice() {
        return null;
    }
    @Override
    public GraphicsDevice[] getScreenDevices() {
        return null;
    }

  @Override
  public boolean isHeadlessInstance() {
    return false;
  }

  @Override
  public boolean registerFont(Font font) {
    if (font == null) {
      throw new NullPointerException("font cannot be null.");
    }
    FontManager fm = FontManager.getInstance();
    return fm.registerFont(font);
  }

  @Override
  public void preferLocaleFonts() {
    FontManager fm = FontManager.getInstance();
    fm.preferLocaleFonts();
  }

  @Override
  public void preferProportionalFonts() {
    FontManager fm = FontManager.getInstance();
    fm.preferProportionalFonts();
  }

  @Override
  public Point getCenterPoint() throws HeadlessException {
    // Default implementation: return the center of the usable bounds of the
    // default screen device.
    Rectangle usableBounds = getUsableBounds(getDefaultScreenDevice());
    return new Point(usableBounds.width / 2 + usableBounds.x,
        usableBounds.height / 2 + usableBounds.y);
  }

  @Override
  public Rectangle getMaximumWindowBounds() throws HeadlessException {
    // Default implementation: return the usable bounds of the default screen
    // device.  This is correct for Microsoft Windows and non-Xinerama X11.
    return getUsableBounds(getDefaultScreenDevice());
  }
}

