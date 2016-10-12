package java.awt;

import android.view.View;
import java.awt.peer.MenuPeer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobMenuPeer extends SkinJobComponentPeerForView<View> implements MenuPeer {
  protected final android.view.Menu androidMenu;

  public SkinJobMenuPeer(Menu target) {
    super(target.androidWidget);
    androidMenu = target.androidMenu;
  }

  @Override
  public void addSeparator() {
    // TODO
  }

  @Override
  public void addItem(MenuItem item) {
    if (item instanceof Menu) {
      // TODO: This is the submenu case.
    } else {
      androidMenu.add(item.getLabel());
    }
  }

  @Override
  public void delItem(int index) {
    androidMenu.removeItem(index);
  }

  @Override
  public void setLabel(String label) {
    // TODO
  }
}
