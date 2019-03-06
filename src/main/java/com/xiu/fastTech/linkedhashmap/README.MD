# LinkedHashMap

## LinkedHashMap类层次结构

```
public class LinkedHashMap<K,V>
    extends HashMap<K,V>
    implements Map<K,V>{
    。。。
}

```

	LinkedHashMap继承了HashMap,LinkedHashMap也继承和使用了HashMap的数据结构，即哈希表（数组+链表）单链表长度超过8时转化为红黑树。除此外LinkdHashMap还维护了一个双向链表，用来解决Hashap无法保持遍历顺序和插入顺序一致的问题，同时也提供了访问顺序支持，即LRU(最近最少使用)原则。

	因此LinkedHashMap的数据结构为：数组+单向链表+红黑树+双向链表

## 双向链表的构建过程：

	![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/linkedhashmap/image/linkedhashmap.jpg)
	
	图中红黄箭头代表插入顺序，蓝箭头代表存储顺序，head表示双向链表的头部，tail代表双向链表的尾部
	
	LinkedHashMap中双向链表的数据结构，记录插入的顺序，或者访问顺序
	
```
	    static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    	}
```
	
	LinkedhashMap中并没有put/remove方法，而是调用父类HashMap的put/remove方法，只不过在生成新节点时，LinkedHashMap重写了HashMap中的newNode方法。
	重写例子可参考：com.xiu.fastTech.linkedhashmap.override包下
	
	![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/linkedhashmap/image/linkedHashmapput.jpg)
	
	remove方法也是类似的,调用父类的方法，然后重写了维护双向链表的部分。
	
## LinkedHashMap的特点：
	
	1.LinkedHashMap拥有与HashMap相同的底层哈希结构，即数组+单链表+红黑树，另外LinkedHashMap内部通过Entry维护了一个双向链表
	
	2.HashMap元素的遍历不一定与元素插入顺序相同，而LinkedHashMap则通过遍历双向链表获取元素，遍历顺序与插入顺序是一致的。
	
	3.LinkedHashMap可通过构造参数accessOrder指定，是否在其元素被访问后改变其在双向链表的位置。
	
	
	参考https://mp.weixin.qq.com/s?src=11&timestamp=1551766307&ver=1465&signature=zELq-KL9TqgJVsEKpfumjhFbi7XRAS7eqZUT0Fs7dhG4xZu3tzi9BTUlMe1VXDUih0BZZRb8tu46olw9v8PczHhV-RtS-A21kNvzQ0mlEk1OvckaQM9DiDe2f*tpt140&new=1
	
	
	
	
	
	