package sun.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;
import sun.text.CodePointIterator;

/**
 * Created by cryoc on 2016-10-15.
 */
public class FontResolver {
  private static final FontResolver instance = new FontResolver();

  public static FontResolver getInstance() {
    return instance;
  }

  public int getFontIndex(int ch) {
    // TODO
    return 0;
  }

  public Font getFont(
      int fontIndex, Map<? extends Attribute, ?> attributes) {
    // TODO
    return null;
  }

  public int nextFontRunIndex(CodePointIterator iter) {
    // TODO
    return 0;
  }
}
