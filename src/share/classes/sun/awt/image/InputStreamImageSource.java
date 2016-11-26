/*
 * Copyright (c) 1995, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;

public abstract class InputStreamImageSource implements ImageProducer {
  ImageConsumerQueue consumers;

  ImageDecoder decoder;
  ImageDecoder decoders;

  boolean awaitingFetch;

  abstract boolean checkSecurity(Object context, boolean quiet);

  @Override
  public void addConsumer(ImageConsumer ic) {
    addConsumer(ic, false);
  }

  @Override
  public synchronized boolean isConsumer(ImageConsumer ic) {
    for (ImageDecoder id = decoders; id != null; id = id.next) {
      if (id.isConsumer(ic)) {
        return true;
      }
    }
    return ImageConsumerQueue.isConsumer(consumers, ic);
  }

  @Override
  public synchronized void removeConsumer(ImageConsumer ic) {
    for (ImageDecoder id = decoders; id != null; id = id.next) {
      id.removeConsumer(ic);
    }
    consumers = ImageConsumerQueue.removeConsumer(consumers, ic, false);
  }

  @Override
  public void startProduction(ImageConsumer ic) {
    addConsumer(ic, true);
  }

  @Override
  public void requestTopDownLeftRightResend(ImageConsumer ic) {
  }

  synchronized void addConsumer(ImageConsumer ic, boolean produce) {
    checkSecurity(null, false);
    for (ImageDecoder id = decoders; id != null; id = id.next) {
      if (id.isConsumer(ic)) {
        // This consumer is already being fed.
        return;
      }
    }
    ImageConsumerQueue cq = consumers;
    while (cq != null && cq.consumer != ic) {
      cq = cq.next;
    }
    if (cq == null) {
      cq = new ImageConsumerQueue(this, ic);
      cq.next = consumers;
      consumers = cq;
    } else {
      if (!cq.secure) {
        Object context = null;
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
          context = security.getSecurityContext();
        }
        if (cq.securityContext == null) {
          cq.securityContext = context;
        } else if (!cq.securityContext.equals(context)) {
          // If there are two different security contexts that both
          // have a handle on the same ImageConsumer, then there has
          // been a security breach and whether or not they trade
          // image data is small fish compared to what they could be
          // trading.  Throw a Security exception anyway...
          errorConsumer(cq, false);
          throw new SecurityException("Applets are trading image data!");
        }
      }
      cq.interested = true;
    }
    if (produce && decoder == null) {
      startProduction();
    }
  }

  private void errorAllConsumers(ImageConsumerQueue cq, boolean needReload) {
    while (cq != null) {
      if (cq.interested) {
        errorConsumer(cq, needReload);
      }
      cq = cq.next;
    }
  }

  private void errorConsumer(ImageConsumerQueue cq, boolean needReload) {
    cq.consumer.imageComplete(ImageConsumer.IMAGEERROR);
    if (needReload && cq.consumer instanceof ImageRepresentation) {
      ((ImageRepresentation) cq.consumer).image.flush();
    }
    removeConsumer(cq.consumer);
  }

  private synchronized void startProduction() {
    if (!awaitingFetch) {
      if (ImageFetcher.add(this)) {
        awaitingFetch = true;
      } else {
        ImageConsumerQueue cq = consumers;
        consumers = null;
        errorAllConsumers(cq, false);
      }
    }
  }

  synchronized void doneDecoding(ImageDecoder mydecoder) {
    if (decoder == mydecoder) {
      decoder = null;
      if (consumers != null) {
        startProduction();
      }
    }
  }

  void latchConsumers(ImageDecoder id) {
    doneDecoding(id);
  }

  synchronized void flush() {
    decoder = null;
  }
}
