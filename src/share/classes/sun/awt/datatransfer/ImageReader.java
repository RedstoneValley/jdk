package sun.awt.datatransfer;

import java.awt.image.BufferedImage;

/**
 * Created by cryoc on 2016-10-17.
 */
public class ImageReader {
  private ImageReadParam defaultReadParam = new ImageReadParam();
  private android.media.ImageReader androidImageReader;
  private int minIndex; // TODO

  public ImageReadParam getDefaultReadParam() {
    return defaultReadParam;
  }

  public void setInput(ImageInputStream imageInputStream, boolean b, boolean b1) {
    androidImageReader = android.media.ImageReader.newInstance(0, 0, 0, 0); // TODO
  }

  public int getMinIndex() {
    return minIndex;
  }

  public BufferedImage read(int minIndex, ImageReadParam param) {
    // TODO
    return null;
  }

  public void dispose() {
    androidImageReader.close();
  }
}
