package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by chris on 12/7/2016.
 */
public class SunCompositeContext implements CompositeContext {
  public SunCompositeContext(AlphaComposite alphaComposite, ColorModel srcColorModel,
      ColorModel dstColorModel) {
  }

  @Override
  public void dispose() {
    // No-op.
  }

  @Override
  public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
    // TODO
  }
}
