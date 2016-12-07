package sun.awt.datatransfer;

import android.graphics.Bitmap;

import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import skinjob.SkinJobGlobals;
import skinjob.util.SkinJobUtil;

/**
 * Reimplementation of the OpenJDK class for use by SkinJob.
 */
public class ImageOutputStream implements Closeable {
  private final OutputStream outputStream;

  public ImageOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void write(RenderedImage renderedImage) {
    Bitmap bitmap = SkinJobUtil.asAndroidBitmap(renderedImage);
    bitmap.compress(SkinJobGlobals.imageOutputFormat, SkinJobGlobals.imageQuality, outputStream);
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
  }

  public void flush() throws IOException {
    outputStream.flush();
  }
}
