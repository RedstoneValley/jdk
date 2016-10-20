package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

/**
 * Created by cryoc on 2016-10-18.
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
