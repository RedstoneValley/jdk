package sun.font;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class FontManager {
  private static final FontManager FONT_MANAGER = new FontManager();
  public static Object LOGICAL_FALLBACK;

  public static FontManager getInstance() {
    return FONT_MANAGER;
  }

  public boolean usingPerAppContextComposites() {
    // TODO
    return false;
  }

  public Font2D findFont2D(String name, int style, Object logicalFallback) {
    return null;
  }
}
