package java.awt;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * Decodes the attribute map from a {@link Font} or {@link AttributedCharacterIterator}.
 */
class SkinJobTextAttributesDecoder {
  private static final String TAG = "TextAttributesDecoder";
  private static double DIP_PER_POINT = 160.0 / 72.0; // for converting font sizes

  private int fgColor;
  private int bgColor = 0;
  private boolean swapColors = false;
  private String fontFamily = "Default";
  private int fontStyle = 0;
  private ArrayList<Object> attributeSpans = new ArrayList<>();

  SkinJobTextAttributesDecoder(int defaultColor) {
    fgColor = defaultColor;
  }

  public SkinJobTextAttributesDecoder addAttributes(
      Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
    for (AttributedCharacterIterator.Attribute attribute : attributes.keySet()) {
      if (attribute.equals(TextAttribute.BACKGROUND)) {
        bgColor = ((android.graphics.Paint) attributes.get(attribute)).getColor();
      } else if (attribute.equals(TextAttribute.BIDI_EMBEDDING)) {
        // TODO
      } else if (attribute.equals(TextAttribute.CHAR_REPLACEMENT)) {
        // TODO
      } else if (attribute.equals(TextAttribute.FAMILY)) {
        fontFamily = (String) attributes.get(attribute);
      } else if (attribute.equals(TextAttribute.FONT)) {
        addAttributes(((Font) attributes.get(attribute)).getAttributes());
      } else if (attribute.equals(TextAttribute.FOREGROUND)) {
        fgColor = ((android.graphics.Paint) attributes.get(attribute)).getColor();
      } else if (attribute.equals(TextAttribute.INPUT_METHOD_HIGHLIGHT)) {
        // TODO
      } else if (attribute.equals(TextAttribute.INPUT_METHOD_UNDERLINE)) {
        // Android doesn't support dotted, double or thick underlines.
        attributeSpans.add(new UnderlineSpan());
      } else if (attribute.equals(TextAttribute.JUSTIFICATION)) {
        // TODO
      } else if (attribute.equals(TextAttribute.KERNING)) {
        // TODO
      } else if (attribute.equals(TextAttribute.LIGATURES)) {
        // TODO
      } else if (attribute.equals(TextAttribute.NUMERIC_SHAPING)) {
        Object value = attributes.get(attribute);
        if (value instanceof NumericShaper) {
          attributeSpans.add(value);
        } else {
          Log.w(
              TAG,
              "Ignoring TextAttribute.NUMERIC_SHAPING value that's not a NumericShaper: " + value);
        }
      } else if (attribute.equals(TextAttribute.POSTURE)) {
        if (attributes.get(attribute).equals(TextAttribute.POSTURE_OBLIQUE)) {
          fontStyle |= Typeface.ITALIC;
        } else {
          fontStyle &= ~(Typeface.ITALIC);
        }
      } else if (attribute.equals(TextAttribute.RUN_DIRECTION)) {
        // TODO
      } else if (attribute.equals(TextAttribute.SIZE)) {
        attributeSpans.add(new AbsoluteSizeSpan((int) (
            ((Number) attributes.get(attribute)).doubleValue() * DIP_PER_POINT), true));
      } else if (attribute.equals(TextAttribute.STRIKETHROUGH)) {
        // Android doesn't support dotted, double or thick strokes through.
        attributeSpans.add(new StrikethroughSpan());
      } else if (attribute.equals(TextAttribute.SUPERSCRIPT)) {
        int value = (Integer) (attributes.get(attribute));
        if (value == TextAttribute.SUPERSCRIPT_SUB) {
          attributeSpans.add(new SubscriptSpan());
          break;
        } else if (value == TextAttribute.SUPERSCRIPT_SUPER) {
          attributeSpans.add(new SuperscriptSpan());
          break;
        } else {
          Log.w(TAG, "Ignoring unknown TextAttribute.SUPERSCRIPT value " + value);
        }
      } else if (attribute.equals(TextAttribute.SWAP_COLORS)) {
        swapColors = (Boolean) attributes.get(attribute);
      } else if (attribute.equals(TextAttribute.TRACKING)) {
        // TODO
      } else if (attribute.equals(TextAttribute.TRANSFORM)) {
        // TODO
      } else if (attribute.equals(TextAttribute.UNDERLINE)) {
        // Android doesn't support dotted, double or thick underlines.
        attributeSpans.add(new UnderlineSpan());
      } else if (attribute.equals(TextAttribute.WEIGHT)) {
        if (((Number) (attributes.get(attribute))).floatValue() >= SkinJob.boldThreshold) {
          fontStyle |= Typeface.BOLD;
        } else {
          fontStyle &= ~(Typeface.BOLD);
        }
      } else if (attribute.equals(TextAttribute.WIDTH)) {
        // TODO
      } else {
        Log.w(TAG, "Ignoring unknown text attribute " + attribute);
      }
    }
    return this;
  }

  /**
   * Converts the decoded attributes to a set of spans.
   *
   * @param spannableStringBuilder A {@link SpannableStringBuilder} where the attributes are to
   *                               be applied.
   * @param start                  Index of the first character to apply the attributes to.
   * @param end                    Index of the first character <i>after</i> the last one to
   *                               apply the attributes to.
   */
  public void applyTo(SpannableStringBuilder spannableStringBuilder, int start, int end) {
    if (swapColors) {
      int temp = fgColor;
      fgColor = bgColor;
      bgColor = temp;
    }
    attributeSpans.add(new ForegroundColorSpan(fgColor));
    attributeSpans.add(new BackgroundColorSpan(bgColor));
    attributeSpans.add(new TypefaceSpan(fontFamily));
    attributeSpans.add(new StyleSpan(fontStyle));
    int length = spannableStringBuilder.length();
    for (Object span : attributeSpans) {
      spannableStringBuilder.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }
  }
}
