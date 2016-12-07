package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;

import sun.awt.SunGraphicsCallback;

/**
 * Created by cryoc on 2016-10-18.
 */
public final class PrintAllCallback extends SunGraphicsCallback {
  private static final PrintAllCallback instance = new PrintAllCallback();

  private PrintAllCallback() {
  }

  public static PrintAllCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.printAll(cg);
  }
}
