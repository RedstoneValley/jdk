package sun.awt;

/**
 * Copy of the OpenJDK class for use by SkinJob.
 */
final class MostRecentKeyValue {
  Object key;
  Object value;

  MostRecentKeyValue(Object k, Object v) {
    key = k;
    value = v;
  }

  void setPair(Object k, Object v) {
    key = k;
    value = v;
  }
}
