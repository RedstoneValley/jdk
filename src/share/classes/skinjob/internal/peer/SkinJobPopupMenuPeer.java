package skinjob.internal.peer;

import java.awt.Event;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobPopupMenuPeer extends SkinJobMenuPeer implements PopupMenuPeer {
  protected final android.widget.PopupMenu androidPopupMenu;

  public SkinJobPopupMenuPeer(PopupMenu target) {
    super(target);
    androidPopupMenu = target.sjAndroidPopupMenu;
  }

  @Override
  public void show(Event e) {
    androidPopupMenu.show();
  }
}
