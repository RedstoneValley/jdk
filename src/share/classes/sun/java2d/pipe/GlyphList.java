package sun.java2d.pipe;

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
}
