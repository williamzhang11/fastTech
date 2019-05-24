# PriorityBlockingQueue

	PriorityBlockingQueue是一个支持优先级的无界阻塞队列。默认情况下元素采用自然顺序升序排序，也可以通过
	构造函数指定Comparator元素进行排序。
## 数据结构
	PriorityBlockingQueue底层采用二叉堆实现的,分为最大堆，最小堆,而堆通常是数组实现的。
	最大堆:父节点的键值总是大于或等于任何一个子节点的键值
	最小堆：父节点的键值总是小于或等于任何一个子节点的键值
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/priorityblockingqueue/image/dui.jpg)


	因此PriorityBlockingQueue操作主要涉及堆的增删改查
	
## 源码
### 内部数据结构
```
	public class PriorityBlockingQueue<E> extends AbstractQueue<E>
	    implements BlockingQueue<E>, java.io.Serializable {
		
		//默认容量
		private static final int DEFAULT_INITIAL_CAPACITY = 11;
		//最大容量
    	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
		//二叉堆数组
		private transient Object[] queue;
		//队列元素个数
		private transient int size;
		//指定的比较器，为空则默认为自然顺序
	    private transient Comparator<? super E> comparator;
		//内部锁
	    private final ReentrantLock lock;
	
	    private final Condition notEmpty;
	
	    private transient volatile int allocationSpinLock;
		
	    private PriorityQueue<E> q;
		
	    public PriorityBlockingQueue() {
	        this(DEFAULT_INITIAL_CAPACITY, null);
	    }
	
	    public PriorityBlockingQueue(int initialCapacity) {
	        this(initialCapacity, null);
	    }
	
	    public PriorityBlockingQueue(int initialCapacity,
	                                 Comparator<? super E> comparator) {
	        if (initialCapacity < 1)
	            throw new IllegalArgumentException();
	        this.lock = new ReentrantLock();
	        this.notEmpty = lock.newCondition();
	        this.comparator = comparator;
	        this.queue = new Object[initialCapacity];
	    }
			    	    
		}

```

### 入队

```
	
    public void put(E e) {
        offer(e); // never need to block
    }
    
    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        final ReentrantLock lock = this.lock;
        //获取锁
        lock.lock();
        int n, cap;
        Object[] array;
        //扩容
        while ((n = size) >= (cap = (array = queue).length))
            tryGrow(array, cap);
        try {
            Comparator<? super E> cmp = comparator;
            //根据比较器是否为空，进行不同操作
            if (cmp == null)
                siftUpComparable(n, e, array);
            else
                siftUpUsingComparator(n, e, array, cmp);
            size = n + 1;
           	// 唤醒等待消费线程
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
        return true;
    }

```

PriorityBlockingQueue采用二叉堆维护，添加操作不断上冒，删除操作不断下掉。





	
	