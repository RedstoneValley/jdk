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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.Bidi;
import java.text.CharacterIterator;
import java.util.Map;

public abstract class TextConstructionTests extends TextTests {
  static Group constructroot;
  static Group constructtestroot;

  public TextConstructionTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
  }

  public static void init() {
    // don't even bother with this at all if we don't have Java2 APIs.
    if (hasGraphics2D) {
      constructroot = new Group(textroot, "construction", "Construction Benchmarks");
      constructtestroot = new Group(constructroot, "tests", "Construction Tests");

      new GVFromFontString();
      new GVFromFontChars();
      new GVFromFontCI();
      new GVFromFontGlyphs();
      new GVFromFontLayout();
      //  new GVClone(); // not public API!

      new TLFromFont();
      new TLFromMap();
        /*
        new TLFromACI();
        new TLClone();
        new TLJustify();
        new TLFromLBM();
        */
    }
  }

  @Override
  public Context createContext() {
    return new TCContext();
  }

  public static class TCContext extends G2DContext {
    char[] chars1;
    CharacterIterator ci;
    GlyphVector gv;
    int[] glyphs;
    int flags;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      chars1 = new char[chars.length + 2];
      System.arraycopy(chars, 0, chars1, 1, chars.length);
      ci = new ArrayCI(chars1, 1, chars.length);
      gv = font.createGlyphVector(frc, text);
      glyphs = gv.getGlyphCodes(0, gv.getNumGlyphs(), null);
      flags = Bidi.requiresBidi(chars, 0, chars.length) ? Font.LAYOUT_LEFT_TO_RIGHT
          : Font.LAYOUT_RIGHT_TO_LEFT;
    }
  }

  public static class GVFromFontString extends TextConstructionTests {
    public GVFromFontString() {
      super(constructtestroot, "gvfromfontstring", "Call Font.createGlyphVector(FRC, String)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      GlyphVector gv;
      --numReps;
      do {
        gv = font.createGlyphVector(frc, text);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVFromFontChars extends TextConstructionTests {
    public GVFromFontChars() {
      super(constructtestroot, "gvfromfontchars", "Call Font.createGlyphVector(FRC, char[])");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      char[] chars = tcctx.chars;
      FontRenderContext frc = tcctx.frc;
      GlyphVector gv;
      --numReps;
      do {
        gv = font.createGlyphVector(frc, chars);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVFromFontCI extends TextConstructionTests {
    public GVFromFontCI() {
      super(constructtestroot,
          "gvfromfontci",
          "Call Font.createGlyphVector(FRC, CharacterIterator)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      CharacterIterator ci = tcctx.ci;
      FontRenderContext frc = tcctx.frc;
      GlyphVector gv;
      --numReps;
      do {
        gv = font.createGlyphVector(frc, ci);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVFromFontGlyphs extends TextConstructionTests {
    public GVFromFontGlyphs() {
      super(constructtestroot, "gvfromfontglyphs", "Call Font.createGlyphVector(FRC, int[])");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      int[] glyphs = tcctx.glyphs;
      FontRenderContext frc = tcctx.frc;
      GlyphVector gv;
      --numReps;
      do {
        gv = font.createGlyphVector(frc, glyphs);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class GVFromFontLayout extends TextConstructionTests {
    public GVFromFontLayout() {
      super(constructtestroot, "gvfromfontlayout", "Call Font.layoutGlyphVector(...)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      char[] chars = tcctx.chars1;
      int start = 1;
      int limit = chars.length - 1;
      FontRenderContext frc = tcctx.frc;
      int flags = tcctx.flags;
      GlyphVector gv;
      --numReps;
      do {
        gv = font.layoutGlyphVector(frc, chars, start, limit, flags);
        --numReps;
      } while (numReps >= 0);
    }
  }

    /*
     * my bad, clone is not public in GlyphVector!

    public static class GVClone extends TextConstructionTests {
        public GVClone() {
            super(constructtestroot, "gvclone", "Call GV.clone");
        }

        public void runTest(Object ctx, int numReps) {
            TCContext tcctx = (TCContext)ctx;
            final GlyphVector origGV = tcctx.gv;
            GlyphVector gv;
            do {
                gv = (GlyphVector)origGV.clone();
            } while (--numReps >= 0);
        }
    }
    */

  public static class TLFromFont extends TextConstructionTests {
    public TLFromFont() {
      super(constructtestroot, "tlfromfont", "TextLayout(String, Font, FRC)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, font, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class TLMapContext extends G2DContext {
    Map map;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      map = (Map) env.getModifier(tlmapList);
    }
  }

  public static class TLFromMap extends TextConstructionTests {
    public TLFromMap() {
      super(constructtestroot, "tlfrommap", "TextLayout(String, Map, FRC)");
    }

    @Override
    public Context createContext() {
      return new TLMapContext();
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TLMapContext tlmctx = (TLMapContext) ctx;
      String text = tlmctx.text;
      FontRenderContext frc = tlmctx.frc;
      Map map = tlmctx.map;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, map, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public static class ACIContext extends G2DContext {
    AttributedCharacterIterator aci;

    @Override
    public void init(TestEnvironment env, Result results) {
      super.init(env, results);

      AttributedString astr = new AttributedString(text);
    }
  }

  public static final class ArrayCI implements CharacterIterator {
    char[] chars;
    int off;
    int max;
    int pos;

    ArrayCI(char[] chars, int off, int len) {
      if (off < 0 || len < 0 || len > 0 && (chars == null || chars.length - off < len)) {
        throw new InternalError("bad ArrayCI params");
      }
      this.chars = chars;
      this.off = off;
      max = off + len;
      pos = off;
    }

    /**
     * Sets the position to getBeginIndex() and returns the character at that
     * position.
     *
     * @return the first character in the text, or DONE if the text is empty
     * @see #getBeginIndex()
     */
    @Override
    public char first() {
      if (max > off) {
        return chars[pos = off];
      }
      return DONE;
    }

    /**
     * Sets the position to getEndIndex()-1 (getEndIndex() if the text is empty)
     * and returns the character at that position.
     *
     * @return the last character in the text, or DONE if the text is empty
     * @see #getEndIndex()
     */
    @Override
    public char last() {
      if (max > off) {
        return chars[pos = max - 1];
      }
      pos = max;
      return DONE;
    }

    /**
     * Gets the character at the current position (as returned by getIndex()).
     *
     * @return the character at the current position or DONE if the current
     * position is off the end of the text.
     * @see #getIndex()
     */
    @Override
    public char current() {
      return pos == max ? DONE : chars[pos];
    }

    /**
     * Increments the iterator's index by one and returns the character
     * at the new index.  If the resulting index is greater or equal
     * to getEndIndex(), the current index is reset to getEndIndex() and
     * a value of DONE is returned.
     *
     * @return the character at the new position or DONE if the new
     * position is off the end of the text range.
     */
    @Override
    public char next() {
      ++pos;
      if (pos < max) {
        return chars[pos];
      }
      pos = max;
      return DONE;
    }

    /**
     * Decrements the iterator's index by one and returns the character
     * at the new index. If the current index is getBeginIndex(), the index
     * remains at getBeginIndex() and a value of DONE is returned.
     *
     * @return the character at the new position or DONE if the current
     * position is equal to getBeginIndex().
     */
    @Override
    public char previous() {
      --pos;
      if (pos >= off) {
        return chars[pos];
      }
      pos = off;
      return DONE;
    }

    /**
     * Sets the position to the specified position in the text and returns that
     * character.
     *
     * @param position the position within the text.  Valid values range from
     *                 getBeginIndex() to getEndIndex().  An IllegalArgumentException is thrown
     *                 if an invalid value is supplied.
     * @return the character at the specified position or DONE if the specified position is equal
     * to getEndIndex()
     */
    @Override
    public char setIndex(int position) {
      if (position < off || position > max) {
        throw new IllegalArgumentException(
            "pos: " + position + " off: " + off + " len: " + (max - off));
      }
      return (pos = position) < max ? chars[position] : DONE;
    }

    /**
     * Returns the start index of the text.
     *
     * @return the index at which the text begins.
     */
    @Override
    public int getBeginIndex() {
      return off;
    }

    /**
     * Returns the end index of the text.  This index is the index of the first
     * character following the end of the text.
     *
     * @return the index after the last character in the text
     */
    @Override
    public int getEndIndex() {
      return max;
    }

    /**
     * Returns the current index.
     *
     * @return the current index.
     */
    @Override
    public int getIndex() {
      return pos;
    }

    /**
     * Create a copy of this iterator
     *
     * @return A copy of this
     */
    @Override
    public Object clone() {
      try {
        return super.clone();
      } catch (Exception e) {
        return new InternalError();
      }
    }
  }

  public class TLFromACI extends TextConstructionTests {
    public TLFromACI() {
      super(constructtestroot, "tlfromaci", "TextLayout(ACI, FRC)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, font, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public class TLClone extends TextConstructionTests {
    public TLClone() {
      super(constructtestroot, "tlclone", "call TextLayout.clone()");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, font, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public class TLJustify extends TextConstructionTests {
    public TLJustify() {
      super(constructtestroot, "tljustify", "call TextLayout.getJustifiedLayout(...)");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, font, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }

  public class TLFromLBM extends TextConstructionTests {
    public TLFromLBM() {
      super(constructtestroot, "tlfromlbm", "call LineBreakMeasurer.next()");
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      TCContext tcctx = (TCContext) ctx;
      Font font = tcctx.font;
      String text = tcctx.text;
      FontRenderContext frc = tcctx.frc;
      TextLayout tl;
      --numReps;
      do {
        tl = new TextLayout(text, font, frc);
        --numReps;
      } while (numReps >= 0);
    }
  }
}
