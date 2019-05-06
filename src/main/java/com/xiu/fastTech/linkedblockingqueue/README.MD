# LinkedBlockingQueue队列
	阻塞队列也是线程同步的一种工具
## 原理
	LinkedBlockingQueue底层是基于单向链表的，如果未制定容量，则大小为Integer.MAX_VALUE,约为21亿个
## 源码
### 核心数据结构
```
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
        
    private static final long serialVersionUID = -6903933977591709194L;

	//链表中的节点
    static class Node<E> {
        E item;
        Node<E> next;

        Node(E x) { item = x; }
    }
	//容量,默认的初始化大小为Integer.MAX_VALUE
    private final int capacity;
	//当前的元素个数
    private final AtomicInteger count = new AtomicInteger();

	//头节点
    transient Node<E> head;
	//尾节点
    private transient Node<E> last;

	//出队锁
    private final ReentrantLock takeLock = new ReentrantLock();
	//唤醒阻塞在非空，notEmpty.await的线程，等待队列，出队
    private final Condition notEmpty = takeLock.newCondition();
	//入队锁
    private final ReentrantLock putLock = new ReentrantLock();
	//等待队列，入队
    private final Condition notFull = putLock.newCondition();
    //未指定容量，默认最大值
    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }
    
    public LinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

```
### put(e)
队列满时一直阻塞等待
```
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        int c = -1;//局部变量表示队列元素数量
        //新建节点
        Node<E> node = new Node<E>(e);
        //
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        //获取入队锁，响应中断
        putLock.lockInterruptibly();
        try {
        //队列满则一直阻塞等待
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            //数量+1
            c = count.getAndIncrement();
            //如果队列未满，唤醒入队线程
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        //队列中数量为1，唤醒等待出队列线程
        if (c == 0)
            signalNotEmpty();
    }
    //入队
    private void enqueue(Node<E> node) {
        last = last.next = node;
    }
    
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        //获取出队锁
        takeLock.lock();
        try {
        //通知线程出队列
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

```
### offer(e，timeout,unit)
入队，该方法返回true/false，队列满时，会阻塞一段时间，添加成功返回true，否则返回false
```
    public boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException {

        if (e == null) throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == capacity) {
                if (nanos <= 0)
                    return false;
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<E>(e));
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
        return true;
    }

```
### offer(e)
该方法返回true/false,不会阻塞，直接返回
```
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        final AtomicInteger count = this.count;
        if (count.get() == capacity)
            return false;//队列满，返回false
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if (count.get() < capacity) {
                enqueue(node);
                c = count.getAndIncrement();
                if (c + 1 < capacity)
                    notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
        return c >= 0;
    }

```

### take()
从队列中取出元素时，如果队列为空，则阻塞
```
    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        //出队列锁
        takeLock.lockInterruptibly();
        try {
        //如果为空，一直阻塞
            while (count.get() == 0) {
                notEmpty.await();
            }
            //出队列
            x = dequeue();
            c = count.getAndDecrement();
            //不为空，通知线程出队列
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }
    
    private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }
    
```
### poll(timeout,unit)
取出元素时，如果队列为空，阻塞给定的时间，如果阻塞的时间内容有数据，则取出，否则返回null。
```
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }

```
### poll()
从队列取出时，如果队列为空则直接返回null
```
    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0)
            return null;
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = dequeue();
                c = count.getAndDecrement();
                if (c > 1)
                    notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }
    
```

### peek()
只返回队头元素的值，不能将节点从队列删除
```
    public E peek() {
        if (count.get() == 0)
            return null;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> first = head.next;
            if (first == null)
                return null;
            else
                return first.item;
        } finally {
            takeLock.unlock();
        }
    }

```










