/*
 * Copyright (c) 1995, 2003, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class ImageDecoder {
  final InputStreamImageSource source;
  protected boolean aborted;
  protected boolean finished;
  InputStream input;
  final Thread feeder;
  ImageConsumerQueue queue;
  ImageDecoder next;

  public ImageDecoder(InputStreamImageSource src, InputStream is) {
    source = src;
    input = is;
    feeder = Thread.currentThread();
  }

  public boolean isConsumer(ImageConsumer ic) {
    return ImageConsumerQueue.isConsumer(queue, ic);
  }

  public void removeConsumer(ImageConsumer ic) {
    queue = ImageConsumerQueue.removeConsumer(queue, ic, false);
    if (!finished && queue == null) {
      abort();
    }
  }

  protected ImageConsumerQueue nextConsumer(ImageConsumerQueue cq) {
    synchronized (source) {
      if (aborted) {
        return null;
      }
      cq = cq == null ? queue : cq.next;
      while (cq != null) {
        if (cq.interested) {
          return cq;
        }
        cq = cq.next;
      }
    }
    return null;
  }

  protected int setDimensions(int w, int h) {
    ImageConsumerQueue cq = null;
    int count = 0;
    while ((cq = nextConsumer(cq)) != null) {
      cq.consumer.setDimensions(w, h);
      count++;
    }
    return count;
  }

  protected int setPixels(
      int x, int y, int w, int h, ColorModel model, byte[] pix, int off, int scansize) {
    source.latchConsumers(this);
    ImageConsumerQueue cq = null;
    int count = 0;
    while ((cq = nextConsumer(cq)) != null) {
      cq.consumer.setPixels(x, y, w, h, model, pix, off, scansize);
      count++;
    }
    return count;
  }

  protected int setPixels(
      int x, int y, int w, int h, ColorModel model, int[] pix, int off, int scansize) {
    source.latchConsumers(this);
    ImageConsumerQueue cq = null;
    int count = 0;
    while ((cq = nextConsumer(cq)) != null) {
      cq.consumer.setPixels(x, y, w, h, model, pix, off, scansize);
      count++;
    }
    return count;
  }

  protected int imageComplete(int status, boolean done) {
    source.latchConsumers(this);
    if (done) {
      finished = true;
      source.doneDecoding(this);
    }
    ImageConsumerQueue cq = null;
    int count = 0;
    while ((cq = nextConsumer(cq)) != null) {
      cq.consumer.imageComplete(status);
      count++;
    }
    return count;
  }

  public abstract void produceImage() throws IOException, ImageFormatException;

  public void abort() {
    aborted = true;
    source.doneDecoding(this);
    close();
    AccessController.doPrivileged(new PrivilegedAction() {
      @Override
      public Object run() {
        feeder.interrupt();
        return null;
      }
    });
  }

  public synchronized void close() {
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
      }
    }
  }
}
