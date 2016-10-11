package java.awt;

import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;

import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;

import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;

/**
 * Created by cryoc on 2016-10-10.
 */

public abstract class SkinJobComponentPeer<T extends View> implements ComponentPeer {
    private static final float DEFAULT_LAYER_DISTANCE = 1.0f;

    protected final T androidComponent;
    protected Graphics graphics;
    protected GraphicsConfiguration graphicsConfiguration;

    public SkinJobComponentPeer(T androidComponent, GraphicsConfiguration configuration) {
        this.androidComponent = androidComponent;
        graphics = new SkinJobGraphics();
        graphicsConfiguration = configuration;
    }

    public SkinJobComponentPeer(T androidComponent) {
        this(androidComponent, SkinJobGraphicsConfiguration.get(androidComponent.getDisplay()));
    }

    @Override
    public boolean isObscured() {
        return false;  // View doesn't implement this, and it's an optional method
    }

    @Override
    public boolean canDetermineObscurity() {
        return false;  // View doesn't implement this, and it's an optional method
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

    protected Canvas getCanvas(Graphics g) {
        if (g instanceof SkinJobGraphics) {
            return ((SkinJobGraphics) g).getCanvas();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void print(Graphics g) {
        // TODO
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
    public void handleEvent(AWTEvent e) {
        // TODO
    }

    @Override
    public void coalescePaintEvent(PaintEvent e) {
        // No-op.
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
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return new SkinJobFontMetrics(font);
    }

    @Override
    public void dispose() {
        // No-op.
    }

    @Override
    public void setForeground(Color c) {
        // TODO
    }

    @Override
    public void setBackground(Color c) {
        androidComponent.setBackgroundColor(c.getRGB());
    }

    @Override
    public void setFont(Font f) {
        // TODO
    }

    @Override
    public void updateCursorImmediately() {
        // TODO
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
    public Image createImage(ImageProducer producer) {
        // TODO
        return null;
    }

    @Override
    public Image createImage(int width, int height) {
        // TODO
        return null;
    }

    @Override
    public VolatileImage createVolatileImage(int width, int height) {
        // TODO
        return null;
    }

    @Override
    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
        // Image loading is synchronous on Android, so report that it's always already finished.
        return true;
    }

    @Override
    public int checkImage(Image img, int w, int h, ImageObserver o) {
        // Image loading is synchronous on Android, so report that it's always already finished.
        return ImageObserver.ALLBITS | ImageObserver.WIDTH | ImageObserver.HEIGHT
                | ImageObserver.PROPERTIES;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return graphicsConfiguration;
    }

    @Override
    public boolean handlesWheelScrolling() {
        return androidComponent.isVerticalScrollBarEnabled();
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {
        // TODO
    }

    @Override
    public Image getBackBuffer() {
        // TODO
        return null;
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {
        // TODO
    }

    @Override
    public void destroyBuffers() {
        // TODO
    }

    @Override
    public void reparent(ContainerPeer newContainer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReparentSupported() {
        return false;
    }

    @Override
    public void layout() {
        // No-op.
    }

    @Override
    public void applyShape(Region shape) {
        // TODO
    }

    @Override
    public void setZOrder(ComponentPeer above) {
        if (above instanceof SkinJobComponentPeer<?>) {
            DisplayMetrics metrics = new DisplayMetrics();
            androidComponent.getDisplay().getMetrics(metrics);
            androidComponent.setCameraDistance(
                    ((SkinJobComponentPeer) above).androidComponent.getCameraDistance()
                            - metrics.density * DEFAULT_LAYER_DISTANCE);
        }
    }

    @Override
    public boolean updateGraphicsData(GraphicsConfiguration gc) {
        graphicsConfiguration = gc;
        return true;
    }
}
