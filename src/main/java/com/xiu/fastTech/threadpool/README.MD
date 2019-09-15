# 线程池介绍

## Executors创建线程池

1. newFixedThreadPool(int nThreads) 创建固定大小的线程池

2. newSingleThreadExecutor()  创建只有一个线程的线程池

3.newCachedThreadPool() 创建一个不限线程数上限的线程池，任何提交的任务都将立即执行

4。newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行


```
//有多个构造方法，最终都指向这个最多参数的构造方法
  public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }


```
## 线程池参数

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadpool/image/threadpoolparam.jpg)

## 线程池参数关系

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadpool/image/threadpoolparam1.jpg)

## 阻塞队列

BlockingQueue有四个具体的实现类,根据不同需求,选择不同的实现类

1、ArrayBlockingQueue：一个由数组支持的有界阻塞队列，规定大小的BlockingQueue,其构造函数必须带一个int参数来指明其大小.其所含的对象是以FIFO(先入先出)顺序排序的。

2、LinkedBlockingQueue：大小不定的BlockingQueue,若其构造函数带一个规定大小的参数,生成的BlockingQueue有大小限制,若不带大小参数,所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定.其所含的对象是以FIFO(先入先出)顺序排序的。

3、PriorityBlockingQueue：类似于LinkedBlockQueue,但其所含对象的排序不是FIFO,而是依据对象的自然排序顺序或者是构造函数的Comparator决定的顺序。

4、SynchronousQueue：特殊的BlockingQueue,对其的操作必须是放和取交替完成的。

LinkedBlockingQueue 可以指定容量，也可以不指定，不指定的话，默认最大是Integer.MAX_VALUE,其中主要用到put和take方法，put方法在队列满的时候会阻塞直到有队列成员被消费，take方法在队列空的时候会阻塞，直到有队列成员被放进来。


## 拒绝策略：

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadpool/image/threadpoolparam1.jpg)



三种阻塞队列：

    BlockingQueue<Runnable> workQueue = null;
    workQueue = new ArrayBlockingQueue<>(5);//基于数组的先进先出队列，有界
    workQueue = new LinkedBlockingQueue<>();//基于链表的先进先出队列，无界
    workQueue = new SynchronousQueue<>();//无缓冲的等待队列，无界

四种拒绝策略：

    RejectedExecutionHandler rejected = null;
    rejected = new ThreadPoolExecutor.AbortPolicy();//默认，队列满了丢任务抛出异常
    rejected = new ThreadPoolExecutor.DiscardPolicy();//队列满了丢任务不异常
    rejected = new ThreadPoolExecutor.DiscardOldestPolicy();//将最早进入队列的任务删，之后再尝试加入队列
    rejected = new ThreadPoolExecutor.CallerRunsPolicy();//如果添加到线程池失败，那么主线程会自己去执行该任务

五种线程池：

    ExecutorService threadPool = null;
    threadPool = Executors.newCachedThreadPool();//有缓冲的线程池，线程数 JVM 控制
    threadPool = Executors.newFixedThreadPool(3);//固定大小的线程池
    threadPool = Executors.newScheduledThreadPool(2);
    threadPool = Executors.newSingleThreadExecutor();//单线程的线程池，只有一个线程在工作
    threadPool = new ThreadPoolExecutor();//默认线程池，可控制参数比较多  




https://blog.csdn.net/zuochao_2013/article/details/81511664

