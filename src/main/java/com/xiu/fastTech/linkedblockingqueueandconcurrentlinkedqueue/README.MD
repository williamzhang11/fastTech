# LinkedBlockingQueue和ConcurrentLinkedQueue的区别

	1.生产者消费者的首选的数据结构为队列（先进先出）队列分为阻塞队列以及非阻塞队列。
	linkedBlockingQueue就是阻塞队列。
	好处是，多线程操作共同队列时不需要额外的同步，队列会自动平衡负载，即生产与消费两边，那边处理快了，会阻塞掉，从而减少两边的处理速度
	ConcurrentLinkedQueue是非阻塞队列。当多线程共享访问一个公共集合时比较合适。
	2.LinkedBlockingQueue多用于任务队列，ConcurrentLinkedQueue多用于消息队列
	单生产者，单消费者，以及多生产者，单消费者用LinkedBlockingQueue
	单生产者，多消费者，以及多生产者，多消费者用ConcurrentLinkedQueue
	
	对于LinkedBlockingQueue的多个生产者是可以接受的，但如果多个消费者就不行了，需要超时机制，否则多个消费者空转会造成cpu飙升。
	对于ConcurrentLinkedQueue收到后需要处理,然后sleep
	
	3.LinkedBlockingQueue是线程安全的，实现先进先出特性，作为生产者消费者的首选
	ConcurrentLinkedQueue是一个Queue的安全实现。采用CAS操作保证元素一致性