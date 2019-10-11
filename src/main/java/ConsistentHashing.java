import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Simple java implementation of consistent hashing
 *
 * Intentionally:
 *  - Not thread safe
 *  - Doesn't support "virtual" values
 *  - Doesn't handle value hashcode collisions
 *
 * @hradba
 */
public class ConsistentHashing<K, V> {
    private SortedMap<Integer, V> ring = new TreeMap<>();

    public V getKeyValue(K key) {
        if (ring.isEmpty()) return null;
        SortedMap<Integer, V> tail = ring.tailMap(key.hashCode());
        Integer valueKey = !tail.isEmpty() ? tail.firstKey() : ring.firstKey();
        return ring.get(valueKey);
    }

    public void addValue(V ip) {
        ring.put(ip.hashCode(), ip);
    }

    public void removeValue(V ip) {
        ring.remove(ip.hashCode());
    }
}


