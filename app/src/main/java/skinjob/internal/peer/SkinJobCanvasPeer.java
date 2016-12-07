package skinjob.internal.peer;

import android.view.Display;
import android.view.View;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.peer.CanvasPeer;

import skinjob.internal.SkinJobGraphicsConfiguration;
import skinjob.internal.SkinJobGraphicsDevice;

/**
 * This shouldn't use an {@link android.graphics.Canvas}, because that may correspond to an
 * off-screen mutable bitmap, whereas an AWT {@link Canvas} is defined as a rectangle <i>on the
 * screen</i>.
 */
public class SkinJobCanvasPeer extends SkinJobComponentPeerForView<View> implements CanvasPeer {
  public SkinJobCanvasPeer(Canvas target) {
    super(target.sjAndroidWidget, SkinJobGraphicsConfiguration.getDefault());
  }

  @Override
  public void setBackground(Color c) {
    androidWidget.setBackgroundColor(c.getRGB());
  }

  @Override
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration gc) {
    Display myDisplay = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
      myDisplay = androidWidget.getDisplay();
    }
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
