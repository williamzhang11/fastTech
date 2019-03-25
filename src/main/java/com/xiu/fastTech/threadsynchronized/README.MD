# 线程间通信的几种方式

## 线程的状态
	
	线程的生命周期中状态(位于java.lang.Thread.State枚举类中)分别为：
	初始（NEW）:		初始状态，线程被构建，未调用start()方法
	运行（RUNNABLE）: 运行状态，调用start()方法后。java线程中，将操作系统线程就绪/运行统称为运行状态
	阻塞（BLOCKED）:	阻塞状态，线程等待进入synchronized代码块或方法中，等待获取锁
	等待（WAITING）:  等待状态，线程调用wait/join操作使自己进入等待状态，等待其他线程做出特定操作（notify/中断）
	超时等待（TIMED_WAITING）:超时等待，线程调用sleep(timeout),wait(timeout)等操作进入超时等待状态，超时后自动返回
	终止（TERMINATED）: 终止状态，线程运行结束
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadsynchronized/image/threadstatus.jpg)
	
	1.等待状态（Waiting）和超时等待（TIMED_WAITING）都会使得线程进入等待状态。TIMED_WAITING会在超时后自动返回，
	而waiting则需要等待至条件改变
	2.进入阻塞状态的唯一前提条件是等待获取同步锁。java中只有2种情况可以使得线程进入阻塞状态：1.等待进入synchronized块或方法2.调用
	wait方法后重新进入synchronized块或方法。
	3.Lock类对于锁的实现不会另线程进入阻塞状态，Lock底层调用LockSupport.park方法，使线程进入等待状态。
	

## 1.wait/notify通知机制

	该方法是针对对象的，利用Object类的wait/notify 方法实现线程阻塞。调用任意对象的wait方法导致线程阻塞，阻塞的同时释放该对象的锁。调用对象的notify/notifyall方法，随机解除该对象阻塞的线程，但它需要重新获取对象的锁，直到获取成功后才能往下执行。
	wait,notify方法必须在synchronized块或方法中被调用，并要保证同步块或方法的锁对象与调用wait/notify方法的对象是同一个，如此一来在调用wait
之前现场就已经成功获取某个对象的锁，在执行wait阻塞后，当前线程将之前获取对象的锁释放。


与sleep的区别
sleep并不会放弃对象锁，而是暂时让当前线程放弃cpu,到了设定时间后，让线程重新回到就绪队列，而非阻塞队列。让其他线程有机会得到执行
调用wait后，当前线程会放弃对象锁，进入等待状态，加入等待队列。获取通知后，进入阻塞对列，竞争获取锁。

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadsynchronized/image/threadflow.jpg)

1.当线程A（消费者）调用wait()方法后，线程A让出对象锁，自己进入等待状态，同时加入锁对象的等待队列。
2.线程B（生产者）获取锁后，调用notify方法通知锁对象的等待队列，使得线程A从等待队列进入阻塞队列。
3.线程A进入阻塞队列后，直至线程B释放锁后，线程A竞争得到锁继续从wait()方法后执行。

## 2.使用Lock和Condition对象
使用方法和wait/notify基本一致

## 3.阻塞队列

## 4.使用管道方式
在java的io库中对应PipedWrite和PipedReader
使用管道的方式，必须保证读写管道的线程同时在线不能退出，否则会报错
PipedReader与普通的io不同，是可中断的



