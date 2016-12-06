/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The abstract base class that manages the various SurfaceData objects that
 * represent an Image's contents.  Subclasses can customize how the surfaces
 * are organized, whether to cache the original contents in an accelerated
 * surface, and so on.
 * <p>
 * The SurfaceManager also maintains an arbitrary "cache" mechanism which
 * allows other agents to store data in it specific to their use of this
 * image.  The most common use of the caching mechanism is for destination
 * SurfaceData objects to store cached copies of the source image.
 */
public abstract class SurfaceManager {

  private ConcurrentHashMap<Object, Object> cacheMap;

  /**
   * Returns an ImageCapabilities object which can be
   * inquired as to the specific capabilities of this
   * Image.  The capabilities object will return true for
   * isAccelerated() if the image has a current and valid
   * SurfaceDataProxy object cached for the specified
   * GraphicsConfiguration parameter.
   * <p>
   * This class provides a default implementation of the
   * ImageCapabilities that will try to determine if there
   * is an associated SurfaceDataProxy object and if it is
   * up to date, but only works for GraphicsConfiguration
   * objects which implement the ProxiedGraphicsConfig
   * interface defined below.  In practice, all configs
   * which can be accelerated are currently implementing
   * that interface.
   * <p>
   * A null GraphicsConfiguration returns a value based on whether the
   * image is currently accelerated on its default GraphicsConfiguration.
   *
   * @see Image#getCapabilities
   * @since 1.5
   */
  public ImageCapabilities getCapabilities(GraphicsConfiguration gc) {
    return new ImageCapabilities(false);
  }

  /**
   * Releases system resources in use by ancillary SurfaceData objects,
   * such as surfaces cached in accelerated memory.  Subclasses should
   * override to release any of their flushable data.
   * <p>
   * The default implementation will visit all of the value objects
   * in the cacheMap and flush them if they implement the
   * FlushableCacheData interface.
   */
  public synchronized void flush() {
    flush(false);
  }

  synchronized void flush(boolean deaccelerate) {
    // No-op
  }

  /**
   * Called when image's acceleration priority is changed.
   * <p>
   * The default implementation will visit all of the value objects
   * in the cacheMap when the priority gets set to 0.0 and flush them
   * if they implement the FlushableCacheData interface.
   */
  public void setAccelerationPriority(float priority) {
    if (priority == 0.0f) {
      flush(true);
    }
  }


}
