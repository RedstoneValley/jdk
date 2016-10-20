package sun.font;

import java.awt.Font;
import java.awt.SkinJob;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Created by cryoc on 2016-10-15.
 */
public class GraphicComponent extends TextLineComponent {
  private final GraphicAttribute graphicAttribute;
  private final int[] charsLtoV;
  private final byte[] levels;
  private final int pos;
  private final int chunkLimit;
  private final AffineTransform baseRot;

  public GraphicComponent(
      GraphicAttribute graphicAttribute, Decoration decorator, int[] charsLtoV, byte[] levels,
      int pos, int chunkLimit, AffineTransform baseRot) {
    super(new char[0], null, null, decorator);
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
        ascent * SkinJob.strikeThroughOffset,
        SkinJob.graphicStrikethroughThickness,
        ascent * SkinJob.underlineOffset,
        SkinJob.graphicUnderlineThickness,
        SkinJob.graphicSsOffset,
        SkinJob.graphicItalicAngle);
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
  protected Rectangle2D getBounds(int measureStart, int measureLimit) {
    return super.getBounds(measureStart, measureLimit);
  }
}
