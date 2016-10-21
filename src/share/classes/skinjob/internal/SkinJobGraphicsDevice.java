package skinjob.internal;

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
    name = androidDisplay.getName();
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
