package sun.font;

import java.awt.Font;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by cryoc on 2016-10-11.
 */
public class FontManager {
  protected static final Font[] A_FONT_ARRAY = new Font[0];
  protected static final String[] A_STRING_ARRAY = new String[0];
  private static final FontManager INSTANCE = new FontManager();
  public static Object LOGICAL_FALLBACK;

  private final TreeSet<Font> installedFonts = new TreeSet<>();
  private final TreeSet<Font> createdFonts = new TreeSet<>();

  public static FontManager getInstance() {
    return INSTANCE;
  }

  public Font[] getAllInstalledFonts() {
    synchronized (installedFonts) {
      return installedFonts.toArray(A_FONT_ARRAY);
    }
  }

  public Font[] getCreatedFonts() {
    synchronized (createdFonts) {
      return createdFonts.toArray(A_FONT_ARRAY);
    }
  }

  public synchronized TreeMap<String, String> getCreatedFontFamilyNames() {
    TreeMap<String, String> familyNames = new TreeMap<>();
    synchronized (createdFonts) {
      for (Font font : createdFonts) {
        familyNames.put(font.getName(), font.getFamily());
      }
    }
    return familyNames;
  }

  public synchronized boolean registerFont(Font font) {
    FontResolver fontResolver = FontResolver.getInstance();
    if (!fontResolver.contains(font)) {
      fontResolver.add(font);
    }
    if (font.isCreated()) {
      synchronized (createdFonts) {
        return createdFonts.add(font);
      }
    } else {
      synchronized (installedFonts) {
        return installedFonts.add(font);
      }
    }
  }

  public void preferLocaleFonts() {
    // TODO
  }

  public void preferProportionalFonts() {
    // TODO
  }

  public String[] getInstalledFontFamilyNames(Locale locale) {
    synchronized (installedFonts) {
      String[] names = new String[installedFonts.size()];
      int i = 0;
      for (Font font : installedFonts) {
        names[i] = font.getFontName(locale);
        i++;
      }
      return names;
    }
  }
}
