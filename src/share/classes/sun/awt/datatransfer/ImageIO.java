package sun.awt.datatransfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * Created by cryoc on 2016-10-17.
 */
public class ImageIO {
  public static Iterator getImageReadersByMIMEType(String mimeType) {
    // TODO
    return null;
  }

  public static ImageInputStream createImageInputStream(ByteArrayInputStream bais) {
    // TODO
    return null;
  }

  public static Iterator<ImageWriter> getImageWritersByMIMEType(String mimeType) {
    // TODO
    return null;
  }

  public static ImageOutputStream createImageOutputStream(ByteArrayOutputStream baos) {
    return new ImageOutputStream(baos);
  }
}
