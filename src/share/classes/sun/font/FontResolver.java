package sun.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Created by cryoc on 2016-10-15.
 */
public class FontResolver {
  private static FontResolver instance = new FontResolver();

  public static FontResolver getInstance() {
    return instance;
  }

  public int getFontIndex(int ch) {
    // TODO
    return 0;
  }

  public Font getFont(
      int fontIndex, Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
    // TODO
    return null;
  }
}
