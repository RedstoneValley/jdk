package java.awt.print;

import sun.awt.DocFlavor;

/**
 * Created by cryoc on 2016-10-17.
 */
public class StreamPrintServiceFactory {
  public static StreamPrintServiceFactory[] lookupStreamPrintServiceFactories(
      DocFlavor.SERVICE_FORMATTED pageable, String mimeType) {
    // TODO
    return new StreamPrintServiceFactory[0];
  }
}
