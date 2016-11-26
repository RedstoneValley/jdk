package skinjob.internal;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Locale;

import skinjob.util.SkinJobUtil;
import sun.font.FontManager;

/**
 * Created by cryoc on 2016-10-18.
 */
public class SkinJobGraphicsEnvironment extends GraphicsEnvironment {
  private final Context androidContext;
  private final Display defaultDisplay;
  private final Display[] displays;
  private final GraphicsDevice defaultDisplayDevice;
  private final GraphicsDevice[] displayDevices;

  public SkinJobGraphicsEnvironment(Context androidContext) {
    this.androidContext = androidContext;
    DisplayManager manager = androidContext.getSystemService(DisplayManager.class);
    defaultDisplay = manager.getDisplay(Display.DEFAULT_DISPLAY);
    displays = manager.getDisplays();
    defaultDisplayDevice = new SkinJobGraphicsDevice(defaultDisplay);
    displayDevices = new GraphicsDevice[displays.length];
    for (int i = 0; i < displays.length; i++) {
      displayDevices[i] = displays[i].equals(defaultDisplay) ? defaultDisplayDevice
          : new SkinJobGraphicsDevice(displays[i]);
    }
  }

  @Override
  public boolean isHeadlessInstance() {
    return false;
  }

  @Override
  public GraphicsDevice[] getScreenDevices() {
    return displayDevices;
  }

  @Override
  public GraphicsDevice getDefaultScreenDevice() {
    return defaultDisplayDevice;
  }

  @Override
  public Graphics2D createGraphics(BufferedImage img) {
    return new SkinJobGraphics(SkinJobUtil.asAndroidBitmap(img));
  }

  @Override
  public Font[] getAllFonts() {
    return FontManager.getInstance().getAllInstalledFonts();
  }

  @Override
  public String[] getAvailableFontFamilyNames() {
    return FontManager.getInstance().getInstalledFontFamilyNames(Locale.getDefault());
  }

  @Override
  public String[] getAvailableFontFamilyNames(Locale l) {
    return FontManager.getInstance().getInstalledFontFamilyNames(l);
  }

  @Override
  public boolean registerFont(Font font) {
    if (font == null) {
      throw new NullPointerException("font cannot be null.");
    }
    FontManager fm = FontManager.getInstance();
    return fm.registerFont(font);
  }

  @Override
  public void preferLocaleFonts() {
    FontManager fm = FontManager.getInstance();
    fm.preferLocaleFonts();
  }

  @Override
  public void preferProportionalFonts() {
    FontManager fm = FontManager.getInstance();
    fm.preferProportionalFonts();
  }

  @Override
  public Point getCenterPoint() throws HeadlessException {
    // Default implementation: return the center of the usable bounds of the
    // default screen device.
    Rectangle usableBounds = getUsableBounds(getDefaultScreenDevice());
    return new Point(usableBounds.width / 2 + usableBounds.x,
        usableBounds.height / 2 + usableBounds.y);
  }

  @Override
  public Rectangle getMaximumWindowBounds() throws HeadlessException {
    // Default implementation: return the usable bounds of the default screen
    // device.  This is correct for Microsoft Windows and non-Xinerama X11.
    return getUsableBounds(getDefaultScreenDevice());
  }

  public Display getDefaultDisplay() {
    return defaultDisplay;
  }
}
