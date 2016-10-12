package java.awt;

import java.awt.peer.PopupMenuPeer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobPopupMenuPeer extends SkinJobMenuPeer implements PopupMenuPeer {
  protected final android.widget.PopupMenu androidPopupMenu;

  public SkinJobPopupMenuPeer(PopupMenu target) {
    super(target);
    androidPopupMenu = target.androidPopupMenu;
  }

  @Override
  public void show(Event e) {
    androidPopupMenu.show();
  }
}
