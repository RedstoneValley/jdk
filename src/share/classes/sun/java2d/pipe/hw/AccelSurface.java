/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.pipe.hw;

import sun.java2d.Surface;

/**
 * Abstraction for a hardware accelerated surface.
 */
public interface AccelSurface extends BufferedContextProvider, Surface {
  /**
   * Undefined
   */
  @Native int UNDEFINED = 0;
  /**
   * Window (or window substitute) surface
   */
  @Native int WINDOW = 1;
  /**
   * Render-To Plain surface (pbuffer for OpenGL, Render Target surface
   * for Direct3D)
   */
  @Native int RT_PLAIN = 2;
  /**
   * Texture surface
   */
  @Native int TEXTURE = 3;
  /**
   * A back-buffer surface (SwapChain surface for Direct3D, backbuffer for
   * OpenGL)
   */
  @Native int FLIP_BACKBUFFER = 4;
  /**
   * Render-To Texture surface (fbobject for OpenGL, texture with render-to
   * attribute for Direct3D)
   */
  @Native int RT_TEXTURE = 5;

  /**
   * Returns a pointer to the native surface data associated with this
   * surface.
   * Note: this pointer is only valid on the rendering thread.
   *
   * @return pointer to the native surface's data
   */
  long getNativeOps();

  /**
   * Marks this surface dirty.
   */
  void markDirty();

  /**
   * Returns whether the pipeline considers this surface valid. A surface
   * may become invalid if it is disposed of, or resized.
   *
   * @return true if valid, false otherwise
   */
  boolean isValid();

  /**
   * Returns whether this surface is lost. The return value is only valid
   * on the render thread, meaning that even if this method returns
   * {@code true} it could be lost in the next moment unless it is called
   * on the rendering thread.
   *
   * @return true if the surface is known to be lost, false otherwise
   */
  boolean isSurfaceLost();
}
