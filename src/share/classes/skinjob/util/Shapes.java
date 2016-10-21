package skinjob.util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Utility methods for use with {@link Shape}.
 */
public final class Shapes {

  public static final PathIterator EMPTY_PATH_ITERATOR = new EmptyPathIterator();
  public static final Shape EMPTY = new EmptyShape();

  /**
   * Utility class; do not instantiate.
   */
  private Shapes() {
  }

  public static Shape getIntersection(Shape shape1, Shape shape2) {
    if (shape1 instanceof EmptyShape || shape2 instanceof EmptyShape
        || !shape1.intersects(shape2.getBounds2D()) || !shape2.intersects(shape1.getBounds2D())) {
      return EMPTY;
    }
    return new ShapeIntersection(shape1, shape2);
  }

  public static Shape getDifference(Shape shape1, Shape shape2) {
    if (shape2 instanceof EmptyShape || !shape1.intersects(shape2.getBounds2D())
        || !shape2.intersects(shape1.getBounds2D())) {
      return shape1;
    }
    return new ShapeDifference(shape1, shape2);
  }

  public static Shape getUnion(Shape shape1, Shape shape2) {
    if (shape1 instanceof EmptyShape || shape2.contains(shape1.getBounds2D())) {
      return shape2;
    }
    if (shape2 instanceof EmptyShape || shape1.contains(shape2.getBounds2D())) {
      return shape1;
    }
    return new ShapeUnion(shape1, shape2);
  }

  private static class ShapeIntersection implements Shape {
    private final Shape shape1;
    private final Shape shape2;

    public ShapeIntersection(Shape shape1, Shape shape2) {
      this.shape1 = shape1;
      this.shape2 = shape2;
    }

    @Override
    public Rectangle getBounds() {
      // TODO: Actual bounds may be smaller
      return shape1.getBounds().intersection(shape2.getBounds());
    }

    @Override
    public Rectangle2D getBounds2D() {
      // TODO: Actual bounds may be smaller
      return shape1.getBounds2D().createIntersection(shape2.getBounds2D());
    }

    @Override
    public boolean contains(double x, double y) {
      return shape1.contains(x, y) && shape2.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
      return shape1.contains(p) && shape2.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
      return shape1.intersects(x, y, w, h) && shape2.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
      return shape1.intersects(r) && shape2.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
      return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
      return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
      // TODO
      return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
      // TODO
      return null;
    }
  }

  private static class ShapeDifference implements Shape {
    private final Shape shape1;
    private final Shape shape2;

    public ShapeDifference(Shape shape1, Shape shape2) {
      this.shape1 = shape1;
      this.shape2 = shape2;
    }

    @Override
    public Rectangle getBounds() {
      // TODO: Actual bounds may be smaller
      return shape1.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
      // TODO: Actual bounds may be smaller
      return shape1.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
      return shape1.contains(x, y) && !shape2.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
      return shape1.contains(p) && !shape2.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
      return shape1.intersects(x, y, w, h) && !shape2.contains(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
      return shape1.intersects(r) && !shape2.contains(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
      return shape1.contains(x, y, w, h) && !shape2.intersects(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
      return shape1.contains(r) && !shape2.intersects(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
      // TODO
      return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
      // TODO
      return null;
    }
  }

  private static class ShapeUnion implements Shape {
    private final Shape shape1;
    private final Shape shape2;

    public ShapeUnion(Shape shape1, Shape shape2) {
      this.shape1 = shape1;
      this.shape2 = shape2;
    }

    @Override
    public Rectangle getBounds() {
      return shape1.getBounds().union(shape2.getBounds());
    }

    @Override
    public Rectangle2D getBounds2D() {
      return shape1.getBounds2D().createUnion(shape2.getBounds2D());
    }

    @Override
    public boolean contains(double x, double y) {
      return shape1.contains(x, y) || shape2.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
      return shape1.contains(p) || shape2.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
      return shape1.intersects(x, y, w, h) || shape2.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
      return shape1.intersects(r) || shape2.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
      if (shape1.contains(x, y, w, h) || shape2.contains(x, y, w, h)) {
        return true;
      }
      // TODO
      return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
      if (shape1.contains(r) || shape2.contains(r)) {
        return true;
      }
      // TODO
      return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
      // TODO
      return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
      // TODO
      return null;
    }
  }

  private static final class EmptyShape implements Shape {

    /**
     * Singleton created by outer class.
     */
    private EmptyShape() {
    }

    @Override
    public Rectangle getBounds() {
      return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public Rectangle2D getBounds2D() {
      return new Rectangle2D.Float(0, 0, 0, 0);
    }

    @Override
    public boolean contains(double x, double y) {
      return false;
    }

    @Override
    public boolean contains(Point2D p) {
      return false;
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
      return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
      return false;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
      return false;
    }

    @Override
    public boolean contains(Rectangle2D r) {
      return false;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
      return EMPTY_PATH_ITERATOR;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
      return EMPTY_PATH_ITERATOR;
    }
  }

  public static final class EmptyPathIterator implements PathIterator {
    /**
     * Singleton created by outer class.
     */
    private EmptyPathIterator() {
    }

    @Override
    public int getWindingRule() {
      return 0;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public void next() {
    }

    @Override
    public int currentSegment(float[] coords) {
      return 0;
    }

    @Override
    public int currentSegment(double[] coords) {
      return 0;
    }
  }
}
