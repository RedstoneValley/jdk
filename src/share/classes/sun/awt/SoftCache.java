package sun.awt;

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ConcurrentHashMap} wrapper that stores its values as {@link SoftReference}s.
 */
public class SoftCache<K, V> extends AbstractMap<K, V> {

  private ConcurrentHashMap<K, SoftReference<V>> hashMap = new ConcurrentHashMap<>();

  public V get(Object key) {
    SoftReference<V> softValue = hashMap.get(key);
    if (softValue == null) {
      return null;
    }
    return softValue.get();
  }

  @Override
  public V put(K key, V value) {
    SoftReference<V> softOld = hashMap.put(key, new SoftReference<>(value));
    if (softOld == null) {
      return null;
    }
    return softOld.get();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> entrySet = new HashSet<>();
    for (K key : hashMap.keySet()) {
      if (get(key) != null) {
        entrySet.add(new Entry<K, V>() {
          @Override
          public K getKey() {
            return key;
          }

          @Override
          public V getValue() {
            return get(key);
          }

          @Override
          public V setValue(V value) {
            return put(key, value);
          }
        });
      }
    }
    return entrySet;
  }
}
