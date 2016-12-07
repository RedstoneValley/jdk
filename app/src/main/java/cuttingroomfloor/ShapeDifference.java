package cuttingroomfloor;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by cryoc on 2016-10-21.
 */
@SuppressWarnings("unused")
public class ShapeDifference implements Shape {
  private final Shape shape1;
  private final Shape shape2;

  public ShapeDifference(Shape shape1, Shape shape2) {
    this.shape1 = shape1;
    this.shape2 = shape2;
  }

  @Override
  public Rectangle getBounds() {
    // T0D0: Actual bounds may be smaller
    return shape1.getBounds();
  }

  @Override
  public Rectangle2D getBounds2D() {
    // T0D0: Actual bounds may be smaller
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
    // T0D0
    return null;
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at, double flatness) {
    // T0D0
    return null;
  }
}
