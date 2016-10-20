package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

/**
 * Created by cryoc on 2016-10-18.
 */
public final class PeerPrintCallback extends SunGraphicsCallback {
  private static final PeerPrintCallback instance = new PeerPrintCallback();

  private PeerPrintCallback() {
  }

  public static PeerPrintCallback getInstance() {
    return instance;
  }

  @Override
  public void run(Component comp, Graphics cg) {
    comp.validate();
    if (comp.peer instanceof LightweightPeer) {
      comp.lightweightPrint(cg);
    } else {
      comp.peer.print(cg);
    }
  }
}
