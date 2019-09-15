# 队列同步器（AQS）底层原理

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/aqs/image/aqs.jpg)
	AQS全称：AbstractQueuedSynchronizer，抽象队列式同步器，它定义了一套多线程访问共享资源的同步器框架，许多同步类依赖它
	如：ReentrantLock,Semaphore,CountDownLatch...
	底层数据结构：双向队列
	它维护了一个volatile int state(代表资源)和FIFO线程等待队列（多线程争用资源被阻塞时会进入此队列）
	
	AQS中定义2种资源共享方式：Exclusive（独占，只有一个线程能执行，如ReentrantLock）
	和Share(共享，多个线程可同时执行，如Semaphore,CountDownLatch)
	AQS实现了具体线程等待队列的相关操作，自定义同步器时，只需要实现共享资源state的获取和释放。
	isHeldExclusively():该线程是否独占资源，只有用到condition才需要实现
	tryAcquire(int):独占方式，尝试获取资源，成功返回true,失败返回false
	tryRelease(int):独占方式，尝试释放资源，成功返回true,失败返回false
	tryAcquireShared(int):共享方式，尝试获取资源，负数表示失败，0成功，但没有剩余可用资源。正数成功，有剩余资源
	tryReleaseShared(int):共享方式，尝试释放资源，释放后允许唤醒后续等待节点返回true,否则返回false.
	
	如：ReentrantLock，state初始化0，表示未锁定状态，A线程lock时，会调用tryAcquire()独占该锁将state+1。此后其他线程tryAcquire()失败
	，直到线程A unlock到state=0,其他线程才有机会获得锁。释放锁之前，A线程可以重复获取此锁（state会累加），即可重入。但获取多少次就要释放多少次
	保证state回到零态
	如：countDownLatch，任务分为N个子线程去执行，state初始化N,这N个子线程并行执行，每个子线程执行完countDown一次，state减一。等所有子线程
	都执行完后（即state=0）,会unpark()主线程，然后主线程从await函数返回，继续后续执行。
	AQS支持自定义同步器实现独占和共享，如ReentrantReadWriteLock
	
## 源码
### acquire(int)
 该方法是是独占模式下线程获取共享资源的顶层入口。主要逻辑是：如果获取到资源，线程直接返回，可以执行临界区代码。否则进入等待队列，直到获取到资源为止。
 该方法在整个过程中忽略中断的,获取到资源后才进行自我中断，将中断补上.过程中响应中断的方法请使用acquireInterruptibly()
 
 ```
     public final void acquire(int arg) {
     	//尝试获取资源，如果成功则返回,具体资源的获取/释放方式由自定义同步器去实现
        if (!tryAcquire(arg) &&
        //使线程在等待队列中获取资源，直到获取到资源才返回。如果在等待过程中被中断过返回true,否则返回false
            acquireQueued(
            //将该线程加入等待队列尾部，标记独占模式
            addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
    //之所以没有定义成abstract，因为独占模式下只需要实现tryAcquire-tryRelease,共享模式下只实现
    tryAcquireShared-tryReleaseShared.如果都定义成abstract,每个模式都要实现另一模式。
     protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }
    //用于将当前线程加入等待队列尾部，并返回当前线程的节点
    private Node addWaiter(Node mode) {
    //构造节点
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        //尾节点不为空时，快速尝试，将当前节点，放入队列尾部
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
            //成功，原尾节点的下一个节点为新节点
                pred.next = node;
                return node;
            }
        }
        //多次尝试，直至成功
        enq(node);
        return node;
    }
    
    private Node enq(final Node node) {
    //cas自旋，直至成功加入尾部
        for (;;) {
            Node t = tail;
            //原尾节点不存在，创建新的尾节点Node
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
            //设置尾节点
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
    //是一个自旋过程，当前线程进入同步队列后，进入自旋过程，每个节点自省观察，当条件满足并且获取资源后，从自旋退出。
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;//标记是否拿到资源
        try {
            boolean interrupted = false;//标记是否中断过
            //自旋
            for (;;) {
            	//获取当前节点的前驱
                final Node p = node.predecessor();
                //如果前驱是head,并且获取资源后
                if (p == head && tryAcquire(arg)) {
                	//将当前节点设置为head节点
                    setHead(node);
                    //老的头节点p不再指向下一个节点，让它自身更快GC
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                //条件不满足或获取资源失败进入线程等待，直到被unpark()
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    //检查是否满足条件去等待，只要前驱节点的状态不是SIGNAL就不能休息。
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;//获取前驱状态
        if (ws == Node.SIGNAL)//如果前驱处于唤醒状态，当前节点可以安全的阻塞等待
            return true;
            //如果前驱放弃了，就一直往找，直到找到一个正常的等待状态，
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
        //0,PROPAGATE状态,如果前驱正常，就把前驱状态设置为SIGNAL
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
    
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);//调用park使当前线程进入waiting状态
        return Thread.interrupted();//查看是否被中断
    }
    
    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }
 
 ```
 Node节点是对每一个访问同步代码的线程的封装，包含了需要同步的线程本身，线程状态（是否被阻塞，是否等待唤醒，是否被取消）等
 Node中的WaitStatus表示当前被封装成Node节点线程的状态，waitStatus > 0表示取消状态，waitStatus<0表示有效状态：
```
static final class Node {

        static final Node SHARED = new Node();
        
        static final Node EXCLUSIVE = null;

		//因为超时或中断，节点被设置为取消状态，被取消的节点不会参与竞争中，会一直保持取消状态，不会转变其他状态
        static final int CANCELLED =  1;
        //处于唤醒状态，只要前继节点释放锁，会通知标记为SIGNAL状态的后继节点的线程执行
        static final int SIGNAL    = -1;
        //该标识的节点在等待队列中，节点线程等待在condition上，当其他线程对condition调用signal()后，该节点从等待队列转移到
        //同步队列中，等待获取同步锁。
        static final int CONDITION = -2;
        //与共享模式相关，共享模式中，该状态标识的线程处于可运行状态
        static final int PROPAGATE = -3;
        
		//等待状态，初始化为0
        volatile int waitStatus;
		//前一个节点
        volatile Node prev;
		//后一个节点
        volatile Node next;

        volatile Thread thread;

        Node nextWaiter;

        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

```
  	acquire()整体流程：
	1.调用自定义同步器tryAcquire()尝试直接获取资源，如果成功直接返回
	2.没有成功，addWaiter()将该线程加入等待队列尾部，标记为独占模式
	3.acquireQueued()使线程在等待队列中休息(阻塞)，有机会时（轮到自己会被unpark）尝试获取资源，获取资源后才返回。
	4.如果线程在等待过程中被中断过，它不响应，获取资源后，再进行自我中断selfInterrupt(),将中断补上
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/aqs/image/acquire.jpg)

### release(int )
该方法是独占模式下线程释放共享资源的顶层入口。它会释放指定量的资源，如果彻底释放了，它会唤醒等待队列里其他线程获取资源。

```
    public final boolean release(int arg) {
        if (tryRelease(arg)) {//尝试释放指定量的资源
            Node h = head;//获取头节点
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);//唤醒后续节点
            return true;
        }
        return false;
    }
    
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }
    
    private void unparkSuccessor(Node node) {
    	//当前节点状态
        int ws = node.waitStatus;
        //如果小于0,设置为0
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);
		//当前节点的后继节点
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {//如果为空或者取消
            s = null;//置为null
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)//<=0的节点还是有效节点
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);//唤醒，unpark唤醒等待队列中最前边未放弃线程。
    }
    

```
 
### acquireShared(int)
该方法是共享模式下线程获取共享资源的顶级入口。它会获取指定量的资源，获取成功直接返回，失败进入等待队列，直到获取资源为止。
流程：尝试获取资源，成功则返回，失败进入等待队列，直到获取资源返回。
```
    public final void acquireShared(int arg) {
    	//尝试获取资源，成功则返回
        if (tryAcquireShared(arg) < 0)
        //失败进入等待队列，将当前线程加入等待队列尾部休息，直到其他线程释放资源唤醒自己，并且成功拿到相应量资源后返回。
            doAcquireShared(arg);
    }
    //需要自定义同步器去实现，AQS已经定义返回值的语义：负值代表失败，0代表成功，但没剩余资源
    //正数代表获取成功，还有剩余资源，其他线程可以去获取
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }
    
    private void doAcquireShared(int arg) {
    	//加入队列尾部
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;//是否成功标志
        try {
            boolean interrupted = false;//等待过程中是否被中断过标志
            for (;;) {
                final Node p = node.predecessor();//获取当前节点前驱
                if (p == head) {//如果是head的下一个节点，因为head拿到资源的线程，此时node被唤醒，很可能是head用完资源唤醒自己
                    int r = tryAcquireShared(arg);//获取资源
                    if (r >= 0) {//成功
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }
                //判断状态，寻找安全点，进入阻塞状态，等待被唤醒
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node);//head指向自己
        if (propagate > 0 || h == null || h.waitStatus < 0 ||
            (h = head) == null || h.waitStatus < 0) {//如果还有剩余量，继续唤醒下一个线程
            Node s = node.next;
            if (s == null || s.isShared())
                doReleaseShared();
        }
    }

```
 
 共享模式下，只有线程是head的后继节点，才会尝试获取资源。有剩余的话会唤醒之后的线程。独占模式下，同一时刻只有一个线程执行，共享模式下，
 多个线程可同时执行。AQS保证严格按照入队顺序唤醒，保证公平。
 因此共享模式与独占模式的区别是,共享模式线程拿到资源后，会唤醒后继线程尝试获取资源.
 
 ### releaseShared()
 该方法是共享模式下线程释放共享资源的顶层入口，它会释放指定量资源，如果成功释放且允许唤醒等待线程，它会唤醒等待队列的其他线程获取资源。
 
```
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {//尝试释放资源
            doReleaseShared();
            return true;
        }
        return false;
    }
    
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }
    private void doReleaseShared() {
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;            // loop to recheck cases
                    unparkSuccessor(h);//唤醒后续节点
                }
                else if (ws == 0 &&
                         !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue;                // loop on failed CAS
            }
            if (h == head)                   // loop if head changed
                break;
        }
    }

```
	独占模式下的tryRelease在完全释放资源（state=0）后，才会返回true去唤醒其他线程。
	共享模式下的releaseShared没有这种要求共享模式实质是控制一定量的线程并发执行，拥有资源的线程
	在释放掉部分资源时可以唤醒后续等待节点。
	如：资源总量13，A(5),B（7）分别获取到资源并发运行，C(4)来时只剩1个资源，需要等待。
	A在运行中释放2个资源，然后tryReleaseShared(2)返回true唤醒c,C看到3个，不够4个继续等待；
	然后B又释放2个，tryReleaseShared(2)返回true唤醒C,C看到可以使用，就和A,B一起运行。
 
 
 参考https://www.cnblogs.com/waterystone/p/4920797.html
 
 
 https://www.jianshu.com/p/282bdb57e343
 https://javadoop.com/2017/07/20/AbstractQueuedSynchronizer
 
 
## AQS之独占锁模式

首先先从整体流程入手，了解下AQS独占锁的执行逻辑，然后再一步一步深入分析源码。

获取锁的过程：

1.当线程调用acquire()申请获取锁资源，如果成功，则进入临界区。

2.当获取锁失败时，则进入一个FIFO等待队列，然后被挂起等待唤醒。

3.当队列中的等待线程被唤醒以后就重新尝试获取锁资源，如果成功则进入临界区，否则继续挂起等待。

释放锁过程：

1.当线程调用release()进行锁资源释放时，如果没有其他线程在等待锁资源，则释放完成。
如果队列中有其他等待锁资源的线程需要唤醒，则唤醒队列中的第一个等待节点（先入先出）。

## AQS之共享锁模式

获取锁的过程：

1.当线程调用acquireShared()申请获取锁资源时，如果成功，则进入临界区。

2.当获取锁失败时，则创建一个共享类型的节点并进入一个FIFO等待队列，然后被挂起等待唤醒。

3.当队列中的等待线程被唤醒以后就重新尝试获取锁资源，如果成功则唤醒后面还在等待的共享节点并把该唤醒事件传递下去，即会依次唤醒在该节点后面的所有共享节点，然后进入临界区，否则继续挂起等待。

释放锁过程：

1.当线程调用releaseShared()进行锁资源释放时，如果释放成功，则唤醒队列中等待的节点，如果有的话。
 
 
非公平锁实现：

C直接尝试对这个status CAS(0,1)操作，并成功改变了status的值，B线程获取锁失败，再次挂起，这就是非公平锁，B在C之前尝试获取锁，而最终是C抢到了锁。
公平锁：

C发现有线程在等待队列，直接将自己进入等待队列并挂起,B获取锁
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



















