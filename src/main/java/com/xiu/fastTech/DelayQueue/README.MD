# DelayQueue原理
## 数据结构
DelayQueue是一种支持延时获取元素的无界阻塞队列。对头的元素先到期，如果队列中的元素没有到期则不能从队头获取元素。
DelayQueue是基于PriorityQueue实现的，而PriorityQueue是基于二叉堆实现的，它是支持优先级的无界队列，二叉堆是数组实现的。

## 源码
### 核心变量

```
public class DelayQueue<E extends Delayed> extends AbstractQueue<E>
    implements BlockingQueue<E> {
	//可重入锁
    private final transient ReentrantLock lock = new ReentrantLock();
    //存储数据的无界队列
    private final PriorityQueue<E> q = new PriorityQueue<E>();
	//优化阻塞队列
    private Thread leader = null;
    //条件
    private final Condition available = lock.newCondition();
    
    ...

```
### offer方法
```
    public boolean offer(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        	//向优先级队列中插入元素
            q.offer(e);
            //如果当前为队首元素
            if (q.peek() == e) {
                leader = null;
            //则唤醒等待出队列的线程
                available.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
    //PariorityQueue中
      public boolean offer(E e) {
      //二叉堆中不允许插入的数据为空
        if (e == null)
            throw new NullPointerException();
            //记录修改次数
        modCount++;
        int i = size;
        //判断是否需要扩容
        if (i >= queue.length)
            grow(i + 1);
        size = i + 1;
        //队首则直接入队列
        if (i == 0)
            queue[] = e;//取第一个
            //否则需要根据二叉堆的特性进行入队列
        else
            siftUp(i, e);
        return true;
    }
    
    private void siftUp(int k, E x) {
        if (comparator != null)
            siftUpUsingComparator(k, x);
        else
            siftUpComparable(k, x);
    }
    //可根据比较器进行入二叉堆
        @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

```
### take()方法

```
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
            //获取队首元素，
                E first = q.peek();
                //为空则阻塞
                if (first == null)
                    available.await();
                else {
                	//获取队首元素的超时时间
                    long delay = first.getDelay(NANOSECONDS);
                    //超时则出队列
                    if (delay <= 0)
                        return q.poll();
                    first = null; // 不保存队首元素的引用
                    //leader，不为空，说明有其他线程正在操作
                    if (leader != null)
                        available.await();
                    else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                        //未超时则阻塞等待
                            available.awaitNanos(delay);
                        } finally {
                        //等待结束后则置空
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
        //队列不为空，并且没有其他线程访问则唤醒其他等待线程
            if (leader == null && q.peek() != null)
                available.signal();
            lock.unlock();
        }
    }
```


