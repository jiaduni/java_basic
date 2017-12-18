package resource.util;

/**
 * describe :
 * Created by jiadu on 2017/10/12 0012.
 */
public class MyLinkedHashMap<K,V>
        extends MyHashMap<K,V>
        implements MyMap<K,V> {

    final boolean accessOrder;

    transient int modCount;

    transient Entry<K,V> head;

    public MyLinkedHashMap() {
        super();
        accessOrder = false;
    }

    transient Entry<K,V> tail;

    static class Entry<K,V> extends MyHashMap.MyNode<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, MyNode<K,V> next) {
            super(hash, key, value, next);
        }
    }

    void afterNodeAccess(MyNode<K,V> e) { // move node to last
        Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            Entry<K,V> p =
                    (Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }

    void afterNodeInsertion(boolean evict) { // possibly remove eldest
        Entry<K,V> first;
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }

    void afterNodeRemoval(MyNode<K,V> e) { // unlink
        Entry<K,V> p =
                (Entry<K,V>)e, b = p.before, a = p.after;
        p.before = p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a == null)
            tail = b;
        else
            a.before = b;
    }

    protected boolean removeEldestEntry(MyMap.MyEntry<K,V> eldest) {
        return false;
    }
}
