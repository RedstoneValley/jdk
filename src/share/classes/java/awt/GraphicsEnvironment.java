/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.image.BufferedImage;
import java.util.Locale;
import skinjob.SkinJobGlobals;
import skinjob.internal.SkinJobGraphicsEnvironment;

/**
 * The {@code GraphicsEnvironment} class describes the collection
 * of {@link GraphicsDevice} objects and {@link Font} objects
 * available to a Java(tm) application on a particular platform.
 * The resources in this {@code GraphicsEnvironment} might be local
 * or on a remote machine.  {@code GraphicsDevice} objects can be
 * screens, printers or image buffers and are the destination of
 * {@link Graphics2D} drawing methods.  Each {@code GraphicsDevice}
 * has a number of {@link GraphicsConfiguration} objects associated with
 * it.  These objects specify the different configurations in which the
 * {@code GraphicsDevice} can be used.
 *
 * @see GraphicsDevice
 * @see GraphicsConfiguration
 */

public abstract class GraphicsEnvironment {
  private static GraphicsEnvironment localEnv;

  /**
   * Singleton unless subclassed.
   */
  protected GraphicsEnvironment() {
  }

  /**
   * Returns the local {@code GraphicsEnvironment}.
   *
   * @return the local {@code GraphicsEnvironment}
   */
  public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
    if (localEnv == null) {
      localEnv = createGE();
    }

    return localEnv;
  }

  /**
   * Creates and returns the GraphicsEnvironment, according to the
   * system property 'java.awt.graphicsenv'.
   *
   * @return the graphics environment
   */
  private static GraphicsEnvironment createGE() {
    GraphicsEnvironment ge;
    String nm = System.getProperty("java.awt.graphicsenv");
    if (nm == null) {
      return new SkinJobGraphicsEnvironment(SkinJobGlobals.getAndroidApplicationContext());
    }
    try {
      //          long t0 = System.currentTimeMillis();
      Class<GraphicsEnvironment> geCls;
      try {
        // First we try if the bootclassloader finds the requested
        // class. This way we can avoid to run in a privileged block.
        geCls = (Class<GraphicsEnvironment>) Class.forName(nm);
      } catch (ClassNotFoundException ex) {
        // If the bootclassloader fails, we try again with the
        // application classloader.
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        geCls = (Class<GraphicsEnvironment>) Class.forName(nm, true, cl);
      }
      ge = geCls.newInstance();
      //          long t1 = System.currentTimeMillis();
      //          System.out.println("GE creation took " + (t1-t0)+ "ms.");
    } catch (ClassNotFoundException e) {
      throw new Error("Could not find class: " + nm);
    } catch (InstantiationException e) {
      throw new Error("Could not instantiate Graphics Environment: " + nm);
    } catch (IllegalAccessException e) {
      throw new Error("Could not access Graphics Environment: " + nm);
    }
    return ge;
  }

  /**
   * Included for backward-compatibility.
   *
   * @return false
   * @since 1.4
   */
  public static boolean isHeadless() {
    return false;
  }

  /**
   * Return the bounds of a GraphicsDevice, less its screen insets.
   * See also java.awt.GraphicsEnvironment.getUsableBounds();
   */
  public static Rectangle getUsableBounds(GraphicsDevice gd) {
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
    Rectangle usableBounds = gc.getBounds();

    usableBounds.x += insets.left;
    usableBounds.y += insets.top;
    usableBounds.width -= insets.left + insets.right;
    usableBounds.height -= insets.top + insets.bottom;

    return usableBounds;
  }

  /**
   * Included for backward-compatibility.
   *
   * @return false
   * @since 1.4
   */
  public abstract boolean isHeadlessInstance();

  /**
   * Returns an array of all of the screen {@code GraphicsDevice}
   * objects.
   *
   * @return an array containing all the {@code GraphicsDevice}
   * objects that represent screen devices
   * @see #isHeadless()
   */
  public abstract GraphicsDevice[] getScreenDevices();

  /**
   * Returns the default screen {@code GraphicsDevice}.
   *
   * @return the {@code GraphicsDevice} that represents the
   * default screen device
   * @throws HeadlessException if isHeadless() returns true
   * @see #isHeadless()
   */
  public abstract GraphicsDevice getDefaultScreenDevice();

  /**
   * Returns a {@code Graphics2D} object for rendering into the
   * specified {@link BufferedImage}.
   *
   * @param img the specified {@code BufferedImage}
   * @return a {@code Graphics2D} to be used for rendering into
   * the specified {@code BufferedImage}
   * @throws NullPointerException if {@code img} is null
   */
  public abstract Graphics2D createGraphics(BufferedImage img);

  /**
   * Returns an array containing a one-point size instance of all fonts
   * available in this {@code GraphicsEnvironment}.  Typical usage
   * would be to allow a user to select a particular font.  Then, the
   * application can size the font and set various font attributes by
   * calling the {@code deriveFont} method on the chosen instance.
   * <p>
   * This method provides for the application the most precise control
   * over which {@code Font} instance is used to render text.
   * If a font in this {@code GraphicsEnvironment} has multiple
   * programmable variations, only one
   * instance of that {@code Font} is returned in the array, and
   * other variations must be derived by the application.
   * <p>
   * If a font in this environment has multiple programmable variations,
   * such as Multiple-Master fonts, only one instance of that font is
   * returned in the {@code Font} array.  The other variations
   * must be derived by the application.
   *
   * @return an array of {@code Font} objects
   * @see #getAvailableFontFamilyNames
   * @see Font
   * @see Font#deriveFont
   * @see Font#getFontName
   * @since 1.2
   */
  public abstract Font[] getAllFonts();

  /**
   * Returns an array containing the names of all font families in this
   * {@code GraphicsEnvironment} localized for the default locale,
   * as returned by {@code Locale.getDefault()}.
   * <p>
   * Typical usage would be for presentation to a user for selection of
   * a particular family name. An application can then specify this name
   * when creating a font, in conjunction with a style, such as bold or
   * italic, giving the font system flexibility in choosing its own best
   * match among multiple fonts in the same font family.
   *
   * @return an array of {@code String} containing font family names
   * localized for the default locale, or a suitable alternative
   * name if no name exists for this locale.
   * @see #getAllFonts
   * @see Font
   * @see Font#getFamily
   * @since 1.2
   */
  public abstract String[] getAvailableFontFamilyNames();

  /**
   * Returns an array containing the names of all font families in this
   * {@code GraphicsEnvironment} localized for the specified locale.
   * <p>
   * Typical usage would be for presentation to a user for selection of
   * a particular family name. An application can then specify this name
   * when creating a font, in conjunction with a style, such as bold or
   * italic, giving the font system flexibility in choosing its own best
   * match among multiple fonts in the same font family.
   *
   * @param l a {@link Locale} object that represents a
   *          particular geographical, political, or cultural region.
   *          Specifying {@code null} is equivalent to
   *          specifying {@code Locale.getDefault()}.
   * @return an array of {@code String} containing font family names
   * localized for the specified {@code Locale}, or a
   * suitable alternative name if no name exists for the specified locale.
   * @see #getAllFonts
   * @see Font
   * @see Font#getFamily
   * @since 1.2
   */
  public abstract String[] getAvailableFontFamilyNames(Locale l);

  /**
   * Registers a <i>created</i> {@code Font}in this
   * {@code GraphicsEnvironment}.
   * A created font is one that was returned from calling
   * {@link Font#createFont}, or derived from a created font by
   * calling {@link Font#deriveFont}.
   * After calling this method for such a font, it is available to
   * be used in constructing new {@code Font}s by name or family name,
   * and is enumerated by {@link #getAvailableFontFamilyNames} and
   * {@link #getAllFonts} within the execution context of this
   * application or applet. This means applets cannot register fonts in
   * a way that they are visible to other applets.
   * <p>
   * Reasons that this method might not register the font and therefore
   * return {@code false} are:
   * <ul>
   * <li>The font is not a <i>created</i> {@code Font}.
   * <li>The font conflicts with a non-created {@code Font} already
   * in this {@code GraphicsEnvironment}. For example if the name
   * is that of a system font, or a logical font as described in the
   * documentation of the {@link Font} class. It is implementation dependent
   * whether a font may also conflict if it has the same family name
   * as a system font.
   * <p>Notice that an application can supersede the registration
   * of an earlier created font with a new one.
   * </ul>
   *
   * @return true if the {@code font} is successfully
   * registered in this {@code GraphicsEnvironment}.
   * @throws NullPointerException if {@code font} is null
   * @since 1.6
   */
  public abstract boolean registerFont(Font font);

  /**
   * Indicates a preference for locale-specific fonts in the mapping of
   * logical fonts to physical fonts. Calling this method indicates that font
   * rendering should primarily use fonts specific to the primary writing
   * system (the one indicated by the default encoding and the initial
   * default locale). For example, if the primary writing system is
   * Japanese, then characters should be rendered using a Japanese font
   * if possible, and other fonts should only be used for characters for
   * which the Japanese font doesn't have glyphs.
   * <p>
   * The actual change in font rendering behavior resulting from a call
   * to this method is implementation dependent; it may have no effect at
   * all, or the requested behavior may already match the default behavior.
   * The behavior may differ between font rendering in lightweight
   * and peered components.  Since calling this method requests a
   * different font, clients should expect different metrics, and may need
   * to recalculate window sizes and layout. Therefore this method should
   * be called before user interface initialisation.
   *
   * @since 1.5
   */
  public abstract void preferLocaleFonts();

  /**
   * Indicates a preference for proportional over non-proportional (e.g.
   * dual-spaced CJK fonts) fonts in the mapping of logical fonts to
   * physical fonts. If the default mapping contains fonts for which
   * proportional and non-proportional variants exist, then calling
   * this method indicates the mapping should use a proportional variant.
   * <p>
   * The actual change in font rendering behavior resulting from a call to
   * this method is implementation dependent; it may have no effect at all.
   * The behavior may differ between font rendering in lightweight and
   * peered components. Since calling this method requests a
   * different font, clients should expect different metrics, and may need
   * to recalculate window sizes and layout. Therefore this method should
   * be called before user interface initialisation.
   *
   * @since 1.5
   */
  public abstract void preferProportionalFonts();

  /**
   * Returns the Point where Windows should be centered.
   * It is recommended that centered Windows be checked to ensure they fit
   * within the available display area using getMaximumWindowBounds().
   *
   * @return the point where Windows should be centered
   * @throws HeadlessException if isHeadless() returns true
   * @see #getMaximumWindowBounds
   * @since 1.4
   */
  public abstract Point getCenterPoint() throws HeadlessException;

  /**
   * Returns the maximum bounds for centered Windows.
   * These bounds account for objects in the native windowing system such as
   * task bars and menu bars.  The returned bounds will reside on a single
   * display with one exception: on multi-screen systems where Windows should
   * be centered across all displays, this method returns the bounds of the
   * entire display area.
   * <p>
   * To get the usable bounds of a single display, use
   * {@code GraphicsConfiguration.getBounds()} and
   * {@code Toolkit.getScreenInsets()}.
   *
   * @return the maximum bounds for centered Windows
   * @throws HeadlessException if isHeadless() returns true
   * @see #getCenterPoint
   * @see GraphicsConfiguration#getBounds
   * @see Toolkit#getScreenInsets
   * @since 1.4
   */
  public abstract Rectangle getMaximumWindowBounds() throws HeadlessException;
}
