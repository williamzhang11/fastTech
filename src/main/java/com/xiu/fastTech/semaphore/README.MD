# 信号量（Semaphore）底层原理
## 介绍
	信号量Semaphore是一个控制访问共享资源的计数器，信号量初始化许可的个数即是当前允许线程同时访问总的个数。
	在获取许可前，会阻塞acquire的线程。每一个release会添加一个许可，并可以释放一个正在阻塞的获取者。
	Semaphore用于限制可以访问某些资源的线程数目
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/semaphore/image/semaphore.jpg)	

	Semaphore内部包含公平锁（FairSync）和非公平锁（NonfairSync）,其同时继承了内部类sync

	当信号量的许可=1时，可以当作互斥锁使用。=1时表示其他线程可以获取，=0时则阻塞其他线程，必须等待。
	
## 源码

### 信号量获取acquire
```
	//获取一个许可
	public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
    //AQS中
    public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
            //小于0阻塞等待，大于0则成功获取
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }
    
    //FairSync公平锁中
     protected int tryAcquireShared(int acquires) {
            for (;;) {
            	//判断当前线程是否位于队列头部
                if (hasQueuedPredecessors())
                    return -1;
                    //获取信号量许可
                int available = getState();
                //设置剩余的许可
                int remaining = available - acquires;
                //CAS设置信号量许可
                if (remaining < 0 ||
                    compareAndSetState(available, remaining))
                    return remaining;
            }
    }
    //非公平锁中
     protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
     }
     //非公平锁中，不需要判断当前线程是否位于队列头部
     final int nonfairTryAcquireShared(int acquires) {
            for (;;) {
            
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 ||
                    compareAndSetState(available, remaining))
                    return remaining;
            }
        }
    //AQS中
    private void doAcquireSharedInterruptibly(int arg)
        throws InterruptedException {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

```

### 信号量释放release

```
    public void release() {
        sync.releaseShared(1);
    }
    
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
    //返回true释放同步状态成功，从而可调用doRealseShared方法唤醒阻塞线程
    protected final boolean tryReleaseShared(int releases) {
            for (;;) {
                int current = getState();
                //信号量的许可数=当前许可数+待释放许可数量
                int next = current + releases;
                if (next < current) // overflow
                    throw new Error("Maximum permit count exceeded");
                if (compareAndSetState(current, next))
                    return true;
            }
    }

```
	































