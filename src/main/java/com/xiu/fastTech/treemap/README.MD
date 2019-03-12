# TreeMap

TreeMap的底层是基于红黑树实现的，是一个有序的key-value集合，根据key的自然顺序或者创建时提供的Comparator进行排序。

## 特点：

	增删改查的时间复杂度都是O(log(n))
	TreeMap是非同步的(线程不安全)，它的iterator方法返回的迭代器是fail-fast(快速失败)
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/treemap/image/redandblack.jpg)
	
## 核心成员变量

```
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
{
	//排序的比较器
    private final Comparator<? super K> comparator;
	//红黑树的根节点
    private transient Entry<K,V> root;
   //红黑树节点个数
    private transient int size = 0;
	//记录修改红黑树的总数
    private transient int modCount = 0;

    public TreeMap() {
        comparator = null;
    }
	//指定比较器
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }
	//指定集合初始化
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }
	//带SortedMap的构造函数
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }
    。。。

```
TreeMap是一个有序的key-value集合，它通过红黑书实现的
TreeMap继承于AbstractMap，它是一个Map,即key-value集合
实现了NavigableMap接口，它支持一系列的导航方法，比如返回有序的key集合
实现了Cloneable接口，意味着它能被克隆
实现了java.io.Serializable接口，意味着它支持序列化


## 红黑树节点类

```

    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;//键
        V value;//值
        Entry<K,V> left;//左孩子
        Entry<K,V> right;//右孩子
        Entry<K,V> parent;//父节点
        boolean color = BLACK;//当前节点颜色

```
Entry根据key进行排序

put方法

```
public V put(K key, V value) {
        Entry<K,V> t = root;
        //若红黑书树空，插入根节点
        if (t == null) {
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
        //在二叉树中，找到（key,value）的插入位置
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //新建红黑树节点
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        //红黑树插入节点后，不再是红黑树，需要调整，恢复红黑树的特性
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }


```


参考：https://www.cnblogs.com/skywang12345/p/3310928.html

