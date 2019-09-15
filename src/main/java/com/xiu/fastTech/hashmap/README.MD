# HashMap底层实现原理
	在软件开发中，像Map这样Key Value键值对的数据存储结构是非常经典的，主要被用在内存中存放数据。
## 数据结构：

在JDK 1.8之前，HashMap的数据结构存储是数组+链表。
从JDK 1.8开始，HashMap的数据结构存储：链表长度不超过8时，数据结构为，数组+链表。默认超过8时，链表转化成红黑树，即数组+红黑树
利用红黑树的快速增删改查的特点提高HashMap性能。

HashMap下的链表为单向链表

##在 jdk1.7下

数据结构图

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/hashmap/image/jdk1.7hashMap.jpg)

HashMap这种数组+链表结构，有的也称哈希桶或哈希表，或哈希散列
```
    //初始桶大小，因为底层是数组，所以数组的默认大小为1<<4即 2的4次方，16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 
	//桶（数组）的最大值,2的30次方
    static final int MAXIMUM_CAPACITY = 1 << 30;
 	//默认的负载因子（0.75）
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
 	
    static final Entry<?,?>[] EMPTY_TABLE = {};
	//存放数据的数组
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;
	//key-value数据存放的数量
    transient int size;
	//可在初始化时，指定的桶大小
    int threshold;
	//可在初始化时，指定负载因子
    final float loadFactor;
```
默认的容量为16,负载因子为0.75。Map中存放的数据达到默认容量*负载因子时，需要进行扩容。而扩容涉及到数据复制等操作，是非常消耗性能和时间的，因此，如果知道数据量多少的
情况下，可以指定初始容量大小，以避免扩容。

Entry是HashMap的一个内部类，其基本的数据结构如下：

```
static class Entry<K,V> implements Map.Entry<K,V> {
		//键
        final K key;
        //值
        V value;
        //下一个Entry的引用，实现了链表结构
        Entry<K,V> next;
        //当前键（key）的hashcode
        int hash;
        
        ...
}

```
HashMap的put方法源码逻辑如下：

```
 public V put(K key, V value) {
 		//第一步：判断当前数组是否需要初始化
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        //第二步：如果key为null,put一个空值进去
        if (key == null)
            return putForNullKey(value);
        //第三步：根据key，计算出hash值
        int hash = hash(key);
        //第四步：根据计算出的hash值，定位出桶的位置，即数组的索引下标
        int i = indexFor(hash, table.length);
        //第五步：如果桶是一个链表，需要遍历整个链表判断，链表上的key和hashcode是否和传入的key值相等。如果相等则覆盖，并返回原来的值
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
		//第六步如果桶是空的（执行到当前代码），说明当前位置没有没有数据传入，新增一个Entry对象写入当前位置
        modCount++;
        //在写入Entry时，会判断是否需要扩容。如果需要扩容就进行两倍扩充，并将当前key重新hash并定位。
        //如果桶中有值会在形成链表
        addEntry(hash, key, value, i);
        return null;
    }
    
   void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])) {
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, table.length);
        }

        createEntry(hash, key, value, bucketIndex);
    }

```
get方法源码逻辑：

```
    public V get(Object key) {
    	//第一步：判断key是否为空
        if (key == null)
            return getForNullKey();
        Entry<K,V> entry = getEntry(key);

        return null == entry ? null : entry.getValue();
    }
    
    
    final Entry<K,V> getEntry(Object key) {
        if (size == 0) {
            return null;
        }
		//第二步:根据key计算出hash值
        int hash = (key == null) ? 0 : hash(key);
        //第三步:定位到具体的桶,如果当前位置是链表，需要继续遍历判断key和hash值是否相等，来返回
        //如果不是链表则判断是否相等
        for (Entry<K,V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        //没取到，返回null
        return null;
    }

```
从上面可以看出有个效率比较低的地方，虽然散列的查询效率为O(1)，但当Hash计算冲突比较严重的时候，在桶上的链表会越来越长，查询的效率也就变低，
此时时间复杂度为O(N)。因此jdk1.8中进行了优化。

##在 jdk1.8下

数据结构图

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/hashmap/image/jdk1.8hashMap.jpg)

jdk1.8下核心成员变量和1.7的差不很多：

TREEIFY_THRESHOLD 用于判断需要将链表转为红黑书的阈值
static class Node<K,V> implements Map.Entry<K,V>{}
```
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //第一步：判断当前桶是否为空，为空需要初始化
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        //第二步：根据当前key的hash值定位到具体的桶中，判断是否为空，为空表明没有Hash冲突
        //直接在当前位置创建一个新桶。
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            //第三步：如果桶中有有值（Hash冲突），比较桶中的key,hash值，与写入的是否相等
            //相等就赋值给e
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            //第四步：如果当前桶为红黑树，按照红黑树方式写入
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
            //第五步：如果当前是链表，封装成新节点，写入当前桶后
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        //第六步：判断当前链表大小是否大于阈值，大于就转换成红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    //第七步，在遍历时找到相同的key，推出遍历
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            //第八步，如果e！=null，相当于存在相同的key，需要覆盖
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        //最后判断是否扩容
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }


```
get方法

```
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        //第一步：对key进行hash计算后，并与当前桶数量求余（不求余的话，数组太大，内存放不下），取到桶的位置。如果桶为空则直接返回。
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            //第二步：查询桶的第一个位置的key是否是要查询的key，是就直接返回。
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
            //第三步：判断是否是红黑树，是的话按照红黑树方式查找
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            //第四步：否则按照链表方式查找
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

```
修改为红黑树后，查询效率提高到了O(logn)。
##HashMap存在的问题：
线程不安全的，并发场景下容易出现死循环。
HashMap扩容时会调用resize方法，这里并发操作容易在一个桶上形成环形链表


当我们往hashmap中put元素的时候，先根据key的hash值得到这个元素在数组中的位置（即下标），然后就可以把这个元素放到对应的位置中了。如果这个元素所在的位子上已经存放有其他元素了，那么在同一个位子上的元素将以链表的形式存放，新加入的放在链头，最先加入的放在链尾。从hashmap中get元素时，首先计算key的hashcode，找到数组中对应位置的某一元素，然后通过key的equals方法在对应位置的链表中找到需要的元素

当hashmap中的元素个数超过数组大小*loadFactor时，就会进行数组扩容，loadFactor的默认值为0.75。
而在hashmap数组扩容之后，最消耗性能的点就出现了：原数组中的数据必须重新计算其在新数组中的位置，并放进去，这就是resize,一次
扩容原来大小的2倍。

首先计算key的hashcode，找到数组中对应位置的某一元素，然后通过key的equals方法在对应位置的链表中找到需要的元素。

Hashmap的key可以是任何类型的对象，例如User这种对象，为了保证两个具有相同属性的user的hashcode相同，我们就需要改写hashcode方法，比方把hashcode值的计算与User对象的id关联起来，那么只要user对象拥有相同id，那么他们的hashcode也能保持一致了，这样就可以找到在hashmap数组中的位置了。如果这个位置上有多个元素，还需要用key的equals方法在对应位置的链表中找到需要的元素，所以只改写了hashcode方法是不够的，equals方法也是需要改写滴~当然啦，按正常思维逻辑，equals方法一般都会根据实际的业务内容来定义，例如根据user对象的id来判断两个user是否相等

理想情况下，在随机哈希代码下，桶中的节点频率遵循泊松分布，文中给出了桶长度k的频率表。由频率
表可以看出，桶的长度超过8的概率非常非常小。所以作者应该是根据概率统计而选择了8作为阀值。










