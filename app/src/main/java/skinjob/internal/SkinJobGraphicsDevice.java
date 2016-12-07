package skinjob.internal;

import android.os.Build;
import android.view.Display;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobGraphicsDevice extends GraphicsDevice {
  public final Display androidDisplay;
  protected final String name;
  protected final GraphicsConfiguration configuration;

  public SkinJobGraphicsDevice(Display androidDisplay) {
    this.androidDisplay = androidDisplay;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      name = androidDisplay.getName();
    } else {
      name = toString();
    }
    configuration = new SkinJobGraphicsConfiguration(androidDisplay, this);
  }

  @Override
  public int getType() {
    return GraphicsDevice.TYPE_RASTER_SCREEN;
  }

  @Override
  public String getIDstring() {
    return name;
  }

  @Override
  public GraphicsConfiguration[] getConfigurations() {
    return new GraphicsConfiguration[]{configuration};
  }

  @Override
  public GraphicsConfiguration getDefaultConfiguration() {
    return configuration;
  }
}
