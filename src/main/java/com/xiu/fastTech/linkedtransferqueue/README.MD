#LinkedTransferQueue

	1.是一个基于链表的FIFO无界阻塞队列，对队列的读或者写不会阻塞整个队列。他是ConcurrentLinkedQueue,
	SynchronousQueue,无界LinkedBlockingQueue的超集。
	2.LinkedTransferQueue采用一种预占模式，有就直接拿走，没有就占着这个位置知道拿到或者超时或中断。即消费者线程到队列取数据时，如果
	队列为空，则生产一个null节点，然后park住等待生产者。后面如果生产者线程入队时发现有一个null元素节点，这时不会入队，直接将元素填充
	到该节点上，唤醒该节点的线程，被唤醒的线程取值离开