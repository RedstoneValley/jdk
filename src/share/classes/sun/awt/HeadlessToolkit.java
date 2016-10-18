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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
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
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SkinJob;
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
import java.awt.event.PaintEvent;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
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
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.datatransfer.DataTransferer;
import sun.java2d.pipe.Region;

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
  public RobotPeer createRobot(Robot target, GraphicsDevice screen)
      throws AWTException, HeadlessException {
    throw new HeadlessException();
  }

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

  public TrayIconPeer createTrayIcon(TrayIcon target) throws HeadlessException {
    throw new HeadlessException();
  }

  public SystemTrayPeer createSystemTray(SystemTray target) throws HeadlessException {
    throw new HeadlessException();
  }

  public boolean isTraySupported() {
    return false;
  }

  public GlobalCursorManager getGlobalCursorManager() throws HeadlessException {
    throw new HeadlessException();
  }

  public int getScreenHeight() throws HeadlessException {
    throw new HeadlessException();
  }

  public int getScreenWidth() throws HeadlessException {
    throw new HeadlessException();
  }

  public boolean isDesktopSupported() {
    return false;
  }

  @Override
  public DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    throw new HeadlessException();
  }

  public ButtonPeer createButton(Button target) throws HeadlessException {
    throw new HeadlessException();
  }

  public TextFieldPeer createTextField(TextField target) throws HeadlessException {
    throw new HeadlessException();
  }

  public LabelPeer createLabel(Label target) throws HeadlessException {
    throw new HeadlessException();
  }

  public ListPeer createList(List target) throws HeadlessException {
    throw new HeadlessException();
  }

  public CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
    throw new HeadlessException();
  }

  public ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
    throw new HeadlessException();
  }

  public ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
    throw new HeadlessException();
  }

  public TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
    throw new HeadlessException();
  }

  public ChoicePeer createChoice(Choice target) throws HeadlessException {
    throw new HeadlessException();
  }

  public FramePeer createFrame(Frame target) throws HeadlessException {
    throw new HeadlessException();
  }

  public CanvasPeer createCanvas(Canvas target) {
    return (CanvasPeer) createComponent(target);
  }

  public PanelPeer createPanel(Panel target) {
    return (PanelPeer) createComponent(target);
  }

  public WindowPeer createWindow(Window target) throws HeadlessException {
    throw new HeadlessException();
  }

  public DialogPeer createDialog(Dialog target) throws HeadlessException {
    throw new HeadlessException();
  }

  public MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
    throw new HeadlessException();
  }

  public MenuPeer createMenu(Menu target) throws HeadlessException {
    throw new HeadlessException();
  }

  public PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
    throw new HeadlessException();
  }

  public MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
    throw new HeadlessException();
  }

  public FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
    throw new HeadlessException();
  }

  public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
      throws HeadlessException {
    throw new HeadlessException();
  }

  /*
   * Fonts
   */
  @Override
  @SuppressWarnings("deprecation")
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

  protected Context getAndroidContext() {
    return SkinJob.getAndroidApplicationContext();
  }

  protected void launchIntent(File file, String action) throws IOException {
    Intent intentToOpen = new Intent(action);
    Uri fileUri = Uri.fromFile(file);
    FileInputStream fileInputStream = new FileInputStream(file);
    try {
      String mime = URLConnection.guessContentTypeFromStream();
      if (mime == null) {
        mime = URLConnection.guessContentTypeFromName(file.getName());
      }
      intentToOpen.setDataAndType(fileUri, mime);
      androidContext.startActivity(intentToOpen);
    } finally {
      fileInputStream.close();
    }
  }

  private static class ButtonPeerImpl implements ButtonPeer {
    public ButtonPeerImpl(Button target) {

    }

    @Override
    public void setLabel(String label) {

    }

    @Override
    public boolean isObscured() {
      return false;
    }

    @Override
    public void applyShape(Region shape) {

    }

    @Override
    public boolean canDetermineObscurity() {
      return false;
    }

    @Override
    public void setVisible(boolean v) {

    }

    @Override
    public void setEnabled(boolean e) {

    }

    @Override
    public void paint(Graphics g) {

    }

    @Override
    public void print(Graphics g) {

    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {

    }

    @Override
    public void handleEvent(AWTEvent e) {

    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {

    }

    @Override
    public Point getLocationOnScreen() {
      return null;
    }

    @Override
    public Dimension getPreferredSize() {
      return null;
    }

    @Override
    public Dimension getMinimumSize() {
      return null;
    }

    @Override
    public ColorModel getColorModel() {
      return null;
    }

    @Override
    public Graphics getGraphics() {
      return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
      return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void setForeground(Color c) {

    }

    @Override
    public void setBackground(Color c) {

    }

    @Override
    public void setFont(Font f) {

    }

    @Override
    public void updateCursorImmediately() {

    }

    @Override
    public boolean requestFocus(
        Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed,
        long time, Cause cause) {
      return false;
    }

    @Override
    public boolean isFocusable() {
      return false;
    }

    @Override
    public Image createImage(ImageProducer producer) {
      return null;
    }

    @Override
    public Image createImage(int width, int height) {
      return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
      return null;
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
      return false;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
      return 0;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
    }

    @Override
    public boolean handlesWheelScrolling() {
      return false;
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

    }

    @Override
    public Image getBackBuffer() {
      return null;
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

    }

    @Override
    public void destroyBuffers() {

    }

    @Override
    public void reparent(ContainerPeer newContainer) {

    }

    @Override
    public boolean isReparentSupported() {
      return false;
    }

    @Override
    public void layout() {

    }

    @Override
    public void setZOrder(ComponentPeer above) {

    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
      return false;
    }
  }

  private static class TextFieldPeerImpl implements TextFieldPeer {
    public TextFieldPeerImpl(TextField target) {

    }

    @Override
    public void setEchoChar(char echoChar) {

    }

    @Override
    public Dimension getPreferredSize(int columns) {
      return null;
    }

    @Override
    public Dimension getMinimumSize(int columns) {
      return null;
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public String getText() {
      return null;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public int getSelectionStart() {
      return 0;
    }

    @Override
    public int getSelectionEnd() {
      return 0;
    }

    @Override
    public void select(int selStart, int selEnd) {

    }

    @Override
    public int getCaretPosition() {
      return 0;
    }

    @Override
    public void setCaretPosition(int pos) {

    }

    @Override
    public InputMethodRequests getInputMethodRequests() {
      return null;
    }

    @Override
    public void applyShape(Region shape) {

    }

    @Override
    public boolean isObscured() {
      return false;
    }

    @Override
    public boolean canDetermineObscurity() {
      return false;
    }

    @Override
    public void setVisible(boolean v) {

    }

    @Override
    public void setEnabled(boolean e) {

    }

    @Override
    public void paint(Graphics g) {

    }

    @Override
    public void print(Graphics g) {

    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {

    }

    @Override
    public void handleEvent(AWTEvent e) {

    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {

    }

    @Override
    public Point getLocationOnScreen() {
      return null;
    }

    @Override
    public Dimension getPreferredSize() {
      return null;
    }

    @Override
    public Dimension getMinimumSize() {
      return null;
    }

    @Override
    public ColorModel getColorModel() {
      return null;
    }

    @Override
    public Graphics getGraphics() {
      return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
      return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void setForeground(Color c) {

    }

    @Override
    public void setBackground(Color c) {

    }

    @Override
    public void setFont(Font f) {

    }

    @Override
    public void updateCursorImmediately() {

    }

    @Override
    public boolean requestFocus(
        Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed,
        long time, Cause cause) {
      return false;
    }

    @Override
    public boolean isFocusable() {
      return false;
    }

    @Override
    public Image createImage(ImageProducer producer) {
      return null;
    }

    @Override
    public Image createImage(int width, int height) {
      return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
      return null;
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
      return false;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
      return 0;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
    }

    @Override
    public boolean handlesWheelScrolling() {
      return false;
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

    }

    @Override
    public Image getBackBuffer() {
      return null;
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

    }

    @Override
    public void destroyBuffers() {

    }

    @Override
    public void reparent(ContainerPeer newContainer) {

    }

    @Override
    public boolean isReparentSupported() {
      return false;
    }

    @Override
    public void layout() {

    }

    @Override
    public void setZOrder(ComponentPeer above) {

    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
      return false;
    }
  }

  private static class LabelPeerImpl implements LabelPeer {
    @Override
    public void setText(String label) {

    }

    @Override
    public void setAlignment(int alignment) {

    }

    @Override
    public void applyShape(Region shape) {

    }

    @Override
    public boolean isObscured() {
      return false;
    }

    @Override
    public boolean canDetermineObscurity() {
      return false;
    }

    @Override
    public void setVisible(boolean v) {

    }

    @Override
    public void setEnabled(boolean e) {

    }

    @Override
    public void paint(Graphics g) {

    }

    @Override
    public void print(Graphics g) {

    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {

    }

    @Override
    public void handleEvent(AWTEvent e) {

    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {

    }

    @Override
    public Point getLocationOnScreen() {
      return null;
    }

    @Override
    public Dimension getPreferredSize() {
      return null;
    }

    @Override
    public Dimension getMinimumSize() {
      return null;
    }

    @Override
    public ColorModel getColorModel() {
      return null;
    }

    @Override
    public Graphics getGraphics() {
      return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
      return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void setForeground(Color c) {

    }

    @Override
    public void setBackground(Color c) {

    }

    @Override
    public void setFont(Font f) {

    }

    @Override
    public void updateCursorImmediately() {

    }

    @Override
    public boolean requestFocus(
        Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed,
        long time, Cause cause) {
      return false;
    }

    @Override
    public boolean isFocusable() {
      return false;
    }

    @Override
    public Image createImage(ImageProducer producer) {
      return null;
    }

    @Override
    public Image createImage(int width, int height) {
      return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
      return null;
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
      return false;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
      return 0;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
    }

    @Override
    public boolean handlesWheelScrolling() {
      return false;
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

    }

    @Override
    public Image getBackBuffer() {
      return null;
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

    }

    @Override
    public void destroyBuffers() {

    }

    @Override
    public void reparent(ContainerPeer newContainer) {

    }

    @Override
    public boolean isReparentSupported() {
      return false;
    }

    @Override
    public void layout() {

    }

    @Override
    public void setZOrder(ComponentPeer above) {

    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
      return false;
    }
  }

  private static class ListPeerImpl implements ListPeer {
    public ListPeerImpl(List target) {

    }

    @Override
    public int[] getSelectedIndexes() {
      return new int[0];
    }

    @Override
    public void add(String item, int index) {

    }

    @Override
    public void delItems(int start, int end) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void select(int index) {

    }

    @Override
    public void deselect(int index) {

    }

    @Override
    public void makeVisible(int index) {

    }

    @Override
    public void setMultipleMode(boolean m) {

    }

    @Override
    public Dimension getPreferredSize(int rows) {
      return null;
    }

    @Override
    public Dimension getMinimumSize(int rows) {
      return null;
    }

    @Override
    public void applyShape(Region shape) {

    }

    @Override
    public boolean isObscured() {
      return false;
    }

    @Override
    public boolean canDetermineObscurity() {
      return false;
    }

    @Override
    public void setVisible(boolean v) {

    }

    @Override
    public void setEnabled(boolean e) {

    }

    @Override
    public void paint(Graphics g) {

    }

    @Override
    public void print(Graphics g) {

    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {

    }

    @Override
    public void handleEvent(AWTEvent e) {

    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {

    }

    @Override
    public Point getLocationOnScreen() {
      return null;
    }

    @Override
    public Dimension getPreferredSize() {
      return null;
    }

    @Override
    public Dimension getMinimumSize() {
      return null;
    }

    @Override
    public ColorModel getColorModel() {
      return null;
    }

    @Override
    public Graphics getGraphics() {
      return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
      return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void setForeground(Color c) {

    }

    @Override
    public void setBackground(Color c) {

    }

    @Override
    public void setFont(Font f) {

    }

    @Override
    public void updateCursorImmediately() {

    }

    @Override
    public boolean requestFocus(
        Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed,
        long time, Cause cause) {
      return false;
    }

    @Override
    public boolean isFocusable() {
      return false;
    }

    @Override
    public Image createImage(ImageProducer producer) {
      return null;
    }

    @Override
    public Image createImage(int width, int height) {
      return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
      return null;
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
      return false;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
      return 0;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
      return null;
    }

    @Override
    public boolean handlesWheelScrolling() {
      return false;
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

    }

    @Override
    public Image getBackBuffer() {
      return null;
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

    }

    @Override
    public void destroyBuffers() {

    }

    @Override
    public void reparent(ContainerPeer newContainer) {

    }

    @Override
    public boolean isReparentSupported() {
      return false;
    }

    @Override
    public void layout() {

    }

    @Override
    public void setZOrder(ComponentPeer above) {

    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
      return false;
    }
  }

  private class DesktopPeerImpl implements DesktopPeer {

    @Override
    public boolean isSupported(Action action) {
      return action != Action.PRINT;
    }

    @Override
    public void open(File file) throws IOException {
      launchIntent(file, Intent.ACTION_VIEW);
    }

    @Override
    public void edit(File file) throws IOException {
      launchIntent(file, Intent.ACTION_EDIT);
    }

    @Override
    public void print(File file) throws IOException {
      throw new UnsupportedOperationException("TODO: Uncomment once support.v4 JAR is installed");
      /*
       if (ContextCompat.checkSelfPermission(androidContext,
       Manifest.permission.READ_CONTACTS)
       != PackageManager.PERMISSION_GRANTED) {

       // Should we show an explanation?
       if (ActivityCompat.shouldShowRequestPermissionRationale(androidContext,
       Manifest.permission.READ_CONTACTS)) {

       // Show an expanation to the user *asynchronously* -- don't block
       // this thread waiting for the user's response! After the user
       // sees the explanation, try again to request the permission.

       } else {

       // No explanation needed, we can request the permission.

       ActivityCompat.requestPermissions(thisActivity,
       new String[]{Manifest.permission.READ_CONTACTS},
       MY_PERMISSIONS_REQUEST_READ_CONTACTS);

       // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
       // app-defined int constant. The callback method gets the
       // result of the request.
       }
       }
       launchIntent(file, Intent.ACTION_PR);
       */
    }

    @Override
    public void mail(URI mailtoURL) throws IOException {
      Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
      mailIntent.setData(Uri.parse(mailtoURL.toString()));
      androidContext.startActivity(mailIntent);
    }

    @Override
    public void browse(URI uri) throws IOException {
      Intent browseIntent = new Intent(Intent.ACTION_VIEW);
      browseIntent.setData(Uri.parse(uri.toString()));
      androidContext.startActivity(browseIntent);
    }
  }
}
