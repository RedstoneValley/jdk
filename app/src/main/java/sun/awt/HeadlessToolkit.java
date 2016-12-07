/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import sun.awt.datatransfer.DataTransferer;

public class HeadlessToolkit extends Toolkit
    implements ComponentFactory, KeyboardFocusManagerPeerProvider {

  private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() {
    @Override
    public Window getCurrentFocusedWindow() {
      return null;
    }

    @Override
    public void setCurrentFocusedWindow(Window win) {
    }

    @Override
    public Component getCurrentFocusOwner() {
      return null;
    }

    @Override
    public void setCurrentFocusOwner(Component comp) {
    }

    @Override
    public void clearGlobalFocusOwner(Window activeWindow) {
    }
  };

  private final Toolkit tk;
  private ComponentFactory componentFactory;

  public HeadlessToolkit(Toolkit tk) {
    this.tk = tk;
    if (tk instanceof ComponentFactory) {
      componentFactory = (ComponentFactory) tk;
    }
  }

  public Toolkit getUnderlyingToolkit() {
    return tk;
  }

    /*
     * Component peer objects.
     */

    /* Lightweight implementation of Canvas and Panel */

  @Override
  public DataTransferer getDataTransferer() {
    return null;
  }

    /*
     * Component peer objects - unsupported.
     */

  @Override
  public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
    // See 6833019.
    return kfmPeer;
  }

  @Override
  public TrayIconPeer createTrayIcon(TrayIcon target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public SystemTrayPeer createSystemTray(SystemTray target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public boolean isTraySupported() {
    return false;
  }

  @Override
  public DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ButtonPeer createButton(Button target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public TextFieldPeer createTextField(TextField target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public LabelPeer createLabel(Label target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ListPeer createList(List target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ChoicePeer createChoice(Choice target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public FramePeer createFrame(Frame target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public CanvasPeer createCanvas(Canvas target) {
    return (CanvasPeer) createComponent(target);
  }

  @Override
  public PanelPeer createPanel(Panel target) {
    return (PanelPeer) createComponent(target);
  }

  @Override
  public WindowPeer createWindow(Window target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public DialogPeer createDialog(Dialog target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public MenuPeer createMenu(Menu target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
      throws HeadlessException {
    throw new HeadlessException();
  }

  /*
   * Fonts
   */
  @Override
  public FontPeer getFontPeer(String name, int style) {
    if (componentFactory != null) {
      return componentFactory.getFontPeer(name, style);
    }
    return null;
  }

  /*
   * Headless toolkit - unsupported.
   */
  @Override
  protected void loadSystemColors(int[] systemColors) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public void setDynamicLayout(boolean dynamic) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  protected boolean isDynamicLayoutSet() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public boolean isDynamicLayoutActive() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public Dimension getScreenSize() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public int getScreenResolution() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public Insets getScreenInsets(GraphicsConfiguration gc) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public ColorModel getColorModel() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  @SuppressWarnings("deprecation")
  public String[] getFontList() {
    return tk.getFontList();
  }

  @Override
  @SuppressWarnings("deprecation")
  public FontMetrics getFontMetrics(Font font) {
    return tk.getFontMetrics(font);
  }

  @Override
  public void sync() {
    // Do nothing
  }

  @Override
  public Image getImage(String filename) {
    return tk.getImage(filename);
  }

  @Override
  public Image getImage(URL url) {
    return tk.getImage(url);
  }

  @Override
  public Image createImage(String filename) {
    return tk.createImage(filename);
  }

  @Override
  public Image createImage(URL url) {
    return tk.createImage(url);
  }

  @Override
  public boolean prepareImage(
      Image img, int w, int h, ImageObserver o) {
    return tk.prepareImage(img, w, h, o);
  }

    /*
     * Headless toolkit - supported.
     */

  /*
   * Images.
   */
  @Override
  public int checkImage(Image img, int w, int h, ImageObserver o) {
    return tk.checkImage(img, w, h, o);
  }

  @Override
  public Image createImage(ImageProducer producer) {
    return tk.createImage(producer);
  }

  @Override
  public Image createImage(byte[] imagedata) {
    return tk.createImage(imagedata);
  }

  @Override
  public Image createImage(byte[] data, int offset, int length) {
    return tk.createImage(data, offset, length);
  }

  @Override
  public PrintJob getPrintJob(Frame frame, String doctitle, Properties props) {
    if (frame != null) {
      // Should never happen
      throw new HeadlessException();
    }
    throw new NullPointerException("frame must not be null");
  }

  /*
   * Printing
   */
  @Override
  public PrintJob getPrintJob(
      Frame frame, String jobtitle, JobAttributes jobAttributes, PageAttributes pageAttributes) {
    if (frame != null) {
      // Should never happen
      throw new HeadlessException();
    }
    throw new NullPointerException("frame must not be null");
  }

  @Override
  public void beep() {
    // Send alert character
    System.out.write(0x07);
  }

  @Override
  public Clipboard getSystemClipboard() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public int getMenuShortcutKeyMask() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public boolean getLockingKeyState(int keyCode) throws UnsupportedOperationException {
    throw new HeadlessException();
  }

  @Override
  public void setLockingKeyState(int keyCode, boolean on) throws UnsupportedOperationException {
    throw new HeadlessException();
  }

  @Override
  public Cursor createCustomCursor(Image cursor, Point hotSpot, String name)
      throws IndexOutOfBoundsException, HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public Dimension getBestCursorSize(int preferredWidth, int preferredHeight)
      throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public int getMaximumCursorColors() throws HeadlessException {
    throw new HeadlessException();
  }

  /*
   * Event Queue
   */
  @Override
  public EventQueue getSystemEventQueueImpl() {
    return SunToolkit.getSystemEventQueueImplPP();
  }

  @Override
  public DragSourceContextPeer createDragSourceContextPeer(
      DragGestureEvent dge) throws InvalidDnDOperationException {
    throw new InvalidDnDOperationException("Headless environment");
  }

    /*
     * Desktop properties
     */

  @Override
  public <T extends DragGestureRecognizer> T createDragGestureRecognizer(
      Class<T> abstractRecognizerClass, DragSource ds, Component c, int srcActions,
      DragGestureListener dgl) {
    return null;
  }

  @Override
  public void addPropertyChangeListener(String name, PropertyChangeListener pcl) {
    tk.addPropertyChangeListener(name, pcl);
  }

  @Override
  public void removePropertyChangeListener(String name, PropertyChangeListener pcl) {
    tk.removePropertyChangeListener(name, pcl);
  }

  /*
   * Always on top
   */
  @Override
  public boolean isAlwaysOnTopSupported() {
    return false;
  }

  /*
   * Modality
   */
  @Override
  public boolean isModalityTypeSupported(ModalityType modalityType) {
    return false;
  }

    /*
     * AWT Event listeners
     */

  @Override
  public boolean isModalExclusionTypeSupported(ModalExclusionType exclusionType) {
    return false;
  }

  @Override
  public void addAWTEventListener(AWTEventListener listener, long eventMask) {
    tk.addAWTEventListener(listener, eventMask);
  }

  @Override
  public void removeAWTEventListener(AWTEventListener listener) {
    tk.removeAWTEventListener(listener);
  }

  @Override
  public AWTEventListener[] getAWTEventListeners() {
    return tk.getAWTEventListeners();
  }

  @Override
  public AWTEventListener[] getAWTEventListeners(long eventMask) {
    return tk.getAWTEventListeners(eventMask);
  }

  @Override
  public Map mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
    throw new HeadlessException();
  }
}
