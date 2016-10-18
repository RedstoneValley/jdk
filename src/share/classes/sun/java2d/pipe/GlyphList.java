package sun.java2d.pipe;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;

/**
 * Created by cryoc on 2016-10-16.
 */
public class GlyphList {
  private boolean usePositions;
  private boolean subPixPos;
  private boolean rgbOrder;
  private int numGlyphs;
  private long[] images;
  private float x;
  private float y;
  private Object strike;
  private float[] positions;
  private int glyphIndex;
  private int[] metrics;
  private byte[] grayBits;
  private static GlyphList instance = new GlyphList();

  public static GlyphList getInstance() {
    return instance;
  }

  public boolean usePositions() {
    return usePositions;
  }

  public boolean isSubPixPos() {
    return subPixPos;
  }

  public boolean isRGBOrder() {
    return rgbOrder;
  }

  public int getNumGlyphs() {
    return numGlyphs;
  }

  public long[] getImages() {
    return images;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public Object getStrike() {
    return strike;
  }

  public float[] getPositions() {
    return positions;
  }

  public int[] getBounds() {
    // TODO
    return new int[0];
  }

  public void setGlyphIndex(int glyphIndex) {
    this.glyphIndex = glyphIndex;
  }

  public int[] getMetrics() {
    return metrics;
  }

  public byte[] getGrayBits() {
    return grayBits;
  }

  public boolean setFromString(FontInfo info, String s, float devx, float devy) {
    // TODO
    return false;
  }

  public void dispose() {
    // TODO
  }

  public void setFromGlyphVector(FontInfo info, GlyphVector gv, float x, float y) {
    // TODO
  }

  public boolean setFromChars(
      FontInfo info, char[] data, int offset, int length, float x, float y) {
    // TODO
    return false;
  }
}
