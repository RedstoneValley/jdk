package sun.font;

import java.awt.font.LayoutPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.java2d.pipe.Region;

/**
 * Created by cryoc on 2016-10-15.
 */
public class LayoutPathImpl extends LayoutPath {
  private final AffineTransform at;

  public LayoutPathImpl(AffineTransform at) {
    this.at = at;
  }

  @Override
  public boolean pointToPath(Point2D point, Point2D location) {
    // TODO
    return false;
  }

  @Override
  public void pathToPoint(Point2D location, boolean preceding, Point2D point) {
    // TODO
  }

  @Override
  public Region mapShape(Rectangle2D r2d) {
    // TODO
    return null;
  }

  @Override
  public void pathToPoint(float loc, float loc1, boolean b, Point2D pt) {
    // TODO
  }

  /**
   * Created by cryoc on 2016-10-15.
   */
  public static class EmptyPath extends LayoutPathImpl {
    public EmptyPath(AffineTransform at) {
      super(at);
    }
  }

  /**
   * Created by cryoc on 2016-10-15.
   */
  public static class SegmentPathBuilder {
    public void moveTo(double loc, double i) {
      // TODO
    }

    public void lineTo(double v, double v1) {
      // TODO
    }

    public LayoutPathImpl complete() {
      // TODO
      return null;
    }
  }
}
