package sun.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Map;
import sun.text.CodePointIterator;

/**
 * Created by cryoc on 2016-10-15.
 */
public class FontResolver extends ArrayList<Font> {
  private static final FontResolver instance = new FontResolver();

  public static FontResolver getInstance() {
    return instance;
  }

  public int getFontIndex(int ch) {
    for (int i = 0; i < size(); i++) {
      if (get(i).canDisplay(ch)) {
        return i;
      }
    }
    return -1;
  }

  public Font getFont(
      int fontIndex, Map<? extends Attribute, ?> attributes) {
    return get(fontIndex).deriveFont(attributes);
  }

  public int nextFontRunIndex(CodePointIterator iter) {
    // TODO
    return 0;
  }
}
