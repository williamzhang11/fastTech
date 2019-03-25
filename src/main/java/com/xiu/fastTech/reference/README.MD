# java中的4中引用方式

## 1.强引用（StrongReference）
使用new关键字创建的对象返回的引用，是强引用，如果对象通过一系列强引用可达，如加入到全局变量map中，即使内存不足时，
垃圾回收器也不会回收它，会抛出OutOfMemoryError错误，程序异常终止。

## 软引用（SoftReference）
只有在内存不足时，垃圾回收器才会回收它，在内存充足的情况下，即使调用system.gc(),也不会被回收，java虚拟机会把这个软引用加入
到与之关联的引用队列中。可用于存储内存敏感的高速缓存

## 弱引用（WeakReference）
垃圾回收器一旦发现具有软引用的对象，不管内存是否足够都会回收它。但垃圾回收器是一个优先级很低的线程，不一定很快发现只具有弱引用的对象，
弱引用可用于解决内存泄露问题

```
	User user4 = new User(4,"zhaoliu");
		WeakRefereceUser  s= new WeakRefereceUser(user4);
		System.out.println(s.get());
		int i=0;
		while(true) {
			if(s.get()!=null) {
				i++;
				System.out.println("循环次数"+i);
			}else {
				
				System.out.println("已经被清空");
				break;
			}
		}

```

## 虚引用（PhantomReference）
在任何时候都可能被垃圾回收器回收，主要用于跟踪一个对象被垃圾回收的过程。
虚引用必须与引用队列联合使用，当垃圾回收器准备回收虚引用对象时，会在回收对象内存之前，把虚引用加入到与之关联的队列中。





























