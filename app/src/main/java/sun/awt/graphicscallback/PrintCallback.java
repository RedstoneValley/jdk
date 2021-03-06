package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
public final class PrintCallback extends SunGraphicsCallback {
  private static final PrintCallback instance = new PrintCallback();

  private PrintCallback() {
  }

  public static PrintCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.print(cg);
  }
}
