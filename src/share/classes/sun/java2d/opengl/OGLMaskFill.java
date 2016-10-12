/*
 * Copyright (c) 2003, 2007, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.opengl;

import static sun.java2d.loops.CompositeType.SrcNoEa;
import static sun.java2d.loops.CompositeType.SrcOver;
import static sun.java2d.loops.SurfaceType.AnyColor;
import static sun.java2d.loops.SurfaceType.GradientPaint;
import static sun.java2d.loops.SurfaceType.LinearGradientPaint;
import static sun.java2d.loops.SurfaceType.OpaqueColor;
import static sun.java2d.loops.SurfaceType.OpaqueGradientPaint;
import static sun.java2d.loops.SurfaceType.OpaqueLinearGradientPaint;
import static sun.java2d.loops.SurfaceType.OpaqueRadialGradientPaint;
import static sun.java2d.loops.SurfaceType.OpaqueTexturePaint;
import static sun.java2d.loops.SurfaceType.RadialGradientPaint;
import static sun.java2d.loops.SurfaceType.TexturePaint;

import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedMaskFill;

class OGLMaskFill extends BufferedMaskFill {

  protected OGLMaskFill(SurfaceType srcType, CompositeType compType) {
    super(OGLRenderQueue.getInstance(), srcType, compType, OGLSurfaceData.OpenGLSurface);
  }

  static void register() {
    GraphicsPrimitive[] primitives = {
        new OGLMaskFill(AnyColor, SrcOver), new OGLMaskFill(OpaqueColor, SrcNoEa),
        new OGLMaskFill(GradientPaint, SrcOver), new OGLMaskFill(OpaqueGradientPaint, SrcNoEa),
        new OGLMaskFill(LinearGradientPaint, SrcOver),
        new OGLMaskFill(OpaqueLinearGradientPaint, SrcNoEa),
        new OGLMaskFill(RadialGradientPaint, SrcOver),
        new OGLMaskFill(OpaqueRadialGradientPaint, SrcNoEa), new OGLMaskFill(TexturePaint, SrcOver),
        new OGLMaskFill(OpaqueTexturePaint, SrcNoEa),};
    GraphicsPrimitiveMgr.register(primitives);
  }

  @Override
  protected native void maskFill(
      int x, int y, int w, int h, int maskoff, int maskscan, int masklen, byte[] mask);

  @Override
  protected void validateContext(SunGraphics2D sg2d, Composite comp, int ctxflags) {
    OGLSurfaceData dstData = (OGLSurfaceData) sg2d.surfaceData;
    OGLContext.validateContext(dstData,
        dstData,
        sg2d.getCompClip(),
        comp,
        null,
        sg2d.paint,
        sg2d,
        ctxflags);
  }
}
