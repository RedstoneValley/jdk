package sun.font;

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
  public boolean isSimple() {
    // TODO
    return false;
  }

  public CoreMetrics getCoreMetrics() {
    // TODO
    return null;
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
    // TODO
    return 0;
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
    TextLineComponent modified = new TextLineComponent();
    // TODO
    return modified;
  }

  public Rectangle2D getItalicBounds() {
    // TODO
    return null;
  }

  public Rectangle2D getVisualBounds() {
    // TODO
    return null;
  }

  public float getCharAdvance(int indexInArray) {
    // TODO
    return 0;
  }

  public float getAdvanceBetween(int measureStart, int measureLimit) {
    float totalAdvance = 0;
    for (int i = measureStart; i < measureLimit; i++) {
      totalAdvance += getCharAdvance(i);
    }
    return totalAdvance;
  }
}
