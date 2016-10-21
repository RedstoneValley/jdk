package skinjob.util;

import java.awt.Shape;
import java.awt.geom.Area;

/**
 * Utility methods for use with {@link Shape}.
 */
public final class Shapes {

  public static final Area EMPTY = new Area();

  /**
   * Utility class; do not instantiate.
   */
  private Shapes() {
  }

  public static boolean isEmptyArea(Shape shape) {
    return shape instanceof Area && ((Area) shape).isEmpty();
  }

  public static Area asArea(Shape shape) {
    if (shape instanceof Area) {
      return (Area) shape;
    }
    return new Area(shape);
  }

  public static Area getIntersection(Shape shape1, Shape shape2) {
    if (isEmptyArea(shape1) || isEmptyArea(shape2)
        || !shape1.intersects(shape2.getBounds2D()) || !shape2.intersects(shape1.getBounds2D())) {
      return EMPTY;
    }
    Area out = new Area(shape1);
    out.intersect(asArea(shape2));
    return out;
  }

  public static Shape getDifference(Shape shape1, Shape shape2) {
    if (isEmptyArea(shape2) || !shape1.intersects(shape2.getBounds2D())
        || !shape2.intersects(shape1.getBounds2D())) {
      return shape1;
    }
    Area out = new Area(shape1);
    out.subtract(asArea(shape2));
    return out;
  }

  public static Shape getUnion(Shape shape1, Shape shape2) {
    if (isEmptyArea(shape1) || shape2.contains(shape1.getBounds2D())) {
      return shape2;
    }
    if (isEmptyArea(shape2) || shape1.contains(shape2.getBounds2D())) {
      return shape1;
    }
    Area out = new Area(shape1);
    out.add(asArea(shape2));
    return out;
  }
}
