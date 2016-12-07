package sun.java2d.cmm;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Partial reimplementation of the OpenJDK class for use by SkinJob.
 */
public class ColorTransform {
  public static int Any = 0;
  public static int In = 1;
  public static int Out = 2;
  public static int Simulation;

  public byte[] colorConvert(byte[] g8Tos8LUT, Object o) {
    // TODO
    return new byte[0];
  }

  public short[] colorConvert(short[] g8Tos8LUT, Object o) {
    // TODO
    return new short[0];
  }

  public void colorConvert(BufferedImage src, BufferedImage dest) {
    // TODO
  }

  public void colorConvert(Raster src, WritableRaster dest, float[] srcMinVals, float[] srcMaxVals,
      float[] dstMinVals, float[] dstMaxVals) {
    // TODO
  }

  public void colorConvert(Raster src, WritableRaster dest) {
    // TODO
  }
}
