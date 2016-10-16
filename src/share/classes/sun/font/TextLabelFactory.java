package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.text.Bidi;

/**
 * Created by cryoc on 2016-10-15.
 */
public class TextLabelFactory {
  protected final FontRenderContext fontRenderContext;
  protected final Bidi bidi;

  public TextLabelFactory(FontRenderContext frc, char[] chars, Bidi bidi, int layoutFlags) {
    fontRenderContext = frc;
    this.bidi = bidi;
  }

  public TextLineComponent createExtended(
      Font font, CoreMetrics cm, Decoration decorator, int startPos, int i) {
    // TODO
    return null;
  }

  public FontRenderContext getFontRenderContext() {
    return fontRenderContext;
  }

  public void setLineContext(int i, int length) {
    // TODO
  }

  public Bidi getLineBidi() {
    return bidi;
  }
}
