package sun.font;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Locale;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class Font2D {
  public void getFontMetrics(
      Font font, AffineTransform identityTx, Object antiAliasingHint, Object fractionalMetricsHint,
      float[] metrics) {
    // TODO
  }

  public byte getBaselineFor(char c) {
    // TODO
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
}
