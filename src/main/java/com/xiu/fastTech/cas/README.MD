# CAS原理

## 使用：
	AQS同步组件，Atomic原子类操作等都是基于CAS的。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/cas/image/casuse.jpg)
## 更新规则：

	CAS中有三个参数：内存值V,旧的预期值A,要更新的值B,当且仅当内存值V等于旧的预期值A时，才会将内存值V的值改为B，否则什么都不干。
	如AtomicInteger:
```
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
```
	Unsafe 是CAS的核心类，java无法直接访问底层操作系统，而是通过本地native方法访问。JVM开了个后门：Unsafe，提供了硬件级别的原子操作
	valueOffset是变量值在内存中的偏移地址，Unsafe是通过偏移地址获得数据的值的
	value是当前值，使用volatile修饰
	
## CAS缺陷
	1.循环时间太长
	如果CAS一直不成功，CAS的自旋会给CPU带来很大的开销，但有些地方也限制了CAS自旋的次数，如BlockingQueue的SynchronousQueue
	2.只能保证一个共享变量的原子操作
	CAS只能针对一个共享变量进行操作，但是如果可以把多个变量变成一个变量，也可以使用CAS，比如读写锁State的高低位，来区分读锁和写锁
	3.ABA问题
	CAS检查操作者有没有发生改变，如果没有改变则更新。存在这样一种情况：如果一个值原来是A，变成了B，然后又变成了A,那么CAS检查时会发现没有改变，但实质上已经发生了改变
	这就是ABA问题。解决方案是加版本号，即每个变量上都加版本号，每次改变加1，即A->B->A，变成1A->2B->3A.
	
## ABA问题解决方案
	AtomicStampedReference类可解决ABA问题，原理则是版本号。




























