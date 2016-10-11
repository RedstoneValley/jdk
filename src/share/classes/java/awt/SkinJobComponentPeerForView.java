package java.awt;

import android.util.DisplayMetrics;
import android.view.View;

import java.awt.peer.ComponentPeer;

import sun.awt.CausedFocusEvent;

/**
 * Skeletal implementation of {@link SkinJobComponentPeer}&lt;T extends {@link View}&gt;.
 */
public abstract class SkinJobComponentPeerForView<T extends View> extends SkinJobComponentPeer<T> {

    public SkinJobComponentPeerForView(T androidComponent) {
        this(androidComponent, SkinJobGraphicsConfiguration.get(androidComponent.getDisplay()));
    }

    public SkinJobComponentPeerForView(T androidComponent, GraphicsConfiguration configuration) {
        super(androidComponent, configuration);
    }

    @Override
    public void setVisible(boolean v) {
        androidComponent.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setEnabled(boolean e) {
        androidComponent.setEnabled(e);
    }

    @Override
    public void paint(Graphics g) {
        androidComponent.draw(getCanvas(g));
    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {
        switch (op) {
            case SET_SIZE:
                androidComponent.setMinimumHeight(height);
                androidComponent.setMinimumWidth(width);
                return;
            case SET_LOCATION:
                // TODO
                return;
            case SET_BOUNDS:
                setBounds(x, y, width, height, SET_LOCATION);
                setBounds(x, y, width, height, SET_SIZE);
                return;
            case SET_CLIENT_SIZE:
                // TODO
                return;
        }
    }

    @Override
    public Point getLocationOnScreen() {
        int[] location = new int[2];
        androidComponent.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                androidComponent.getMeasuredWidth(), androidComponent.getMeasuredHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(
                androidComponent.getMinimumWidth(), androidComponent.getMinimumHeight());
    }

    @Override
    public void setBackground(Color c) {
        androidComponent.setBackgroundColor(c.getRGB());
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        return androidComponent.requestFocus();
    }

    @Override
    public boolean isFocusable() {
        return androidComponent.isFocusable();
    }

    @Override
    public boolean handlesWheelScrolling() {
        return androidComponent.isSelected() && androidComponent.isVerticalScrollBarEnabled();
    }

    @Override
    public void setZOrder(ComponentPeer above) {
        if (above instanceof SkinJobComponentPeer<?>) {
            Object otherAndroidComponent = ((SkinJobComponentPeer<?>) above).androidComponent;
            if (otherAndroidComponent instanceof View) {
                DisplayMetrics metrics = new DisplayMetrics();
                androidComponent.getDisplay().getMetrics(metrics);
                androidComponent.setCameraDistance(
                        ((View) otherAndroidComponent).getCameraDistance()
                        - metrics.density * SkinJob.layerZSpacing);
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

}
