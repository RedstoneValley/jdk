package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.text.Bidi;
import skinjob.util.SkinJobUtil;

/**
 * Created by cryoc on 2016-10-15.
 */
public class TextLabelFactory {
  protected final FontRenderContext fontRenderContext;
  protected final Bidi bidi;
  private final char[] chars;
  private final int layoutFlags;

  public TextLabelFactory(FontRenderContext frc, char[] chars, Bidi bidi, int layoutFlags) {
    fontRenderContext = frc;
    this.chars = chars;
    this.bidi = bidi;
    this.layoutFlags = layoutFlags;
  }

  public TextLineComponent createExtended(
      Font font, CoreMetrics cm, Decoration decorator, int startPos, int length) {
    return new TextLineComponent(
        SkinJobUtil.rangeMaybeCopy(chars, startPos, length),
        font,
        cm,
        decorator);
  }

  public FontRenderContext getFontRenderContext() {
    return fontRenderContext;
  }

  public void setLineContext(int i, int length) {
    // TODO
  }

  public Bidi getLineBidi() {
    // TODO
    return bidi;
  }
}
