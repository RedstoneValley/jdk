package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

/**
 * Created by cryoc on 2016-10-14.
 */
public class FontLineMetrics extends LineMetrics implements Cloneable {
  public int numchars;
  public final FontRenderContext frc;

  public FontLineMetrics(int numchars, CoreMetrics cm, FontRenderContext frc) {
    super(cm);
    this.numchars = numchars;
    this.frc = frc;
  }

  @Override
  public int getNumChars() {
    return numchars;
  }

  @Override
  public FontLineMetrics clone() {
    try {
      return (FontLineMetrics) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
