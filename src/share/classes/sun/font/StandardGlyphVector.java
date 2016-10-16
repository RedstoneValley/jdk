package sun.font;

import android.text.SpannableStringBuilder;
import android.widget.TextView;
import java.awt.Font;
import java.awt.Shape;
import java.awt.SkinJob;
import java.awt.SkinJobTextAttributesDecoder;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by cryoc on 2016-10-14.
 */
// TODO: All stubs
public class StandardGlyphVector extends GlyphVector {
  private final TextView parentView;
  private final ArrayList<TextView> childViews;
  private final SpannableStringBuilder spannableString;
  private final Font font;
  private final FontRenderContext fontRenderContext;
  private final int color;

  public StandardGlyphVector(Font font, FontRenderContext fontRenderContext) {
    this.font = font;
    this.fontRenderContext = fontRenderContext;
    Integer color = (Integer) font.getAttributes().get(TextAttribute.FOREGROUND);
    this.color = (color == null) ? 0x000000FF : color;
    spannableString = new SpannableStringBuilder();
    parentView = new TextView(SkinJob.getAndroidApplicationContext());
    childViews = new ArrayList<>();
  }

  public StandardGlyphVector(Font font, CharSequence str, FontRenderContext frc) {
    this(font, frc);
    append(str);
  }

  public StandardGlyphVector(Font font, char[] chars, FontRenderContext frc) {
    this(font, frc);
    append(new String(chars));
  }

  public StandardGlyphVector(Font font, CharacterIterator ci, FontRenderContext frc) {
    this(font, frc);
    int charsWritten = 0;
    for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next()) {
      append(c);
      if (ci instanceof AttributedCharacterIterator) {
        charsWritten++;
        Map<AttributedCharacterIterator.Attribute, Object> attributes = ((AttributedCharacterIterator) ci).getAttributes();
        if (!attributes.isEmpty()) {
          new SkinJobTextAttributesDecoder(color)
              .addAttributes(attributes)
              .applyTo(spannableString, charsWritten - 1, charsWritten);
        }
      }
    }
  }

  public StandardGlyphVector(Font font, int[] glyphCodes, FontRenderContext frc) {
    this(font, frc);
    for (int glyphCode : glyphCodes) {
      append(Character.toChars(glyphCode));
    }
  }

  private void append(char[] chars) {
    for (char c : chars) {
      append(c);
    }
  }

  private void append(char c) {
    spannableString.append(c);
    // TODO
  }

  protected void append(CharSequence str) {
    spannableString.append(str);
    // TODO
  }

  @Override
  public Font getFont() {
    return font;
  }

  @Override
  public FontRenderContext getFontRenderContext() {
    return fontRenderContext;
  }

  @Override
  public void performDefaultLayout() {
    // TODO
  }

  @Override
  public synchronized int getNumGlyphs() {
    return spannableString.length();
  }

  @Override
  public synchronized int getGlyphCode(int glyphIndex) {
    return spannableString.charAt(glyphIndex);
  }

  @Override
  public synchronized int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn) {
    int numGlyphs = getNumGlyphs();
    int[] glyphCodes = new int[numGlyphs];
    System.arraycopy(spannableString.toString().toCharArray(), 0, glyphCodes, 0, numGlyphs);
    return glyphCodes;
  }

  @Override
  public Rectangle2D getLogicalBounds() {
    return null;
  }

  @Override
  public Rectangle2D getVisualBounds() {
    return null;
  }

  @Override
  public Shape getOutline() {
    return null;
  }

  @Override
  public Shape getOutline(float x, float y) {
    return null;
  }

  @Override
  public Shape getGlyphOutline(int glyphIndex) {
    return null;
  }

  @Override
  public Point2D getGlyphPosition(int glyphIndex) {
    return null;
  }

  @Override
  public void setGlyphPosition(int glyphIndex, Point2D newPos) {

  }

  @Override
  public AffineTransform getGlyphTransform(int glyphIndex) {
    return null;
  }

  @Override
  public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {

  }

  @Override
  public float[] getGlyphPositions(int beginGlyphIndex, int numEntries, float[] positionReturn) {
    return new float[0];
  }

  @Override
  public Shape getGlyphLogicalBounds(int glyphIndex) {
    return null;
  }

  @Override
  public Shape getGlyphVisualBounds(int glyphIndex) {
    return null;
  }

  @Override
  public GlyphMetrics getGlyphMetrics(int glyphIndex) {
    return null;
  }

  @Override
  public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
    return null;
  }

  @Override
  public boolean equals(GlyphVector set) {
    return false;
  }
}
