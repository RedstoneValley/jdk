package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import sun.awt.SunGraphicsCallback;

/**
 * Created by cryoc on 2016-10-18.
 */
public final class PaintCallback extends SunGraphicsCallback {
  private static final PaintCallback instance = new PaintCallback();

  private PaintCallback() {
  }

  public static PaintCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.paint(cg);
  }
}
