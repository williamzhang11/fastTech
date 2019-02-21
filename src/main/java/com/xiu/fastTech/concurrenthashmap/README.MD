# ConcurrentHashMap底层实现原理
	
## 数据结构：


##在 jdk1.7下

数据结构图

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/concurrenthashmap/image/concurrenthashmap.jpg)

ConcurrentHashMap的核心成员变量由Segment数组，HashEntiry组成，底层数据结构仍然是数组+链表。

```
	final Segment<K,V>[] segments;

    transient Set<K> keySet;
    transient Set<Map.Entry<K,V>> entrySet;

```
Segment是ConcurrentHashMap的内部类，核心成员变量为：

```
static final class Segment<K,V> extends ReentrantLock implements Serializable{

		...
 		transient volatile HashEntry<K,V>[] table;

        transient int count;

        transient int modCount;

        transient int threshold;

        final float loadFactor;
		...

}

```

HashEntry的成员变量如下：
```
static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;
        ...
}

```
和HashMap的区别是其核心数据如value,以及链表都是volatile修饰的，保证了获取时的可见性

原理上：ConcurrentHashMap采用分段锁技术，其中Segment继承于ReetrantLock。不会像HashTable那样不管是put还是get操作，都需要做
同步处理，理论上ConcurrentHashMap支持CurrentcyLevel（Segment数组数量）的线程并发。每当一个线程占用锁访问一个Segment时，不会
影响到其他的Segment。

put方法
```
public V put(K key, V value) {
        Segment<K,V> s;
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
             (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
            s = ensureSegment(j);
        return s.put(key, hash, value, false);
    }

```




























