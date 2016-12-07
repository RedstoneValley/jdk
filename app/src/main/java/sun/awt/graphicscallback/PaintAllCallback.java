package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
public final class PaintAllCallback extends SunGraphicsCallback {
  private static final PaintAllCallback instance = new PaintAllCallback();

  private PaintAllCallback() {
  }

  public static PaintAllCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.paintAll(cg);
  }
}
