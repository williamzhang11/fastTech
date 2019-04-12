# ReentrantReadWriteLock 底层原理
	
	ReentrantLock是可重入的排它锁，同一时刻仅有一个线程可以进行访问，大多数场景下，大部分读服务多于写服务，读服务之间不存在数据竞争，
	因此提供了ReentrantReadWriteLock读写锁。
	ReentrantReadWriteLock维护着一对锁，一个读锁，一个写锁。通过这种读写锁分离的方式，使得并发性比一般的排它锁高。
## 特点：
	1.同一个时间，允许多个线程同时获取读锁。但写线程会堵塞读线程和写线程，同时读线程只会阻塞写线程。
	2.可重入性上，不允许锁升级的操作，同一个线程进入一个读锁方法，可以进入另一个读锁方法，同一个线程，进入一个写锁方法，同时可以进入
	      另一个写锁方法，或者读锁方法。
	3.同时支持公平锁与非公平锁。公平锁：锁的获取，如果等待队列中有线程正在等待获取锁，则需要排队获取。而非公平锁，
	     不管等待队列中有没有线程等待，直接尝试获取锁。
	4.支持锁降级：首先获取写锁，然后可继续获取读锁，最后释放写锁，这时就完成了锁降级。锁降级最大的优点就是降低锁的竞争，
	     提高性能，其他需要只需要读锁的线程，可直接获取读锁，达到并发读目的。
	     
	ReentrantReadWriteLock的底层数据结构：AQS(基于双向队列的同步器)，AQS留给子类实现的是tryAcquire（独占锁）或tryAcquireShared（共享锁）方法
## 代码结构：

```
public class ReentrantReadWriteLock
        implements ReadWriteLock, java.io.Serializable {
    //读锁
	private final ReentrantReadWriteLock.ReadLock readerLock;
	//写锁
	private final ReentrantReadWriteLock.WriteLock writerLock;
	
    final Sync sync;        
	//默认非公平锁
    public ReentrantReadWriteLock() {
        this(false);
    }
    //选择公平锁还是非公平锁配置
    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }
	
    public ReentrantReadWriteLock.WriteLock writeLock() { return writerLock; }
    public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }
        
    //同步器的部分实现
    abstract static class Sync extends AbstractQueuedSynchronizer {   
    	... 
    }
    //非公平锁的实现部分
    static final class NonfairSync extends Sync {
        ...
    }
    //公平锁的实现部分
    static final class FairSync extends Sync {
    	...
    }
    //读锁实现
    public static class ReadLock implements Lock, java.io.Serializable {
    	...
    }
    //写锁实现
    public static class WriteLock implements Lock, java.io.Serializable {
    	...
    }
    ...
}

```
	基于AQS的锁都是通过state控制同步状态的，在ReentrantLock中state可表示被一个线程重复获取的次数。
	而在ReentrantReadWriteLock中维护了一对读写锁，用一个变量维护多个状态，需要采用“按位切割使用”的方法，
	高16位表示读，低16为表示写。
```

    abstract static class Sync extends AbstractQueuedSynchronizer {
		//位数
        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        //最大重入次数65535
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
		//获取高16位，即读锁状态，由于读锁，可以被多个线程同时持有，每个线程同样支持重入性，因此需要为每个线程的持有锁数量都计数，这里使用到HoldCounter计数器
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        //获取低16位，即写锁状态，由于写锁只能被一个线程独占，因此该方法是获取写锁的重入次数
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
        
        //共享锁专用计数器，获取共享锁，该计数器+1，释放则-1。记录当前线程持有共享锁的数量（可重入的），这个数量与线程绑定。
        static final class HoldCounter {
            int count = 0;//计数
            final long tid = getThreadId(Thread.currentThread());//当前线程编号
        }
		//与线程绑定
        static final class ThreadLocalHoldCounter
            extends ThreadLocal<HoldCounter> {
            public HoldCounter initialValue() {
                return new HoldCounter();
            }
        }
		//缓存当前线程的读锁重入次数的ThreadLocal
        private transient ThreadLocalHoldCounter readHolds;
        //最后一个获取到读锁的线程计数器，每当有新的线程获取到读锁，该变量会更新。目的是，当最后
        	一个获取读锁的线程重复获取读锁，或释放读锁，可直接使用该变量，速度很快。
        private transient HoldCounter cachedHoldCounter;
        //获取读锁的第一个线程，如果只有一个线程获取读锁，速度很快
        private transient Thread firstReader = null;
        //获取读锁的第一个线程的锁的计数器
        private transient int firstReaderHoldCount;
        
	...
}


```
## 写锁加锁
new ReentrantReadWriteLock().writeLock().lock()

```
		//锁方法
        public void lock() {
            sync.acquire(1);
        }
        //AQS中定义的加独占锁模板方法 
        public final void acquire(int arg) {
        //
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    	}
    	//获取同步状态资源，尝试获取写锁
    	protected final boolean tryAcquire(int acquires) {
    	
            Thread current = Thread.currentThread();
            //状态值
            int c = getState();
            //获取写锁对应的值
            int w = exclusiveCount(c);
            //第一步，判断锁是否空闲
            if (c != 0) {
             	//第二步：
            	//c!=0,w==0 说明当前线程已经持有读锁，不能继续获取写锁
            	 c!=0,w!=0,需要继续判断持有写锁的是不是当前线程，如果是获取可重入锁。如果不是当前线程，不允许继续获取写锁。
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                setState(c + acquires);
                return true;
            }
            //第三步：
            //根据是否公平锁，继续判断如果是非公平锁使用CAS操作修改state变量，成功后则修改AQS持有线程。
            //如果是公平锁，查看等待队列中是否还有前驱，如果有则放弃尝试，如果没有则继续使用CAS操作。
            if (writerShouldBlock() ||
                !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }

```
	流程：
	1.判断锁是否空闲
	2.如果空闲，根据公平锁还是非公平锁，进行判断是否需要使用CAS修改state变量，获取锁。成功则修改AQS持有线程
	3.如果不空闲，判断写锁是否空闲
		如果写锁空闲，但state不是0，说明有读锁，不能获取写锁
		如果写锁不空闲，判断持有锁的线程是否是当前线程，如果不是，不能获取，是的话，获取重入锁。
## 写锁解锁
new ReentrantReadWriteLock().writeLock().unlock()	
	
```
		//写锁的解锁
        public void unlock() {
            sync.release(1);
        }
        //AQS中的模板释放锁方法
        public final boolean release(int arg) {
	        if (tryRelease(arg)) {
	            Node h = head;
	            if (h != null && h.waitStatus != 0)
	                unparkSuccessor(h);
	            return true;
	        }
	        return false;
    	}
    	//获取同步状态
    	protected final boolean tryRelease(int releases) {
    	//持有写锁的是否是当前线程
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
        	//计算state值
            int nextc = getState() - releases;
            boolean free = exclusiveCount(nextc) == 0;
            //写锁状态如果是0，设置持有锁的线程为null
            if (free)
                setExclusiveOwnerThread(null);
                //设置写锁的状态
            setState(nextc);
            return free;
        }
```
## 读锁加锁
读锁是获取共享锁的过程
new ReentrantReadWriteLock().readLock().lock()

```
		//加锁方法
        public void lock() {
            sync.acquireShared(1);
        }
        //AQS的模板加共享锁方法
	    public final void acquireShared(int arg) {
	        if (tryAcquireShared(arg) < 0)
	            doAcquireShared(arg);
	    }
    	
    	protected final int tryAcquireShared(int unused) {
            Thread current = Thread.currentThread();
            int c = getState();
            //写锁被其他线程获取，返回false，获取锁失败
            if (exclusiveCount(c) != 0 &&
                getExclusiveOwnerThread() != current)
                return -1;
            int r = sharedCount(c);
            //写锁没有被其他线程获取，根据公平与否，是否通过cas，修改state值
            if (!readerShouldBlock() &&
                r < MAX_COUNT &&
                compareAndSetState(c, c + SHARED_UNIT)) {
                //读锁是空闲的，获取锁成功
                if (r == 0) {
                //当前线程设置为第一个读锁线程
                    firstReader = current;
                    //读锁计数器=1
                    firstReaderHoldCount = 1;
                    //读锁不为空，且第一个读线程是当前线程，获取锁成功，重入锁
                } else if (firstReader == current) {
                	//计数器增加
                    firstReaderHoldCount++;
                    //如果不是第一个线程，获取锁成功，其他线程获取读锁
                } else {
               	 	//cachedHoldCounter代表上个获取读锁的线程计数器
                    HoldCounter rh = cachedHoldCounter;
                    //如果最后一个线程计数器是null或不是当前线程，就新建一个HoldCounter对象
                    
                    if (rh == null || rh.tid != getThreadId(current))
                    	//当前线程就是最后获取锁的线程，将当前线程的holdCounter赋值给cachedHoldCounter
                        cachedHoldCounter = rh = readHolds.get();
                        //如果不是null,且为0，就将上个线程的holdCounter覆盖本地
                    else if (rh.count == 0)
                    //缓存当前线程的holdCounter
                    //fullTryAcquireShared()方法中，获取读锁失败的线程会执行readHolds.remove()因此需要重新设置
                        readHolds.set(rh);
                    rh.count++;
                }
                return 1;
            }
            //首次获取读锁失败，重新尝试
            return fullTryAcquireShared(current);
        }

        final int fullTryAcquireShared(Thread current) {
            HoldCounter rh = null;
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current)
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock.
                } else if (readerShouldBlock()) {
                    // Make sure we're not acquiring read lock reentrantly
                    if (firstReader == current) {
                        // assert firstReaderHoldCount > 0;
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0)
                                    readHolds.remove();
                            }
                        }
                        if (rh.count == 0)
                            return -1;
                    }
                }
                if (sharedCount(c) == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null)
                            rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release
                    }
                    return 1;
                }
            }
        }
```
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/reentrantreadwritelock/image/ReentrantReadWriteLock.jpg)

## 读锁解锁

new ReentrantReadWriteLock().readLock().unlock()
	
```
        public void unlock() {
            sync.releaseShared(1);
        }
        
        public final boolean releaseShared(int arg) {
	        if (tryReleaseShared(arg)) {
	            doReleaseShared();
	            return true;
	        }
	        return false;
    	}
    	
    	protected final boolean tryReleaseShared(int unused) {
            Thread current = Thread.currentThread();
            //如果是第一个线程
            if (firstReader == current) {
            //如果是1，将第一个线程设置null
                if (firstReaderHoldCount == 1)
                    firstReader = null;
                else
                //不是减一
                    firstReaderHoldCount--;
            } else {
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    rh = readHolds.get();
                int count = rh.count;
                if (count <= 1) {
                    readHolds.remove();
                    if (count <= 0)
                        throw unmatchedUnlockException();
                }
                --rh.count;
            }
            for (;;) {//CAS修改状态
                int c = getState();
                int nextc = c - SHARED_UNIT;//读锁减一操作
                if (compareAndSetState(c, nextc))
                    return nextc == 0;//如果是0，成功，会唤醒后面等待线程
            }
        }

```
	参考：https://www.jianshu.com/p/6221746fc777
		https://www.jianshu.com/p/cd485e16456e
		http://www.iocoder.cn/JUC/sike/ReentrantReadWriteLock/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	