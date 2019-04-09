# ReentrantLock底层原理

	ReentrantLock是一种可重入锁，一种递归无阻塞的同步机制，也叫递归锁，指同一线程外层函数获得锁后，内层函数仍然可以获取该锁。
	ReentrantLock更强大，也更加灵活，可减少死锁的发生。
	ReentrantLock底层是基于AQS的，定义了一套多线程访问共享资源的同步器框架；
	
## 公平锁与非公平锁
	ReentrantLock提供了公平锁与非公平锁，可通过构造函数传入的参数选择。
	公平锁与非公平锁的区别：公平锁的锁获取是有顺序的。
	1.公平锁每次获取锁为同步队列中的第一个节点，保证请求资源时间上的绝对顺序，非公平锁可能刚释放锁的线程下次继续获取该锁，
	导致其他线程永远无法获取到锁，造成饥饿现象
	2.公平锁保证时间上的绝对顺序，需要频繁上下文切换，非公平锁降低一定的上下文切换，降低性能开销。因此ReetrantLock默认选择
	非公平锁，保证了系统更大的吞吐量。

## 整体结构图
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/reentrantlock/image/reentrantlock.jpg)
	
	ReentrantLock实现了Lock接口，基于内部类Sync实现的
	Sync 实现了AQS,提供了FairSync和NonFairSync两种实现

```
	public class ReentrantLock implements Lock, java.io.Serializable {
		...
		private final Sync sync;
	    abstract static class Sync extends AbstractQueuedSynchronizer {
	    ...
		}
		
		static final class NonfairSync extends Sync {
		...
		}
		static final class FairSync extends Sync {
		}
		
		public ReentrantLock() {
       		 sync = new NonfairSync();
   	 	}
   	 	
   	 	public ReentrantLock(boolean fair) {
       	 	sync = fair ? new FairSync() : new NonfairSync();
    	}
   	 	...

	}

```
### Sync抽象类
是ReentrantLock的内部静态类，实现AbstractQueuedSynchronizer抽象类。使用AQS的state字段，表示
当前锁的持有数量。

```
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;
		//抽象了该方法，允许子类不同的实现逻辑
        abstract void lock();
		//非公平锁获取锁
        final boolean nonfairTryAcquire(int acquires) {
        	//当前线程
            final Thread current = Thread.currentThread();
            //获取同步状态
            int c = getState();
            //state ==0说明该锁没有被线程持有，处于空闲状态
            if (c == 0) {
            //获取锁成功，设置当前线程所有
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            //可重入，锁持有的线程是否是当前线程
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
	
        protected final boolean tryRelease(int releases) {
        
            int c = getState() - releases;
            //判断线程的锁释放是否当前线程，保证线程安全
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            //state==0时，完全释放了资源，其他线程可获取同步状态
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
		//当前线程是否独占
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }
		//新生成条件
        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        // Methods relayed from outer class
		//获得占用同步状态的线程
        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }
		//获得当前线程持有锁的数量
        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }
		//是否被锁定
        final boolean isLocked() {
            return getState() != 0;
        }
		//自定义反序列化
        private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }
```
	从上面可以看出，ReentrantLock是独占获取同步状态的模式

### NonfairSync
	NonfairSync是ReentrantLock的内部静态类，实现了Sync抽象类，非公平锁实现类
	
```

    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        final void lock() {
        	//基于AQS state 进行CAS操作，如果成功获取锁成功，失败则执行AQS的正常同步状态，这里是非公平锁的实现逻辑
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }
		//非公平锁的实现获取锁
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

```

### FairSync

公平锁实现类

```
 static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }
		//公平的方式，获取同步状态
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }
```

	公平锁与非公平锁的区别是,公平锁在获取同步状态（state）时多了个hasQueuedPredecessors(),是否有前序节点，
	如果没有前序节点，即可获取到同步状态，如果有的话获取不到同步状态，需要进入同步队列等待。
	非公平锁，直接尝试获取同步状态

### 其他重要方法
```
	...
	//默认非公平锁
    public ReentrantLock() {
        sync = new NonfairSync();
    }
	//用于指定公平锁或非公平锁
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
	//一直阻塞直到获取到锁
    public void lock() {
        sync.lock();
    }
    //一直阻塞直到获取到锁，可响应中断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    //非公平锁，尝试获取锁，获取到返回true,否则返回false
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }
    //在指定的时间内阻塞尝试获取锁，超时则返回false
    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
    //释放锁，如果state==0 完全释放锁，并唤醒下一个线程
    public void unlock() {
        sync.release(1);
    }
    
	public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
    
    protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
    }
	//可用于线程间的同步通信
    public Condition newCondition() {
        return sync.newCondition();
    }
    //获取当前锁的同步状态值
    public int getHoldCount() {
        return sync.getHoldCount();
    }

	...
```

### ReentrantLock与synchronized的区别
	1.ReentrantLock提供了比synchronized更全面的功能，具备更强的扩展性。如时间锁等待(tryLock(long timeout, TimeUnit unit))、可中断锁（lockInterruptibly()）
	2.ReentrantLock提供了条件Condition，对线程的等待，唤醒更加详细，灵活，在多个条件变量和高度竞争锁的地方，ReentrantLock更适合
	3.ReentrantLock提供可轮询的锁请求，它会尝试获取锁，如果成功则继续，否则等到下次运行时处理。而synchronized一旦进入锁请求要么成功要么阻塞
	4.ReentrantLock支持更加灵活的同步代码，使用synchronized时，只能在同一个synchronized块结构获取和释放。ReentrantLock的锁释放一定要在finally中
	5.ReentrantLock支持中断，性能好些


















