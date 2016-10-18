package sun.font;

import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

/**
 * Created by cryoc on 2016-10-11.
 */
public class AttributeValues extends HashMap<TextAttribute, Object> {
  private static final long serialVersionUID = 8820590967652117455L;

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
    // TODO: Is there supposed to be a difference here?
    return getTransform();
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
    // TODO
    return 0;
  }

  public int getBidiEmbedding() {
    // TODO
    return 0;
  }
}
