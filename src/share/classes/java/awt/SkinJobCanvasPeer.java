package java.awt;

import android.graphics.Canvas;
import android.graphics.Rect;
import java.awt.peer.CanvasPeer;
import java.awt.peer.ComponentPeer;
import sun.awt.CausedFocusEvent;

/**
 * Created by cryoc on 2016-10-11.
 */
// TODO: Should this actually be backed by a View? An android.graphics.Canvas can be non-displayable
public class SkinJobCanvasPeer extends SkinJobComponentPeer<Canvas> implements CanvasPeer {
  public SkinJobCanvasPeer(java.awt.Canvas target) {
    super(target.androidCanvas, SkinJobGraphicsConfiguration.getDefault());
  }

  @Override
  public void setVisible(boolean v) {
    // TODO
  }

  @Override
  public void setEnabled(boolean e) {
    // TODO
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
    // TODO
    return null;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(androidWidget.getWidth(), androidWidget.getHeight());
  }

  @Override
  public Dimension getMinimumSize() {
    Rect clipBounds = androidWidget.getClipBounds();
    return new Dimension(clipBounds.width(), clipBounds.height());
  }

  @Override
  public void setBackground(Color c) {
    // TODO: Maybe paint existing contents onto a new bitmap of color c, then copy back?
  }

  @Override
  public boolean requestFocus(
      Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time,
      CausedFocusEvent.Cause cause) {
    return false;
  }

  @Override
  public boolean isFocusable() {
    return false;
  }

  @Override
  public boolean handlesWheelScrolling() {
    return false; // TODO
  }

  @Override
  public void setZOrder(ComponentPeer above) {
    // TODO
  }

  @Override
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration gc) {
    return gc; // No reason to prefer a different GraphicsConfiguration
  }
}
