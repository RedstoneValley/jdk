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
  private int lineStart;
  private int lineLimit;
  private Bidi lineBidi;

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

  /**
   * Set a line context for the factory.  Shaping only occurs on this line. Characters are ordered
   * as they would appear on this line.
   *
   * @param lineStart the index within the text of the start of the line.
   * @param lineLimit the index within the text of the limit of the line.
   */
  public void setLineContext(int lineStart, int lineLimit) {
    this.lineStart = lineStart;
    this.lineLimit = lineLimit;
    if (bidi != null) {
      lineBidi = bidi.createLineBidi(lineStart, lineLimit);
    }
  }

  public Bidi getLineBidi() {
    return lineBidi;
  }
}
