package skinjob.util;

import android.graphics.Rect;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Miscellaneous utility methods.
 */
public final class SkinJobUtil {
  public static ThreadGroup getRootThreadGroup() {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    if (group == null) {
      return null;
    }
    while (true) {
      ThreadGroup parent = group.getParent();
      if (parent == null || parent.equals(group)) {
        return group;
      }
      group = parent;
    }
  }

  /**
   * @param text   A character array to copy from.
   * @param start  The index of the first character to copy.
   * @param length The length of the substring of {@code char}s to copy.
   * @return {@code text} if {@code start == 0 && length == text.length};
   * {@link Arrays#copyOfRange(char[], int, int)}(text, start, length) otherwise.
   */
  public static char[] rangeMaybeCopy(char[] text, int start, int length) {
    if (start == 0 && length == text.length) {
      return text;
    } else {
      return Arrays.copyOfRange(text, start, length);
    }
  }

  /**
   * Converts the given {@link Rect} (Android class) to a {@link Rectangle2D.Double} (AWT class).
   *
   * @param rect A {@link Rect} to convert.
   * @return A {@link Rectangle2D.Double} representing the same area.
   */
  public static Rectangle2D.Double androidRectToRectangle2D(Rect rect) {
    return new Rectangle2D.Double(rect.left, rect.top, rect.width(), rect.height());
  }
}
