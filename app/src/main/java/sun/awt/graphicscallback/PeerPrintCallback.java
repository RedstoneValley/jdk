package sun.awt.graphicscallback;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.peer.LightweightPeer;

import sun.awt.SunGraphicsCallback;

/**
 * Copy of the OpenJDK class for use by SkinJob.
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
