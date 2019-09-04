# mysql B+索引和哈希索引的区别
	
	
	B+树是一个平衡的多叉树，从根节点到每个叶子节点的高度差值不超过1，而且同层级的节点间有指针相互链接。

	在B+树上的常规检索，从根节点到叶子节点的搜索效率基本相当，不会出现大幅波动，而且基于索引的顺序扫描时，
	也可以利用双向指针快速左右移动，效率非常高。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/Bplushashindex/image/btree.jpg)


![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/Bplushashindex/image/hash.jpg)

	简单地说，哈希索引就是采用一定的哈希算法，把键值换算成新的哈希值，检索时不需要类似B+树那样从根节点到叶子节点逐级查找，
	只需一次哈希算法即可立刻定位到相应的位置，速度非常快。
	
	
	1.哈希索引也没办法利用索引完成排序
	2.不支持最左匹配原则
	3.在有大量重复键值情况下，哈希索引的效率也是极低的---->哈希碰撞问题。
	4.不支持范围查询
	
	在大多数场景下，都会有范围查询、排序、分组等查询特征，用B+树索引就可以了
	
	
