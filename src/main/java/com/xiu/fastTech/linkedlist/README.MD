##  LinkedList

	底层数据结构：双向链表，可以作为队列/堆栈/双端队列的实现。
	实现了：
	List接口，元素有序的
	Deque接口（继承了Queue），双端队列/队列操作
	Cloneable接口，可以用来做浅克隆
	Serializable接口，用来做网络传输和持久化，同时持久化可以做深克隆
	
优点：1.不需要扩容和预留空间，物理存储不连续
	2. 在list的头尾增加和删除效率高（O(1)）

缺点：1.随机访问效率低
     2.修改，查询效率低

成员变量

```
	//当前存储元素的个数
	transient int size = 0;
	//首节点
    transient Node<E> first;
	//尾节点
    transient Node<E> last;
    //核心数据结构
    private static class Node<E> {
    	//当前节点
        E item;
        //后置节点
        Node<E> next;
        //前置节点
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
    。。。
```

public LinkedList(Collection<? extends E> c) 带参数的初始化方法

```
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }
    
    public boolean addAll(int index, Collection<? extends E> c) {
    	//1.校验是否越界
        checkPositionIndex(index);
		//2.集合参数转化为数组
        Object[] a = c.toArray();
        int numNew = a.length;
        //3.如果初始化集合参数为空，返回
        if (numNew == 0)
            return false;

        Node<E> pred, succ;
        //4.如果为末尾插入
        if (index == size) {
            succ = null;//后置节点为空
            pred = last;//前置节点是当前LinkedList的最后一个节点
            //5.中间插入
        } else {
            succ = node(index);//后置节点为待插入的index节点
            pred = succ.prev;//index节点的前置节点赋值当前节点
        }
		//6.遍历对象数组
        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            //7.当前新节点初始化
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

```




















