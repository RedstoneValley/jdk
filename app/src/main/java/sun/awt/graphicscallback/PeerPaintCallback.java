package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
public final class PeerPaintCallback extends SunGraphicsCallback {
  private static final PeerPaintCallback instance = new PeerPaintCallback();

  private PeerPaintCallback() {
  }

  public static PeerPaintCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.validate();
    if (comp.peer instanceof LightweightPeer) {
      comp.lightweightPaint(cg);
    } else {
      comp.peer.paint(cg);
    }
  }
}
