package resource.util;


import resource.io.MySerializable;
import resource.lang.MyCloneable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * describe :
 * Created by jiadu on 2017/10/12 0012.
 */
public class MyHashMap<K, V> extends MyAbstractMap<K, V>
        implements MyMap<K, V>, MyCloneable, MySerializable {

    transient int modCount;//修改次数，主要是用在线程安全的作用上

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 初始化map容量2的4次方

    static final float DEFAULT_LOAD_FACTOR = 0.75f;//默认加载因子0.75

    final float loadFactor;//加载因子

    static final int MAXIMUM_CAPACITY = 1 << 30;// 最大容量为2的30次方

    transient int size;// 已存元素的个数

    int threshold;// 下次扩容的临界值，size>=threshold就会扩容

    transient MyNode<K, V>[] table;

    static final int UNTREEIFY_THRESHOLD = 6;//由树转换成链表的阈值

    static final int MIN_TREEIFY_CAPACITY = 64;//当桶中的bin被树化时最小的hash表容量

    static final int TREEIFY_THRESHOLD = 8;//由链表转换成树的阈值

    transient Set<MyEntry<K, V>> entrySet;

    static class MyNode<K, V> implements MyMap.MyEntry<K, V> {//用于存放真正数值的对象，其实也是接口的实现
        final int hash;
        final K key;
        V value;
        MyNode<K, V> next;

        MyNode(int hash, K key, V value, MyNode<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof MyMap.MyEntry) {
                MyMap.MyEntry<?, ?> e = (MyMap.MyEntry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /**
     * 构造器，初始化容量16，默认加载因子0.75
     */
    public MyHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    @Override
    public V get(Object key) {
        MyNode<K, V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    final MyNode<K, V> getNode(int hash, Object key) {
        MyNode<K, V>[] tab;
        MyNode<K, V> first, e;
        int n;
        K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & hash]) != null) {//检查第一个是否有值
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))//如果检查第一个有值，并且key相等，则返回
                return first;
            if ((e = first.next) != null) {//如果不是第一个
                if (first instanceof MyTreeNode)//如果已经是红黑树结构，那么调用红黑树的get方法
                    return ((MyTreeNode<K, V>) first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);//一直往下找
            }
        }
        return null;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * @param hash         key的hash值
     * @param key
     * @param value        the value to put
     * @param onlyIfAbsent 如果是true,不会改变已存在的值
     * @param evict        if false, the table is in creation mode.
     * @return
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        MyNode<K, V>[] tab;
        MyNode<K, V> p;
        int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)//如果之前没有map的话
            n = (tab = resize()).length;//扩容并获取
        if ((p = tab[i = (n - 1) & hash]) == null)//如果map的该节点上没有数据，则new一个
            tab[i] = newMyNode(hash, key, value, null);
        else {
            MyNode<K, V> e;
            K k;
            if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))//如果key相同，则直接覆盖
                e = p;
            else if (p instanceof MyTreeNode)//如果是树形的，则new一个树结构
                e = ((MyTreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            else {//如果map有值，但还没有被转换成树的时候
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {//如果下个节点为空
                        p.next = newMyNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    final void treeifyBin(MyNode<K, V>[] tab, int hash) {
        int n, index;
        MyNode<K, V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            MyTreeNode<K, V> hd = null, tl = null;
            do {
                MyTreeNode<K, V> p = replacementTreeNode(e, null);//把链表转换成树
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

    /**
     * 初始化map或扩容
     *
     * @return
     */
    final MyNode<K, V>[] resize() {
        MyNode<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {//判断是否有map，并且容量大小大于0
            if (oldCap >= MAXIMUM_CAPACITY) {//判断是否长度是否大于等于最大值
                threshold = Integer.MAX_VALUE;//把临界值设置为
                return oldTab;
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)//如果存在Map,并且Map容量的平方也不大于最大容量
                newThr = oldThr << 1; // 设置临界值是现在的平方
        } else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // 全部不大于0的话，初始化
            newCap = DEFAULT_INITIAL_CAPACITY;//默认容量16
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);//设置临界值
        }
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                    (int) ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes", "unchecked"})
        MyNode<K, V>[] newTab = (MyNode<K, V>[]) new MyNode[newCap];//初始化一个容量的数组
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                MyNode<K, V> e;
                if ((e = oldTab[j]) != null) {//取出，赋值
                    oldTab[j] = null;
                    if (e.next == null)//如果没有下一个元素
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof MyTreeNode)//如果是树结构的话
                        ((MyTreeNode<K, V>) e).split(this, newTab, j, oldCap);
                    else { // 有下一个元素，并且还没有被转换成树
                        MyNode<K, V> loHead = null, loTail = null;
                        MyNode<K, V> hiHead = null, hiTail = null;
                        MyNode<K, V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            } else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    final MyNode<K, V> removeNode(int hash, Object key, Object value,
                                  boolean matchValue, boolean movable) {
        MyNode<K, V>[] tab;
        MyNode<K, V> p;
        int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            MyNode<K, V> node = null, e;
            K k;
            V v;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                if (p instanceof MyTreeNode)
                    node = ((MyTreeNode<K, V>) p).getTreeNode(hash, key);
                else {
                    do {
                        if (e.hash == hash &&
                                ((k = e.key) == key ||
                                        (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            if (node != null && (!matchValue || (v = node.value) == value ||
                    (value != null && value.equals(v)))) {
                if (node instanceof MyTreeNode)
                    ((MyTreeNode<K, V>) node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                ++modCount;
                --size;
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }

    MyTreeNode<K, V> newMyTreeNode(int hash, K key, V value, MyNode<K, V> next) {
        return new MyTreeNode<>(hash, key, value, next);
    }

    MyNode<K, V> newMyNode(int hash, K key, V value, MyNode<K, V> next) {
        return new MyNode<>(hash, key, value, next);
    }

    MyTreeNode<K, V> replacementTreeNode(MyNode<K, V> p, MyNode<K, V> next) {
        return new MyTreeNode<>(p.hash, p.key, p.value, next);
    }

    void afterNodeAccess(MyNode<K, V> p) {
    }

    void afterNodeInsertion(boolean evict) {
    }

    void afterNodeRemoval(MyNode<K, V> p) {
    }

    //------------------------------------------MyTreeNode
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = (ParameterizedType) t).getRawType() ==
                                    Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable) k).compareTo(x));
    }

    static <K, V> void moveRootToFront(MyNode<K, V>[] tab, MyTreeNode<K, V> root) {
        int n;
        if (root != null && tab != null && (n = tab.length) > 0) {
            int index = (n - 1) & root.hash;
            MyTreeNode<K, V> first = (MyTreeNode<K, V>) tab[index];
            if (root != first) {
                MyNode<K, V> rn;
                tab[index] = root;
                MyTreeNode<K, V> rp = root.prev;
                if ((rn = root.next) != null)
                    ((MyTreeNode<K, V>) rn).prev = rp;
                if (rp != null)
                    rp.next = rn;
                if (first != null)
                    first.prev = root;
                root.next = first;
                root.prev = null;
            }
            assert checkInvariants(root);
        }
    }

    static <K, V> boolean checkInvariants(MyTreeNode<K, V> t) {
        MyTreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
                tb = t.prev, tn = (MyTreeNode<K, V>) t.next;
        if (tb != null && tb.next != t)
            return false;
        if (tn != null && tn.prev != t)
            return false;
        if (tp != null && t != tp.left && t != tp.right)
            return false;
        if (tl != null && (tl.parent != t || tl.hash > t.hash))
            return false;
        if (tr != null && (tr.parent != t || tr.hash < t.hash))
            return false;
        if (t.red && tl != null && tl.red && tr != null && tr.red)
            return false;
        if (tl != null && !checkInvariants(tl))
            return false;
        if (tr != null && !checkInvariants(tr))
            return false;
        return true;
    }

    MyNode<K, V> replacementNode(MyNode<K, V> p, MyNode<K, V> next) {
        return new MyNode<>(p.hash, p.key, p.value, next);
    }

    static final class MyTreeNode<K, V> extends MyLinkedHashMap.Entry<K, V> {
        MyTreeNode<K, V> parent;  // red-black tree links
        MyTreeNode<K, V> left;
        MyTreeNode<K, V> right;
        MyTreeNode<K, V> prev;    // needed to unlink next upon deletion
        boolean red;

        MyTreeNode(int hash, K key, V val, MyNode<K, V> next) {
            super(hash, key, val, next);
        }

        /**
         * Returns root of tree containing this node.
         */
        final MyTreeNode<K, V> root() {
            for (MyTreeNode<K, V> r = this, p; ; ) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

        /**
         * Ensures that the given root is the first node of its bin.
         */
        static <K, V> void moveRootToFront(MyNode<K, V>[] tab, MyTreeNode<K, V> root) {
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {
                int index = (n - 1) & root.hash;
                MyTreeNode<K, V> first = (MyTreeNode<K, V>) tab[index];
                if (root != first) {
                    MyNode<K, V> rn;
                    tab[index] = root;
                    MyTreeNode<K, V> rp = root.prev;
                    if ((rn = root.next) != null)
                        ((MyTreeNode<K, V>) rn).prev = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)
                        first.prev = root;
                    root.next = first;
                    root.prev = null;
                }
                assert checkInvariants(root);
            }
        }

        final void removeTreeNode(MyHashMap<K, V> map, MyNode<K, V>[] tab,
                                  boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            MyTreeNode<K, V> first = (MyTreeNode<K, V>) tab[index], root = first, rl;
            MyTreeNode<K, V> succ = (MyTreeNode<K, V>) next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || root.right == null ||
                    (rl = root.left) == null || rl.left == null) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            MyTreeNode<K, V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                MyTreeNode<K, V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red;
                s.red = p.red;
                p.red = c; // swap colors
                MyTreeNode<K, V> sr = s.right;
                MyTreeNode<K, V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                } else {
                    MyTreeNode<K, V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            } else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                MyTreeNode<K, V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            MyTreeNode<K, V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                MyTreeNode<K, V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }

        /**
         * Finds the node starting at root p with the given hash and key.
         * The kc argument caches comparableClassFor(key) upon first use
         * comparing keys.
         */
        final MyTreeNode<K, V> find(int h, Object k, Class<?> kc) {
            MyTreeNode<K, V> p = this;
            do {
                int ph, dir;
                K pk;
                MyTreeNode<K, V> pl = p.left, pr = p.right, q;
                if ((ph = p.hash) > h)
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if (pl == null)
                    p = pr;
                else if (pr == null)
                    p = pl;
                else if ((kc != null ||
                        (kc = comparableClassFor(k)) != null) &&
                        (dir = compareComparables(kc, k, pk)) != 0)
                    p = (dir < 0) ? pl : pr;
                else if ((q = pr.find(h, k, kc)) != null)
                    return q;
                else
                    p = pl;
            } while (p != null);
            return null;
        }

        /**
         * Calls find for root node.
         */
        final MyTreeNode<K, V> getMyTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }

        final MyTreeNode<K, V> getTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }

        /**
         * Tie-breaking utility for ordering insertions when equal
         * hashCodes and non-comparable. We don't require a total
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                    (d = a.getClass().getName().
                            compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                        -1 : 1);
            return d;
        }

        /**
         * Forms tree of the nodes linked from this node.
         *
         * @return root of tree
         */
        final void treeify(MyNode<K, V>[] tab) {
            MyTreeNode<K, V> root = null;
            for (MyTreeNode<K, V> x = this, next; x != null; x = next) {
                next = (MyTreeNode<K, V>) x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                } else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (MyTreeNode<K, V> p = root; ; ) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                (kc = comparableClassFor(k)) == null) ||
                                (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        MyTreeNode<K, V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
        }

        /**
         * Returns a list of non-MyTreeNodes replacing those linked from
         * this node.
         */
        final MyNode<K, V> untreeify(MyHashMap<K, V> map) {
            MyNode<K, V> hd = null, tl = null;
            for (MyNode<K, V> q = this; q != null; q = q.next) {
                MyNode<K, V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        /**
         * Tree version of putVal.
         */
        final MyTreeNode<K, V> putTreeVal(MyHashMap<K, V> map, MyNode<K, V>[] tab,
                                          int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            MyTreeNode<K, V> root = (parent != null) ? root() : this;
            for (MyTreeNode<K, V> p = root; ; ) {
                int dir, ph;
                K pk;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        MyTreeNode<K, V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                                (q = ch.find(h, k, kc)) != null) ||
                                ((ch = p.right) != null &&
                                        (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                MyTreeNode<K, V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    MyNode<K, V> xpn = xp.next;
                    MyTreeNode<K, V> x = map.newMyTreeNode(h, k, v, xpn);
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.prev = xp;
                    if (xpn != null)
                        ((MyTreeNode<K, V>) xpn).prev = x;
                    moveRootToFront(tab, balanceInsertion(root, x));
                    return null;
                }
            }
        }

        /**
         * Removes the given node, that must be present before this call.
         * This is messier than typical red-black deletion code because we
         * cannot swap the contents of an interior node with a leaf
         * successor that is pinned by "next" pointers that are accessible
         * independently during traversal. So instead we swap the tree
         * linkages. If the current tree appears to have too few nodes,
         * the bin is converted back to a plain bin. (The test triggers
         * somewhere between 2 and 6 nodes, depending on tree structure).
         */
        final void removeMyTreeNode(MyHashMap<K, V> map, MyNode<K, V>[] tab,
                                    boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            MyTreeNode<K, V> first = (MyTreeNode<K, V>) tab[index], root = first, rl;
            MyTreeNode<K, V> succ = (MyTreeNode<K, V>) next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || root.right == null ||
                    (rl = root.left) == null || rl.left == null) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            MyTreeNode<K, V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                MyTreeNode<K, V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red;
                s.red = p.red;
                p.red = c; // swap colors
                MyTreeNode<K, V> sr = s.right;
                MyTreeNode<K, V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                } else {
                    MyTreeNode<K, V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            } else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                MyTreeNode<K, V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            MyTreeNode<K, V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                MyTreeNode<K, V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }

        /**
         * Splits nodes in a tree bin into lower and upper tree bins,
         * or untreeifies if now too small. Called only from resize;
         * see above discussion about split bits and indices.
         *
         * @param map   the map
         * @param tab   the table for recording bin heads
         * @param index the index of the table being split
         * @param bit   the bit of hash to split on
         */
        final void split(MyHashMap<K, V> map, MyNode<K, V>[] tab, int index, int bit) {
            MyTreeNode<K, V> b = this;
            // Relink into lo and hi lists, preserving order
            MyTreeNode<K, V> loHead = null, loTail = null;
            MyTreeNode<K, V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (MyTreeNode<K, V> e = b, next; e != null; e = next) {
                next = (MyTreeNode<K, V>) e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                } else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR

        static <K, V> MyTreeNode<K, V> rotateLeft(MyTreeNode<K, V> root,
                                                  MyTreeNode<K, V> p) {
            MyTreeNode<K, V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K, V> MyTreeNode<K, V> rotateRight(MyTreeNode<K, V> root,
                                                   MyTreeNode<K, V> p) {
            MyTreeNode<K, V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K, V> MyTreeNode<K, V> balanceInsertion(MyTreeNode<K, V> root,
                                                        MyTreeNode<K, V> x) {
            x.red = true;
            for (MyTreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                } else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K, V> MyTreeNode<K, V> balanceDeletion(MyTreeNode<K, V> root,
                                                       MyTreeNode<K, V> x) {
            for (MyTreeNode<K, V> xp, xpl, xpr; ; ) {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (x.red) {
                    x.red = false;
                    return root;
                } else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        MyTreeNode<K, V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                                (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        } else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                        null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                } else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        MyTreeNode<K, V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                                (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        } else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                        null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K, V> boolean checkInvariants(MyTreeNode<K, V> t) {
            MyTreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
                    tb = t.prev, tn = (MyTreeNode<K, V>) t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }
    }

}
