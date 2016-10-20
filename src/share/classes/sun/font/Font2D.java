package sun.font;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Locale;

/**
 * Created by cryoc on 2016-10-11.
 */
public class Font2D {

  private static final int UNICODE_WHITE_VERTICAL_RECTANGLE = 0x25AF;

  public String getFamilyName(Locale l) {
    // TODO
    return "(Font with unknown name)";
  }

  public String getFontName(Locale l) {
    // TODO
    return "(Font with unknown name)";
  }

  public float getItalicAngle(Font font, AffineTransform identityTx, Object aa, Object fm) {
    // TODO
    return 0;
  }

  public int getNumGlyphs() {
    // TODO
    return Character.MAX_CODE_POINT;
  }

  public int getMissingGlyphCode() {
    return UNICODE_WHITE_VERTICAL_RECTANGLE;
  }
}
