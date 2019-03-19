# 线程未捕获异常

线程内抛出未捕获异常会导致线程的终止，可能会导致一些系统资源无法回收，连接无法关闭的情况。有2种方法可以解决

第一：线程内捕获异常，线程外是无法捕获到异常的
第二：使用Thread中提供的UncaughtExceptionHandle接口
也可以将2者结合,可以防止线程异常带来的资源泄露问题。

## 注意

1.以线程池方式execute运行需要将异常封装到Runnable或Callable中
run方法中使用：Thread.currentThread().setUncaughtExceptionHandler();
2.线程池通过submit提交任务，抛出的异常，都是任务返回状态的一部分。会在Future.get封装在ExecutionException重新抛出
