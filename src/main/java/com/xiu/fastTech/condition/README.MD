# Lock中的Condition底层原理

Lock中提供了Condition,对线程的等待和唤醒操作，更加灵活。
ConditionObject 是AQS的内部类，是Condition接口的实现

```
    public class ConditionObject implements Condition, java.io.Serializable {
    
        private transient Node firstWaiter;
        
        private transient Node lastWaiter;
    	...
    }

```

## 实现原理

AQS等待队列和Condition队列是2个相互独立的队列
await()是在当前线程持有锁的基础上释放锁资源，然后新建Condition节点，加入到Condition队列尾部，阻塞当前线程
signal()是将Condition的头节点移动到AQS等待节点尾部，让其等待再次获取锁。

## await方法
调用Condition的await()方法，使当前线程进入等待状态，加入到Condition等待队列，同时释放锁。

```
        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
             //第一步:新建节点，并加入到Condition队列尾部
            Node node = addConditionWaiter();
            //第二步：释放当前线程持有的同步状态
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            //第三步：不断检测该节点是否出现在AQS同步队列中（收到signal信号后，会在AQS队列检测到），不存在就一直挂起
            while (!isOnSyncQueue(node)) {
            //挂起
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            //第四步：存在AQS同步队列中了，重新参与竞争同步状态
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
                //清除Condition队列中不是等待状态的节点
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }
```
## signal()方法

唤醒在等待队列中等待时间最长的节点（条件队列中的首节点），唤醒前，将节点移到AQS同步队列
```
        public final void signal() {
        //第一步：判断当前线程是否已经持有锁，没有则抛出异常
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
            //唤醒条件队列首节点
                doSignal(first);
        }
        
       private void doSignal(Node first) {
            do {
            //第二步：从条件队列的头节点移除
                if ( (firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) &&
                     (first = firstWaiter) != null);
        }
        
      final boolean transferForSignal(Node node) {
      
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;
		//第三步：将节点加入到AQS同步队列中的末尾，返回的是前一个节点
        Node p = enq(node);
        int ws = p.waitStatus;
        //第四步：如果前一个节点的状态为cancel或修改前一个节点为Node.SIGNAL状态失败，则直接唤醒当前节点
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }

```






















