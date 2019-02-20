# HashMap底层实现原理
	在软件开发中，像Map这样Key Value键值对的数据存储结构是非常经典的，主要被用在内存中存放数据。
## 数据结构：

在JDK 1.8之前，HashMap的数据结构存储是数组+链表。
从JDK 1.8开始，HashMap的数据结构存储：链表长度不超过8时，数据结构为，数组+链表。默认超过8时，链表转化成红黑树，即数组+红黑树
利用红黑树的快速增删改查的特点提高HashMap性能。

##在 jdk1.7下

数据结构图

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/hashmap/image/640.jpg)

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
默认的容量为16,负载因子为0.75

































