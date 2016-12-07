package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
public final class PaintHeavyweightComponentsCallback extends SunGraphicsCallback {
  private static final PaintHeavyweightComponentsCallback instance
      = new PaintHeavyweightComponentsCallback();

  private PaintHeavyweightComponentsCallback() {
  }

  public static PaintHeavyweightComponentsCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    if (comp.peer instanceof LightweightPeer) {
      comp.paintHeavyweightComponents(cg);
    } else {
      comp.paintAll(cg);
    }
  }
}
