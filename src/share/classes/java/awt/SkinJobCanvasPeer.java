package java.awt;

import android.view.Display;
import android.view.View;
import java.awt.peer.CanvasPeer;

/**
 * This shouldn't use an {@link android.graphics.Canvas}, because that may correspond to an
 * off-screen mutable bitmap, whereas an AWT {@link Canvas} is defined as a rectangle <i>on the
 * screen</i>.
 */
public class SkinJobCanvasPeer extends SkinJobComponentPeerForView<View> implements CanvasPeer {
  public SkinJobCanvasPeer(Canvas target) {
    super(target.androidWidget, SkinJobGraphicsConfiguration.getDefault());
  }

  @Override
  public void setBackground(Color c) {
    androidWidget.setBackgroundColor(c.getRGB());
  }

  @Override
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration gc) {
    Display myDisplay = androidWidget.getDisplay();
    if (gc instanceof SkinJobGraphicsConfiguration) {
      GraphicsDevice gcDevice = gc.getDevice();
      if (gcDevice instanceof SkinJobGraphicsDevice
          && ((SkinJobGraphicsDevice) gcDevice).androidDisplay.equals(myDisplay)) {
        return gc; // any SkinJobGraphicsConfiguration for the right display should be appropriate
      }
    }
    return SkinJobGraphicsConfiguration.get(myDisplay);
  }
}
