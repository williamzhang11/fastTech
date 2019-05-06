# ArrayBlockingQueue底层原理
## 描述
ArrayBlockingQueue底层数据结构为数组，是一种有界阻塞队列，其内部主要由ReentrantLock+Condition来进行并发操作的控制

## 源码

### 核心数据变量

```
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    private static final long serialVersionUID = -817911632652898426L;
	//定长数组，用于存储数据
    final Object[] items;
	//队首索引位置
    int takeIndex;
	//队尾索引位置
    int putIndex;
	//数组长度
    int count;

	//锁
    final ReentrantLock lock;
	//非空，出队列条件
    private final Condition notEmpty;
	//未满，入队列条件
    private final Condition notFull;

    transient Itrs itrs = null;
    
	...    
}

```

### 入队

add(E e)方法，将指定元素插入到队列尾部，成功返回true,如果队列已满，抛出IllegalStateException异常
offer(E e)方法，将指定元素插入到队列尾部，成功则返回true,队列满，返回false
offer(E e,long timeout,TumeUnit)，将元素插入到队列尾部，如果队列满，在指定等待时间之前等待可用空间，超时则返回false
put(E e)方法，将指定元素插入队列尾部，如果队列满，则阻塞等待



#### 入队的核心方法

```
	//队尾插入元素
    private void enqueue(E x) {
    	
        final Object[] items = this.items;
        //队尾插入元素
        items[putIndex] = x;
        //如果putIndex+1后，到达队尾，则putIndex回归队头
        if (++putIndex == items.length)
            putIndex = 0;
            //数量增加
        count++;
        //不为空，可进行出队列，通知阻塞在出队列的线程
        notEmpty.signal();
    }

```



#### add
```
    public boolean add(E e) {
        return super.add(e);
    }
    
    //继承的抽象类AbstractQueue中
    public boolean add(E e) {
		//数组未满则返回true
        if (offer(e))
            return true;
        //数组满则返回false,抛出异常      
        else
            throw new IllegalStateException("Queue full");
    }
    //ArrayBlockingQueue类
    public boolean offer(E e) {
    	//检查是否为空，空抛出异常
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        //队列满，返回false
            if (count == items.length)
                return false;
            else {
         //未满，插入到队尾
                enqueue(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

```
#### offer
```

    public boolean offer(E e) {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        //如果队列满，返回false，不是抛出异常
            if (count == items.length)
                return false;
            else {
                enqueue(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

```
#### offer带超时时间
```
    public boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException {

        checkNotNull(e);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
        //队列满了，
            while (count == items.length) {
                if (nanos <= 0)
                    return false;
                    //等待入队列，直到超时后，返回false，在等待期间如果有线程出队列，则不满（notfull），则等待notfull.notify通知.
                    被通知后，重新队列是否满，不满继续执行，退出等待，否则继续等待
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(e);
            return true;
        } finally {
            lock.unlock();
        }
    }
```
#### put

```
	
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
            	//队列满则等待，直到被通知notfull,不满，notFull.notify
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }

```
### 出队

	poll()，移除队头，如果队列为空，返回null
	poll(long timeout, TimeUnit unit),移除队头，如果队列为空，则等待一段时间，时间过后，还是为空则返回null
	take(),移除队头，如果队列为空,则一直等待
	remove(Object o)，队列移除指定元素的某个实例，成功返回true,失败返回false(包括不存在)

#### 核心方法

```
	//队头出队列
	private E dequeue() {
        //去除队首元素
        final Object[] items = this.items;
        @SuppressWarnings("unchecked")
        E x = (E) items[takeIndex];//置空
        items[takeIndex] = null;
        //如果到达队尾，回归队头
        if (++takeIndex == items.length)
            takeIndex = 0;
            //数量减1
        count--;
        
        if (itrs != null)
            itrs.elementDequeued();
            //已经不满了，通知阻塞在notfull.await的线程，可以入队了
        notFull.signal();
        return x;
    }

```

#### poll

```

    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        //队列为空返回null,否则调用dequeue，队头出队列
            return (count == 0) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }
    
```
#### 可超时的poll
```
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
        
            while (count == 0) {
                if (nanos <= 0)
                    return null;
                    //队列为空时阻塞等待一段时间，等待非空调用notEmpty.notify通知，从等待中唤醒后，判断如果count不等于0，则继续向下执行
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

```

#### take

```
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
            //队列为空时，阻塞进入等待状态，直到非空被通知notEmpty.notify,判断是否为空，为空继续等待，否则队首出队列
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

```

#### remove
```
    public boolean remove(Object o) {
        if (o == null) return false;
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count > 0) {
                final int putIndex = this.putIndex;
                int i = takeIndex;
                //循环查找，查找到匹配的位置
                do {
                    if (o.equals(items[i])) {
                        removeAt(i);
                        return true;
                    }
                    if (++i == items.length)
                        i = 0;
                } while (i != putIndex);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    void removeAt(final int removeIndex) {
        final Object[] items = this.items;
        //如果为队头，直接移除
        if (removeIndex == takeIndex) {
            // removing front item; just advance
            items[takeIndex] = null;
            if (++takeIndex == items.length)
                takeIndex = 0;
            count--;
            if (itrs != null)
                itrs.elementDequeued();
                //非队头，移除的同时需要向前拷贝
        } else {
            // an "interior" remove

            // slide over all others up through putIndex.
            final int putIndex = this.putIndex;
            for (int i = removeIndex;;) {
                int next = i + 1;
                if (next == items.length)
                    next = 0;
                if (next != putIndex) {
                    items[i] = items[next];
                    i = next;
                } else {
                    items[i] = null;
                    this.putIndex = i;
                    break;
                }
            }
            count--;
            if (itrs != null)
                itrs.removedAt(removeIndex);
        }
        notFull.signal();
    }
```



