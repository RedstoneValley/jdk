/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d.pisces;

import sun.awt.geom.PathConsumer2D;

/**
 * The {@code Dasher} class takes a series of linear commands
 * ({@code moveTo}, {@code lineTo}, {@code close} and
 * {@code end}) and breaks them into smaller segments according to a
 * dash pattern array and a starting dash phase.
 * <p>
 * <p> Issues: in J2Se, a zero length dash segment as drawn as a very
 * short dash, whereas Pisces does not draw anything.  The PostScript
 * semantics are unclear.
 */
final class Dasher implements PathConsumer2D {

  private final PathConsumer2D out;
  private final float[] dash;
  private final float startPhase;
  private final boolean startDashOn;
  private final int startIdx;
  // temporary storage for the current curve
  private final float[] curCurvepts;
  private boolean starting;
  private boolean needsMoveTo;
  private int idx;
  private boolean dashOn;
  private float phase;
  private float sx, sy;
  private float x0, y0;
  // We don't emit the first dash right away. If we did, caps would be
  // drawn on it, but we need joins to be drawn if there's a closePath()
  // So, we store the path elements that make up the first dash in the
  // buffer below.
  private float[] firstSegmentsBuffer = new float[7];
  private int firstSegidx;
  private LengthIterator li;

  /**
   * Constructs a {@code Dasher}.
   *
   * @param out   an output {@code PathConsumer2D}.
   * @param dash  an array of {@code float}s containing the dash pattern
   * @param phase a {@code float} containing the dash phase
   */
  public Dasher(PathConsumer2D out, float[] dash, float phase) {
    if (phase < 0) {
      throw new IllegalArgumentException("phase < 0 !");
    }

    this.out = out;

    // Normalize so 0 <= phase < dash[0]
    int idx = 0;
    dashOn = true;
    float d;
    while (phase >= (d = dash[idx])) {
      phase -= d;
      idx = (idx + 1) % dash.length;
      dashOn = !dashOn;
    }

    this.dash = dash;
    startPhase = this.phase = phase;
    startDashOn = dashOn;
    startIdx = idx;
    starting = true;

    // we need curCurvepts to be able to contain 2 curves because when
    // dashing curves, we need to subdivide it
    curCurvepts = new float[(8 << 1)];
  }

  private static boolean pointCurve(float[] curve, int type) {
    for (int i = 2; i < type; i++) {
      if (curve[i] != curve[i - 2]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void moveTo(float x0, float y0) {
    if (firstSegidx > 0) {
      out.moveTo(sx, sy);
      emitFirstSegments();
    }
    needsMoveTo = true;
    idx = startIdx;
    dashOn = startDashOn;
    phase = startPhase;
    sx = this.x0 = x0;
    sy = this.y0 = y0;
    starting = true;
  }

  @Override
  public void lineTo(float x1, float y1) {
    float dx = x1 - x0;
    float dy = y1 - y0;

    float len = (float) Math.sqrt(dx * dx + dy * dy);

    if (len == 0) {
      return;
    }

    // The scaling factors needed to get the dx and dy of the
    // transformed dash segments.
    float cx = dx / len;
    float cy = dy / len;

    while (true) {
      float leftInThisDashSegment = dash[idx] - phase;
      if (len <= leftInThisDashSegment) {
        curCurvepts[0] = x1;
        curCurvepts[1] = y1;
        goTo(curCurvepts, 0, 4);
        // Advance phase within current dash segment
        phase += len;
        if (len == leftInThisDashSegment) {
          phase = 0f;
          idx = (idx + 1) % dash.length;
          dashOn = !dashOn;
        }
        return;
      }

      float dashdx = dash[idx] * cx;
      float dashdy = dash[idx] * cy;
      if (phase == 0) {
        curCurvepts[0] = x0 + dashdx;
        curCurvepts[1] = y0 + dashdy;
      } else {
        float p = leftInThisDashSegment / dash[idx];
        curCurvepts[0] = x0 + p * dashdx;
        curCurvepts[1] = y0 + p * dashdy;
      }

      goTo(curCurvepts, 0, 4);

      len -= leftInThisDashSegment;
      // Advance to next dash segment
      idx = (idx + 1) % dash.length;
      dashOn = !dashOn;
      phase = 0;
    }
  }

  @Override
  public void quadTo(float x1, float y1, float x2, float y2) {
    curCurvepts[0] = x0;
    curCurvepts[1] = y0;
    curCurvepts[2] = x1;
    curCurvepts[3] = y1;
    curCurvepts[4] = x2;
    curCurvepts[5] = y2;
    somethingTo(6);
  }

  @Override
  public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    curCurvepts[0] = x0;
    curCurvepts[1] = y0;
    curCurvepts[2] = x1;
    curCurvepts[3] = y1;
    curCurvepts[4] = x2;
    curCurvepts[5] = y2;
    curCurvepts[6] = x3;
    curCurvepts[7] = y3;
    somethingTo(8);
  }

  @Override
  public void closePath() {
    lineTo(sx, sy);
    if (firstSegidx > 0) {
      if (!dashOn || needsMoveTo) {
        out.moveTo(sx, sy);
      }
      emitFirstSegments();
    }
    moveTo(sx, sy);
  }

  @Override
  public void pathDone() {
    if (firstSegidx > 0) {
      out.moveTo(sx, sy);
      emitFirstSegments();
    }
    out.pathDone();
  }

  @Override
  public long getNativeConsumer() {
    throw new InternalError("Dasher does not use a native consumer");
  }

  private void emitSeg(float[] buf, int off, int type) {
    switch (type) {
      case 8:
        out.curveTo(buf[off], buf[off + 1], buf[off + 2], buf[off + 3], buf[off + 4], buf[off + 5]);
        break;
      case 6:
        out.quadTo(buf[off], buf[off + 1], buf[off + 2], buf[off + 3]);
        break;
      case 4:
        out.lineTo(buf[off], buf[off + 1]);
    }
  }

  private void emitFirstSegments() {
    int i = 0;
    while (i < firstSegidx) {
      emitSeg(firstSegmentsBuffer, i + 1, (int) firstSegmentsBuffer[i]);
      i += (int) firstSegmentsBuffer[i] - 1;
    }
    firstSegidx = 0;
  }

  // precondition: pts must be in relative coordinates (relative to x0,y0)
  // fullCurve is true iff the curve in pts has not been split.
  private void goTo(float[] pts, int off, int type) {
    float x = pts[off + type - 4];
    float y = pts[off + type - 3];
    if (dashOn) {
      if (starting) {
        firstSegmentsBuffer = Helpers.widenArray(firstSegmentsBuffer, firstSegidx, type - 2);
        firstSegmentsBuffer[firstSegidx] = type;
        firstSegidx++;
        System.arraycopy(pts, off, firstSegmentsBuffer, firstSegidx, type - 2);
        firstSegidx += type - 2;
      } else {
        if (needsMoveTo) {
          out.moveTo(x0, y0);
          needsMoveTo = false;
        }
        emitSeg(pts, off, type);
      }
    } else {
      starting = false;
      needsMoveTo = true;
    }
    x0 = x;
    y0 = y;
  }

  // preconditions: curCurvepts must be an array of length at least 2 * type,
  // that contains the curve we want to dash in the first type elements
  private void somethingTo(int type) {
    if (pointCurve(curCurvepts, type)) {
      return;
    }
    if (li == null) {
      li = new LengthIterator(4, 0.01f);
    }
    li.initializeIterationOnCurve(curCurvepts, type);

    int curCurveoff = 0; // initially the current curve is at curCurvepts[0...type]
    float lastSplitT = 0;
    float t;
    float leftInThisDashSegment = dash[idx] - phase;
    while ((t = li.next(leftInThisDashSegment)) < 1) {
      if (t != 0) {
        Helpers.subdivideAt((t - lastSplitT) / (1 - lastSplitT),
            curCurvepts,
            curCurveoff,
            curCurvepts,
            0,
            curCurvepts,
            type,
            type);
        lastSplitT = t;
        goTo(curCurvepts, 2, type);
        curCurveoff = type;
      }
      // Advance to next dash segment
      idx = (idx + 1) % dash.length;
      dashOn = !dashOn;
      phase = 0;
      leftInThisDashSegment = dash[idx];
    }
    goTo(curCurvepts, curCurveoff + 2, type);
    phase += li.lastSegLen();
    if (phase >= dash[idx]) {
      phase = 0f;
      idx = (idx + 1) % dash.length;
      dashOn = !dashOn;
    }
  }

  // Objects of this class are used to iterate through curves. They return
  // t values where the left side of the curve has a specified length.
  // It does this by subdividing the input curve until a certain error
  // condition has been met. A recursive subdivision procedure would
  // return as many as 1<<limit curves, but this is an iterator and we
  // don't need all the curves all at once, so what we carry out a
  // lazy inorder traversal of the recursion tree (meaning we only move
  // through the tree when we need the next subdivided curve). This saves
  // us a lot of memory because at any one time we only need to store
  // limit+1 curves - one for each level of the tree + 1.
  // NOTE: the way we do things here is not enough to traverse a general
  // tree; however, the trees we are interested in have the property that
  // every non leaf node has exactly 2 children
  private static class LengthIterator {
    private final int limit;

    private final float ERR;
    private final float minTincrement;
    // Holds the curves at various levels of the recursion. The root
    // (i.e. the original curve) is at recCurveStack[0] (but then it
    // gets subdivided, the left half is put at 1, so most of the time
    // only the right half of the original curve is at 0)
    private final float[][] recCurveStack;
    // sides[i] indicates whether the node at level i+1 in the path from
    // the root to the current leaf is a left or right child of its parent.
    private final Side[] sides;
    // the lengths of the lines of the control polygon. Only its first
    // curveType/2 - 1 elements are valid. This is an optimization. See
    // next(float) for more detail.
    private final float[] curLeafCtrlPolyLengths = new float[3];
    // we want to avoid allocations/gc so we keep this array so we
    // can put roots in it,
    private final float[] nextRoots = new float[4];
    // caches the coefficients of the current leaf in its flattened
    // form (see inside next() for what that means). The cache is
    // invalid when it's third element is negative, since in any
    // valid flattened curve, this would be >= 0.
    private final float[] flatLeafCoefCache = {0, 0, -1, 0};
    private int curveType;
    // lastT and nextT delimit the current leaf.
    private float nextT;
    private float lenAtNextT;
    private float lastT;
    private float lenAtLastT;
    private float lenAtLastSplit;
    private float lastSegLen;
    // the current level in the recursion tree. 0 is the root. limit
    // is the deepest possible leaf.
    private int recLevel;
    private boolean done;
    // 0 == false, 1 == true, -1 == invalid cached value.
    private int cachedHaveLowAcceleration = -1;

    public LengthIterator(int reclimit, float err) {
      limit = reclimit;
      minTincrement = 1f / (1 << limit);
      ERR = err;
      recCurveStack = new float[reclimit + 1][8];
      sides = new Side[reclimit];
      // if any methods are called without first initializing this object on
      // a curve, we want it to fail ASAP.
      nextT = Float.MAX_VALUE;
      lenAtNextT = Float.MAX_VALUE;
      lenAtLastSplit = Float.MIN_VALUE;
      recLevel = Integer.MIN_VALUE;
      lastSegLen = Float.MAX_VALUE;
      done = true;
    }

    static void subdivide(
        float[] src, int srcoff, float[] left, int leftoff, float[] right, int rightoff, int type) {
      switch (type) {
        case 6:
          subdivideQuad(src, srcoff, left, leftoff, right, rightoff);
          break;
        case 8:
          subdivideCubic(src, srcoff, left, leftoff, right, rightoff);
          break;
        default:
          throw new InternalError("Unsupported curve type");
      }
    }

    static void subdivideQuad(
        float[] src, int srcoff, float[] left, int leftoff, float[] right, int rightoff) {
      float x1 = src[srcoff];
      float y1 = src[srcoff + 1];
      float ctrlx = src[srcoff + 2];
      float ctrly = src[srcoff + 3];
      float x2 = src[srcoff + 4];
      float y2 = src[srcoff + 5];
      if (left != null) {
        left[leftoff] = x1;
        left[leftoff + 1] = y1;
      }
      if (right != null) {
        right[rightoff + 4] = x2;
        right[rightoff + 5] = y2;
      }
      x1 = (x1 + ctrlx) / 2.0f;
      y1 = (y1 + ctrly) / 2.0f;
      x2 = (x2 + ctrlx) / 2.0f;
      y2 = (y2 + ctrly) / 2.0f;
      ctrlx = (x1 + x2) / 2.0f;
      ctrly = (y1 + y2) / 2.0f;
      if (left != null) {
        left[leftoff + 2] = x1;
        left[leftoff + 3] = y1;
        left[leftoff + 4] = ctrlx;
        left[leftoff + 5] = ctrly;
      }
      if (right != null) {
        right[rightoff] = ctrlx;
        right[rightoff + 1] = ctrly;
        right[rightoff + 2] = x2;
        right[rightoff + 3] = y2;
      }
    }

    /**
     * Subdivides the cubic curve specified by the coordinates
     * stored in the {@code src} array at indices {@code srcoff}
     * through ({@code srcoff}&nbsp;+&nbsp;7) and stores the
     * resulting two subdivided curves into the two result arrays at the
     * corresponding indices.
     * Either or both of the {@code left} and {@code right}
     * arrays may be {@code null} or a reference to the same array
     * as the {@code src} array.
     * Note that the last point in the first subdivided curve is the
     * same as the first point in the second subdivided curve. Thus,
     * it is possible to pass the same array for {@code left}
     * and {@code right} and to use offsets, such as {@code rightoff}
     * equals ({@code leftoff} + 6), in order
     * to avoid allocating extra storage for this common point.
     *
     * @param src      the array holding the coordinates for the source curve
     * @param srcoff   the offset into the array of the beginning of the
     *                 the 6 source coordinates
     * @param left     the array for storing the coordinates for the first
     *                 half of the subdivided curve
     * @param leftoff  the offset into the array of the beginning of the
     *                 the 6 left coordinates
     * @param right    the array for storing the coordinates for the second
     *                 half of the subdivided curve
     * @param rightoff the offset into the array of the beginning of the
     *                 the 6 right coordinates
     * @since 1.7
     */
    static void subdivideCubic(
        float[] src, int srcoff, float[] left, int leftoff, float[] right, int rightoff) {
      float x1 = src[srcoff];
      float y1 = src[srcoff + 1];
      float ctrlx1 = src[srcoff + 2];
      float ctrly1 = src[srcoff + 3];
      float ctrlx2 = src[srcoff + 4];
      float ctrly2 = src[srcoff + 5];
      float x2 = src[srcoff + 6];
      float y2 = src[srcoff + 7];
      if (left != null) {
        left[leftoff] = x1;
        left[leftoff + 1] = y1;
      }
      if (right != null) {
        right[rightoff + 6] = x2;
        right[rightoff + 7] = y2;
      }
      x1 = (x1 + ctrlx1) / 2.0f;
      y1 = (y1 + ctrly1) / 2.0f;
      x2 = (x2 + ctrlx2) / 2.0f;
      y2 = (y2 + ctrly2) / 2.0f;
      float centerx = (ctrlx1 + ctrlx2) / 2.0f;
      float centery = (ctrly1 + ctrly2) / 2.0f;
      ctrlx1 = (x1 + centerx) / 2.0f;
      ctrly1 = (y1 + centery) / 2.0f;
      ctrlx2 = (x2 + centerx) / 2.0f;
      ctrly2 = (y2 + centery) / 2.0f;
      centerx = (ctrlx1 + ctrlx2) / 2.0f;
      centery = (ctrly1 + ctrly2) / 2.0f;
      if (left != null) {
        left[leftoff + 2] = x1;
        left[leftoff + 3] = y1;
        left[leftoff + 4] = ctrlx1;
        left[leftoff + 5] = ctrly1;
        left[leftoff + 6] = centerx;
        left[leftoff + 7] = centery;
      }
      if (right != null) {
        right[rightoff] = centerx;
        right[rightoff + 1] = centery;
        right[rightoff + 2] = ctrlx2;
        right[rightoff + 3] = ctrly2;
        right[rightoff + 4] = x2;
        right[rightoff + 5] = y2;
      }
    }

    public void initializeIterationOnCurve(float[] pts, int type) {
      System.arraycopy(pts, 0, recCurveStack[0], 0, type);
      curveType = type;
      recLevel = 0;
      lastT = 0;
      lenAtLastT = 0;
      nextT = 0;
      lenAtNextT = 0;
      goLeft(); // initializes nextT and lenAtNextT properly
      lenAtLastSplit = 0;
      if (recLevel > 0) {
        sides[0] = Side.LEFT;
        done = false;
      } else {
        // the root of the tree is a leaf so we're done.
        sides[0] = Side.RIGHT;
        done = true;
      }
      lastSegLen = 0;
    }

    private boolean haveLowAcceleration(float err) {
      if (cachedHaveLowAcceleration == -1) {
        float len1 = curLeafCtrlPolyLengths[0];
        float len2 = curLeafCtrlPolyLengths[1];
        // the test below is equivalent to !within(len1/len2, 1, err).
        // It is using a multiplication instead of a division, so it
        // should be a bit faster.
        if (!Helpers.within(len1, len2, err * len2)) {
          cachedHaveLowAcceleration = 0;
          return false;
        }
        if (curveType == 8) {
          float len3 = curLeafCtrlPolyLengths[2];
          // if len1 is close to 2 and 2 is close to 3, that probably
          // means 1 is close to 3 so the second part of this test might
          // not be needed, but it doesn't hurt to include it.
          if (!(Helpers.within(len2, len3, err * len3) && Helpers.within(len1, len3, err * len3))) {
            cachedHaveLowAcceleration = 0;
            return false;
          }
        }
        cachedHaveLowAcceleration = 1;
        return true;
      }

      return cachedHaveLowAcceleration == 1;
    }

    // returns the t value where the remaining curve should be split in
    // order for the left subdivided curve to have length len. If len
    // is >= than the length of the uniterated curve, it returns 1.
    public float next(float len) {
      float targetLength = lenAtLastSplit + len;
      while (lenAtNextT < targetLength) {
        if (done) {
          lastSegLen = lenAtNextT - lenAtLastSplit;
          return 1;
        }
        goToNextLeaf();
      }
      lenAtLastSplit = targetLength;
      float leaflen = lenAtNextT - lenAtLastT;
      float t = (targetLength - lenAtLastT) / leaflen;

      // cubicRootsInAB is a fairly expensive call, so we just don't do it
      // if the acceleration in this section of the curve is small enough.
      if (!haveLowAcceleration(0.05f)) {
        // We flatten the current leaf along the x axis, so that we're
        // left with a, b, c which define a 1D Bezier curve. We then
        // solve this to get the parameter of the original leaf that
        // gives us the desired length.

        if (flatLeafCoefCache[2] < 0) {
          float x = 0 + curLeafCtrlPolyLengths[0],
              y = x + curLeafCtrlPolyLengths[1];
          if (curveType == 8) {
            float z = y + curLeafCtrlPolyLengths[2];
            flatLeafCoefCache[0] = 3 * (x - y) + z;
            flatLeafCoefCache[1] = 3 * (y - 2 * x);
            flatLeafCoefCache[2] = 3 * x;
            flatLeafCoefCache[3] = -z;
          } else if (curveType == 6) {
            flatLeafCoefCache[0] = 0f;
            flatLeafCoefCache[1] = y - 2 * x;
            flatLeafCoefCache[2] = 2 * x;
            flatLeafCoefCache[3] = -y;
          }
        }
        float a = flatLeafCoefCache[0];
        float b = flatLeafCoefCache[1];
        float c = flatLeafCoefCache[2];
        float d = t * flatLeafCoefCache[3];

        // we use cubicRootsInAB here, because we want only roots in 0, 1,
        // and our quadratic root finder doesn't filter, so it's just a
        // matter of convenience.
        int n = Helpers.cubicRootsInAB(a, b, c, d, nextRoots, 0, 0, 1);
        if (n == 1 && !Float.isNaN(nextRoots[0])) {
          t = nextRoots[0];
        }
      }
      // t is relative to the current leaf, so we must make it a valid parameter
      // of the original curve.
      t = t * (nextT - lastT) + lastT;
      if (t >= 1) {
        t = 1;
        done = true;
      }
      // even if done = true, if we're here, that means targetLength
      // is equal to, or very, very close to the total length of the
      // curve, so lastSegLen won't be too high. In cases where len
      // overshoots the curve, this method will exit in the while
      // loop, and lastSegLen will still be set to the right value.
      lastSegLen = len;
      return t;
    }

    public float lastSegLen() {
      return lastSegLen;
    }

    // go to the next leaf (in an inorder traversal) in the recursion tree
    // preconditions: must be on a leaf, and that leaf must not be the root.
    private void goToNextLeaf() {
      // We must go to the first ancestor node that has an unvisited
      // right child.
      recLevel--;
      while (sides[recLevel] == Side.RIGHT) {
        if (recLevel == 0) {
          done = true;
          return;
        }
        recLevel--;
      }

      sides[recLevel] = Side.RIGHT;
      System.arraycopy(recCurveStack[recLevel], 0, recCurveStack[recLevel + 1], 0,

          curveType);
      recLevel++;
      goLeft();
    }

    // go to the leftmost node from the current node. Return its length.
    private void goLeft() {
      float len = onLeaf();
      if (len >= 0) {
        lastT = nextT;
        lenAtLastT = lenAtNextT;
        nextT += (1 << limit - recLevel) * minTincrement;
        lenAtNextT += len;
        // invalidate caches
        flatLeafCoefCache[2] = -1;
        cachedHaveLowAcceleration = -1;
      } else {
        subdivide(recCurveStack[recLevel],
            0,
            recCurveStack[recLevel + 1],
            0,
            recCurveStack[recLevel],
            0,
            curveType);
        sides[recLevel] = Side.LEFT;
        recLevel++;
        goLeft();
      }
    }

    // this is a bit of a hack. It returns -1 if we're not on a leaf, and
    // the length of the leaf if we are on a leaf.
    private float onLeaf() {
      float[] curve = recCurveStack[recLevel];
      float polyLen = 0;

      float x0 = curve[0], y0 = curve[1];
      for (int i = 2; i < curveType; i += 2) {
        float x1 = curve[i], y1 = curve[i + 1];
        float len = Helpers.linelen(x0, y0, x1, y1);
        polyLen += len;
        curLeafCtrlPolyLengths[i / 2 - 1] = len;
        x0 = x1;
        y0 = y1;
      }

      float lineLen = Helpers.linelen(curve[0],
          curve[1],
          curve[curveType - 2],
          curve[curveType - 1]);
      if (polyLen - lineLen < ERR || recLevel == limit) {
        return (polyLen + lineLen) / 2;
      }
      return -1;
    }

    private enum Side {LEFT, RIGHT}
  }
}

