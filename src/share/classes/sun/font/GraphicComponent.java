package sun.font;

import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;

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
    // TODO
    this.charsLtoV = charsLtoV;
    this.levels = levels;
    this.pos = pos;
    this.chunkLimit = chunkLimit;
    this.baseRot = baseRot;
  }

  public static CoreMetrics createCoreMetrics(GraphicAttribute graphic) {
    // TODO
    return null;
  }
}
