package sun.font;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class FontManager {
  private static final FontManager FONT_MANAGER = new FontManager();

  public static FontManager getInstance() {
    return FONT_MANAGER;
  }

  public boolean usingPerAppContextComposites() {
    // TODO
    return false;
  }
}
