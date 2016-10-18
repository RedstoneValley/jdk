/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.PageAttributes;
import java.awt.Point;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.DesktopPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TrayIconPeer;
import java.util.Map;
import java.util.Properties;
import sun.awt.datatransfer.DataTransferer;

/*
 * HToolkit is a platform independent Toolkit used
 * with the HeadlessToolkit.  It is primarily used
 * in embedded JRE's that do not have sun/awt/X11 classes.
 */
public class HToolkit extends SunToolkit {

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

  public HToolkit() {
  }

    /*
     * Component peer objects - unsupported.
     */

  @Override
  public FramePeer createLightweightFrame(LightweightFrame target) throws HeadlessException {
    throw new HeadlessException();
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
  public RobotPeer createRobot(Robot target, GraphicsDevice screen)
      throws AWTException, HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
    // See 6833019.
    return kfmPeer;
  }

  @Override
  public int getScreenWidth() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public int getScreenHeight() throws HeadlessException {
    throw new HeadlessException();
  }

  @Override
  public DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    throw new HeadlessException();
  }

  /*
   * Fonts
   */
  @Override
  public FontPeer getFontPeer(String name, int style) {
    return null;
  }

  @Override
  public Dimension getScreenSize() throws HeadlessException {
    throw new HeadlessException();
  }

  /*
   * Modality
   */
  @Override
  public boolean isModalityTypeSupported(ModalityType modalityType) {
    return false;
  }

  @Override
  public boolean isModalExclusionTypeSupported(ModalExclusionType exclusionType) {
    return false;
  }

  @Override
  protected boolean syncNativeQueue(long timeout) {
    return false;
  }

  @Override
  public void grab(Window w) {
  }

  @Override
  public void ungrab(Window w) {
  }

  @Override
  public boolean isDesktopSupported() {
    return false;
  }

  @Override
  public boolean isWindowShapingSupported() {
    return false;
  }

  @Override
  public boolean isWindowTranslucencySupported() {
    return false;
  }

  @Override
  public DataTransferer getDataTransferer() {
    return null;
  }

  public GlobalCursorManager getGlobalCursorManager() throws HeadlessException {
    throw new HeadlessException();
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
  public void sync() {
    // Do nothing
  }

    /*
     * Headless toolkit - supported.
     */

  @Override
  public PrintJob getPrintJob(Frame frame, String doctitle, Properties props) {
    if (frame != null) {
      // Should never happen
      throw new HeadlessException();
    }
    throw new IllegalArgumentException("PrintJob not supported in a headless environment");
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
    throw new IllegalArgumentException("PrintJob not supported in a headless environment");
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

  @Override
  public <T extends DragGestureRecognizer> T createDragGestureRecognizer(
      Class<T> abstractRecognizerClass, DragSource ds, Component c, int srcActions,
      DragGestureListener dgl) {
    return null;
  }

  @Override
  public Map mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
    throw new HeadlessException();
  }

  public boolean isWindowOpacityControlSupported() {
    return false;
  }

  protected boolean syncNativeQueue() {
    return false;
  }

  @Override
  public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
    return null;
  }
}
