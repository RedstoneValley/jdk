package sun.awt;

import android.util.Pair;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ConcurrentHashMap} wrapper that stores its values as {@link SoftReference}s.
 */
public class SoftCache<K, V> extends AbstractMap<K, V> {
  private static class PairAsEntry<K, V> extends Pair<K, V> implements Entry<K, V> {
    public PairAsEntry(K first, V second) {
      super(first, second);
    }

    @Override
    public K getKey() {
      return first;
    }

    @Override
    public V getValue() {
      return second;
    }

    @Override
    public V setValue(V value) {
      V old = second;
      second = value;
      return old;
    }
  }

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
    for (Entry<K, SoftReference<V>> softEntry : hashMap.entrySet()) {
      V value = softEntry.getValue().get();
      if (value != null) {
        entrySet.add(new PairAsEntry<>(softEntry.getKey(), value));
      }
    }
    return entrySet;
  }
}
