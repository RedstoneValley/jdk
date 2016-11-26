package skinjob.util;

import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;
import static java.awt.geom.PathIterator.WIND_EVEN_ODD;
import static java.awt.geom.PathIterator.WIND_NON_ZERO;

/**
 * Utility methods for use with {@link Shape}.
 */
public final class Geometry {

  public static final Area EMPTY = new Area();

  private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
  private static final String TAG = "PathIterators";
  private static final int COORD_ARRAY_SIZE = 23;

  /**
   * Utility class; do not instantiate.
   */
  private Geometry() {
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

    public static Path asAndroidPath(Shape shape, AffineTransform transform) {
      PathIterator iterator = shape.getPathIterator(IDENTITY_TRANSFORM);
      double[] coords = new double[COORD_ARRAY_SIZE];
      Path path = new Path();
      int windingRule = iterator.getWindingRule();
      switch (windingRule) {
          case WIND_EVEN_ODD:
              path.setFillType(Path.FillType.EVEN_ODD);
              break;
          case WIND_NON_ZERO:
              path.setFillType(Path.FillType.WINDING);
              break;
          default:
              Log.e(TAG, "Unknown winding rule " + windingRule);
      }
      while (!iterator.isDone()) {
          int segmentType = iterator.currentSegment(coords);
          switch (segmentType) {
              case SEG_MOVETO:
                  path.moveTo((float) coords[0], (float) coords[1]);
                  break;
              case SEG_LINETO:
                  path.lineTo((float) coords[0], (float) coords[1]);
                  break;
              case SEG_QUADTO:
                  path.quadTo((float) coords[0], (float) coords[1],
                          (float) coords[2], (float) coords[3]);
                  break;
              case SEG_CUBICTO:
                  path.cubicTo((float) coords[0], (float) coords[1],
                          (float) coords[2], (float) coords[3],
                          (float) coords[4], (float) coords[5]);
                  break;
              case SEG_CLOSE:
                  path.close();
                  break;
              default:
                  Log.e(TAG, "Unknown path segment type " + segmentType);
          }
      }
      if (!transform.isIdentity()) {
          path.transform(transformToMatrix(transform));
      }
      return path;
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

  /**
   * See Javadoc for {@link AffineTransform#getDeterminant()} for underlying matrix.
   */
  public static Matrix transformToMatrix(AffineTransform transform) {
      Matrix matrix = new Matrix();
      matrix.setValues(new float[]{
              (float) transform.getScaleX(), (float) transform.getShearX(), (float) transform.getTranslateX(),
              (float) transform.getShearY(), (float) transform.getScaleY(), (float) transform.getTranslateY(),
              0.0f, 0.0f, 1.0f});
      return matrix;
  }

}
