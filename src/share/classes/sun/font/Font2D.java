package sun.font;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Locale;

/**
 * Created by cryoc on 2016-10-11.
 */
public class Font2D {

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

  public boolean useAAForPtSize(int pixelHeight) {
    // TODO
    return true;
  }

  public int getNumGlyphs() {
    // TODO
    return Character.MAX_CODE_POINT;
  }

  public int getMissingGlyphCode() {
    return 0x25AF; // WHITE VERTICAL RECTANGLE
  }
}
