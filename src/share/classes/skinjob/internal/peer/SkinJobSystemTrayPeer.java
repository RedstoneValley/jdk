package skinjob.internal.peer;

import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.peer.SystemTrayPeer;

/**
 * Created by cryoc on 2016-10-21.
 */
public class SkinJobSystemTrayPeer implements SystemTrayPeer {
  public SkinJobSystemTrayPeer(SystemTray target) {
  }

  @Override
  public Dimension getTrayIconSize() {
    // TODO
    return null;
  }
}
