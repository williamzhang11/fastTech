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
        //第一步：通过key定位到Segment,之后在对应的Segment进行put
        int hash = hash(key);
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
             (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
            s = ensureSegment(j);
        return s.put(key, hash, value, false);
    }
	//虽然HashEntry中的value是用volatile关键字修饰的，但是不能保证并发的原子性，所以put操作时需要加锁
	final V put(K key, int hash, V value, boolean onlyIfAbsent) {
	//第二步：尝试获取锁，如果获取失败就有其他线程存在竞争，然后利用scanAndLockForPut（）自旋获取锁
	//如果重试的次数达到MAX_SCAN_RETRIES ，则改为阻塞锁获取，保证能成功。
	
            HashEntry<K,V> node = tryLock() ? null :
                scanAndLockForPut(key, hash, value);
            V oldValue;
            try {
                HashEntry<K,V>[] tab = table;
                //第三步：将当前的Segment中的table通过key的hash值取余定位到HashEntry
                int index = (tab.length - 1) & hash;
                HashEntry<K,V> first = entryAt(tab, index);
               
                for (HashEntry<K,V> e = first;;) {
                 //第四步：遍历该HashEntry,如果不为空则判断传入的key和当前遍历的key是否相等相等则覆盖旧的value
                    if (e != null) {
                        K k;
                        if ((k = e.key) == key ||
                            (e.hash == hash && key.equals(k))) {
                            oldValue = e.value;
                            if (!onlyIfAbsent) {
                                e.value = value;
                                ++modCount;
                            }
                            break;
                        }
                        e = e.next;
                    }
                    else {
                    //第五步：为空，则新建一个HashEntry加入到Segment中，同时判断是否需要扩容
                        if (node != null)
                            node.setNext(first);
                        else
                            node = new HashEntry<K,V>(hash, key, value, first);
                        int c = count + 1;
                        if (c > threshold && tab.length < MAXIMUM_CAPACITY)
                            rehash(node);
                        else
                            setEntryAt(tab, index, node);
                        ++modCount;
                        count = c;
                        oldValue = null;
                        break;
                    }
                }
            } finally {
            //第六步:释放获取的锁
                unlock();
            }
            return oldValue;
        }
```
get方法

```
	public V get(Object key) {
        Segment<K,V> s; // manually integrate access methods to reduce overhead
        HashEntry<K,V>[] tab;
        //第一步:计算key的hash值，定位到具体的segment
        int h = hash(key);
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        if ((s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)) != null &&
            (tab = s.table) != null) {
            //第二步：再一次hash定位到具体的元素上
            for (HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile
                     (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
                 e != null; e = e.next) {
                K k;
                if ((k = e.key) == key || (e.hash == h && key.equals(k)))
                    return e.value;
            }
        }
        return null;
    }	

```
由于HashEntry中的value属性是用volatile关键字修饰的，保证了内存的可见性，所以每次获取的都是最新值
ConcurrentHashMap的get方法是非常高效的，因为整个过程没有加锁

ConcurrentHashMap再jdk1.7中虽然已经解决了并发问题，甚至能支持N个Segment次数的并发，但依然存在的问题，和jdk1.7 HashMap
中一样的问题--查询遍历链表的效率低

























