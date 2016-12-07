package sun.awt.print;

import java.awt.print.PrintRequestAttributeSet;

/**
 * Partial reimplementation of the OpenJDK class for use by SkinJob.
 */
public class PrintService {
  public Object getSupportedAttributeValues(
      Class<?> clazz, Object o, PrintRequestAttributeSet attributes) {
    // TODO
    return null;
  }

  public boolean isAttributeCategorySupported(Class<?> clazz) {
    // TODO
    return false;
  }

  public boolean isAttributeValueSupported(
      Object attribute, Object o, PrintRequestAttributeSet attributes) {
    // TODO
    return false;
  }
}
