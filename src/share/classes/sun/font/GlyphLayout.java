package sun.font;

import java.awt.Font;
import java.awt.SkinJob;
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
      Font font, FontRenderContext frc, char[] text, int start, int length, int flags, Object o) {
    // TODO: Find and implement the contract for handling flags and o
    char[] substring = SkinJob.rangeMaybeCopy(text, start, length);
    return new StandardGlyphVector(font, substring, frc);
  }
}
