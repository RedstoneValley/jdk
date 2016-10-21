package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import skinjob.SkinJobGlobals;

/**
 * Created by cryoc on 2016-10-15.
 */
public class GraphicComponent extends TextLineComponent {
  /**
   * Best possible text representation of an arbitrary graphic, in case this somehow gets processed
   * as text (although it shouldn't).
   */
  protected static final char[] OBJECT_REPLACEMENT = new char[]{'\uFFFC'};

  protected static final char[] EMPTY_CHAR_ARRAY = new char[0];
  private final GraphicAttribute graphicAttribute;
  private final int[] charsLtoV; // TODO: What should this do?
  private final byte[] levels; // TODO: What should this do?
  private final int pos; // TODO: What should this do?
  private final int chunkLimit; // TODO: What should this do?
  private final AffineTransform baseRot; // TODO: What should this do?

  public GraphicComponent(
      GraphicAttribute graphicAttribute, Decoration decorator, int[] charsLtoV, byte[] levels,
      int pos, int chunkLimit, AffineTransform baseRot) {
    super(
        OBJECT_REPLACEMENT,
        SkinJobGlobals.defaultFont,
        createCoreMetrics(graphicAttribute),
        decorator);
    this.graphicAttribute = graphicAttribute;
    this.charsLtoV = charsLtoV;
    this.levels = levels;
    this.pos = pos;
    this.chunkLimit = chunkLimit;
    this.baseRot = baseRot;
  }

  public static CoreMetrics createCoreMetrics(GraphicAttribute graphic) {
    float height = (float) graphic.getBounds().getHeight();
    float ascent = graphic.getAscent();
    return new CoreMetrics(
        ascent,
        graphic.getDescent(),
        /* TODO: leading */
        height,
        height,
        /* TODO: is this correct for baselineIndex? */
        Font.CENTER_BASELINE,
        /* TODO: is this correct for baselineOffsets? */
        new float[]{height},
        ascent * SkinJobGlobals.strikeThroughOffset,
        SkinJobGlobals.graphicStrikethroughThickness,
        ascent * SkinJobGlobals.underlineOffset,
        SkinJobGlobals.graphicUnderlineThickness,
        SkinJobGlobals.graphicSsOffset,
        SkinJobGlobals.graphicItalicAngle);
  }

  @Override
  public float getAdvance() {
    return (float) graphicAttribute.getBounds().getWidth();
  }

  @Override
  public int getNumCharacters() {
    return 1;
  }

  @Override
  public void draw(Graphics2D g2, float x, float y) {
    graphicAttribute.draw(g2, x, y);
  }

  @Override
  public Rectangle2D getItalicBounds() {
    return graphicAttribute.getBounds();
  }

  @Override
  public Rectangle2D getVisualBounds() {
    return graphicAttribute.getBounds();
  }

  @Override
  public float getCharAdvance(int indexInArray) {
    return getAdvance();
  }

  @Override
  public float getAdvanceBetween(int measureStart, int measureLimit) {
    if (measureLimit <= measureStart) {
      return 0;
    }
    return getAdvance();
  }

  @Override
  public TextLineComponent getSubset(int start, int length, int subsetFlag) {
    if (length >= 1) {
      return this;
    } else {
      return new TextLineComponent(EMPTY_CHAR_ARRAY, null, getCoreMetrics(), decorator);
    }
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + graphicAttribute.hashCode();
    result = 31 * result + Arrays.hashCode(charsLtoV);
    result = 31 * result + Arrays.hashCode(levels);
    result = 31 * result + pos;
    result = 31 * result + chunkLimit;
    result = 31 * result + (baseRot != null ? baseRot.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GraphicComponent)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    GraphicComponent that = (GraphicComponent) o;

    if (pos != that.pos) {
      return false;
    }
    if (chunkLimit != that.chunkLimit) {
      return false;
    }
    if (!graphicAttribute.equals(that.graphicAttribute)) {
      return false;
    }
    if (!Arrays.equals(charsLtoV, that.charsLtoV)) {
      return false;
    }
    if (!Arrays.equals(levels, that.levels)) {
      return false;
    }
    return baseRot != null ? baseRot.equals(that.baseRot) : that.baseRot == null;
  }
}
