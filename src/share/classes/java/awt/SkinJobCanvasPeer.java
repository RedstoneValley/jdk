package java.awt;

import android.graphics.*;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.view.Display;

import java.awt.peer.ComponentPeer;

import sun.awt.CausedFocusEvent;

/**
 * Created by cryoc on 2016-10-11.
 */
// TODO: Should this actually be backed by a View? An android.graphics.Canvas can be non-displayable
public class SkinJobCanvasPeer extends SkinJobComponentPeer<Canvas> {
    public SkinJobCanvasPeer(Canvas androidComponent, GraphicsConfiguration configuration) {
        super(androidComponent, configuration);
    }

    public SkinJobCanvasPeer(Canvas androidComponent) {
        this(androidComponent,
                new SkinJobGraphicsConfiguration(SkinJobUtil.getAndroidApplicationContext().getSystemService(DisplayManager.class).getDisplay(Display.DEFAULT_DISPLAY)));
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
        return new Dimension(androidComponent.getWidth(), androidComponent.getHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        Rect clipBounds = androidComponent.getClipBounds();
        return new Dimension(clipBounds.width(), clipBounds.height());
    }

    @Override
    public void setBackground(Color c) {
        // TODO: Maybe paint existing contents onto a new bitmap of color c, then copy back?
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
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
}
