package sun.util.logging;

import android.util.Log;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class PlatformLogger {
  private final String tag;

  public PlatformLogger(String tag) {
    this.tag = tag;
  }

  public static PlatformLogger getLogger(String tag) {
    boolean truncated = false;
    String androidTag;
    if (tag.length() > 23) {
      truncated = true;
      androidTag = tag.substring(tag.length() - 23);
    } else {
      androidTag = tag;
    }
    PlatformLogger logger = new PlatformLogger(tag);
    if (truncated) {
      Log.e(androidTag, "This log tag was truncated. The full tag is " + tag);
    }
    return logger;
  }

  public enum Level {FINE, FINER, FINEST}
}
