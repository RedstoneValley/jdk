/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.CanvasPeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.PanelPeer;

import sun.awt.CausedFocusEvent.Cause;

/**
 * Implements the LightweightPeer interface for use in lightweight components that have no native
 * window associated with them.  This gets created by default in Component so that Component and
 * Container can be directly extended to create useful components written entirely in java.  These
 * components must be hosted somewhere higher up in the component tree by a native container (such
 * as a Frame).
 * <p>
 * This implementation provides no useful semantics and serves only as a marker.  One could provide
 * alternative implementations in java that do something useful for some of the other peer
 * interfaces to minimize the native code.
 * <p>
 * This was renamed from java.awt.LightweightPeer (a horrible and confusing name) and moved from
 * java.awt.Toolkit into sun.awt as a public class in its own file.
 *
 * @author Timothy Prinzing
 * @author Michael Martak
 */

public class NullComponentPeer implements LightweightPeer, CanvasPeer, PanelPeer {

  @Override
  public boolean isObscured() {
    return false;
  }

  @Override
  public boolean canDetermineObscurity() {
    return false;
  }

  @Override
  public void setVisible(boolean b) {
  }

  @Override
  public void setEnabled(boolean b) {
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
  public void handleEvent(AWTEvent arg0) {
  }

  @Override
  public void coalescePaintEvent(PaintEvent e) {
  }

  @Override
  public Point getLocationOnScreen() {
    return new Point(0, 0);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1, 1);
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(1, 1);
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
    // no native code
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
      Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time,
      Cause cause) {
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
    throw new AWTException("Page-flipping is not allowed on a lightweight component");
  }

  @Override
  public Image getBackBuffer() {
    throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
  }

  @Override
  public void flip(
      int x1, int y1, int x2, int y2, FlipContents flipAction) {
    throw new IllegalStateException("Page-flipping is not allowed on a lightweight component");
  }

  @Override
  public void destroyBuffers() {
  }

  /**
   * @see ComponentPeer#reparent
   */
  @Override
  public void reparent(ContainerPeer newNativeParent) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see ComponentPeer#isReparentSupported
   */
  @Override
  public boolean isReparentSupported() {
    return false;
  }

  @Override
  public void layout() {
  }

  /**
   * Applies the shape to the native component window.
   *
   * @param shape
   * @since 1.7
   */
  @Override
  public void applyShape(Shape shape) {
  }

  /**
   * Lowers this component at the bottom of the above HW peer. If the above parameter is null then
   * the method places this component at the top of the Z-order.
   */
  @Override
  public void setZOrder(ComponentPeer above) {
  }

  @Override
  public boolean updateGraphicsData(GraphicsConfiguration gc) {
    return false;
  }

  public void show() {
  }

  public void hide() {
  }

  public void enable() {
  }

  public void disable() {
  }

  public void repaint(long tm, int x, int y, int width, int height) {
  }

  public void reshape(int x, int y, int width, int height) {
  }

  public boolean handleEvent(Event e) {
    return false;
  }

  public void setCursor(Cursor cursor) {
  }

  public Dimension preferredSize() {
    return getPreferredSize();
  }

  public Dimension minimumSize() {
    return getMinimumSize();
  }

  @Override
  public Insets getInsets() {
    return insets();
  }

  @Override
  public void beginValidate() {
  }

  @Override
  public void endValidate() {
  }

  @Override
  public void beginLayout() {
  }

  @Override
  public void endLayout() {
  }

  public Insets insets() {
    return new Insets(0, 0, 0, 0);
  }

  public boolean isPaintPending() {
    return false;
  }

  public Rectangle getBounds() {
    return new Rectangle(0, 0, 0, 0);
  }

  @Override
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(
      GraphicsConfiguration gc) {
    return gc;
  }
}
