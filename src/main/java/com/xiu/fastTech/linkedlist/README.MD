##  LinkedList底层原理

	底层数据结构：双向链表，可以作为队列/堆栈/双端队列的实现。
	实现了：
	List接口，元素有序的
	Deque接口（继承了Queue），双端队列/队列操作
	Cloneable接口，可以用来做浅克隆
	Serializable接口，用来做网络传输和持久化，同时持久化可以做深克隆
	
优点：1.不需要扩容和预留空间，在内容中是不连续的地址用多少申请多少。
	2. 在list的头尾增加和删除效率高，时间复杂度（O(1)）
	3.可以当作双端队列和堆栈容器

缺点：1.随机访问效率低
     2.修改，查询效率低，时间复杂度O(n)
     3.非线程安全的
     
     ![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/linkedlist/image/LinkedList.jpg)

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

## public boolean add(E e)函数

```
	public boolean add(E e) {
        linkLast(e);
        return true;
    }
    
    void linkLast(E e) {
    	//获取当前链表的最后一个节点
        final Node<E> l = last;
        //构造出新的节点，它的前置节点是当前链表的最后一个节点
        final Node<E> newNode = new Node<>(l, e, null);
        //把当前新节点作为当前链表最后一个节点
        last = newNode;
        //首次插入
        if (l == null)
            first = newNode;
        else
            l.next = newNode;//非首次插入把最后一个节点执行新插入的节点
        size++;
        modCount++;
    }

```

## LinkedList序列化与反序列化

```
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }
    
     private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }

```
	序列化时只写入x.item而不是整个Node,这样避免java自带的序列化机制会把整个Node的数据写入序列化，并且Node还是双端链表，会导致重复2倍的空间浪费
	反序列化时，先读取size，然后读取item，重新构建双端链表的数据结构。

##LinkedList当作堆栈，队列

	堆栈：特点，先进后出。入栈：在链表的一端入队。出栈，在同一端出栈。如 addFirst与removeFirst,addLast与removeLast
	队列：特点，先进先出。入队：在链表一端入队。出队，在另一端出队













