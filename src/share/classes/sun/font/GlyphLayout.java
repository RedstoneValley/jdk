/*
 * Copyright (c) 2003, 2008, Oracle and/or its affiliates. All rights reserved.
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

/*
 *
 * (C) Copyright IBM Corp. 1999-2003 - All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by IBM. These materials are provided
 * under terms of a License Agreement between IBM and Sun.
 * This technology is protected by multiple US and International
 * patents. This notice and attribution to IBM may not be removed.
 */

/*
 * GlyphLayout is used to process a run of text into a run of run of
 * glyphs, optionally with position and char mapping info.
 *
 * The text has already been processed for numeric shaping and bidi.
 * The run of text that layout works on has a single bidi level.  It
 * also has a single font/style.  Some operations need context to work
 * on (shaping, script resolution) so context for the text run text is
 * provided.  It is assumed that the text array contains sufficient
 * context, and the offset and count delimit the portion of the text
 * that needs to actually be processed.
 *
 * The font might be a composite font.  Layout generally requires
 * tables from a single physical font to operate, and so it must
 * resolve the 'single' font run into runs of physical fonts.
 *
 * Some characters are supported by several fonts of a composite, and
 * in order to properly emulate the glyph substitution behavior of a
 * single physical font, these characters might need to be mapped to
 * different physical fonts.  The script code that is assigned
 * characters normally considered 'common script' can be used to
 * resolve which physical font to use for these characters. The input
 * to the char to glyph mapper (which assigns physical fonts as it
 * processes the glyphs) should include the script code, and the
 * mapper should operate on runs of a single script.
 *
 * To perform layout, call get() to get a new (or reuse an old)
 * GlyphLayout, call layout on it, then call done(GlyphLayout) when
 * finished.  There's no particular problem if you don't call done,
 * but it assists in reuse of the GlyphLayout.
 */

package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public final class GlyphLayout {
  // data for glyph vector
  private GVData _gvdata;

  private int _ercount;
  private Point2D.Float _pt;
  private float[] _mat;
  private int _typo_flags;

  /**
   * Return the old instance of GlyphLayout when you are done.  This enables reuse
   * of GlyphLayout objects.
   */
  public static void done(GlyphLayout gl) {
  }

  private static final class SDCache {
    public Font key_font;
    public FontRenderContext key_frc;

    public AffineTransform dtx;
    public AffineTransform invdtx;
    public AffineTransform gtx;
    public Point2D.Float delta;

    private SDCache(Font font, FontRenderContext frc) {
      key_font = font;
      key_frc = frc;

      // !!! add getVectorTransform and hasVectorTransform to frc?  then
      // we could just skip this work...

      dtx = frc.getTransform();
      dtx.setTransform(dtx.getScaleX(), dtx.getShearY(),
              dtx.getShearX(), dtx.getScaleY(),
              0, 0);
      if (!dtx.isIdentity()) {
        try {
          invdtx = dtx.createInverse();
        }
        catch (NoninvertibleTransformException e) {
          throw new InternalError();
        }
      }

      float ptSize = font.getSize2D();
      if (font.isTransformed()) {
        gtx = font.getTransform();
        gtx.scale(ptSize, ptSize);
        delta = new Point2D.Float((float)gtx.getTranslateX(),
                (float)gtx.getTranslateY());
        gtx.setTransform(gtx.getScaleX(), gtx.getShearY(),
                gtx.getShearX(), gtx.getScaleY(),
                0, 0);
        gtx.preConcatenate(dtx);
      } else {
        delta = ZERO_DELTA;
        gtx = new AffineTransform(dtx);
        gtx.scale(ptSize, ptSize);
      }
    }

    private static final Point2D.Float ZERO_DELTA = new Point2D.Float();

    private static
    SoftReference<ConcurrentHashMap<SDKey, SDCache>> cacheRef;

    private static final class SDKey {
      private final Font font;
      private final FontRenderContext frc;
      private final int hash;

      SDKey(Font font, FontRenderContext frc) {
        this.font = font;
        this.frc = frc;
        this.hash = font.hashCode() ^ frc.hashCode();
      }

      public int hashCode() {
        return hash;
      }

      public boolean equals(Object o) {
        try {
          SDKey rhs = (SDKey)o;
          return
                  hash == rhs.hash &&
                          font.equals(rhs.font) &&
                          frc.equals(rhs.frc);
        }
        catch (ClassCastException e) {
        }
        return false;
      }
    }

    public static SDCache get(Font font, FontRenderContext frc) {

      // It is possible a translation component will be in the FRC.
      // It doesn't affect us except adversely as we would consider
      // FRC's which are really the same to be different. If we
      // detect a translation component, then we need to exclude it
      // by creating a new transform which excludes the translation.
      if (frc.isTransformed()) {
        AffineTransform transform = frc.getTransform();
        if (transform.getTranslateX() != 0 ||
                transform.getTranslateY() != 0) {
          transform = new AffineTransform(transform.getScaleX(),
                  transform.getShearY(),
                  transform.getShearX(),
                  transform.getScaleY(),
                  0, 0);
          frc = new FontRenderContext(transform,
                  frc.getAntiAliasingHint(),
                  frc.getFractionalMetricsHint()
          );
        }
      }

      SDKey key = new SDKey(font, frc); // garbage, yuck...
      ConcurrentHashMap<SDKey, SDCache> cache = null;
      SDCache res = null;
      if (cacheRef != null) {
        cache = cacheRef.get();
        if (cache != null) {
          res = cache.get(key);
        }
      }
      if (res == null) {
        res = new SDCache(font, frc);
        if (cache == null) {
          cache = new ConcurrentHashMap<SDKey, SDCache>(10);
          cacheRef = new
                  SoftReference<ConcurrentHashMap<SDKey, SDCache>>(cache);
        } else if (cache.size() >= 512) {
          cache.clear();
        }
        cache.put(key, res);
      }
      return res;
    }
  }

  /**
   * Create a glyph vector.
   * @param font the font to use
   * @param frc the font render context
   * @param text the text, including optional context before start and after start + count
   * @param offset the start of the text to lay out
   * @param count the length of the text to lay out
   * @param flags bidi and context flags {@see #java.awt.Font}
   * @param result a StandardGlyphVector to modify, can be null
   * @return the layed out glyphvector, if result was passed in, it is returned
   */
  public StandardGlyphVector layout(Font font, FontRenderContext frc,
                                    char[] text, int offset, int count,
                                    int flags, StandardGlyphVector result)
  {
    if (text == null || offset < 0 || count < 0 || (count > text.length - offset)) {
      throw new IllegalArgumentException();
    }

    init(count);

    // need to set after init
    // go through the back door for this
    if (font.hasLayoutAttributes()) {
      AttributeValues values = (AttributeValues) (font.getAttributes());
      if (values.getKerning() != 0) _typo_flags |= 0x1;
      if (values.getLigatures() != 0) _typo_flags |= 0x2;
    }

    // use cache now - can we use the strike cache for this?

    SDCache txinfo = SDCache.get(font, frc);
    _mat[0] = (float)txinfo.gtx.getScaleX();
    _mat[1] = (float)txinfo.gtx.getShearY();
    _mat[2] = (float)txinfo.gtx.getShearX();
    _mat[3] = (float)txinfo.gtx.getScaleY();
    _pt.setLocation(txinfo.delta);

    int lim = offset + count;

    int min = 0;
    int max = text.length;
    if (flags != 0) {
      if ((flags & Font.LAYOUT_RIGHT_TO_LEFT) != 0) {
        _typo_flags |= 0x80000000; // RTL
      }

      if ((flags & Font.LAYOUT_NO_START_CONTEXT) != 0) {
        min = offset;
      }

      if ((flags & Font.LAYOUT_NO_LIMIT_CONTEXT) != 0) {
        max = lim;
      }
    }

     //        if (txinfo.invdtx != null) {
    //            _gvdata.adjustPositions(txinfo.invdtx);
    //        }

    StandardGlyphVector gv = _gvdata.createGlyphVector(font, frc, result);
    //        System.err.println("Layout returns: " + gv);
    return gv;
  }

  //
  // private methods
  //

  public GlyphLayout() {
    this._gvdata = new GVData();
    this._pt = new Point2D.Float();
    this._mat = new float[4];
  }

  private void init(int capacity) {
    this._typo_flags = 0;
    this._ercount = 0;
    this._gvdata.init(capacity);
  }

  /**
   * Storage for layout to build glyph vector data, then generate a real GlyphVector
   */
  public static final class GVData {
    public int _count; // number of glyphs, >= number of chars
    public int _flags;
    public int[] _glyphs;
    public float[] _positions;
    public int[] _indices;

    private static final int UNINITIALIZED_FLAGS = -1;

    public void init(int size) {
      _count = 0;
      _flags = UNINITIALIZED_FLAGS;

      if (_glyphs == null || _glyphs.length < size) {
        if (size < 20) {
          size = 20;
        }
        _glyphs = new int[size];
        _positions = new float[size * 2 + 2];
        _indices = new int[size];
      }
    }

    public void grow() {
      grow(_glyphs.length / 4); // always grows because min length is 20
    }

    public void grow(int delta) {
      int size = _glyphs.length + delta;
      int[] nglyphs = new int[size];
      System.arraycopy(_glyphs, 0, nglyphs, 0, _count);
      _glyphs = nglyphs;

      float[] npositions = new float[size * 2 + 2];
      System.arraycopy(_positions, 0, npositions, 0, _count * 2 + 2);
      _positions = npositions;

      int[] nindices = new int[size];
      System.arraycopy(_indices, 0, nindices, 0, _count);
      _indices = nindices;
    }

    public void adjustPositions(AffineTransform invdtx) {
      invdtx.transform(_positions, 0, _positions, 0, _count);
    }

    public StandardGlyphVector createGlyphVector(Font font, FontRenderContext frc, StandardGlyphVector result) {

      // !!! default initialization until we let layout engines do it
      if (_flags == UNINITIALIZED_FLAGS) {
        _flags = 0;

        if (_count > 1) { // if only 1 glyph assume LTR
          boolean ltr = true;
          boolean rtl = true;

          int rtlix = _count; // rtl index
          for (int i = 0; i < _count && (ltr || rtl); ++i) {
            int cx = _indices[i];

            ltr = ltr && (cx == i);
            rtl = rtl && (cx == --rtlix);
          }

          if (rtl) _flags |= GlyphVector.FLAG_RUN_RTL;
          if (!rtl && !ltr) _flags |= GlyphVector.FLAG_COMPLEX_GLYPHS;
        }

        // !!! layout engines need to tell us whether they performed
        // position adjustments. currently they don't tell us, so
        // we must assume they did
        _flags |= GlyphVector.FLAG_HAS_POSITION_ADJUSTMENTS;
      }

      int[] glyphs = new int[_count];
      System.arraycopy(_glyphs, 0, glyphs, 0, _count);

      float[] positions = null;
      if ((_flags & GlyphVector.FLAG_HAS_POSITION_ADJUSTMENTS) != 0) {
        positions = new float[_count * 2 + 2];
        System.arraycopy(_positions, 0, positions, 0, positions.length);
      }

      int[] indices = null;
      if ((_flags & GlyphVector.FLAG_COMPLEX_GLYPHS) != 0) {
        indices = new int[_count];
        System.arraycopy(_indices, 0, indices, 0, _count);
      }

      if (result == null) {
        result = new StandardGlyphVector(font, frc, glyphs, positions, indices, _flags);
      } else {
        result.initGlyphVector(font, frc, glyphs, positions, indices, _flags);
      }

      return result;
    }
  }
}
