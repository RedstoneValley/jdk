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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Vector;

import skinjob.internal.NullWidgetSupplier;
import skinjob.internal.WrappedAndroidObjectsSupplier;

/**
 * The {@code Choice} class presents a pop-up menu of choices. The current choice is displayed as
 * the title of the menu.
 * <p>
 * The following code example produces a pop-up menu:
 * <p>
 * <hr><blockquote><pre>
 * Choice ColorChooser = new Choice();
 * ColorChooser.add("Green");
 * ColorChooser.add("Red");
 * ColorChooser.add("Blue");
 * </pre></blockquote><hr>
 * <p>
 * After this choice menu has been added to a panel, it appears as follows in its normal state:
 * <p>
 * <img src="doc-files/Choice-1.gif" alt="The following text describes the graphic"
 * style="float:center; margin: 7px 10px;">
 * <p>
 * In the picture, {@code "Green"} is the current choice. Pushing the mouse button down on the
 * object causes a menu to appear with the current choice highlighted.
 * <p>
 * Some native platforms do not support arbitrary resizing of {@code Choice} components and the
 * behavior of {@code setSize()/getSize()} is bound by such limitations. Native GUI {@code Choice}
 * components' size are often bound by such attributes as font size and length of items contained
 * within the {@code Choice}.
 * <p>
 *
 * @author Sami Shaio
 * @author Arthur van Hoff
 * @since JDK1.0
 */
public class Choice extends Component implements ItemSelectable {
  private static final String base = "choice";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -4075310674757313071L;
  private static int nameCounter;

  static {
        /* ensure that the necessary native libraries are loaded */
    /* initialize JNI field and method ids */
  }

  /**
   * The items for the {@code Choice}. This can be a {@code null} value.
   *
   * @serial
   * @see #add(String)
   * @see #addItem(String)
   * @see #getItem(int)
   * @see #getItemCount()
   * @see #insert(String, int)
   * @see #remove(String)
   */
  final Vector<String> pItems;
  /*
   * Choice Serial Data Version.
   * @serial
   */
  private final int choiceSerializedDataVersion = 1;
  /**
   * The index of the current choice for this {@code Choice} or -1 if nothing is selected.
   *
   * @serial
   * @see #getSelectedItem()
   * @see #select(int)
   */
  int selectedIndex = -1;
  transient ItemListener itemListener;

  /**
   * Creates a new choice menu. The menu initially has no items in it.
   * <p>
   * By default, the first item added to the choice menu becomes the selected item, until a
   * different selection is made by the user by calling one of the {@code select} methods.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
   * @see GraphicsEnvironment#isHeadless
   * @see #select(int)
   * @see #select(String)
   */
  public Choice() throws HeadlessException {
    pItems = new Vector<>();
  }

  @Override
  protected WrappedAndroidObjectsSupplier<?> sjGetWrappedAndroidObjectsSupplier() {
    return NullWidgetSupplier.getInstance();
  }

  /**
   * Constructs a name for this component.  Called by {@code getName} when the name is {@code
   * null}.
   */
  @Override
  String constructComponentName() {
    synchronized (Choice.class) {
      String result = base + nameCounter;
      nameCounter++;
      return result;
    }
  }

  // REMIND: remove when filtering is done at lower level
  @Override
  boolean eventEnabled(AWTEvent e) {
    if (e.id == ItemEvent.ITEM_STATE_CHANGED) {
      return (eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 || itemListener != null;
    }
    return super.eventEnabled(e);
  }

  /**
   * Returns an array of all the objects currently registered as <code><em>Foo</em>Listener</code>s
   * upon this {@code Choice}. <code><em>Foo</em>Listener</code>s are registered using the
   * <code>add<em>Foo</em>Listener</code> method.
   * <p>
   * <p>
   * You can specify the {@code listenerType} argument with a class literal, such as
   * <code><em>Foo</em>Listener.class</code>. For example, you can query a {@code Choice} {@code c}
   * for its item listeners with the following code:
   * <p>
   * <pre>ItemListener[] ils = (ItemListener[])(c.getListeners(ItemListener.class));</pre>
   *
   * If no such listeners exist, this method returns an empty array.
   *
   * @param listenerType the type of listeners requested; this parameter should specify an interface
   * that descends from {@code java.util.EventListener}
   * @return an array of all objects registered as <code><em>Foo</em>Listener</code>s on this
   * choice, or an empty array if no such listeners have been added
   * @throws ClassCastException if {@code listenerType} doesn't specify a class or interface that
   * implements {@code java.util.EventListener}
   * @see #getItemListeners
   * @since 1.3
   */
  @Override
  public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    EventListener l;
    if (listenerType == ItemListener.class) {
      l = itemListener;
    } else {
      return super.getListeners(listenerType);
    }
    return AWTEventMulticaster.getListeners(l, listenerType);
  }

  /**
   * Processes events on this choice. If the event is an instance of {@code ItemEvent}, it invokes
   * the {@code processItemEvent} method. Otherwise, it calls its superclass's {@code processEvent}
   * method. <p>Note that if the event parameter is {@code null} the behavior is unspecified and may
   * result in an exception.
   *
   * @param e the event
   * @see ItemEvent
   * @see #processItemEvent
   * @since JDK1.1
   */
  @Override
  protected void processEvent(AWTEvent e) {
    if (e instanceof ItemEvent) {
      processItemEvent((ItemEvent) e);
      return;
    }
    super.processEvent(e);
  }

  /**
   * Creates the {@code Choice}'s peer.  This peer allows us to change the look of the {@code
   * Choice} without changing its functionality.
   *
   * @see Toolkit#createChoice(Choice)
   * @see Component#getToolkit()
   */
  @Override
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = getToolkit().createChoice(this);
      }
      super.addNotify();
    }
  }

  /**
   * Returns a string representing the state of this {@code Choice} menu. This method is intended to
   * be used only for debugging purposes, and the content and format of the returned string may vary
   * between implementations. The returned string may be empty but may not be {@code null}.
   *
   * @return the parameter string of this {@code Choice} menu
   */
  @Override
  protected String paramString() {
    return super.paramString() + ",current=" + getSelectedItem();
  }

  /**
   * Returns the number of items in this {@code Choice} menu.
   *
   * @return the number of items in this {@code Choice} menu
   * @see #getItem
   * @since JDK1.1
   */
  public int getItemCount() {
    return countItems();
  }

  /**
   * @deprecated As of JDK version 1.1, replaced by {@code getItemCount()}.
   */
  @Deprecated
  public int countItems() {
    return pItems.size();
  }

  /**
   * Gets the string at the specified index in this {@code Choice} menu.
   *
   * @param index the index at which to begin
   * @see #getItemCount
   */
  public String getItem(int index) {
    return getItemImpl(index);
  }

  /*
   * This is called by the native code, so client code can't
   * be called on the toolkit thread.
   */
  final String getItemImpl(int index) {
    return pItems.elementAt(index);
  }

  /**
   * Adds an item to this {@code Choice} menu.
   *
   * @param item the item to be added
   * @throws NullPointerException if the item's value is {@code null}
   * @since JDK1.1
   */
  public void add(String item) {
    addItem(item);
  }

  /**
   * Obsolete as of Java 2 platform v1.1.  Please use the {@code add} method instead.
   * <p>
   * Adds an item to this {@code Choice} menu.
   *
   * @param item the item to be added
   * @throws NullPointerException if the item's value is equal to {@code null}
   */
  public void addItem(String item) {
    synchronized (this) {
      insertNoInvalidate(item, pItems.size());
    }

    // This could change the preferred size of the Component.
    invalidateIfValid();
  }

  /**
   * Inserts an item to this {@code Choice}, but does not invalidate the {@code Choice}. Client
   * methods must provide their own synchronization before invoking this method.
   *
   * @param item the item to be added
   * @param index the new item position
   * @throws NullPointerException if the item's value is equal to {@code null}
   */
  private void insertNoInvalidate(String item, int index) {
    if (item == null) {
      throw new NullPointerException("cannot add null item to Choice");
    }
    pItems.insertElementAt(item, index);
    ChoicePeer peer = (ChoicePeer) this.peer;
    if (peer != null) {
      peer.add(item, index);
    }
    // no selection or selection shifted up
    if (selectedIndex < 0 || selectedIndex >= index) {
      select(0);
    }
  }

  /**
   * Inserts the item into this choice at the specified position. Existing items at an index greater
   * than or equal to {@code index} are shifted up by one to accommodate the new item.  If {@code
   * index} is greater than or equal to the number of items in this choice, {@code item} is added to
   * the end of this choice.
   * <p>
   * If the item is the first one being added to the choice, then the item becomes selected.
   * Otherwise, if the selected item was one of the items shifted, the first item in the choice
   * becomes the selected item.  If the selected item was no among those shifted, it remains the
   * selected item.
   *
   * @param item the non-{@code null} item to be inserted
   * @param index the position at which the item should be inserted
   * @throws IllegalArgumentException if index is less than 0
   */
  public void insert(String item, int index) {
    synchronized (this) {
      if (index < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
            /* if the index greater than item count, add item to the end */
      index = Math.min(index, pItems.size());

      insertNoInvalidate(item, index);
    }

    // This could change the preferred size of the Component.
    invalidateIfValid();
  }

  /**
   * Removes the first occurrence of {@code item} from the {@code Choice} menu.  If the item being
   * removed is the currently selected item, then the first item in the choice becomes the selected
   * item.  Otherwise, the currently selected item remains selected (and the selected index is
   * updated accordingly).
   *
   * @param item the item to remove from this {@code Choice} menu
   * @throws IllegalArgumentException if the item doesn't exist in the choice menu
   * @since JDK1.1
   */
  public void remove(String item) {
    synchronized (this) {
      int index = pItems.indexOf(item);
      if (index < 0) {
        throw new IllegalArgumentException("item " + item +
            " not found in choice");
      } else {
        removeNoInvalidate(index);
      }
    }

    // This could change the preferred size of the Component.
    invalidateIfValid();
  }

  /**
   * Removes an item from the choice menu at the specified position.  If the item being removed is
   * the currently selected item, then the first item in the choice becomes the selected item.
   * Otherwise, the currently selected item remains selected (and the selected index is updated
   * accordingly).
   *
   * @param position the position of the item
   * @throws IndexOutOfBoundsException if the specified position is out of bounds
   * @since JDK1.1
   */
  public void remove(int position) {
    synchronized (this) {
      removeNoInvalidate(position);
    }

    // This could change the preferred size of the Component.
    invalidateIfValid();
  }

  /**
   * Removes an item from the {@code Choice} at the specified position, but does not invalidate the
   * {@code Choice}. Client methods must provide their own synchronization before invoking this
   * method.
   *
   * @param position the position of the item
   */
  private void removeNoInvalidate(int position) {
    pItems.removeElementAt(position);
    ChoicePeer peer = (ChoicePeer) this.peer;
    if (peer != null) {
      peer.remove(position);
    }
        /* Adjust selectedIndex if selected item was removed. */
    if (pItems.isEmpty()) {
      selectedIndex = -1;
    } else if (selectedIndex == position) {
      select(0);
    } else if (selectedIndex > position) {
      select(selectedIndex - 1);
    }
  }

  /**
   * Removes all items from the choice menu.
   *
   * @see #remove
   * @since JDK1.1
   */
  public void removeAll() {
    synchronized (this) {
      if (peer != null) {
        ((ChoicePeer) peer).removeAll();
      }
      pItems.removeAllElements();
      selectedIndex = -1;
    }

    // This could change the preferred size of the Component.
    invalidateIfValid();
  }

  /**
   * Gets a representation of the current choice as a string.
   *
   * @return a string representation of the currently selected item in this choice menu
   * @see #getSelectedIndex
   */
  public synchronized String getSelectedItem() {
    return selectedIndex >= 0 ? getItem(selectedIndex) : null;
  }

  /**
   * Returns an array (length 1) containing the currently selected item.  If this choice has no
   * items, returns {@code null}.
   *
   * @see ItemSelectable
   */
  @Override
  public synchronized Object[] getSelectedObjects() {
    if (selectedIndex >= 0) {
      Object[] items = new Object[1];
      items[0] = getItem(selectedIndex);
      return items;
    }
    return null;
  }

  /**
   * Adds the specified item listener to receive item events from this {@code Choice} menu.  Item
   * events are sent in response to user input, but not in response to calls to {@code select}. If l
   * is {@code null}, no exception is thrown and no action is performed. <p>Refer to <a
   * href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading Issues</a> for details on
   * AWT's threading model.
   *
   * @param l the item listener
   * @see #removeItemListener
   * @see #getItemListeners
   * @see #select
   * @see ItemEvent
   * @see ItemListener
   * @since JDK1.1
   */
  @Override
  public synchronized void addItemListener(ItemListener l) {
    if (l == null) {
      return;
    }
    itemListener = AWTEventMulticaster.add(itemListener, l);
    newEventsOnly = true;
  }

  /**
   * Removes the specified item listener so that it no longer receives item events from this {@code
   * Choice} menu. If l is {@code null}, no exception is thrown and no action is performed. <p>Refer
   * to <a href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading Issues</a> for
   * details on AWT's threading model.
   *
   * @param l the item listener
   * @see #addItemListener
   * @see #getItemListeners
   * @see ItemEvent
   * @see ItemListener
   * @since JDK1.1
   */
  @Override
  public synchronized void removeItemListener(ItemListener l) {
    if (l == null) {
      return;
    }
    itemListener = AWTEventMulticaster.remove(itemListener, l);
  }

  /**
   * Returns the index of the currently selected item. If nothing is selected, returns -1.
   *
   * @return the index of the currently selected item, or -1 if nothing is currently selected
   * @see #getSelectedItem
   */
  public int getSelectedIndex() {
    return selectedIndex;
  }

  /**
   * Sets the selected item in this {@code Choice} menu to be the item at the specified position.
   * <p>
   * <p>Note that this method should be primarily used to initially select an item in this
   * component. Programmatically calling this method will <i>not</i> trigger an {@code ItemEvent}.
   * The only way to trigger an {@code ItemEvent} is by user interaction.
   *
   * @param pos the position of the selected item
   * @throws IllegalArgumentException if the specified position is greater than the number of items
   * or less than zero
   * @see #getSelectedItem
   * @see #getSelectedIndex
   */
  public synchronized void select(int pos) {
    if (pos >= pItems.size() || pos < 0) {
      throw new IllegalArgumentException("illegal Choice item position: " + pos);
    }
    if (!pItems.isEmpty()) {
      selectedIndex = pos;
      ChoicePeer peer = (ChoicePeer) this.peer;
      if (peer != null) {
        peer.select(pos);
      }
    }
  }

  /**
   * Sets the selected item in this {@code Choice} menu to be the item whose name is equal to the
   * specified string. If more than one item matches (is equal to) the specified string, the one
   * with the smallest index is selected.
   * <p>
   * <p>Note that this method should be primarily used to initially select an item in this
   * component. Programmatically calling this method will <i>not</i> trigger an {@code ItemEvent}.
   * The only way to trigger an {@code ItemEvent} is by user interaction.
   *
   * @param str the specified string
   * @see #getSelectedItem
   * @see #getSelectedIndex
   */
  public synchronized void select(String str) {
    int index = pItems.indexOf(str);
    if (index >= 0) {
      select(index);
    }
  }

  /**
   * Returns an array of all the item listeners registered on this choice.
   *
   * @return all of this choice's {@code ItemListener}s or an empty array if no item listeners are
   * currently registered
   * @see #addItemListener
   * @see #removeItemListener
   * @see ItemEvent
   * @see ItemListener
   * @since 1.4
   */
  public synchronized ItemListener[] getItemListeners() {
    return getListeners(ItemListener.class);
  }


    /* Serialization support.
     */

  /**
   * Processes item events occurring on this {@code Choice} menu by dispatching them to any
   * registered {@code ItemListener} objects.
   * <p>
   * This method is not called unless item events are enabled for this component. Item events are
   * enabled when one of the following occurs: <ul> <li>An {@code ItemListener} object is registered
   * via {@code addItemListener}. <li>Item events are enabled via {@code enableEvents}. </ul>
   * <p>Note that if the event parameter is {@code null} the behavior is unspecified and may result
   * in an exception.
   *
   * @param e the item event
   * @see ItemEvent
   * @see ItemListener
   * @see #addItemListener(ItemListener)
   * @see Component#enableEvents
   * @since JDK1.1
   */
  protected void processItemEvent(ItemEvent e) {
    ItemListener listener = itemListener;
    if (listener != null) {
      listener.itemStateChanged(e);
    }
  }

  /**
   * Writes default serializable fields to stream.  Writes a list of serializable {@code
   * ItemListeners} as optional data. The non-serializable {@code ItemListeners} are detected and no
   * attempt is made to serialize them.
   *
   * @param s the {@code ObjectOutputStream} to write
   * @serialData {@code null} terminated sequence of 0 or more pairs; the pair consists of a {@code
   * String} and an {@code Object}; the {@code String} indicates the type of object and is one of
   * the following: {@code itemListenerK} indicating an {@code ItemListener} object
   * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
   * @see Component#itemListenerK
   * @see #readObject(ObjectInputStream)
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();

    AWTEventMulticaster.save(s, itemListenerK, itemListener);
    s.writeObject(null);
  }

  /**
   * Reads the {@code ObjectInputStream} and if it isn't {@code null} adds a listener to receive
   * item events fired by the {@code Choice} item. Unrecognized keys or values will be ignored.
   *
   * @param s the {@code ObjectInputStream} to read
   * @throws HeadlessException if {@code GraphicsEnvironment.isHeadless} returns {@code true}
   * @serial
   * @see #removeItemListener(ItemListener)
   * @see #addItemListener(ItemListener)
   * @see GraphicsEnvironment#isHeadless
   * @see #writeObject(ObjectOutputStream)
   */
  private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException {
    s.defaultReadObject();

    Object keyOrNull;
    while (null != (keyOrNull = s.readObject())) {
      String key = ((String) keyOrNull).intern();

      if (itemListenerK == key) {
        addItemListener((ItemListener) s.readObject());
      } else // skip value for unrecognized key
      {
        s.readObject();
      }
    }
  }
}
