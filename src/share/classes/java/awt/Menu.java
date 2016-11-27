/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

/**
 * A {@code Menu} object is a pull-down menu component
 * that is deployed from a menu bar.
 * <p>
 * A menu can optionally be a <i>tear-off</i> menu. A tear-off menu
 * can be opened and dragged away from its parent menu bar or menu.
 * It remains on the screen after the mouse button has been released.
 * The mechanism for tearing off a menu is platform dependent, since
 * the look and feel of the tear-off menu is determined by its peer.
 * On platforms that do not support tear-off menus, the tear-off
 * property is ignored.
 * <p>
 * Each item in a menu must belong to the {@code MenuItem}
 * class. It can be an instance of {@code MenuItem}, a submenu
 * (an instance of {@code Menu}), or a check box (an instance of
 * {@code CheckboxMenuItem}).
 *
 * @author Sami Shaio
 * @see MenuItem
 * @see CheckboxMenuItem
 * @since JDK1.0
 */
public class Menu extends MenuItem implements MenuContainer {

  private static final String base = "menu";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -8809584163345499784L;
  private static int nameCounter;

  static {
  }

  /**
   * The menu serialized Data Version.
   *
   * @serial
   */
  private final int menuSerializedDataVersion = 1;
  public android.view.Menu androidMenu;
  /**
   * A vector of the items that will be part of the Menu.
   *
   * @serial
   * @see #countItems()
   */
  final Vector<MenuComponent> items = new Vector<>();
  /**
   * This field indicates whether the menu has the
   * tear of property or not.  It will be set to
   * {@code true} if the menu has the tear off
   * property and it will be set to {@code false}
   * if it does not.
   * A torn off menu can be deleted by a user when
   * it is no longer needed.
   *
   * @serial
   * @see #isTearOff()
   */
  final boolean tearOff;
  /**
   * This field will be set to {@code true}
   * if the Menu in question is actually a help
   * menu.  Otherwise it will be set to {@code
   * false}.
   *
   * @serial
   */
  boolean isHelpMenu;

  /**
   * Constructs a new menu with an empty label. This menu is not
   * a tear-off menu.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   * @since JDK1.1
   */
  public Menu() throws HeadlessException {
    this("", false);
  }

  /**
   * Constructs a new menu with the specified label. This menu is not
   * a tear-off menu.
   *
   * @param label the menu's label in the menu bar, or in
   *              another menu of which this menu is a submenu.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Menu(String label) throws HeadlessException {
    this(label, false);
  }

  /**
   * Constructs a new menu with the specified label,
   * indicating whether the menu can be torn off.
   * <p>
   * Tear-off functionality may not be supported by all
   * implementations of AWT.  If a particular implementation doesn't
   * support tear-off menus, this value is silently ignored.
   *
   * @param label   the menu's label in the menu bar, or in
   *                another menu of which this menu is a submenu.
   * @param tearOff if {@code true}, the menu
   *                is a tear-off menu.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   * @since JDK1.0.
   */
  public Menu(String label, boolean tearOff) throws HeadlessException {
    super(label);
    this.tearOff = tearOff;
  }

  /**
   * Construct a name for this MenuComponent.  Called by getName() when
   * the name is null.
   */
  @Override
  String constructComponentName() {
    synchronized (Menu.class) {
      String result = base + nameCounter;
      nameCounter++;
      return result;
    }
  }

  /**
   * Returns a string representing the state of this {@code Menu}.
   * This method is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not be
   * {@code null}.
   *
   * @return the parameter string of this menu
   */
  @Override
  public String paramString() {
    String str = ",tearOff=" + tearOff + ",isHelpMenu=" + isHelpMenu;
    return super.paramString() + str;
  }

  /**
   * Gets the AccessibleContext associated with this Menu.
   * For menus, the AccessibleContext takes the form of an
   * AccessibleAWTMenu.
   * A new AccessibleAWTMenu instance is created if necessary.
   *
   * @return an AccessibleAWTMenu that serves as the
   * AccessibleContext of this Menu
   * @since 1.3
   */
  @Override
  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTMenu();
    }
    return accessibleContext;
  }

  /**
   * Creates the menu's peer.  The peer allows us to modify the
   * appearance of the menu without changing its functionality.
   */
  @Override
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = Toolkit.getDefaultToolkit().createMenu(this);
      }
      int nitems = getItemCount();
      for (int i = 0; i < nitems; i++) {
        MenuItem mi = getItem(i);
        mi.parent = this;
        mi.addNotify();
      }
    }
  }

  @Override
  void deleteShortcut(MenuShortcut s) {
    int nitems = getItemCount();
    for (int i = 0; i < nitems; i++) {
      getItem(i).deleteShortcut(s);
    }
  }

  /*
   * Post an ActionEvent to the target of the MenuPeer
   * associated with the specified keyboard event (on
   * keydown).  Returns true if there is an associated
   * keyboard event.
   */
  @Override
  boolean handleShortcut(KeyEvent e) {
    int nitems = getItemCount();
    for (int i = 0; i < nitems; i++) {
      MenuItem mi = getItem(i);
      if (mi.handleShortcut(e)) {
        return true;
      }
    }
    return false;
  }

  @Override
  MenuItem getShortcutMenuItem(MenuShortcut s) {
    int nitems = getItemCount();
    for (int i = 0; i < nitems; i++) {
      MenuItem mi = getItem(i).getShortcutMenuItem(s);
      if (mi != null) {
        return mi;
      }
    }
    return null;
  }

  /**
   * Removes the menu's peer.  The peer allows us to modify the appearance
   * of the menu without changing its functionality.
   */
  @Override
  public void removeNotify() {
    synchronized (getTreeLock()) {
      int nitems = getItemCount();
      for (int i = 0; i < nitems; i++) {
        getItem(i).removeNotify();
      }
      super.removeNotify();
    }
  }

  /**
   * Defined in MenuComponent. Overridden here.
   */
  @Override
  int getAccessibleChildIndex(MenuComponent child) {
    return items.indexOf(child);
  }

  /**
   * Indicates whether this menu is a tear-off menu.
   * <p>
   * Tear-off functionality may not be supported by all
   * implementations of AWT.  If a particular implementation doesn't
   * support tear-off menus, this value is silently ignored.
   *
   * @return {@code true} if this is a tear-off menu;
   * {@code false} otherwise.
   */
  public boolean isTearOff() {
    return tearOff;
  }

  /**
   * Get the number of items in this menu.
   *
   * @return the number of items in this menu.
   * @since JDK1.1
   */
  public int getItemCount() {
    return countItems();
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code getItemCount()}.
   */
  @Deprecated
  public int countItems() {
    return countItemsImpl();
  }

  /*
   * This is called by the native code, so client code can't
   * be called on the toolkit thread.
   */
  final int countItemsImpl() {
    return items.size();
  }

  /**
   * Gets the item located at the specified index of this menu.
   *
   * @param index the position of the item to be returned.
   * @return the item located at the specified index.
   */
  public MenuItem getItem(int index) {
    return getItemImpl(index);
  }

  /*
   * This is called by the native code, so client code can't
   * be called on the toolkit thread.
   */
  final MenuItem getItemImpl(int index) {
    return (MenuItem) items.elementAt(index);
  }

  /**
   * Adds the specified menu item to this menu. If the
   * menu item has been part of another menu, removes it
   * from that menu.
   *
   * @param mi the menu item to be added
   * @return the menu item added
   * @see Menu#insert(String, int)
   * @see Menu#insert(MenuItem, int)
   */
  public MenuItem add(MenuItem mi) {
    synchronized (getTreeLock()) {
      if (mi.parent != null) {
        mi.parent.remove(mi);
      }
      items.addElement(mi);
      mi.parent = this;
      MenuPeer peer = (MenuPeer) this.peer;
      if (peer != null) {
        mi.addNotify();
        peer.addItem(mi);
      }
      return mi;
    }
  }

  /**
   * Adds an item with the specified label to this menu.
   *
   * @param label the text on the item
   * @see Menu#insert(String, int)
   * @see Menu#insert(MenuItem, int)
   */
  public void add(String label) {
    add(new MenuItem(label));
  }

  /**
   * Inserts a menu item into this menu
   * at the specified position.
   *
   * @param menuitem the menu item to be inserted.
   * @param index    the position at which the menu
   *                 item should be inserted.
   * @throws IllegalArgumentException if the value of
   *                                  {@code index} is less than zero
   * @see Menu#add(String)
   * @see Menu#add(MenuItem)
   * @since JDK1.1
   */

  public void insert(MenuItem menuitem, int index) {
    synchronized (getTreeLock()) {
      if (index < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }

      int nitems = getItemCount();
      Vector<MenuItem> tempItems = new Vector<>();

            /* Remove the item at index, nitems-index times
               storing them in a temporary vector in the
               order they appear on the menu.
            */
      for (int i = index; i < nitems; i++) {
        tempItems.addElement(getItem(index));
        remove(index);
      }

      add(menuitem);

            /* Add the removed items back to the menu, they are
               already in the correct order in the temp vector.
            */
      for (int i = 0; i < tempItems.size(); i++) {
        add(tempItems.elementAt(i));
      }
    }
  }

  /**
   * Inserts a menu item with the specified label into this menu
   * at the specified position.  This is a convenience method for
   * {@code insert(menuItem, index)}.
   *
   * @param label the text on the item
   * @param index the position at which the menu item
   *              should be inserted
   * @throws IllegalArgumentException if the value of
   *                                  {@code index} is less than zero
   * @see Menu#add(String)
   * @see Menu#add(MenuItem)
   * @since JDK1.1
   */

  public void insert(String label, int index) {
    insert(new MenuItem(label), index);
  }

  /**
   * Adds a separator line, or a hypen, to the menu at the current position.
   *
   * @see Menu#insertSeparator(int)
   */
  public void addSeparator() {
    add("-");
  }


    /* Serialization support.  A MenuContainer is responsible for
     * restoring the parent fields of its children.
     */

  /**
   * Inserts a separator at the specified position.
   *
   * @param index the position at which the
   *              menu separator should be inserted.
   * @throws IllegalArgumentException if the value of
   *                                  {@code index} is less than 0.
   * @see Menu#addSeparator
   * @since JDK1.1
   */

  public void insertSeparator(int index) {
    synchronized (getTreeLock()) {
      if (index < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }

      int nitems = getItemCount();
      Vector<MenuItem> tempItems = new Vector<>();

            /* Remove the item at index, nitems-index times
               storing them in a temporary vector in the
               order they appear on the menu.
            */
      for (int i = index; i < nitems; i++) {
        tempItems.addElement(getItem(index));
        remove(index);
      }

      addSeparator();

            /* Add the removed items back to the menu, they are
               already in the correct order in the temp vector.
            */
      for (int i = 0; i < tempItems.size(); i++) {
        add(tempItems.elementAt(i));
      }
    }
  }

  /**
   * Removes the menu item at the specified index from this menu.
   *
   * @param index the position of the item to be removed.
   */
  public void remove(int index) {
    synchronized (getTreeLock()) {
      MenuItem mi = getItem(index);
      items.removeElementAt(index);
      MenuPeer peer = (MenuPeer) this.peer;
      if (peer != null) {
        mi.removeNotify();
        mi.parent = null;
        peer.delItem(index);
      }
    }
  }

  /**
   * Removes the specified menu item from this menu.
   *
   * @param item the item to be removed from the menu.
   *             If {@code item} is {@code null}
   *             or is not in this menu, this method does
   *             nothing.
   */
  @Override
  public void remove(MenuComponent item) {
    synchronized (getTreeLock()) {
      int index = items.indexOf(item);
      if (index >= 0) {
        remove(index);
      }
    }
  }

  /**
   * Removes all items from this menu.
   *
   * @since JDK1.0.
   */
  public void removeAll() {
    synchronized (getTreeLock()) {
      int nitems = getItemCount();
      for (int i = nitems - 1; i >= 0; i--) {
        remove(i);
      }
    }
  }

  synchronized Enumeration<MenuShortcut> shortcuts() {
    Vector<MenuShortcut> shortcuts = new Vector<>();
    int nitems = getItemCount();
    for (int i = 0; i < nitems; i++) {
      MenuItem mi = getItem(i);
      if (mi instanceof Menu) {
        Enumeration<MenuShortcut> e = ((Menu) mi).shortcuts();
        while (e.hasMoreElements()) {
          shortcuts.addElement(e.nextElement());
        }
      } else {
        MenuShortcut ms = mi.getShortcut();
        if (ms != null) {
          shortcuts.addElement(ms);
        }
      }
    }
    return shortcuts.elements();
  }

  /////////////////
  // Accessibility support
  ////////////////

  /**
   * Writes default serializable fields to stream.
   *
   * @param s the {@code ObjectOutputStream} to write
   * @see #readObject(ObjectInputStream)
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
  }

  /**
   * Reads the {@code ObjectInputStream}.
   * Unrecognized keys or values will be ignored.
   *
   * @param s the {@code ObjectInputStream} to read
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless} returns
   *                           {@code true}
   * @see GraphicsEnvironment#isHeadless
   * @see #writeObject(ObjectOutputStream)
   */
  private void readObject(ObjectInputStream s)
      throws IOException, ClassNotFoundException, HeadlessException {
    // HeadlessException will be thrown from MenuComponent's readObject
    s.defaultReadObject();
    for (int i = 0; i < items.size(); i++) {
      MenuItem item = (MenuItem) items.elementAt(i);
      item.parent = this;
    }
  }

  /**
   * Inner class of Menu used to provide default support for
   * accessibility.  This class is not meant to be used directly by
   * application developers, but is instead meant only to be
   * subclassed by menu component developers.
   * <p>
   * This class implements accessibility support for the
   * {@code Menu} class.  It provides an implementation of the
   * Java Accessibility API appropriate to menu user-interface elements.
   *
   * @since 1.3
   */
  protected class AccessibleAWTMenu extends AccessibleAWTMenuItem {
    /*
     * JDK 1.3 serialVersionUID
     */
    private static final long serialVersionUID = 5228160894980069094L;

    /**
     * Get the role of this object.
     *
     * @return an instance of AccessibleRole describing the role of the
     * object
     */
    @Override
    public AccessibleRole getAccessibleRole() {
      return AccessibleRole.MENU;
    }
  } // class AccessibleAWTMenu
}
