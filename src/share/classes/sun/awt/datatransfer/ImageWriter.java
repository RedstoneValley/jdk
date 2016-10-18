package sun.awt.datatransfer;

import android.util.Log;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * Created by cryoc on 2016-10-17.
 */

public class ImageWriter {
  private static final String TAG = "AWT ImageWriter";
  private ImageWriterSpi originatingProvider;
  private ImageOutputStream output;

  public ImageWriterSpi getOriginatingProvider() {
    return originatingProvider;
  }

  public void setOutput(ImageOutputStream output) {
    this.output = output;
  }

  public void write(RenderedImage renderedImage) {
    output.write(renderedImage);
  }

  public void dispose() {
    try {
      output.close();
    } catch (IOException e) {
      Log.e(TAG, "Error while closing output stream", e);
    }
  }

  @Override
  protected void finalize() {
    dispose();
  }
}
