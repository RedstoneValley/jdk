/*
 * Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 */


/*
 * (C) Copyright IBM Corp. 2003, All Rights Reserved.
 * This technology is protected by multiple US and International
 * patents. This notice and attribution to IBM may not be removed.
 */

package j2dbench.tests.text;

import j2dbench.Group;
import j2dbench.Result;
import j2dbench.TestEnvironment;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.Bidi;
import java.util.ArrayList;

public abstract class TextMeasureTests extends TextTests {
  static Group measureroot;
  static Group measuretestroot;

  public TextMeasureTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
  }

  public static void init() {
    measureroot = new Group(textroot, "Measuring", "Measuring Benchmarks");
    measuretestroot = new Group(measureroot, "tests", "Measuring Tests");

    new StringWidth();
    new StringBounds();
    new CharsWidth();
    new CharsBounds();
    new FontCanDisplay();

    if (hasGraphics2D) {
      new GVWidth();
      new GVLogicalBounds();
      new GVVisualBounds();
      new GVPixelBounds();
      new GVOutline();
      new GVGlyphLogicalBounds();
      new GVGlyphVisualBounds();
      new GVGlyphPixelBounds();
      new GVGlyphOutline();
      new GVGlyphTransform();
      new GVGlyphMetrics();

      new TLAdvance();
      new TLAscent();
      new TLBounds();
      new TLGetCaretInfo();
      new TLGetNextHit();
      new TLGetCaretShape();
      new TLGetLogicalHighlightShape();
      new TLHitTest();
      new TLOutline();

        /*
            new FontLineMetrics();
            new FontStringBounds();
        */
    }
  }

  @Override
  public Context createContext() {
    return new SWContext();
  }

  static class SWContext extends TextContext {
    FontMetrics fm;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);
      fm = graphics.getFontMetrics(font);
    }
  }

  public static class StringWidth extends TextMeasureTests {
    public StringWidth() {
      super(measuretestroot, "stringWidth", "Measuring String Width");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      SWContext swctx = (SWContext) ctx;
      String text = swctx.text;
      FontMetrics fm = swctx.fm;
      int wid = 0;
      --numReps;
      do {
        wid += fm.stringWidth(text);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class StringBounds extends TextMeasureTests {
    public StringBounds() {
      super(measuretestroot, "stringBounds", "Measuring String Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      SWContext swctx = (SWContext) ctx;
      String text = swctx.text;
      FontMetrics fm = swctx.fm;
      int wid = 0;
      Rectangle r = null;
      --numReps;
      do {
        int dx = fm.stringWidth(text);
        int dy = fm.getAscent() + fm.getDescent() + fm.getLeading();
        int x = 0;
        int y = -fm.getAscent();
        r = new Rectangle(x, y, dx, dy);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class CharsWidth extends TextMeasureTests {
    public CharsWidth() {
      super(measuretestroot, "charsWidth", "Measuring Chars Width");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      SWContext swctx = (SWContext) ctx;
      FontMetrics fm = swctx.fm;
      char[] chars = swctx.chars;
      int wid = 0;
      --numReps;
      do {
        wid += fm.charsWidth(chars, 0, chars.length);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class CharsBounds extends TextMeasureTests {
    public CharsBounds() {
      super(measuretestroot, "charsBounds", "Measuring Chars Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      SWContext swctx = (SWContext) ctx;
      FontMetrics fm = swctx.fm;
      char[] chars = swctx.chars;
      int wid = 0;
      Rectangle r = null;
      --numReps;
      do {
        int dx = fm.charsWidth(chars, 0, chars.length);
        int dy = fm.getAscent() + fm.getDescent() + fm.getLeading();
        int x = 0;
        int y = -fm.getAscent();
        r = new Rectangle(x, y, dx, dy);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class FontCanDisplay extends TextMeasureTests {
    public FontCanDisplay() {
      super(measuretestroot, "fontcandisplay", "Font canDisplay(char)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Font font = ((TextContext) ctx).font;
      boolean b = false;
      --numReps;
      do {
        for (int i = 0; i < 0x10000; i += 0x64) {
          b ^= font.canDisplay((char) i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVContext extends G2DContext {
    GlyphVector gv;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      int flags = Font.LAYOUT_LEFT_TO_RIGHT;
      if (Bidi.requiresBidi(chars, 0, chars.length)) { // assume rtl
        flags = Font.LAYOUT_RIGHT_TO_LEFT;
      }
      gv = font.layoutGlyphVector(frc, chars, 0, chars.length, flags);

      // gv options
    }
  }

  public abstract static class GVMeasureTest extends TextMeasureTests {
    protected GVMeasureTest(Group parent, String nodeName, String description) {
      super(parent, nodeName, description);
    }

    @Override
    public Context createContext() {
      return new GVContext();
    }
  }

  public static class GVWidth extends GVMeasureTest {
    public GVWidth() {
      super(measuretestroot, "gvWidth", "Measuring GV Width");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      double wid = 0;
      --numReps;
      do {
        wid += gv.getGlyphPosition(gv.getNumGlyphs()).getX();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVLogicalBounds extends GVMeasureTest {
    public GVLogicalBounds() {
      super(measuretestroot, "gvLogicalBounds", "Measuring GV Logical Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Rectangle2D r;
      --numReps;
      do {
        r = gv.getLogicalBounds();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVVisualBounds extends GVMeasureTest {
    public GVVisualBounds() {
      super(measuretestroot, "gvVisualBounds", "Measuring GV Visual Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Rectangle2D r;
      --numReps;
      do {
        r = gv.getVisualBounds();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVPixelBounds extends GVMeasureTest {
    public GVPixelBounds() {
      super(measuretestroot, "gvPixelBounds", "Measuring GV Pixel Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Rectangle2D r;
      --numReps;
      do {
        r = gv.getPixelBounds(null, 0, 0); // !!! add opt to provide different frc?
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVOutline extends GVMeasureTest {
    public GVOutline() {
      super(measuretestroot, "gvOutline", "Getting GV Outline");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Shape s;
      --numReps;
      do {
        s = gv.getOutline();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphLogicalBounds extends GVMeasureTest {
    public GVGlyphLogicalBounds() {
      super(measuretestroot, "gvGlyphLogicalBounds", "Measuring GV Glyph Logical Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Shape s;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          s = gv.getGlyphLogicalBounds(i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphVisualBounds extends GVMeasureTest {
    public GVGlyphVisualBounds() {
      super(measuretestroot, "gvGlyphVisualBounds", "Measuring GV Glyph Visual Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Shape s;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          s = gv.getGlyphVisualBounds(i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphPixelBounds extends GVMeasureTest {
    public GVGlyphPixelBounds() {
      super(measuretestroot, "gvGlyphPixelBounds", "Measuring GV Glyph Pixel Bounds");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Rectangle2D r;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          r = gv.getGlyphPixelBounds(i, null, 0, 0); // !!! add opt to provide different frc?
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphOutline extends GVMeasureTest {
    public GVGlyphOutline() {
      super(measuretestroot, "gvGlyphOutline", "Getting GV Glyph Outline");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      Shape s;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          s = gv.getGlyphOutline(i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphTransform extends GVMeasureTest {
    public GVGlyphTransform() {
      super(measuretestroot, "gvGlyphTransform", "Getting GV Glyph Transform");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      AffineTransform tx;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          tx = gv.getGlyphTransform(i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVGlyphMetrics extends GVMeasureTest {
    public GVGlyphMetrics() {
      super(measuretestroot, "gvGlyphMetrics", "Getting GV Glyph Metrics");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      GVContext gvctx = (GVContext) ctx;
      GlyphVector gv = gvctx.gv;
      GlyphMetrics gm;
      --numReps;
      do {
        for (int i = 0, e = gv.getNumGlyphs(); i < e; ++i) {
          gm = gv.getGlyphMetrics(i);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLContext extends G2DContext {
    TextLayout tl;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      // need more tl options here
      tl = new TextLayout(text, font, frc);
    }
  }

  public abstract static class TLMeasureTest extends TextMeasureTests {
    protected TLMeasureTest(Group parent, String nodeName, String description) {
      super(parent, nodeName, description);
    }

    @Override
    public Context createContext() {
      return new TLContext();
    }
  }

  public static class TLAdvance extends TLMeasureTest {
    public TLAdvance() {
      super(measuretestroot, "tlAdvance", "Measuring TL advance");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLContext tlctx = (TLContext) ctx;
      TextLayout tl = tlctx.tl;
      double wid = 0;
      --numReps;
      do {
        wid += tl.getAdvance();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLAscent extends TLMeasureTest {
    public TLAscent() {
      super(measuretestroot, "tlAscent", "Measuring TL ascent");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLContext tlctx = (TLContext) ctx;
      TextLayout tl = tlctx.tl;
      float ht = 0;
      --numReps;
      do {
        ht += tl.getAscent();
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLBounds extends TLMeasureTest {
    public TLBounds() {
      super(measuretestroot, "tlBounds", "Measuring TL advance");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLContext tlctx = (TLContext) ctx;
      TextLayout tl = tlctx.tl;
      Rectangle2D r;
      --numReps;
      do {
        r = tl.getBounds();
        --numReps;
      } while (numReps >= 0);
    }
  }

  static class TLExContext extends TLContext {
    TextHitInfo[] hits;
    Rectangle2D lb;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      ArrayList list = new ArrayList((text.length() << 1) + 2);
      TextHitInfo hit = TextHitInfo.trailing(-1);
      do {
        list.add(hit);
        hit = tl.getNextRightHit(hit);
      } while (hit != null);
      hits = (TextHitInfo[]) list.toArray(new TextHitInfo[list.size()]);

      lb = tl.getBounds();
      lb.setRect(lb.getMinX() - 10, lb.getMinY(), lb.getWidth() + 20, lb.getHeight());
    }
  }

  public abstract static class TLExtendedMeasureTest extends TLMeasureTest {
    protected TLExtendedMeasureTest(Group parent, String nodeName, String description) {
      super(parent, nodeName, description);
    }

    @Override
    public Context createContext() {
      return new TLExContext();
    }
  }

  public static class TLGetCaretInfo extends TLExtendedMeasureTest {
    public TLGetCaretInfo() {
      super(measuretestroot, "tlGetCaretInfo", "Measuring TL caret info");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      TextHitInfo[] hits = tlctx.hits;
      --numReps;
      do {
        for (TextHitInfo hit : hits) {
          tl.getCaretInfo(hit);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLGetNextHit extends TLExtendedMeasureTest {
    public TLGetNextHit() {
      super(measuretestroot, "tlGetNextHit", "Measuring TL getNextRight/LeftHit");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      TextHitInfo[] hits = tlctx.hits;
      TextHitInfo hit;
      --numReps;
      do {
        for (TextHitInfo hit1 : hits) {
          hit = tl.getNextLeftHit(hit1);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLGetCaretShape extends TLExtendedMeasureTest {
    public TLGetCaretShape() {
      super(measuretestroot, "tlGetCaretShape", "Measuring TL getCaretShape");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      TextHitInfo[] hits = tlctx.hits;
      Shape s;
      --numReps;
      do {
        for (TextHitInfo hit : hits) {
          s = tl.getCaretShape(hit);
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLGetLogicalHighlightShape extends TLExtendedMeasureTest {
    public TLGetLogicalHighlightShape() {
      super(measuretestroot, "tlGetLogicalHighlightShape", "Measuring TL getLogicalHighlightShape");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      int len = tlctx.text.length();
      Rectangle2D lb = tlctx.lb;
      Shape s;
      if (len < 3) {
        --numReps;
        do {
          s = tl.getLogicalHighlightShape(0, len, lb);
          --numReps;
        } while (numReps >= 0);
      } else {
        --numReps;
        do {
          for (int i = 3; i < len; ++i) {
            s = tl.getLogicalHighlightShape(i - 3, i, lb);
          }
          --numReps;
        } while (numReps >= 0);
      }
    }
  }

  public static class TLGetVisualHighlightShape extends TLExtendedMeasureTest {
    public TLGetVisualHighlightShape() {
      super(measuretestroot, "tlGetVisualHighlightShape", "Measuring TL getVisualHighlightShape");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      TextHitInfo[] hits = tlctx.hits;
      Rectangle2D lb = tlctx.lb;
      Shape s;
      if (hits.length < 3) {
        --numReps;
        do {
          s = tl.getVisualHighlightShape(hits[0], hits[hits.length - 1], lb);
          --numReps;
        } while (numReps >= 0);
      } else {
        --numReps;
        do {
          for (int i = 3; i < hits.length; ++i) {
            s = tl.getVisualHighlightShape(hits[i - 3], hits[i], lb);
          }
          --numReps;
        } while (numReps >= 0);
      }
    }
  }

  public static class TLHitTest extends TLExtendedMeasureTest {
    public TLHitTest() {
      super(measuretestroot, "tlHitTest", "Measuring TL hitTest");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLExContext tlctx = (TLExContext) ctx;
      TextLayout tl = tlctx.tl;
      int numhits = tlctx.hits.length;
      Rectangle2D lb = tlctx.lb;
      TextHitInfo hit;
      for (int i = 0; i <= numhits; ++i) {
        float x = (float) (lb.getMinX() + lb.getWidth() * i / numhits);
        float y = (float) (lb.getMinY() + lb.getHeight() * i / numhits);
        hit = tl.hitTestChar(x, y, lb);
      }
    }
  }

  public static class TLOutline extends TLMeasureTest {
    public TLOutline() {
      super(measuretestroot, "tlOutline", "Measuring TL outline");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLContext tlctx = (TLContext) ctx;
      TextLayout tl = tlctx.tl;
      Shape s;
      --numReps;
      do {
        s = tl.getOutline(null);
        --numReps;
      } while (numReps >= 0);
    }
  }
}
