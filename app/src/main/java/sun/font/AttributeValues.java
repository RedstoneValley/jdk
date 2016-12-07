package sun.font;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.font.NumericShaper;
import java.awt.font.NumericShaper.Range;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;

import skinjob.SkinJobGlobals;

/**
 * Partial reimplementation of the OpenJDK class for use by SkinJob.
 */
public class AttributeValues extends HashMap<TextAttribute, Object> {
  private static final long serialVersionUID = 8820590967652117455L;
  private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
  private byte bidiEmbedding;
  private byte runDirection;

  public AttributeValues() {
    put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
    put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
    put(TextAttribute.TRANSFORM, new AffineTransform());
    put(TextAttribute.FAMILY, "Default");
    put(TextAttribute.SIZE, 1.0f);
  }

  public float getWeight() {
    Float weight = (Float) get(TextAttribute.WEIGHT);
    if (weight == null) {
      return TextAttribute.WEIGHT_REGULAR;
    }
    return weight;
  }

  public void setWeight(float weight) {
    put(TextAttribute.WEIGHT, weight);
  }

  public float getPosture() {
    Float posture = (Float) get(TextAttribute.POSTURE);
    if (posture == null) {
      return TextAttribute.POSTURE_REGULAR;
    }
    return posture;
  }

  public void setPosture(float posture) {
    put(TextAttribute.POSTURE, posture);
  }

  public AffineTransform getTransform() {
    return (AffineTransform) get(TextAttribute.TRANSFORM);
  }

  public void setTransform(AffineTransform transform) {
    put(TextAttribute.TRANSFORM, transform);
  }

  public String getFamily() {
    return (String) get(TextAttribute.FAMILY);
  }

  public void setFamily(String family) {
    put(TextAttribute.FAMILY, family);
  }

  public float getSize() {
    return (float) get(TextAttribute.SIZE);
  }

  public void setSize(float size) {
    put(TextAttribute.SIZE, size);
  }

  public boolean hasLayoutAttributes() {
    return containsKey(TextAttribute.CHAR_REPLACEMENT) || containsKey(TextAttribute.FOREGROUND)
        || containsKey(TextAttribute.BACKGROUND) || containsKey(TextAttribute.UNDERLINE)
        || containsKey(TextAttribute.STRIKETHROUGH) || containsKey(TextAttribute.RUN_DIRECTION)
        || containsKey(TextAttribute.BIDI_EMBEDDING) || containsKey(TextAttribute.JUSTIFICATION)
        || containsKey(TextAttribute.INPUT_METHOD_HIGHLIGHT)
        || containsKey(TextAttribute.INPUT_METHOD_UNDERLINE)
        || containsKey(TextAttribute.SWAP_COLORS) || containsKey(TextAttribute.NUMERIC_SHAPING)
        || containsKey(TextAttribute.KERNING) || containsKey(TextAttribute.LIGATURES)
        || containsKey(TextAttribute.TRACKING) || containsKey(TextAttribute.SUPERSCRIPT);
  }

  public boolean hasNonIdentityTx() {
    return !getTransform().isIdentity() || containsKey(TextAttribute.SUPERSCRIPT) || containsKey(
        TextAttribute.WIDTH);
  }

  public AffineTransform getCharTransform() {
    AffineTransform transform = (AffineTransform) get(TextAttribute.TRANSFORM);
    if (transform == null) {
      return IDENTITY_TRANSFORM;
    }
    Point2D.Double pt = new Point2D.Double(1, 0);
    transform.deltaTransform(pt, pt);
    AffineTransform rtx = AffineTransform.getRotateInstance(pt.x, pt.y);
    try {
      AffineTransform rtxi = rtx.createInverse();
      transform.preConcatenate(rtxi);
      double dx = transform.getTranslateX();
      double dy = transform.getTranslateY();
      if (dx != 0 || dy != 0) {
        transform.setTransform(transform.getScaleX(), transform.getShearY(),
            transform.getShearX(), transform.getScaleY(), 0, 0);
        rtx.setTransform(rtx.getScaleX(), rtx.getShearY(),
            rtx.getShearX(), rtx.getScaleY(), dx, dy);
      }
    } catch (NoninvertibleTransformException e) {
      return IDENTITY_TRANSFORM;
    }
    return rtx;
  }

  public int getSuperscript() {
    Integer sup = (Integer) get(TextAttribute.SUPERSCRIPT);
    if (sup == null) {
      return 0;
    }
    return sup;
  }

  public boolean hasNonDefaultWidth() {
    return getWidth() != 1.0f;
  }

  public float getWidth() {
    Float width = (Float) get(TextAttribute.WIDTH);
    if (width == null) {
      return 1.0f;
    }
    return width;
  }

  public int getRunDirection() {
    return runDirection;
  }

  public void setRunDirection(int f) {
    runDirection = (byte) f;
  }

  public int getBidiEmbedding() {
    return bidiEmbedding;
  }

  public void setBidiEmbedding(int f) {
    bidiEmbedding = (byte) f;
  }

  public static float getJustification(Map<? extends Attribute, ?> paragraphAttrs) {
    Float justification = (Float) paragraphAttrs.get(TextAttribute.JUSTIFICATION);
    return justification == null ? TextAttribute.JUSTIFICATION_NONE : justification;
  }

  public static NumericShaper getNumericShaping(Map<? extends Attribute, ?> paragraphAttrs) {
    NumericShaper shaping = (NumericShaper) paragraphAttrs.get(TextAttribute.NUMERIC_SHAPING);
    return shaping == null ? NumericShaper.getShaper(Range.EUROPEAN) : shaping;
  }

  public int getKerning() {
    Integer kerning = (Integer) get(TextAttribute.KERNING);
    if (kerning == null) {
      return 0;
    }
    return kerning;
  }

  public int getLigatures() {
    Integer ligatures = (Integer) get(TextAttribute.LIGATURES);
    if (ligatures == null) {
      return 0;
    }
    return ligatures;
  }

  /**
   * If this has an imHighlight, create copy of this with those attributes applied to it. Otherwise
   * return this unchanged.
   */
  public AttributeValues applyIMHighlight() {
    Object imHighlight = get(TextAttribute.INPUT_METHOD_HIGHLIGHT);
    if (imHighlight != null) {
      InputMethodHighlight hl = null;
      if (imHighlight instanceof InputMethodHighlight) {
        hl = (InputMethodHighlight) imHighlight;
      } else {
        hl = (InputMethodHighlight) ((Annotation) imHighlight).getValue();
      }
      Map imStyles = hl.getStyle();
      if (imStyles == null) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        imStyles = tk.mapInputMethodHighlight(hl);
      }
      if (imStyles != null) {
        AttributeValues clone = (AttributeValues) clone();
        clone.putAll(imStyles);
        return clone;
      }
    }
    return this;
  }

  public Paint getForeground() {
    Color color = (Color) get(TextAttribute.FOREGROUND);
    if (color == null) {
      return new Color(SkinJobGlobals.defaultForegroundColor);
    }
    return color;
  }

  public Paint getBackground() {
    Color color = (Color) get(TextAttribute.BACKGROUND);
    if (color == null) {
      return new Color(SkinJobGlobals.defaultBackgroundColor);
    }
    return color;
  }

  public boolean getSwapColors() {
    Boolean swapColors = (Boolean) get(TextAttribute.SWAP_COLORS);
    return swapColors != null && swapColors;
  }

  public boolean getStrikethrough() {
    return get(TextAttribute.STRIKETHROUGH) != null;
  }

  public boolean getUnderline() {
    Integer underline = (Integer) get(TextAttribute.UNDERLINE);
    return underline != null && underline != 0;
  }

  public boolean getInputMethodUnderline() {
    Integer underline = (Integer) get(TextAttribute.INPUT_METHOD_UNDERLINE);
    return underline != null && underline != 0;
  }

  public static AttributeValues fromMap(Map<? extends TextAttribute, ?> map) {
    AttributeValues values = new AttributeValues();
    values.putAll(map);
    return values;
  }
}
