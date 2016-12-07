package sun.font;

import java.awt.font.GraphicAttribute;
import java.awt.font.LineMetrics;

/**
 * Created by cryoc on 2016-10-14.
 */
public class CoreMetrics {
  public final float ascent;
  public final float descent;
  public final float leading;
  public final int baselineIndex;
  public final float[] baselineOffsets;
  public final float ssOffset;
  public final float italicAngle;
  public final float height;
  final float strikethroughOffset;
  final float strikethroughThickness;
  final float underlineOffset;
  final float underlineThickness;

  public CoreMetrics(
      float ascent, float descent, float leading, float height, int baselineIndex,
      float[] baselineOffsets, float strikethroughOffset, float strikethroughThickness,
      float underlineOffset, float underlineThickness, float ssOffset, float italicAngle) {
    this.ascent = ascent;
    this.descent = descent;
    this.leading = leading;
    this.height = height;
    this.baselineIndex = baselineIndex;
    this.baselineOffsets = baselineOffsets;
    this.strikethroughOffset = strikethroughOffset;
    this.strikethroughThickness = strikethroughThickness;
    this.underlineOffset = underlineOffset;
    this.underlineThickness = underlineThickness;
    this.ssOffset = ssOffset;
    this.italicAngle = italicAngle;
  }

  public CoreMetrics(CoreMetrics toCopy) {
    this(toCopy.getAscent(),
        toCopy.getDescent(),
        toCopy.getLeading(),
        toCopy.getHeight(),
        toCopy.getBaselineIndex(),
        toCopy.getBaselineOffsets(),
        toCopy.getStrikethroughOffset(),
        toCopy.getStrikethroughThickness(),
        toCopy.getUnderlineOffset(),
        toCopy.getUnderlineThickness(),
        toCopy.getSsOffset(),
        toCopy.getItalicAngle());
  }

  public static CoreMetrics get(LineMetrics lineMetrics) {
    return new CoreMetrics(lineMetrics);
  }

  /**
   * Returns the ascent of the text.  The ascent is the distance from the baseline to the ascender
   * line.  The ascent usually represents the the height of the capital letters of the text.  Some
   * characters can extend above the ascender line.
   *
   * @return the ascent of the text.
   */
  public float getAscent() {
    return ascent;
  }

  /**
   * Returns the descent of the text.  The descent is the distance from the baseline to the
   * descender line.  The descent usually represents the distance to the bottom of lower case
   * letters like 'p'.  Some characters can extend below the descender line.
   *
   * @return the descent of the text.
   */
  public float getDescent() {
    return descent;
  }

  /**
   * Returns the leading of the text. The leading is the recommended distance from the bottom of the
   * descender line to the top of the next line.
   *
   * @return the leading of the text.
   */
  public float getLeading() {
    return leading;
  }

  /**
   * Returns the height of the text.  The height is equal to the sum of the ascent, the descent and
   * the leading.
   *
   * @return the height of the text.
   */
  public float getHeight() {
    return height;
  }

  /**
   * Returns the baseline index of the text. The index is one of {@link java.awt.Font#ROMAN_BASELINE
   * ROMAN_BASELINE}, {@link java.awt.Font#CENTER_BASELINE CENTER_BASELINE}, {@link
   * java.awt.Font#HANGING_BASELINE HANGING_BASELINE}.
   *
   * @return the baseline of the text.
   */
  public int getBaselineIndex() {
    return baselineIndex;
  }

  /**
   * Returns the baseline offsets of the text, relative to the baseline of the text.  The offsets
   * are indexed by baseline index.  For example, if the baseline index is {@code CENTER_BASELINE}
   * then {@code offsets[HANGING_BASELINE]} is usually negative, {@code offsets[CENTER_BASELINE]} is
   * zero, and {@code offsets[ROMAN_BASELINE]} is usually positive.
   *
   * @return the baseline offsets of the text.
   */
  public float[] getBaselineOffsets() {
    return baselineOffsets;
  }

  /**
   * Returns the position of the strike-through line relative to the baseline.
   *
   * @return the position of the strike-through line.
   */
  public float getStrikethroughOffset() {
    return strikethroughOffset;
  }

  /**
   * Returns the thickness of the strike-through line.
   *
   * @return the thickness of the strike-through line.
   */
  public float getStrikethroughThickness() {
    return strikethroughThickness;
  }

  /**
   * Returns the position of the underline relative to the baseline.
   *
   * @return the position of the underline.
   */
  public float getUnderlineOffset() {
    return underlineOffset;
  }

  public float getSsOffset() {
    return ssOffset;
  }

  public float getItalicAngle() {
    return italicAngle;
  }

  /**
   * Returns the thickness of the underline.
   *
   * @return the thickness of the underline.
   */
  public float getUnderlineThickness() {
    return underlineThickness;
  }

  // fullOffsets is an array of 5 baseline offsets,
  // roman, center, hanging, bottom, and top in that order
  // this does NOT add the ssOffset
  public final float effectiveBaselineOffset(float[] fullOffsets) {
    switch (baselineIndex) {
      case GraphicAttribute.TOP_ALIGNMENT:
        return fullOffsets[4] + ascent;
      case GraphicAttribute.BOTTOM_ALIGNMENT:
        return fullOffsets[3] - descent;
      default:
        return fullOffsets[baselineIndex];
    }
  }
}
