package skinjob.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import skinjob.internal.SkinJobAndroidBitmapWrapper;

import static java.awt.peer.ComponentPeer.SET_BOUNDS;
import static java.awt.peer.ComponentPeer.SET_LOCATION;
import static java.awt.peer.ComponentPeer.SET_SIZE;

/**
 * Miscellaneous utility methods.
 */
public final class SkinJobUtil {
  public static final Constructor<? extends Window> ANDROID_WINDOW_IMPL_CTOR;
  
  static {
    try {
      ANDROID_WINDOW_IMPL_CTOR = Class
          .forName("com.android.internal.policy.impl.PhoneWindow")
          .asSubclass(Window.class)
          .getConstructor(Context.class);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

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

  public static Window newAndroidWindow(Context androidContext) {
    if (ANDROID_WINDOW_IMPL_CTOR == null) {
      throw new UnsupportedOperationException();
    }
    Window window;
    try {
      window = ANDROID_WINDOW_IMPL_CTOR.newInstance(androidContext);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    Toolkit.getDefaultToolkit().sjMaybeWatchWidgetForMouseCoords(window.getDecorView());
    return window;
  }

  public static Bitmap asAndroidBitmap(Image image) {
    if (image instanceof SkinJobAndroidBitmapWrapper) {
      return ((SkinJobAndroidBitmapWrapper) image).sjGetAndroidBitmap();
    } else {
      // TODO
      return null;
    }
  }

  public static <T extends View> void setBounds(View androidWidget, int x, int y, int width, int height, int op) {
    switch (op) {
      case SET_SIZE:
        androidWidget.setMinimumHeight(height);
        androidWidget.setMinimumWidth(width);
        return;
      case SET_LOCATION:
        androidWidget.setX(x);
        androidWidget.setY(y);
        return;
      case SET_BOUNDS:
        setBounds(androidWidget, x, y, width, height, SET_LOCATION);
        setBounds(androidWidget, x, y, width, height, SET_SIZE);
        return;
      default:
        throw new IllegalArgumentException("Unknown setBounds operation " + op);
    }
  }
}
