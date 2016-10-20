package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

/**
 * Created by cryoc on 2016-10-18.
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
