package cuttingroomfloor;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Optimized representation of the empty set of points in 2D space.
 */
@SuppressWarnings("unused")
public enum EmptyShape implements Shape {
  INSTANCE; // Singleton

  private static final EmptyPathIterator EMPTY_PATH_ITERATOR = new EmptyPathIterator();

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

  /**
   * Created by cryoc on 2016-10-21.
   */
  public static final class EmptyPathIterator implements PathIterator {
    /**
     * Singleton created by outer class.
     */
    public EmptyPathIterator() {
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
