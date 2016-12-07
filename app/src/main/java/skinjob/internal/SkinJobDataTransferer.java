package skinjob.internal;

import android.graphics.BitmapFactory;

import java.awt.Image;
import java.io.IOException;

import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

/**
 * Created by chris on 12/5/2016.
 */
public class SkinJobDataTransferer extends DataTransferer {

  private static final long HASH_CODE_PREFIX = 0x4CED_C0DE_0000_0000L;

  @Override
  public String getDefaultUnicodeEncoding() {
    return "UTF-8";
  }

  @Override
  public boolean isLocaleDependentTextFormat(long format) {
    // TODO
    return true;
  }

  @Override
  public boolean isFileFormat(long format) {
    // TODO
    return true;
  }

  @Override
  public boolean isImageFormat(long format) {
    // TODO
    return true;
  }

  @Override
  protected Long getFormatForNativeAsLong(String str) {
    // TODO
    try {
      return Long.parseLong(str, 16);
    } catch (NumberFormatException e) {
      return HASH_CODE_PREFIX + str.hashCode();
    }
  }

  @Override
  protected String getNativeForFormat(long format) {
    // TODO
    return String.format("%016X", format);
  }

  @Override
  protected String[] dragQueryFile(byte[] bytes) {
    // TODO
    return new String[0];
  }

  @Override
  protected Image platformImageBytesToImage(byte[] bytes, long format) throws IOException {
    return new SkinJobBufferedImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
  }

  @Override
  public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
    return new SkinJobToolkitThreadBlockedHandler();
  }
}
