package skinjob.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import skinjob.internal.SkinJobAndroidBitmapWrapper;
import skinjob.internal.SkinJobBufferedImage;
import skinjob.internal.SkinJobGraphics;

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
    } catch (Exception e) {
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
   * @param text A character array to copy from.
   * @param start The index of the first character to copy.
   * @param length The length of the substring of {@code char}s to copy.
   * @return {@code text} if {@code start == 0 && length == text.length}; {@link
   * Arrays#copyOfRange(char[], int, int)}(text, start, length) otherwise.
   */
  public static char[] rangeMaybeCopy(char[] text, int start, int length) {
    if (start == 0 && length == text.length) {
      return text;
    } else {
      return Arrays.copyOfRange(text, start, length);
    }
  }

  public static Window newAndroidWindow(Context androidContext) {
    if (ANDROID_WINDOW_IMPL_CTOR == null) {
      throw new UnsupportedOperationException();
    }
    Window window;
    try {
      window = ANDROID_WINDOW_IMPL_CTOR.newInstance(androidContext);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    Toolkit.getDefaultToolkit().sjMaybeWatchWidgetForMouseCoords(window.getDecorView());
    return window;
  }

  public static Bitmap asAndroidBitmap(Image image) {
    if (image instanceof SkinJobAndroidBitmapWrapper) {
      return ((SkinJobAndroidBitmapWrapper) image).sjGetAndroidBitmap();
    } else if (image instanceof RenderedImage) {
      return copyToAndroidBitmap((RenderedImage) image);
    } else {
      Graphics graphics = image.getGraphics();
      if (graphics instanceof SkinJobGraphics) {
        return ((SkinJobGraphics) graphics).sjGetAndroidBitmap();
      } else {
        throw new UnsupportedOperationException("Unable to read this Image's data");
      }
    }
  }

  private static Bitmap copyToAndroidBitmap(RenderedImage image) {
    Raster raster = image.getData();
    int width = image.getWidth();
    int height = image.getHeight();
    SkinJobBufferedImage buffered = new SkinJobBufferedImage(width, height);
    buffered.setData(raster);
    return buffered.sjGetAndroidBitmap();
  }

  public static Bitmap asAndroidBitmap(RenderedImage image) {
    if (image instanceof Image) {
      return asAndroidBitmap((Image) image);
    } else {
      return copyToAndroidBitmap(image);
    }
  }

  public static <T extends View> void setBounds(View androidWidget, int x, int y, int width,
      int height, int op) {
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
