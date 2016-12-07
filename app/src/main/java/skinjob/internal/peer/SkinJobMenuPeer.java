package skinjob.internal.peer;

import android.view.SubMenu;
import android.view.View;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobMenuPeer extends SkinJobComponentPeerForView<View> implements MenuPeer {

  protected final android.view.Menu androidMenu;
  protected final List<MenuIndexOccupant> indexOccupants = new ArrayList<>();
  protected final SkinJobMenuPeer parent; // Null unless this is a submenu
  private volatile int groupIdForNextItem = android.view.Menu.FIRST;

  public SkinJobMenuPeer(Menu target) {
    this(target, null);
  }

  public SkinJobMenuPeer(Menu target, SkinJobMenuPeer parent) {
    super(target.sjAndroidWidget);
    androidMenu = target.androidMenu;
    this.parent = parent;
  }

  /**
   * AWT treats separators as menu items in their own right, whereas Android expects each menu item
   * to have a "group" and then generates separators between groups. Thus, we need to completely
   * rebuild the underlying Android menu whenever a separator is deleted, so that the group number
   * of every menu item below the separator is updated. We also need to do this if a submenu's name
   * has changed, because submenu names are immutable in Android; in that case, rebuildMenu is
   * called on the parent menu's peer by the submenu's peer.
   */
  private synchronized void rebuildMenu() {
    groupIdForNextItem = android.view.Menu.FIRST;
    androidMenu.clear();
    for (MenuIndexOccupant occupant : indexOccupants) {
      occupant.addToMenu();
    }
  }

  @Override
  public synchronized void addSeparator() {
    groupIdForNextItem++;
    indexOccupants.add(new Separator());
  }

  @Override
  public synchronized void addItem(MenuItem item) {
    addItem(item, androidMenu);
    indexOccupants.add(new NormalMenuItem(item));
  }

  @Override
  public synchronized void delItem(int index) {
    if (indexOccupants.remove(index) instanceof Separator) {
      rebuildMenu();
    }
  }

  protected synchronized void addItem(MenuItem item, android.view.Menu target) {
    if (item instanceof Menu) {
      SubMenu subMenu = target.addSubMenu(item.getLabel());
      for (int i = 0; i < ((Menu) item).getItemCount(); i++) {
        addItem(((Menu) item).getItem(i), subMenu);
      }
    } else {
      target.add(groupIdForNextItem, 0, 0, item.getLabel());
    }
  }

  @Override
  public void setLabel(String label) {
    if (parent != null) {
      parent.rebuildMenu(); // Propagate new name into parent menu
    }
  }

  private interface MenuIndexOccupant {
    void addToMenu();
  }

  private class NormalMenuItem implements MenuIndexOccupant {
    final MenuItem item;

    private NormalMenuItem(MenuItem item) {
      this.item = item;
    }

    @Override
    public void addToMenu() {
      addItem(item, androidMenu);
    }
  }

  private class Separator implements MenuIndexOccupant {

    @Override
    public void addToMenu() {
      groupIdForNextItem++;
    }
  }
}
