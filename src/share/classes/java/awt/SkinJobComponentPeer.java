package java.awt;

import android.graphics.Canvas;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import sun.java2d.pipe.Region;

/**
 * Created by cryoc on 2016-10-10.
 */

public abstract class SkinJobComponentPeer<T> implements ComponentPeer {

  protected final T androidWidget;
  protected GraphicsConfiguration graphicsConfiguration;

  public SkinJobComponentPeer(T androidWidget, GraphicsConfiguration configuration) {
    this.androidWidget = androidWidget;
    graphicsConfiguration = configuration;
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
  public void print(Graphics g) {
    // TODO
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
  public ColorModel getColorModel() {
    return ColorModel.getRGBdefault();
  }

  @Override
  public Graphics getGraphics() {
    return null; // Most widgets can't be drawn on
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
  public void setFont(Font f) {
    // TODO
  }

  @Override
  public void updateCursorImmediately() {
    // TODO
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
  public boolean updateGraphicsData(GraphicsConfiguration gc) {
    graphicsConfiguration = gc;
    return true;
  }

  protected Canvas getCanvas(Graphics g) {
    if (g instanceof SkinJobGraphics) {
      return ((SkinJobGraphics) g).getCanvas();
    } else {
      throw new UnsupportedOperationException();
    }
  }
}
