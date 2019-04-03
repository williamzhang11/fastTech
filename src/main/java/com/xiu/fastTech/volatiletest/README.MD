# volatile底层原理

	volatile关键字是一种轻量的synchronized，volatile变量的特性：
	1.可见性，保证volatile变量对所有线程可见。当一个线程修改了变量值，volatile保证了新值能立即同步到主内存，其他线程每次使用前从主内存刷新。
	2.禁止指令重排序。
	有序性指程序执行的顺序按照代码的先后顺序执行。
	处理器为了提高程序运行效率，会对输入代码进行优化，它不保证程序语句的执行先后顺序和代码中的顺序一直，但是
	它会保证程序最终执行结果和代码顺序执行的结果一致的。靠的是数据依赖性。但数据依赖性仅仅对单线程不会改变执行结果，多线程则不行，可能改变执行结果。
	这时需要内存屏障保证可见性。
	
## 内存屏障
	内存屏障分为2种，Load Barrier（读屏障）和Store Barrier（写屏障）
	作用：
	1.阻止屏障两侧的指令重排序
	2.强制把写缓冲区的脏数据写回主内存，让缓存中数据失效。
	每个volatile写操作前插入storestore屏障，写操作后插入storeLoad屏障
	每个volatile读操作前插入loadload屏障，读操作后插入loadstore屏障
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/volatiletest/image/volatile.jpg)





























