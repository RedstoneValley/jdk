package sun.awt.datatransfer;

import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cryoc on 2016-10-17.
 */
public class ImageOutputStream implements Closeable {
  private final OutputStream outputStream;

  public ImageOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void write(RenderedImage renderedImage) {
    // TODO
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
  }

  public void flush() throws IOException {
    outputStream.flush();
  }
}
