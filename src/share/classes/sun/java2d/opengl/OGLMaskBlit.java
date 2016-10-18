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
import static sun.java2d.loops.SurfaceType.IntArgb;
import static sun.java2d.loops.SurfaceType.IntArgbPre;
import static sun.java2d.loops.SurfaceType.IntBgr;
import static sun.java2d.loops.SurfaceType.IntRgb;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.BufferedMaskBlit;
import sun.java2d.pipe.Region;

final class OGLMaskBlit extends BufferedMaskBlit {

  private OGLMaskBlit(SurfaceType srcType, CompositeType compType) {
    super(OGLRenderQueue.getInstance(), srcType, compType, OGLSurfaceData.OpenGLSurface);
  }

  static void register() {
    GraphicsPrimitive[] primitives = {
        new OGLMaskBlit(IntArgb, SrcOver), new OGLMaskBlit(IntArgbPre, SrcOver),
        new OGLMaskBlit(IntRgb, SrcOver), new OGLMaskBlit(IntRgb, SrcNoEa),
        new OGLMaskBlit(IntBgr, SrcOver), new OGLMaskBlit(IntBgr, SrcNoEa),};
    GraphicsPrimitiveMgr.register(primitives);
  }

  @Override
  protected void validateContext(SurfaceData dstData, Composite comp, Region clip) {
    OGLSurfaceData oglDst = (OGLSurfaceData) dstData;
    BufferedContext.validateContext(oglDst,
        oglDst,
        clip,
        comp,
        null,
        null,
        null,
        OGLContext.NO_CONTEXT_FLAGS);
  }
}
