/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.pipe;

import static sun.java2d.pipe.BufferedOpCodes.DRAW_AAPARALLELOGRAM;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_LINE;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_PARALLELOGRAM;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_PIXEL;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_POLY;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_RECT;
import static sun.java2d.pipe.BufferedOpCodes.DRAW_SCANLINES;
import static sun.java2d.pipe.BufferedOpCodes.FILL_AAPARALLELOGRAM;
import static sun.java2d.pipe.BufferedOpCodes.FILL_PARALLELOGRAM;
import static sun.java2d.pipe.BufferedOpCodes.FILL_RECT;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.ProcessPath;
import sun.java2d.loops.ProcessPath.DrawHandler;

/**
 * Base class for enqueuing rendering operations in a single-threaded
 * rendering environment.  Instead of each operation being rendered
 * immediately by the underlying graphics library, the operation will be
 * added to the provided RenderQueue, which will be processed at a later
 * time by a single thread.
 * <p>
 * This class provides implementations of drawLine(), drawRect(), drawPoly(),
 * fillRect(), draw(Shape), and fill(Shape), which are useful for a
 * hardware-accelerated renderer.  The other draw*() and fill*() methods
 * simply delegate to draw(Shape) and fill(Shape), respectively.
 */
public abstract class BufferedRenderPipe
    implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, ParallelogramPipe {
  protected static final int OPSIZE_DRAW_LINE = 20;
  protected static final int OPSIZE_DRAW_RECT = 20;
  protected static final int OPSIZE_FILL_RECT = 20;
  protected static final int OPSIZE_DRAW_POLY_HEADER = 20;
  protected static final int OPSIZE_FILL_SPANS = 24;
  protected static final int OPSIZE_FILL_PARALLELOGRAM = 28;
  protected static final int OPSIZE_DRAW_PIXEL = 12;
  protected static final int OPSIZE_START_FILL_PATH = 20;
  protected static final int OPSIZE_DRAW_PARALLELOGRAM = 36;
  protected static final int OPSIZE_FILL_AAPARALLELOGRAM = OPSIZE_FILL_PARALLELOGRAM;
  protected static final int OPSIZE_DRAW_AAPARALLELOGRAM = OPSIZE_DRAW_PARALLELOGRAM;
  static final int BYTES_PER_POLY_POINT = 8;
  static final int BYTES_PER_SCANLINE = 12;
  static final int BYTES_PER_SPAN = 16;
  private final BufferedDrawHandler drawHandler;
  protected final RenderQueue rq;
  protected final RenderBuffer buf;
  final ParallelogramPipe aapgrampipe = new AAParallelogramPipe();

  public BufferedRenderPipe(RenderQueue rq) {
    this.rq = rq;
    buf = rq.getBuffer();
    drawHandler = new BufferedDrawHandler();
  }

  public ParallelogramPipe getAAParallelogramPipe() {
    return aapgrampipe;
  }

  /**
   * Validates the state in the provided SunGraphics2D object and sets up
   * any special resources for this operation (e.g. enabling gradient
   * shading).
   */
  protected abstract void validateContext(SunGraphics2D sg2d);

  protected abstract void validateContextAA(SunGraphics2D sg2d);

  @Override
  public void drawLine(SunGraphics2D sg2d, int x1, int y1, int x2, int y2) {
    int transx = sg2d.transX;
    int transy = sg2d.transY;
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_DRAW_LINE);
      buf.putInt(DRAW_LINE);
      buf.putInt(x1 + transx);
      buf.putInt(y1 + transy);
      buf.putInt(x2 + transx);
      buf.putInt(y2 + transy);
    } finally {
      rq.unlock();
    }
  }

  @Override
  public void drawRect(SunGraphics2D sg2d, int x, int y, int width, int height) {
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_DRAW_RECT);
      buf.putInt(DRAW_RECT);
      buf.putInt(x + sg2d.transX);
      buf.putInt(y + sg2d.transY);
      buf.putInt(width);
      buf.putInt(height);
    } finally {
      rq.unlock();
    }
  }

  @Override
  public void drawRoundRect(
      SunGraphics2D sg2d, int x, int y, int width, int height, int arcWidth, int arcHeight) {
    draw(sg2d, new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
  }

  @Override
  public void drawOval(SunGraphics2D sg2d, int x, int y, int width, int height) {
    draw(sg2d, new Ellipse2D.Float(x, y, width, height));
  }

  @Override
  public void drawArc(
      SunGraphics2D sg2d, int x, int y, int width, int height, int startAngle, int arcAngle) {
    draw(sg2d, new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
  }

  @Override
  public void drawPolyline(SunGraphics2D sg2d, int[] xPoints, int[] yPoints, int nPoints) {
    drawPoly(sg2d, xPoints, yPoints, nPoints, false);
  }

  @Override
  public void drawPolygon(SunGraphics2D sg2d, int[] xPoints, int[] yPoints, int nPoints) {
    drawPoly(sg2d, xPoints, yPoints, nPoints, true);
  }

  @Override
  public void fillRect(SunGraphics2D sg2d, int x, int y, int width, int height) {
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_FILL_RECT);
      buf.putInt(FILL_RECT);
      buf.putInt(x + sg2d.transX);
      buf.putInt(y + sg2d.transY);
      buf.putInt(width);
      buf.putInt(height);
    } finally {
      rq.unlock();
    }
  }

  @Override
  public void fillRoundRect(
      SunGraphics2D sg2d, int x, int y, int width, int height, int arcWidth, int arcHeight) {
    fill(sg2d, new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
  }

  @Override
  public void fillOval(SunGraphics2D sg2d, int x, int y, int width, int height) {
    fill(sg2d, new Ellipse2D.Float(x, y, width, height));
  }

  @Override
  public void fillArc(
      SunGraphics2D sg2d, int x, int y, int width, int height, int startAngle, int arcAngle) {
    fill(sg2d, new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
  }

  @Override
  public void fillPolygon(SunGraphics2D sg2d, int[] xPoints, int[] yPoints, int nPoints) {
    fill(sg2d, new Polygon(xPoints, yPoints, nPoints));
  }

  protected void drawPoly(
      SunGraphics2D sg2d, int[] xPoints, int[] yPoints, int nPoints, boolean isClosed) {
    if (xPoints == null || yPoints == null) {
      throw new NullPointerException("coordinate array");
    }
    if (xPoints.length < nPoints || yPoints.length < nPoints) {
      throw new ArrayIndexOutOfBoundsException("coordinate array");
    }

    if (nPoints < 2) {
      // render nothing
      return;
    }
    if (nPoints == 2 && !isClosed) {
      // render a simple line
      drawLine(sg2d, xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
      return;
    }

    rq.lock();
    try {
      validateContext(sg2d);

      int pointBytesRequired = nPoints * BYTES_PER_POLY_POINT;
      int totalBytesRequired = OPSIZE_DRAW_POLY_HEADER + pointBytesRequired;

      if (totalBytesRequired <= buf.capacity()) {
        if (totalBytesRequired > buf.remaining()) {
          // process the queue first and then enqueue the points
          rq.flushNow();
        }
        buf.putInt(DRAW_POLY);
        // enqueue parameters
        buf.putInt(nPoints);
        buf.putInt(isClosed ? 1 : 0);
        buf.putInt(sg2d.transX);
        buf.putInt(sg2d.transY);
        // enqueue the points
        buf.put(xPoints, 0, nPoints);
        buf.put(yPoints, 0, nPoints);
      } else {
        // queue is too small to accommodate all points; perform the
        // operation directly on the queue flushing thread
        rq.flushAndInvokeNow(new Runnable() {
          @Override
          public void run() {
            drawPoly(xPoints, yPoints, nPoints, isClosed, sg2d.transX, sg2d.transY);
          }
        });
      }
    } finally {
      rq.unlock();
    }
  }

  protected abstract void drawPoly(
      int[] xPoints, int[] yPoints, int nPoints, boolean isClosed, int transX, int transY);

  protected void drawPath(SunGraphics2D sg2d, Path2D.Float p2df, int transx, int transy) {
    rq.lock();
    try {
      validateContext(sg2d);
      drawHandler.validate(sg2d);
      ProcessPath.drawPath(drawHandler, p2df, transx, transy);
    } finally {
      rq.unlock();
    }
  }

  protected void fillPath(SunGraphics2D sg2d, Path2D.Float p2df, int transx, int transy) {
    rq.lock();
    try {
      validateContext(sg2d);
      drawHandler.validate(sg2d);
      drawHandler.startFillPath();
      ProcessPath.fillPath(drawHandler, p2df, transx, transy);
      drawHandler.endFillPath();
    } finally {
      rq.unlock();
    }
  }

  private int fillSpans(
      RenderQueue rq, long buf, int pos, int limit, SpanIterator si, long iterator, int transx,
      int transy) {
    // TODO: In OpenJDK AWT, this is native
    return 0;
  }

  protected void fillSpans(SunGraphics2D sg2d, SpanIterator si, int transx, int transy) {
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_FILL_SPANS); // so that we have room for at least a span
      int newpos = fillSpans(rq,
          buf.getAddress(),
          buf.position(),
          buf.capacity(),
          si,
          si.getNativeIterator(),
          transx,
          transy);
      buf.position(newpos);
    } finally {
      rq.unlock();
    }
  }

  @Override
  public void fillParallelogram(
      SunGraphics2D sg2d, double ux1, double uy1, double ux2, double uy2, double x, double y,
      double dx1, double dy1, double dx2, double dy2) {
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_FILL_PARALLELOGRAM);
      buf.putInt(FILL_PARALLELOGRAM);
      buf.putFloat((float) x);
      buf.putFloat((float) y);
      buf.putFloat((float) dx1);
      buf.putFloat((float) dy1);
      buf.putFloat((float) dx2);
      buf.putFloat((float) dy2);
    } finally {
      rq.unlock();
    }
  }

  @Override
  public void draw(SunGraphics2D sg2d, Shape s) {
    if (sg2d.strokeState == SunGraphics2D.STROKE_THIN) {
      if (s instanceof Polygon) {
        if (sg2d.transformState < SunGraphics2D.TRANSFORM_TRANSLATESCALE) {
          Polygon p = (Polygon) s;
          drawPolygon(sg2d, p.xpoints, p.ypoints, p.npoints);
          return;
        }
      }
      Path2D.Float p2df;
      int transx, transy;
      if (sg2d.transformState <= SunGraphics2D.TRANSFORM_INT_TRANSLATE) {
        p2df = s instanceof Path2D.Float ? (Path2D.Float) s : new Path2D.Float(s);
        transx = sg2d.transX;
        transy = sg2d.transY;
      } else {
        p2df = new Path2D.Float(s, sg2d.transform);
        transx = 0;
        transy = 0;
      }
      drawPath(sg2d, p2df, transx, transy);
    } else if (sg2d.strokeState < SunGraphics2D.STROKE_CUSTOM) {
      ShapeSpanIterator si = LoopPipe.getStrokeSpans(sg2d, s);
      try {
        fillSpans(sg2d, si, 0, 0);
      } finally {
        si.dispose();
      }
    } else {
      fill(sg2d, sg2d.stroke.createStrokedShape(s));
    }
  }

  @Override
  public void fill(SunGraphics2D sg2d, Shape s) {
    int transx, transy;

    if (sg2d.strokeState == SunGraphics2D.STROKE_THIN) {
      // Here we are able to use fillPath() for
      // high-quality fills.
      Path2D.Float p2df;
      if (sg2d.transformState <= SunGraphics2D.TRANSFORM_INT_TRANSLATE) {
        p2df = s instanceof Path2D.Float ? (Path2D.Float) s : new Path2D.Float(s);
        transx = sg2d.transX;
        transy = sg2d.transY;
      } else {
        p2df = new Path2D.Float(s, sg2d.transform);
        transx = 0;
        transy = 0;
      }
      fillPath(sg2d, p2df, transx, transy);
      return;
    }

    AffineTransform at;
    if (sg2d.transformState <= SunGraphics2D.TRANSFORM_INT_TRANSLATE) {
      // Transform (translation) will be done by FillSpans (we could
      // delegate to fillPolygon() here, but most hardware accelerated
      // libraries cannot handle non-convex polygons, so we will use
      // the FillSpans approach by default)
      at = null;
      transx = sg2d.transX;
      transy = sg2d.transY;
    } else {
      // Transform will be done by the PathIterator
      at = sg2d.transform;
      transx = transy = 0;
    }

    ShapeSpanIterator ssi = LoopPipe.getFillSSI(sg2d);
    try {
      // Subtract transx/y from the SSI clip to match the
      // (potentially untranslated) geometry fed to it
      Region clip = sg2d.getCompClip();
      ssi.setOutputAreaXYXY(clip.getLoX() - transx,
          clip.getLoY() - transy,
          clip.getHiX() - transx,
          clip.getHiY() - transy);
      ssi.appendPath(s.getPathIterator(at));
      fillSpans(sg2d, ssi, transx, transy);
    } finally {
      ssi.dispose();
    }
  }

  private class BufferedDrawHandler extends DrawHandler {
    /**
     * fillPath() support...
     */

    private int scanlineCount;
    private int scanlineCountIndex;
    private int remainingScanlines;

    BufferedDrawHandler() {
      // these are bogus values; the caller will use validate()
      // to ensure that they are set properly prior to each usage
      super(0, 0, 0, 0);
    }

    /**
     * This method needs to be called prior to each draw/fillPath()
     * operation to ensure the clip bounds are up to date.
     */
    void validate(SunGraphics2D sg2d) {
      Region clip = sg2d.getCompClip();
      setBounds(clip.getLoX(), clip.getLoY(), clip.getHiX(), clip.getHiY(), sg2d.strokeHint);
    }

    /**
     * drawPath() support...
     */

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
      // assert rq.lock.isHeldByCurrentThread();
      rq.ensureCapacity(OPSIZE_DRAW_LINE);
      buf.putInt(DRAW_LINE);
      buf.putInt(x1);
      buf.putInt(y1);
      buf.putInt(x2);
      buf.putInt(y2);
    }

    @Override
    public void drawPixel(int x, int y) {
      // assert rq.lock.isHeldByCurrentThread();
      rq.ensureCapacity(OPSIZE_DRAW_PIXEL);
      buf.putInt(DRAW_PIXEL);
      buf.putInt(x);
      buf.putInt(y);
    }

    @Override
    public void drawScanline(int x1, int x2, int y) {
      if (remainingScanlines == 0) {
        updateScanlineCount();
        rq.flushNow();
        resetFillPath();
      }
      buf.putInt(x1);
      buf.putInt(x2);
      buf.putInt(y);
      scanlineCount++;
      remainingScanlines--;
    }

    private void resetFillPath() {
      buf.putInt(DRAW_SCANLINES);
      scanlineCountIndex = buf.position();
      buf.putInt(0);
      scanlineCount = 0;
      remainingScanlines = buf.remaining() / BYTES_PER_SCANLINE;
    }

    private void updateScanlineCount() {
      buf.putInt(scanlineCountIndex, scanlineCount);
    }

    /**
     * Called from fillPath() to indicate that we are about to
     * start issuing drawScanline() calls.
     */
    public void startFillPath() {
      rq.ensureCapacity(OPSIZE_START_FILL_PATH); // to ensure room for at least a scanline
      resetFillPath();
    }

    /**
     * Called from fillPath() to indicate that we are done
     * issuing drawScanline() calls.
     */
    public void endFillPath() {
      updateScanlineCount();
    }
  }

  private class AAParallelogramPipe implements ParallelogramPipe {
    AAParallelogramPipe() {
    }

    @Override
    public void fillParallelogram(
        SunGraphics2D sg2d, double ux1, double uy1, double ux2, double uy2, double x, double y,
        double dx1, double dy1, double dx2, double dy2) {
      rq.lock();
      try {
        validateContextAA(sg2d);
        rq.ensureCapacity(OPSIZE_FILL_AAPARALLELOGRAM);
        buf.putInt(FILL_AAPARALLELOGRAM);
        buf.putFloat((float) x);
        buf.putFloat((float) y);
        buf.putFloat((float) dx1);
        buf.putFloat((float) dy1);
        buf.putFloat((float) dx2);
        buf.putFloat((float) dy2);
      } finally {
        rq.unlock();
      }
    }

    @Override
    public void drawParallelogram(
        SunGraphics2D sg2d, double ux1, double uy1, double ux2, double uy2, double x, double y,
        double dx1, double dy1, double dx2, double dy2, double lw1, double lw2) {
      rq.lock();
      try {
        validateContextAA(sg2d);
        rq.ensureCapacity(OPSIZE_DRAW_AAPARALLELOGRAM);
        buf.putInt(DRAW_AAPARALLELOGRAM);
        buf.putFloat((float) x);
        buf.putFloat((float) y);
        buf.putFloat((float) dx1);
        buf.putFloat((float) dy1);
        buf.putFloat((float) dx2);
        buf.putFloat((float) dy2);
        buf.putFloat((float) lw1);
        buf.putFloat((float) lw2);
      } finally {
        rq.unlock();
      }
    }
  }  @Override
  public void drawParallelogram(
      SunGraphics2D sg2d, double ux1, double uy1, double ux2, double uy2, double x, double y,
      double dx1, double dy1, double dx2, double dy2, double lw1, double lw2) {
    rq.lock();
    try {
      validateContext(sg2d);
      rq.ensureCapacity(OPSIZE_DRAW_PARALLELOGRAM);
      buf.putInt(DRAW_PARALLELOGRAM);
      buf.putFloat((float) x);
      buf.putFloat((float) y);
      buf.putFloat((float) dx1);
      buf.putFloat((float) dy1);
      buf.putFloat((float) dx2);
      buf.putFloat((float) dy2);
      buf.putFloat((float) lw1);
      buf.putFloat((float) lw2);
    } finally {
      rq.unlock();
    }
  }


}
