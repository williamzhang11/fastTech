# Executor体系与Future体系

## Executor体系

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadpool/image/threadpool.jpg)

	Executor 任务执行者接口，线程池几乎所有类都直接或间接实现Executor接口，它是线程池框架的基础。
	ExecutorService继承Executor接口，它是执行者服务接口。
	AvstractExecutorService，抽象类，是ExecutorService接口的默认实现。
	ScheduledExecutorService延迟和定期执行ExecutorService。
	ScheduledThreadPoolExecutor继承ThreadPoolExecutor,实现ScheduledExecutorService接口
	Executors静态工厂类提供Executor,ExecutorService,ScheduledExecutorService,ThreadFactory,Callable等类的静态工厂方法。
## Future体系
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadpool/image/future.jpg)
	Future与FutureTask代表了线程池的异步计算结果
	可以获取任务执行完成之后返回的结果
	
### Callable和Runnable

```
//由于run方法返回值是void类型，在执行完任务后无法返回任何值
public interface Runnable {
    public abstract void run();
}
//返回的类型就是传过来的类型，Callable通常与ExecutorService配合使用
public interface Callable<V> {
    V call() throws Exception;
}

<T> Future<T> submit(Callable<T> task);

```
### Future

Future是对Runnable或Callable任务的执行结果进行取消，查询是否完成，获取结果，通过get方法可获取结果，该方法会阻塞直至任务完成。
```
public interface Future<V> {
	//用来取消任务，取消成功返回true,失败返回false。
    boolean cancel(boolean mayInterruptIfRunning);
	//任务是否被取消成功，如果取消成功则返回true.
    boolean isCancelled();
	//任务是否已经完成，若任务完成，返回true
    boolean isDone();
	//获取执行结果，该方法会阻塞知道任务执行完毕返回
    V get() throws InterruptedException, ExecutionException;
	//在指定时间内获取结果，如果没有获取到结果，直接返回null
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```


### FutureTask
```
public class FutureTask<V> implements RunnableFuture<V> {

	...
}

public interface RunnableFuture<V> extends Runnable, Future<V> {
    void run();
}

```



