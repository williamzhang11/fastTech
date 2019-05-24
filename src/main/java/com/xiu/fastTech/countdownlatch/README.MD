# CountDownLatch
## 介绍
	使用给定计数初始化CountDownLatch，由于调用countDown方法，在当前计数到达0之前，await方法会一直阻塞。之后会释放等待线程计数无法被重置。如果需要重置计数可以使用CyclicBarrier
	CountDownLatch是通过计数器实现的，创建CountDownLatch对象时，需要带计数器的值，该值代表了线程的数量。
	每当一个线程完成自己的任务后，计数器的值减1，当计数器的值变为0时，表示所有线程完成了任务，就可以恢复等待的线程继续执行。
	
## CountDownLatch与CyclicBarrier区别
	1.CountDownLatch作用是允许1或N个线程等待其他线程完成执行，而CyclicBarrier则允许N个线程相互等待
	2.CountDownLatch的计数器无法被重置，CyclicBarrier计数器可被重置后使用。
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/countdownlatch/image/CountDownLach.jpg)	

```
    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

```
## 源码
### sync

```
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }
		//获取同步状态
        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
		//释放同步状态
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

```
### await

await方法使当前线程在计数器至零之前一直等待，除非被中断
```
	
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
    
    public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }
    //计数器不等于0，增调用doAcquireSharedInterruptibly，尝试一直获取同步状态
    protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
    }


```
### countDown

```
	//递减锁存器的计数，到达零则唤醒所有等待线程
    public void countDown() {
        sync.releaseShared(1);
    }
    
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
    
    protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

```







































	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	