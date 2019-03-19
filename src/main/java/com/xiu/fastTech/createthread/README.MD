# 常见的创建线程有4种方式

1.继承Thread
2.实现Runnable接口
3.实现Callable接口与FutureTask创建
4.线程池创建

# 特点：

java是单继承的语言，因此通过继承Thread类创建，不能再继承其他父类，不是很灵活，获取当前线程信息，直接使用this即可

通过实现Runnable/Callable接口，还可以继承其他类，但访问当前线程信息时，只能使用Thread.currentThread()方法
多个线程可以共享同一个Runnable/callable对象，非常适合多个相同线程同时处理同一份资源的情况。

通过实现Callable接口， 还可以获取线程执行结果的返回值。

