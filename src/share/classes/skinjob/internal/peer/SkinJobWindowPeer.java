package skinjob.internal.peer;

import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FramePeer;

import skinjob.internal.SkinJobDefaultAttributeSet;
import skinjob.internal.SkinJobGraphics;
import skinjob.internal.SkinJobGraphicsConfiguration;
import skinjob.util.SkinJobUtil;
import sun.awt.CausedFocusEvent.Cause;

/**
 * SkinJobGlobals android implementation of {@link FramePeer}.
 */
public class SkinJobWindowPeer<T extends Window> extends SkinJobComponentPeer<T>
    implements FramePeer, DialogPeer {
  private static final AttributeSet MENU_BAR_ATTRIBUTES = new SkinJobDefaultAttributeSet(); // TODO
  protected final java.awt.Window thisAwtWindow;
  protected final Graphics graphics;
  private final ViewGroup.LayoutParams menuBarLayoutParams;

  public SkinJobWindowPeer(java.awt.Window target) {
    super((T) target.sjAndroidWindow, SkinJobGraphicsConfiguration.getDefault());
    thisAwtWindow = target;
    graphics = new SkinJobGraphics(androidWidget.getDecorView().getDrawingCache());
    menuBarLayoutParams = new ViewGroup.LayoutParams(
        androidWidget.getContext(),
        MENU_BAR_ATTRIBUTES);
  }

  @Override
  public void setTitle(String title) {
    androidWidget.setTitle(title);
  }

  @Override
  public void setMenuBar(MenuBar mb) {
    androidWidget.addContentView((View) mb.androidWidget, menuBarLayoutParams);
  }

  @Override
  public void setResizable(boolean resizeable) {
    // TODO
  }

  @Override
  public int getState() {
    // TODO
    return 0;
  }

  @Override
  public void setState(int state) {
    // TODO
  }

  @Override
  public void setMaximizedBounds(Rectangle bounds) {
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height, SET_BOUNDS);
  }

  @Override
  public void setBoundsPrivate(int x, int y, int width, int height) {
    WindowInsets insets = sjGetAndroidWindowInsets();
    setBounds(x - insets.getSystemWindowInsetLeft(), y - insets.getSystemWindowInsetTop(),
            width + insets.getSystemWindowInsetLeft() + insets.getSystemWindowInsetRight(),
            height + insets.getSystemWindowInsetTop() + insets.getSystemWindowInsetBottom(),
            SET_BOUNDS);
  }

  protected WindowInsets sjGetAndroidWindowInsets() {
    View decorView = androidWidget.getDecorView();
    return decorView.getRootWindowInsets();
  }

  @Override
  public Rectangle getBoundsPrivate() {
    WindowInsets insets = sjGetAndroidWindowInsets();
    return new Rectangle(
            insets.getSystemWindowInsetLeft(),
            insets.getSystemWindowInsetTop(),
            getWidth() - insets.getSystemWindowInsetRight(),
            getHeight() - insets.getSystemWindowInsetBottom());
  }

  @Override
  public void emulateActivation(boolean activate) {
    // TODO
  }

  @Override
  public void toFront() {
    androidWidget.makeActive();
  }

  @Override
  public void toBack() {
    java.awt.Window owner = thisAwtWindow.getOwner();
    if (owner != null) {
      owner.toFront();
    }
  }

  @Override
  public void updateAlwaysOnTopState() {
    // TODO
  }

  @Override
  public void updateFocusableWindowState() {
    // TODO
  }

  @Override
  public void setModalBlocked(Dialog blocker, boolean blocked) {
    // TODO
  }

  @Override
  public void updateMinimumSize() {
    // TODO
  }

  @Override
  public void updateIconImages() {
    // No-op -- Android windows don't have icons.
  }

  @Override
  public void setOpacity(float opacity) {
    androidWidget.getDecorView().setAlpha(opacity);
  }

  @Override
  public void setOpaque(boolean isOpaque) {
    if (isOpaque) {
      setOpacity(1.0f);
    }
    // Otherwise no-op, since alpha channel is always supported on Android
  }

  @Override
  public void updateWindow() {
    androidWidget.getDecorView().requestLayout();
    // TODO: Do this for all other descendant View instances as well
  }

  @Override
  public void repositionSecurityWarning() {
    // TODO
  }

  @Override
  public Insets getInsets() {
    Rect displayArea = new Rect();
    androidWidget.getDecorView().getWindowVisibleDisplayFrame(displayArea);
    return new Insets(
        displayArea.top,
        displayArea.left,
        getHeight() - displayArea.bottom,
        getWidth() - displayArea.right);
  }

  @Override
  public void endLayout() {
    updateWindow();
  }

  protected int getWidth() {
    return androidWidget.getDecorView().getWidth();
  }

  protected int getHeight() {
    return androidWidget.getDecorView().getHeight();
  }

  @Override
  public void setVisible(boolean v) {
    androidWidget.getDecorView().setVisibility(v ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public void setEnabled(boolean e) {
    androidWidget.getDecorView().setEnabled(e);
  }

  @Override
  public void paint(Graphics g) {
    // TODO
  }

  @Override
  public void setBounds(int x, int y, int width, int height, int op) {
    View decorView = androidWidget.getDecorView();
    if (op == SET_CLIENT_SIZE) {
      WindowInsets insets = decorView.getRootWindowInsets();
      SkinJobUtil.setBounds(decorView, 0, 0,
              width + insets.getSystemWindowInsetLeft() + insets.getSystemWindowInsetRight(),
              height + insets.getSystemWindowInsetTop() + insets.getSystemWindowInsetBottom(),
              SET_SIZE);
    } else {
      SkinJobUtil.setBounds(decorView, x, y, width, height, op);
    }
  }

  @Override
  public Point getLocationOnScreen() {
    return new Point(0, 0);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(getWidth(), getHeight());
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public void setBackground(Color c) {
    androidWidget.setStatusBarColor(c.getRGB());
    androidWidget.setNavigationBarColor(c.getRGB());
    androidWidget.getDecorView().setBackgroundColor(c.getRGB());
  }

  @Override
  public boolean requestFocus(
      Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time,
      Cause cause) {
    if (focusedWindowChangeAllowed) {
      androidWidget.makeActive();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isFocusable() {
    return false;
  }

  @Override
  public boolean handlesWheelScrolling() {
    return true; // Must handle scrolling, since there's no parent to delegate to
  }

  @Override
  public void setZOrder(ComponentPeer above) {
    if (above instanceof SkinJobWindowPeer
        && ((SkinJobComponentPeer<Window>) above).androidWidget.isActive()) {
      androidWidget.makeActive();
    }
  }

  @Override
  public void blockWindows(java.util.List<java.awt.Window> windows) {
    // TODO
  }

  @Override
  public void layout() {
    endLayout();
  }
}
