# BlockingQueue总结


![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/blockingqueue/image/blockingqueue.jpg)

## 分类
	blockingQueue常用于生产者消费者模式
	JDK8中提供了7个阻塞队列
	ArrayBlockingQueue：一个由数组组成的有界阻塞队列
	LinkedBlockingQueue:一个由链表组成的无界阻塞队列
	PriorityBlockingQueue:一个支持优先级排序的无界阻塞队列
	DelayQueue:一个使用优先级队列实现的无界阻塞队列
	SyschronousQueue:一个不存储元素的阻塞队列
	LinkedTransferQueue:一个由链表结构组成的无界阻塞队列
	LinkedBlockingDeque:一个由链表结构组成的双向阻塞队列
	
### ArrayBlockingQueue
	
	基于数组的阻塞队列，ArrayBlockingQueue内部维护一个定长数组，阻塞队列的大小在初始化时已经确定，其后无法更改
	采用可重入锁ReentrantLock保证线程安全性，但生产者和消费者是公用同一个锁对象，会降低吞吐量。同时支持公平性和非公平性模式，可通过
	构造函数设置
### PriorityBlockingQueue
	PriorityBlockingQueue支持优先级的无界队列。默认情况下采用自然顺序排序，也可以自定义Comparator指定元素的排序顺序
	内部采用二叉堆实现的，添加操作不断上冒，删除操作不断下掉。
### DelayQueue
	DelayQueue一个支持延时操作的无界阻塞队列。队头元素最先到期,如果队列里没有元素到期，则不能从列头获取元素。
	主要适用的场景：
	缓存系统设计：缓存有一定的时效性，可用DelayQueue保存缓存的有效期，然后利用一个线程查询DelayQueue，如果获取到则证明该缓存失效
	定时任务调度：DelayQueue保存当前要执行的任务和执行时间，取到元素，就执行该任务。
	DelayQueue采用支持优先级的PriorityQueue实现，但队列中元素必须实现Delayed接口，Delayed接口用来标记那些应该在给定延迟时间之后执行的对象，该
	接口提供了getDelay方法返回元素节点的剩余时间，同时，元素也必须要实现compareTo方法，compareTo方向需要提供与getDelay方法一致额排序
### SynchronousQueue
	SynchronousQueue是一个不存储元素的阻塞队列，也就是每一个put操作需要等待一个take操作，否则不能继续添加元素。
	队列本身不存储任何元素，因此非常适用于传递性场景，两者直接进行对接，吞吐量高于ArrayBlockingQueue和LinkedBlockingQueue
### LinkedTransferQueue
	LinkedTransferQueue是一个由链表组成的无界阻塞队列，该队列是ConcurrentLinkedQueue,SynchronousQueue(公平模式下)，
	无界的LinkedBlockingQueues的超集，对队列的读或者写不会阻塞整个队列。
### LinkedBlockingDeque
	LinkedBlockingDeque是一个由链表组成的双向阻塞队列
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
