package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;

/**
 * Created by cryoc on 2016-10-14.
 */
public class GlyphLayout {
  public static GlyphLayout get(Object o) {
    return new GlyphLayout();
  }

  public static void done(GlyphLayout gl) {
    // TODO
  }

  public StandardGlyphVector layout(
      Font font, FontRenderContext frc, char[] text, int start, int i, int flags, Object o) {
    // TODO
    return new StandardGlyphVector(font, text, frc);
  }
}
