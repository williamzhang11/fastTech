# 二叉树，满二叉树，完全二叉树知识点

## 树的相关知识点

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/binarytree/image/binarytreepoint.png)


## 树
	树是一种数据结构，它由n个有限节点组成一个具有层次关系的集合。
	
## 特点
	1.每个节点有零个或多个子节点
	2.没有父节点的节点成为根节点
	3.每个非根节点有且只有一个父节点
	4.除了根节点，每个子节点可以分为多个不相交的子树

## 基本术语
	节点的度：结点拥有的子节点的数目。
	叶子：度为0的节点
	分支节点：度不为0的节点
	树的度：树中节点最大的度
	层次：根节点层次为1，其余节点的层次等于该节点双亲节点层次加1
	树的高度：从叶子节点开始其高度为1，自底向上逐层累加
	树的深度：从根节点开始深度为1，自上而下逐层累加
	树的宽度：最多节点数层中包含的节点数
	无序树：如果树中节点的各子树之间的次序是不重要的，可以交换位置
	有序树：如果树中节点的各子数直接的次序是重要的，不可交换位置
	m阶：一个节点最多有m颗子树

## 二叉树
	二叉树：最多由2颗子树的树成为二叉树
## 二叉树的基本形态
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/binarytree/image/binarytree.jpg)

## 二叉树的性质

	1.二叉树中，第i层的节点总数不超过2的{i-1}次方 (i≥1)
	2.深度为h的二叉树最多有二叉树最多有2的{h}次方 -1个节点
	3.包含n个节点的二叉树高度至少为log以2为底（n+1）的对数
	4.在任意一颗二叉树，若叶子节点个数n0，度为2的节点数为n2,则n0=n2+1

## 树与二叉树的区别
	1.树中节点的最大度没有限制，二叉树节点最大度为2
	2.树的节点没有左右的区分，二叉树有
	

## 满二叉树
	定义：高度为h，并且拥有{2的h次方-1}个节点的二叉树，称为满二叉树
	


## 完全二叉树
	定义：一棵二叉树中，只有最下面2层节点的度可以小于2，并且最下一层的叶子节点集中靠左的位置，成为完全二叉树
	特点：叶子节点只能出现在最下面和次下层，且最下面叶子节点集中在树的左部。满二叉树必定是一颗完全二叉树，而完全二叉树不一定是满二叉树。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/binarytree/image/fullbinarytree.jpg)	
	
## 二叉堆
	定义：二叉堆是一种完全二叉树，分为，最大堆和最小堆
	最大堆:任何一个父节点的值都大于等于它左右孩子节点的值
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/binarytree/image/maxheap.jpg)	
	
	最小堆:任何一个父节点的值都小于等于它左右孩子节点的值
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/binarytree/image/minheap.jpg)	
	堆顶：二叉堆的根节点
	最大堆的堆顶是整个堆最大值，最小堆堆顶是整个堆最小值
	
二叉堆虽然是完全二叉树，但是存储方式是顺序存储的，所有节点存储在数组中
