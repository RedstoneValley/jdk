package java.awt;

import android.util.AttributeSet;

/**
 * A default instance of {@link AttributeSet}.
 */
class SkinJobDefaultAttributeSet implements AttributeSet {
  @Override
  public int getAttributeCount() {
    return 0;
  }

  @Override
  public String getAttributeName(int index) {
    return null;
  }

  @Override
  public String getAttributeValue(int index) {
    return null;
  }

  @Override
  public String getAttributeValue(String namespace, String name) {
    return null;
  }

  @Override
  public String getPositionDescription() {
    return null;
  }

  @Override
  public int getAttributeNameResource(int index) {
    return 0;
  }

  @Override
  public int getAttributeListValue(
      String namespace, String attribute, String[] options, int defaultValue) {
    return defaultValue;
  }

  @Override
  public boolean getAttributeBooleanValue(
      String namespace, String attribute, boolean defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
    return defaultValue;
  }

  @Override
  public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeListValue(int index, String[] options, int defaultValue) {
    return defaultValue;
  }

  @Override
  public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeResourceValue(int index, int defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeIntValue(int index, int defaultValue) {
    return defaultValue;
  }

  @Override
  public int getAttributeUnsignedIntValue(int index, int defaultValue) {
    return defaultValue;
  }

  @Override
  public float getAttributeFloatValue(int index, float defaultValue) {
    return defaultValue;
  }

  @Override
  public String getIdAttribute() {
    // TODO
    return null;
  }

  @Override
  public String getClassAttribute() {
    // TODO
    return null;
  }

  @Override
  public int getIdAttributeResourceValue(int defaultValue) {
    return defaultValue;
  }

  @Override
  public int getStyleAttribute() {
    // TODO
    return 0;
  }
}
