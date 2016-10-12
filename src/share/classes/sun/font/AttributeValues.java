package sun.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class AttributeValues {
  private final float weight;
  private final float posture;
  private final Font font;

  public AttributeValues(float weight, float posture, Font font) {
    this.weight = weight;
    this.posture = posture;
    this.font = font;
  }

  public float getWeight() {
    return weight;
  }

  public float getPosture() {
    return posture;
  }

  public static AttributeValues fromMap(
      Map<? extends AttributedCharacterIterator.Attribute, ?> attributes, int recognizedMask) {
    return null;
  }

  public boolean anyDefined(int secondaryMask) {
    // TODO
    return false;
  }

  public Font getFont() {
    return font;
  }

  public void merge(
      Map<? extends AttributedCharacterIterator.Attribute, ?> attributes, int secondaryMask) {
    // TODO
  }
}
