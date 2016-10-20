package sun.font;

import android.graphics.Rect;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Created by cryoc on 2016-10-15.
 */
public class TextLineComponent {
  public static final int LEFT_TO_RIGHT = 1;
  public static final int RIGHT_TO_LEFT = -1;
  public static final int UNCHANGED = 0;
  protected final Decoration decorator;
  private final char[] chars;
  private final Font font;
  private final CoreMetrics coreMetrics;

  public TextLineComponent(
      char[] chars, Font font, CoreMetrics coreMetrics, Decoration decorator) {
    this.chars = chars;
    this.font = font;
    this.coreMetrics = coreMetrics;
    this.decorator = decorator;
  }

  public boolean isSimple() {
    // TODO
    return false;
  }

  public CoreMetrics getCoreMetrics() {
    return coreMetrics;
  }

  public float getAdvance() {
    return getAdvanceBetween(0, getNumCharacters());
  }

  public AffineTransform getBaselineTransform() {
    // TODO
    return null;
  }

  public float getCharX(int indexInArray) {
    // TODO
    return 0;
  }

  public float getCharY(int indexInArray) {
    // TODO
    return 0;
  }

  public int getNumCharacters() {
    return chars.length;
  }

  public Rectangle getPixelBounds(FontRenderContext frc, float v, float v1) {
    // TODO
    return null;
  }

  public boolean caretAtOffsetIsValid(int i) {
    // TODO
    return false;
  }

  public Rectangle2D getCharVisualBounds(int indexInTlc) {
    // TODO
    return null;
  }

  public void draw(Graphics2D g2, float v, float v1) {
    // TODO
  }

  public Shape getOutline(float loc, float loc1) {
    // TODO
    return null;
  }

  public int getNumJustificationInfos() {
    // TODO
    return 0;
  }

  public void getJustificationInfos(
      GlyphJustificationInfo[] infos, int infoPosition, int rangeMin, int rangeMax) {
    // TODO
  }

  public TextLineComponent applyJustificationDeltas(float[] deltas, int i, boolean[] flags) {
    // TODO
    return new TextLineComponent();
  }

  public Rectangle2D getItalicBounds() {
    // TODO
    return null;
  }

  public Rectangle2D getVisualBounds() {
    Rect bounds = getBounds(0, chars.length);
    return new Rectangle2D.Double(bounds.left, bounds.top, bounds.width(), bounds.height());
  }

  public float getCharAdvance(int indexInArray) {
    return getAdvanceBetween(indexInArray, 1);
  }

  public float getAdvanceBetween(int measureStart, int measureLimit) {
    Rect bounds = getBounds(measureStart, measureLimit);
    return bounds.width();
  }

  protected Rect getBounds(int measureStart, int measureLimit) {
    Rect bounds = new Rect();
    font.getAndroidPaint().getTextBounds(chars, measureStart, measureLimit, bounds);
    return bounds;
  }

  public int getLineBreakIndex(int i, float width) {
    // TODO
    return 0;
  }

  public TextLineComponent getSubset(int i, int i1, int subsetFlag) {
    // TODO
    return null;
  }
}
