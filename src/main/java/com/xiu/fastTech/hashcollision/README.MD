# 哈希冲突的3种常用解决方式

## 定义

	散列表也称为哈希表，可以把查找的时间复杂度降到常数级别。哈希表中计算索引的函数称为哈希函数或散列函数，
	如果2个不同的key通过哈希函数计算出同一个索引，就出现了哈希冲突。

## 哈希函数特点
	相等的key产生相等的哈希值
	计算简单方便
	哈希值均匀分布（若过度集中，效率也会降到o(n)）
## 哈希冲突解决方式
### 拉链法
	HashMap中的底层实现中解决冲突就是使用了拉链法，它是将链表和数据相结合，若遇到冲突将冲突值加入链表中即可
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/hashcollision/image/lalianfa.jpg)

步骤：
	1.得到一个key
	2.计算key的哈希值
	3.根据哈希值定位到数组中的位置
	4.如果数据位置为空，直接插入
	5.否则添加到链表尾端
因此哈希函数要尽可能保证哈希值的均匀分布，若集中在一条链表，时间复杂度O(N),和链表相同

### 线性探测法

	线性探测法也称为开放定址法，线性探测法直接使用数组存储。ThreadLocal的底层实现解决冲突就是使用了线性探测法
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/hashcollision/image/xianxingtance.jpg)

步骤：
	1.得到key，计算哈希值
	2.如果不冲突，插入数组
	3.否则，继续向数组后查找，知道找到第一个为空的位置，如果到了尾部可循环到前面
	
### 二次探测法
	二次探测法基本上和线性探测法是一致的，唯一的区别就是，冲突时，向当后查找数组位置的偏移量上，为2的次方，如：
	key1：hash(key)+0 
	key2：hash(key)+1^2 
	key3：hash(key)+2^2

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	