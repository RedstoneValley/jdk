package sun.awt;

/**
 * Created by chris on 12/6/2016.
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
