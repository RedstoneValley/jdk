/*
 * Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public class IconInfo {
  /**
   * Width of icon image. Being set in constructor.
   */
  private final int width;
  /**
   * Height of icon image. Being set in constructor.
   */
  private final int height;
  /**
   * Representation of image as an int array.
   * It's used on platforms where icon data
   * is expected to be in 32-bit format.
   */
  private int[] intIconData;
  /**
   * Representation of image as an long array.
   * It's used on platforms where icon data
   * is expected to be in 64-bit format.
   */
  private long[] longIconData;
  /**
   * Icon image.
   */
  private Image image;
  /**
   * Width of scaled icon image. Can be set in setScaledDimension.
   */
  private int scaledWidth;
  /**
   * Height of scaled icon image. Can be set in setScaledDimension.
   */
  private int scaledHeight;
  /**
   * Length of raw data. Being set in constructor / setScaledDimension.
   */
  private int rawLength;

  public IconInfo(int[] intIconData) {
    this.intIconData = null == intIconData ? null : Arrays.copyOf(intIconData, intIconData.length);
    width = intIconData[0];
    height = intIconData[1];
    scaledWidth = width;
    scaledHeight = height;
    rawLength = width * height + 2;
  }

  public IconInfo(long[] longIconData) {
    this.longIconData = null == longIconData ? null
        : Arrays.copyOf(longIconData, longIconData.length);
    width = (int) longIconData[0];
    height = (int) longIconData[1];
    scaledWidth = width;
    scaledHeight = height;
    rawLength = width * height + 2;
  }

  public IconInfo(Image image) {
    this.image = image;
    if (image instanceof ToolkitImage) {
      ImageRepresentation ir = ((ToolkitImage) image).getImageRep();
      ir.reconstruct(ImageObserver.ALLBITS);
      width = ir.getWidth();
      height = ir.getHeight();
    } else {
      width = image.getWidth(null);
      height = image.getHeight(null);
    }
    scaledWidth = width;
    scaledHeight = height;
    rawLength = width * height + 2;
  }

  private static int[] longArrayToIntArray(long[] longData) {
    int[] intData = new int[longData.length];
    for (int i = 0; i < longData.length; i++) {
      // Such a conversion is valid since the
      // original data (see
      // make/sun/xawt/ToBin.java) were ints
      intData[i] = (int) longData[i];
    }
    return intData;
  }

  private static long[] intArrayToLongArray(int[] intData) {
    long[] longData = new long[intData.length];
    for (int i = 0; i < intData.length; i++) {
      longData[i] = intData[i];
    }
    return longData;
  }

  static Image intArrayToImage(int[] raw) {
    ColorModel cm = new DirectColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB),
        32,
        0x00ff0000,
        0x0000ff00,
        0x000000ff,
        0xff000000,
        false,
        DataBuffer.TYPE_INT);
    DataBuffer buffer = new DataBufferInt(raw, raw.length - 2, 2);
    WritableRaster raster = Raster.createPackedRaster(
        buffer,
        raw[0],
        raw[1],
        raw[0],
        new int[]{0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000},
        null);
    return new BufferedImage(cm, raster, false, null);
  }

  /*
   * Returns array of integers which holds data for the image.
   * It scales the image if necessary.
   */
  static int[] imageToIntArray(Image image, int width, int height) {
    if (width <= 0 || height <= 0) {
      return null;
    }
    ColorModel cm = new DirectColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB),
        32,
        0x00ff0000,
        0x0000ff00,
        0x000000ff,
        0xff000000,
        false,
        DataBuffer.TYPE_INT);
    DataBufferInt buffer = new DataBufferInt(width * height);
    WritableRaster raster = Raster.createPackedRaster(
        buffer,
        width,
        height,
        width,
        new int[]{0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000},
        null);
    BufferedImage im = new BufferedImage(cm, raster, false, null);
    Graphics g = im.getGraphics();
    g.drawImage(image, 0, 0, width, height, null);
    g.dispose();
    int[] data = buffer.getData();
    int[] raw = new int[width * height + 2];
    raw[0] = width;
    raw[1] = height;
    System.arraycopy(data, 0, raw, 2, width * height);
    return raw;
  }

  /*
   * It sets size of scaled icon.
   */
  public void setScaledSize(int width, int height) {
    scaledWidth = width;
    scaledHeight = height;
    rawLength = width * height + 2;
  }

  public boolean isValid() {
    return width > 0 && height > 0;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public String toString() {
    return "IconInfo[w=" + width + ",h=" + height + ",sw=" + scaledWidth + ",sh=" + scaledHeight
        + "]";
  }

  public int getRawLength() {
    return rawLength;
  }

  public int[] getIntData() {
    if (intIconData == null) {
      if (longIconData != null) {
        intIconData = longArrayToIntArray(longIconData);
      } else if (image != null) {
        intIconData = imageToIntArray(image, scaledWidth, scaledHeight);
      }
    }
    return intIconData;
  }

  public long[] getLongData() {
    if (longIconData == null) {
      if (intIconData != null) {
        longIconData = intArrayToLongArray(intIconData);
      } else if (image != null) {
        int[] intIconData = imageToIntArray(image, scaledWidth, scaledHeight);
        longIconData = intArrayToLongArray(intIconData);
      }
    }
    return longIconData;
  }

  public Image getImage() {
    if (image == null) {
      if (intIconData != null) {
        image = intArrayToImage(intIconData);
      } else if (longIconData != null) {
        int[] intIconData = longArrayToIntArray(longIconData);
        image = intArrayToImage(intIconData);
      }
    }
    return image;
  }
}
