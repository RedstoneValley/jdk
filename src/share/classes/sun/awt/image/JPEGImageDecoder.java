/*
 * Copyright (c) 1995, 2012, Oracle and/or its affiliates. All rights reserved.
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
/*-
 *      Reads JPEG images from an InputStream and reports the
 *      image data to an InputStreamImageSource object.
 *
 * The native implementation of the JPEG image decoder was adapted from
 * release 6 of the free JPEG software from the Independent JPEG Group.
 */
package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

/**
 * JPEG Image converter
 *
 * @author Jim Graham
 */
public class JPEGImageDecoder extends ImageDecoder {
  private static final Class InputStreamClass = InputStream.class;
  /**
   * The ImageConsumer hints flag for a JPEG image.
   */
  private static final int hintflags = ImageConsumer.TOPDOWNLEFTRIGHT
      | ImageConsumer.COMPLETESCANLINES |
      ImageConsumer.SINGLEFRAME;
  private static final ColorModel RGBcolormodel;
  private static final ColorModel ARGBcolormodel;
  private static final ColorModel Graycolormodel;

  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      @Override
      public Void run() {
        System.loadLibrary("jpeg");
        return null;
      }
    });
    RGBcolormodel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
    ARGBcolormodel = ColorModel.getRGBdefault();
    byte[] g = new byte[256];
    for (int i = 0; i < 256; i++) {
      g[i] = (byte) i;
    }
    Graycolormodel = new IndexColorModel(8, 256, g, g, g);
  }

  final Hashtable props = new Hashtable();
  private ColorModel colormodel;

  public JPEGImageDecoder(InputStreamImageSource src, InputStream is) {
    super(src, is);
  }

  private void readImage(InputStream is, byte[] buf)
      throws ImageFormatException, IOException {
    // TODO: Native in OpenJDK AWT
  }

  /**
   * produce an image from the stream.
   */
  @Override
  public void produceImage() throws IOException, ImageFormatException {
    try {
      readImage(input, new byte[1024]);
      if (!aborted) {
        imageComplete(ImageConsumer.STATICIMAGEDONE, true);
      }
    } catch (IOException e) {
      if (!aborted) {
        throw e;
      }
    } finally {
      close();
    }
  }
}
