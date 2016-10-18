package sun.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class FontManager {
  private static final FontManager INSTANCE = new FontManager();
  protected static final Font[] A_FONT_ARRAY = new Font[0];
  protected static final String[] A_STRING_ARRAY = new String[0];
  public static Object LOGICAL_FALLBACK;

  private final TreeSet<Font> installedFonts = new TreeSet<>();
  private final TreeSet<Font> createdFonts = new TreeSet<>();

  public static FontManager getInstance() {
    return INSTANCE;
  }

  public Font2D findFont2D(String name, int style, Object logicalFallback) {
    // TODO
    return null;
  }

  public String[] getInstalledFontFamilyNames(Locale requestedLocale) {
    ArrayList<String> names = new ArrayList<>();
    for (Font font : installedFonts) {
      names.add(font.getFamily(requestedLocale));
    }
    return names.toArray(A_STRING_ARRAY);
  }

  public void useAlternateFontforJALocales() {
    // TODO
  }

  public Font[] getAllInstalledFonts() {
    return installedFonts.toArray(A_FONT_ARRAY);
  }

  public Font[] getCreatedFonts() {
    return createdFonts.toArray(A_FONT_ARRAY);
  }

  public TreeMap<String, String> getCreatedFontFamilyNames() {
    TreeMap<String, String> familyNames = new TreeMap<>();
    for (Font font : createdFonts) {
      familyNames.put(font.getName(), font.getFamily());
    }
    return familyNames;
  }

  public boolean registerFont(Font font) {
    if (font.isCreated()) {
      return createdFonts.add(font);
    } else {
      return installedFonts.add(font);
    }
  }

  public void preferLocaleFonts() {
    // TODO
  }

  public void preferProportionalFonts() {
    // TODO
  }
}
