package java.awt;

import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FramePeer;
import sun.awt.CausedFocusEvent.Cause;

/**
 * SkinJob android implementation of {@link FramePeer}.
 */
public class SkinJobWindowPeer<T extends Window> extends SkinJobComponentPeer<T>
    implements FramePeer, DialogPeer {
  private static final AttributeSet MENU_BAR_ATTRIBUTES = new SkinJobDefaultAttributeSet(); // TODO
  protected final java.awt.Window thisAwtWindow;
  protected final Graphics graphics;
  private final ViewGroup.LayoutParams menuBarLayoutParams;

  public SkinJobWindowPeer(java.awt.Window target) {
    super((T) target.androidWindow, SkinJobGraphicsConfiguration.getDefault());
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
    // TODO
  }

  @Override
  public void setBoundsPrivate(int x, int y, int width, int height) {
    // TODO
  }

  @Override
  public Rectangle getBoundsPrivate() {
    // TODO
    return null;
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
    // TODO
  }

  @Override
  public void setOpacity(float opacity) {
    // TODO
  }

  @Override
  public void setOpaque(boolean isOpaque) {
    // TODO
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
  public void beginValidate() {
    // TODO
  }

  @Override
  public void endValidate() {
    // TODO
  }

  @Override
  public void beginLayout() {
    // TODO
  }

  @Override
  public void endLayout() {
    // TODO
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
    // TODO
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
}
