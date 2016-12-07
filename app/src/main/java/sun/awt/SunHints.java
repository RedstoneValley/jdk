/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.RenderingHints;

/**
 * This class contains rendering hints that can be used by the {@link java.awt.Graphics2D} class,
 * and classes that implement {@link java.awt.image.BufferedImageOp} and {@link
 * java.awt.image.Raster}.
 */
public final class SunHints {
  /**
   * Rendering hint key and values
   */
  public static final int INTKEY_RENDERING = 0;
  public static final int INTVAL_RENDER_DEFAULT = 0;
  public static final int INTVAL_RENDER_SPEED = 1;
  public static final int INTVAL_RENDER_QUALITY = 2;
  /**
   * Antialiasing hint key and values
   */
  public static final int INTKEY_ANTIALIASING = 1;
  public static final int INTVAL_ANTIALIAS_DEFAULT = 0;
  public static final int INTVAL_ANTIALIAS_OFF = 1;
  public static final int INTVAL_ANTIALIAS_ON = 2;
  /**
   * Text antialiasing hint key and values
   */
  public static final int INTKEY_TEXT_ANTIALIASING = 2;
  public static final int INTVAL_TEXT_ANTIALIAS_DEFAULT = 0;
  public static final int INTVAL_TEXT_ANTIALIAS_OFF = 1;
  public static final int INTVAL_TEXT_ANTIALIAS_ON = 2;
  public static final int INTVAL_TEXT_ANTIALIAS_GASP = 3;
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_HRGB = 4;
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_HBGR = 5;
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_VRGB = 6;
  public static final int INTVAL_TEXT_ANTIALIAS_LCD_VBGR = 7;
  /**
   * Font fractional metrics hint key and values
   */
  public static final int INTKEY_FRACTIONALMETRICS = 3;
  public static final int INTVAL_FRACTIONALMETRICS_DEFAULT = 0;
  public static final int INTVAL_FRACTIONALMETRICS_OFF = 1;
  public static final int INTVAL_FRACTIONALMETRICS_ON = 2;
  /**
   * Dithering hint key and values
   */
  public static final int INTKEY_DITHERING = 4;
  public static final int INTVAL_DITHER_DEFAULT = 0;
  public static final int INTVAL_DITHER_DISABLE = 1;
  public static final int INTVAL_DITHER_ENABLE = 2;
  /**
   * Interpolation hint key and values
   */
  public static final int INTKEY_INTERPOLATION = 5;
  public static final int INTVAL_INTERPOLATION_NEAREST_NEIGHBOR = 0;
  public static final int INTVAL_INTERPOLATION_BILINEAR = 1;
  public static final int INTVAL_INTERPOLATION_BICUBIC = 2;
  /**
   * Alpha interpolation hint key and values
   */
  public static final int INTKEY_ALPHA_INTERPOLATION = 6;
  public static final int INTVAL_ALPHA_INTERPOLATION_DEFAULT = 0;
  public static final int INTVAL_ALPHA_INTERPOLATION_SPEED = 1;
  public static final int INTVAL_ALPHA_INTERPOLATION_QUALITY = 2;
  /**
   * Color rendering hint key and values
   */
  public static final int INTKEY_COLOR_RENDERING = 7;
  public static final int INTVAL_COLOR_RENDER_DEFAULT = 0;
  public static final int INTVAL_COLOR_RENDER_SPEED = 1;
  public static final int INTVAL_COLOR_RENDER_QUALITY = 2;
  /**
   * Stroke normalization control hint key and values
   */
  public static final int INTKEY_STROKE_CONTROL = 8;
  public static final int INTVAL_STROKE_DEFAULT = 0;
  public static final int INTVAL_STROKE_NORMALIZE = 1;
  public static final int INTVAL_STROKE_PURE = 2;
  /**
   * Image scaling hint key and values
   */
  public static final int INTKEY_RESOLUTION_VARIANT = 9;
  public static final int INTVAL_RESOLUTION_VARIANT_DEFAULT = 0;
  public static final int INTVAL_RESOLUTION_VARIANT_OFF = 1;
  public static final int INTVAL_RESOLUTION_VARIANT_ON = 2;
  /**
   * LCD text contrast control hint key. Value is "100" to make discontiguous with the others which
   * are all enumerative and are of a different class.
   */
  public static final int INTKEY_AATEXT_LCD_CONTRAST = 100;
  /**
   * Rendering hint key and value objects
   */
  public static final Key KEY_RENDERING = new Key(INTKEY_RENDERING, "Global rendering quality key");
  /**
   * Antialiasing hint key and value objects
   */
  public static final Key KEY_ANTIALIASING = new Key(INTKEY_ANTIALIASING,
      "Global antialiasing enable key");
  /**
   * Text antialiasing hint key and value objects
   */
  public static final Key KEY_TEXT_ANTIALIASING = new Key(INTKEY_TEXT_ANTIALIASING,
      "Text-specific antialiasing enable key");
  /**
   * Font fractional metrics hint key and value objects
   */
  public static final Key KEY_FRACTIONALMETRICS = new Key(
      INTKEY_FRACTIONALMETRICS,
      "Fractional metrics enable key");
  /**
   * Dithering hint key and value objects
   */
  public static final Key KEY_DITHERING = new Key(INTKEY_DITHERING, "Dithering quality key");
  /**
   * Interpolation hint key and value objects
   */
  public static final Key KEY_INTERPOLATION = new Key(INTKEY_INTERPOLATION,
      "Image interpolation method key");
  /**
   * Alpha interpolation hint key and value objects
   */
  public static final Key KEY_ALPHA_INTERPOLATION = new Key(INTKEY_ALPHA_INTERPOLATION,
      "Alpha blending interpolation method key");
  /**
   * Color rendering hint key and value objects
   */
  public static final Key KEY_COLOR_RENDERING = new Key(INTKEY_COLOR_RENDERING,
      "Color rendering quality key");
  /**
   * Stroke normalization control hint key and value objects
   */
  public static final Key KEY_STROKE_CONTROL = new Key(INTKEY_STROKE_CONTROL,
      "Stroke normalization control key");
  /**
   * Image resolution variant hint key and value objects
   */
  public static final Key KEY_RESOLUTION_VARIANT = new Key(
      INTKEY_RESOLUTION_VARIANT,
      "Global image resolution variant key");
  /**
   * LCD text contrast hint key
   */
  public static final RenderingHints.Key KEY_TEXT_ANTIALIAS_LCD_CONTRAST = new LCDContrastKey
      (INTKEY_AATEXT_LCD_CONTRAST,
          "Text-specific LCD contrast key");
  private static final int NUM_KEYS = 10;
  private static final int VALS_PER_KEY = 8;

  private SunHints() {
  }

  /**
   * Defines the type of all keys used to control various aspects of the rendering and imaging
   * pipelines.  Instances of this class are immutable and unique which means that tests for matches
   * can be made using the == operator instead of the more expensive equals() method.
   */
  public static class Key extends RenderingHints.Key {
    final String description;

    /**
     * Construct a key using the indicated private key.  Each subclass of Key maintains its own
     * unique domain of integer keys.  No two objects with the same integer key and of the same
     * specific subclass can be constructed.  An exception will be thrown if an attempt is made to
     * construct another object of a given class with the same integer key as a pre-existing
     * instance of that subclass of Key.
     */
    public Key(int privatekey, String description) {
      super(privatekey);
      this.description = description;
    }

    /**
     * Returns the numeric index associated with this Key.  This is useful for use in switch
     * statements and quick lookups of the setting of a particular key.
     */
    public final int getIndex() {
      return intKey();
    }

    /**
     * Returns true if the specified object is a valid value for this Key.
     */
    @Override
    public boolean isCompatibleValue(Object val) {
      return val instanceof Value && ((Value) val).isCompatibleKey(this);
    }

    /**
     * Returns a string representation of the Key.
     */
    public final String toString() {
      return description;
    }
  }

  /**
   * Defines the type of all "enumerative" values used to control various aspects of the rendering
   * and imaging pipelines.  Instances of this class are immutable and unique which means that tests
   * for matches can be made using the == operator instead of the more expensive equals() method.
   */
  public enum Value {
    VALUE_RENDER_SPEED(KEY_RENDERING,
        INTVAL_RENDER_SPEED,
        "Fastest rendering methods"),
    VALUE_RENDER_QUALITY(KEY_RENDERING,
        INTVAL_RENDER_QUALITY,
        "Highest quality rendering methods"),
    VALUE_RENDER_DEFAULT(KEY_RENDERING,
        INTVAL_RENDER_DEFAULT,
        "Default rendering methods"),
    VALUE_ANTIALIAS_ON(KEY_ANTIALIASING,
        INTVAL_ANTIALIAS_ON,
        "Antialiased rendering mode"),
    VALUE_ANTIALIAS_OFF(KEY_ANTIALIASING,
        INTVAL_ANTIALIAS_OFF,
        "Nonantialiased rendering mode"),
    VALUE_ANTIALIAS_DEFAULT(KEY_ANTIALIASING,
        INTVAL_ANTIALIAS_DEFAULT,
        "Default antialiasing rendering mode"),
    VALUE_TEXT_ANTIALIAS_ON(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_ON,
        "Antialiased text mode"),
    VALUE_TEXT_ANTIALIAS_OFF(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_OFF,
        "Nonantialiased text mode"),
    VALUE_TEXT_ANTIALIAS_DEFAULT(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_DEFAULT,
        "Default antialiasing text mode"),
    VALUE_TEXT_ANTIALIAS_GASP(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_GASP,
        "gasp antialiasing text mode"),
    VALUE_TEXT_ANTIALIAS_LCD_HRGB(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_LCD_HRGB,
        "LCD HRGB antialiasing text mode"),
    VALUE_TEXT_ANTIALIAS_LCD_HBGR(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_LCD_HBGR,
        "LCD HBGR antialiasing text mode"),
    VALUE_TEXT_ANTIALIAS_LCD_VRGB(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_LCD_VRGB,
        "LCD VRGB antialiasing text mode"),
    VALUE_TEXT_ANTIALIAS_LCD_VBGR(KEY_TEXT_ANTIALIASING,
        INTVAL_TEXT_ANTIALIAS_LCD_VBGR,
        "LCD VBGR antialiasing text mode"),
    VALUE_FRACTIONALMETRICS_ON(KEY_FRACTIONALMETRICS,
        INTVAL_FRACTIONALMETRICS_ON,
        "Fractional text metrics mode"),
    VALUE_FRACTIONALMETRICS_OFF(KEY_FRACTIONALMETRICS,
        INTVAL_FRACTIONALMETRICS_OFF,
        "Integer text metrics mode"),
    VALUE_FRACTIONALMETRICS_DEFAULT(KEY_FRACTIONALMETRICS,
        INTVAL_FRACTIONALMETRICS_DEFAULT,
        "Default fractional text metrics mode"),
    VALUE_DITHER_ENABLE(KEY_DITHERING,
        INTVAL_DITHER_ENABLE,
        "Dithered rendering mode"),
    VALUE_DITHER_DISABLE(KEY_DITHERING,
        INTVAL_DITHER_DISABLE,
        "Nondithered rendering mode"),
    VALUE_DITHER_DEFAULT(KEY_DITHERING,
        INTVAL_DITHER_DEFAULT,
        "Default dithering mode"),
    VALUE_INTERPOLATION_NEAREST_NEIGHBOR(KEY_INTERPOLATION,
        INTVAL_INTERPOLATION_NEAREST_NEIGHBOR,
        "Nearest Neighbor image interpolation mode"),
    VALUE_INTERPOLATION_BILINEAR(KEY_INTERPOLATION,
        INTVAL_INTERPOLATION_BILINEAR,
        "Bilinear image interpolation mode"),
    VALUE_INTERPOLATION_BICUBIC(KEY_INTERPOLATION,
        INTVAL_INTERPOLATION_BICUBIC,
        "Bicubic image interpolation mode"),
    VALUE_ALPHA_INTERPOLATION_SPEED(KEY_ALPHA_INTERPOLATION,
        INTVAL_ALPHA_INTERPOLATION_SPEED,
        "Fastest alpha blending methods"),
    VALUE_ALPHA_INTERPOLATION_QUALITY(KEY_ALPHA_INTERPOLATION,
        INTVAL_ALPHA_INTERPOLATION_QUALITY,
        "Highest quality alpha blending methods"),
    VALUE_ALPHA_INTERPOLATION_DEFAULT(KEY_ALPHA_INTERPOLATION,
        INTVAL_ALPHA_INTERPOLATION_DEFAULT,
        "Default alpha blending methods"),
    VALUE_COLOR_RENDER_SPEED(KEY_COLOR_RENDERING,
        INTVAL_COLOR_RENDER_SPEED,
        "Fastest color rendering mode"),
    VALUE_COLOR_RENDER_QUALITY(KEY_COLOR_RENDERING,
        INTVAL_COLOR_RENDER_QUALITY,
        "Highest quality color rendering mode"),
    VALUE_COLOR_RENDER_DEFAULT(KEY_COLOR_RENDERING,
        INTVAL_COLOR_RENDER_DEFAULT,
        "Default color rendering mode"),
    VALUE_STROKE_DEFAULT(KEY_STROKE_CONTROL,
        INTVAL_STROKE_DEFAULT,
        "Default stroke normalization"),
    VALUE_STROKE_NORMALIZE(KEY_STROKE_CONTROL,
        INTVAL_STROKE_NORMALIZE,
        "Normalize strokes for consistent rendering"),
    VALUE_STROKE_PURE(KEY_STROKE_CONTROL,
        INTVAL_STROKE_PURE,
        "Pure stroke conversion for accurate paths"),
    VALUE_RESOLUTION_VARIANT_DEFAULT(KEY_RESOLUTION_VARIANT,
        INTVAL_RESOLUTION_VARIANT_DEFAULT,
        "Choose image resolutions based on a default heuristic"),
    VALUE_RESOLUTION_VARIANT_OFF(KEY_RESOLUTION_VARIANT,
        INTVAL_RESOLUTION_VARIANT_OFF,
        "Use only the standard resolution of an image"),
    VALUE_RESOLUTION_VARIANT_ON(KEY_RESOLUTION_VARIANT,
        INTVAL_RESOLUTION_VARIANT_ON,
        "Always use resolution-specific variants of images");
    private static final Value[][] ValueObjects = new Value[NUM_KEYS][VALS_PER_KEY];
    private final Key myKey;
    private final int index;
    private final String description;

    /**
     * Construct a value using the indicated private index.  Each subclass of Value maintains its
     * own unique domain of integer indices.  Enforcing the uniqueness of the integer indices is
     * left to the subclass.
     */
    Value(Key key, int index, String description) {
      myKey = key;
      this.index = index;
      this.description = description;

      register(key, this);
    }

    private static synchronized void register(Key key, Value value) {
      int kindex = key.getIndex();
      int vindex = value.getIndex();
      if (ValueObjects[kindex][vindex] != null) {
        throw new InternalError("duplicate index: " + vindex);
      }
      ValueObjects[kindex][vindex] = value;
    }

    public static Value get(int keyindex, int valueindex) {
      return ValueObjects[keyindex][valueindex];
    }

    /**
     * Returns the numeric index associated with this Key.  This is useful for use in switch
     * statements and quick lookups of the setting of a particular key.
     */
    public final int getIndex() {
      return index;
    }

    /**
     * Returns true if the specified object is a valid Key for this Value.
     */
    public final boolean isCompatibleKey(Key k) {
      return myKey == k;
    }

    /**
     * Returns a string representation of this Value.
     */
    public final String toString() {
      return description;
    }
  }

  public static class LCDContrastKey extends Key {

    public LCDContrastKey(int privatekey, String description) {
      super(privatekey, description);
    }

    /**
     * Returns true if the specified object is a valid value for this Key. The allowable range is
     * 100 to 250.
     */
    @Override
    public final boolean isCompatibleValue(Object val) {
      if (val instanceof Integer) {
        int ival = (Integer) val;
        return ival >= 100 && ival <= 250;
      }
      return false;
    }
  }
}
