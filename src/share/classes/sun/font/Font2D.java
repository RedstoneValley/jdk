package sun.font;

import android.graphics.Paint;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Locale;

/**
 * Created by cryoc on 2016-10-11.
 */
public class Font2D {

  private static final int UNICODE_WHITE_VERTICAL_RECTANGLE = 0x25AF;
  private final Paint androidPaint;
  private int numGlyphs = -1;

  public Font2D(Paint androidPaint) {
    this.androidPaint = androidPaint;
  }

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

  public synchronized int getNumGlyphs() {
    if (numGlyphs == -1) {
      numGlyphs = 0;
      for (int i = Font.FIRST_CODE_POINT; i < Character.MAX_CODE_POINT; i++) {
        if (androidPaint.hasGlyph(new String(Character.toChars(i)))) {
          numGlyphs++;
        }
      }
    }
    return numGlyphs;
  }

  public int getMissingGlyphCode() {
    return UNICODE_WHITE_VERTICAL_RECTANGLE;
  }
}
