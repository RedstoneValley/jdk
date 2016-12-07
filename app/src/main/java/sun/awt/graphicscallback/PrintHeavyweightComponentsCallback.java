package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
public final class PrintHeavyweightComponentsCallback extends SunGraphicsCallback {
  private static final PrintHeavyweightComponentsCallback instance
      = new PrintHeavyweightComponentsCallback();

  private PrintHeavyweightComponentsCallback() {
  }

  public static PrintHeavyweightComponentsCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    if (comp.peer instanceof LightweightPeer) {
      comp.printHeavyweightComponents(cg);
    } else {
      comp.printAll(cg);
    }
  }
}
