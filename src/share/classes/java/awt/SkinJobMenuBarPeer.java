package java.awt;

import android.widget.ListView;
import java.awt.peer.MenuBarPeer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobMenuBarPeer extends SkinJobComponentPeerForView<ListView>
    implements MenuBarPeer {
  public SkinJobMenuBarPeer(MenuBar target) {
    super((ListView) target.androidWidget);
  }

  @Override
  public void addMenu(Menu m) {
    // TODO
  }

  @Override
  public void delMenu(int index) {
    // TODO
  }

  @Override
  public void addHelpMenu(Menu m) {
    // TODO
  }
}
