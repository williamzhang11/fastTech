# synchronized重量锁的优化

	由于monitor监视器锁是基于操作系统底层Mutex Lock实现的，对线程加的是互斥锁，加锁时间比其他指令长很多，因此这种互斥锁机制也
	称为重量级锁。
	在JDK1,6后，对synchronized进行了优化，出现了偏向锁，轻量锁，自旋锁，自适应自旋锁等。
	在解释几种锁之前，需要描述下synchronized的线程排队和锁标志位
	
## 对象头
	java对象是创建在堆上的，指向堆的引用放在栈上，堆上的对象结构如下：
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/object.jpg)

	其中对象头结构如下:
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/objecthead.jpg)
 	Mark Word会随着程序的运行发生变化，状态如下：
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/markword.jpg)

	其中无锁状态：对象HashCode+对象分代年龄+状态位01 是否偏向锁 0
	当一个对象获取锁后，标志位会发生变化
	
## Monitor Record

	MonitorRecord简称MR,是java线程私有的数据机构，每个线程都有一个可用MonitorRecord列表，同时还有个全局的可用列表
	一个被锁住的对象都会和一个MonitorRecord关联，即对象头的MarkWord中的LockWord指向MonitorRecord的起始地址
	MonitorRecord有一个Owner字段存放拥有该锁的唯一标识，标识这个锁被当前线程占用。
	1.Owner：初始时为 NULL 表示当前没有任何线程拥有该 Monitor；2）当线程成功拥有该锁后保存线程唯一标识；3）当锁被释放时又设置为 NULL 。
	2.EntryQ：关联一个系统互斥锁（ semaphore ），阻塞所有试图锁住 Monitor Record失败的线程 。
	3.RcThis：表示 blocked 或 waiting 在该 Monitor Record 上的所有线程的个数。
	4.Nest：用来实现重入锁的计数。
	5.HashCode：保存从对象头拷贝过来的 HashCode 值（可能还包含 GC age ）。
	6.Candidate：用来避免不必要的阻塞或等待线程唤醒。因为每一次只有一个线程能够成功拥有锁，如果每次前一个释放锁的线程唤醒所有正在阻塞或等待的线程，
	会引起不必要的上下文	切	换（从阻塞到就绪然后因为竞争锁失败又被阻塞）从而导致性能严重下降。Candidate 只有两种可能的值 ：1）0 表示没有需要唤
	醒的线程；2）1 表示要唤醒	一个继任线程来竞争锁。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/monitorrecord.jpg)

	如果某个线程获取监视器锁成功，成为该监视锁对象的拥有者，在任意时刻监视器对象只属于一个活动线程(owner)
	拥有者可调用wait方法释放监视器锁，进入等待状态。
	
	
## 锁优化
	JVM的monitorenter和monitorexit字节码指令，依赖于底层操作系统的Mutex Lock实现的，
	由于使用Mutex Lock,需要将当前线程挂起，并从用户态切换到内核态，这种切换代价非常昂贵。而大部分
	情况是，同步方法运行在无锁竞争环境，即单线程下，如果每次都调用Mutex Lock会严重影响性能。
	因此进行锁优化，包括自旋锁，自适应自旋锁，锁消除，锁粗化，锁升级（包括偏向锁，轻量级锁）等减少锁开销。
### 自旋锁

	原因：线程的阻塞和唤醒需要CPU从用户态转为内核态，频繁的阻塞和唤醒对CPU是一件负担很重的工作。在很多应用上，对象锁的锁状态
	只会持续很短一段时间，为了很短时间频繁阻塞，唤醒非常不值得，因此引入自旋锁。
	
	定义：自旋锁，是让线程等待一段时间（执行一段无意义的循环，即自旋），不会立即被阻塞，看持有锁的线程是否很快释放锁。
	缺点：自旋不能替代阻塞，虽然它可避免线程切换带来的开销，但占用处理器时间，如果持有锁很快释放锁，自旋的效率就很好，反之就会白白浪费资源。
	可以通过设置自旋次数调整，超过这个次数就被阻塞。
	
### 自适应自选锁
	自适应自旋锁，自选的次数不是固定的，它是由前一次在同一个锁上自选时间和锁拥有者状态决定的。
	大体为：如果上次自选成功，下次自旋的次数会增多。反之则减少
	
### 锁消除
	在有些情况下JVM检测到不可能存在共享数据的竞争，JVM会对同步锁进行锁消除。
	锁消除的依据是变量的逃逸分析。
```
	public void vectorTest(){
	    Vector<String> vector = new Vector<String>();
	    for (int i = 0 ; i < 10 ; i++){
	    	vector.add(i + "");
	    }
	    System.out.println(vector);
	}

```
	JVM可检测到变量vector没有逃逸出vectorTest（）外，JVM对vector内部加锁消除

### 锁粗化

	在使用同步锁时，需要让同步块作用范围尽可能小，即仅在共享数据的实际作用区域进行同步，目的是为了减少同步操作的数目，如果存在锁竞争，
	那么等待锁线程也可尽快拿到锁。
	但有些情况下一系列 的连续加锁解锁，导致性能损耗，这事，可将多个连续的加锁，解锁操作连在一起，扩展成更大范围的锁。
	如：for循环内加锁，JVM会将加锁解锁移到循环外。
	
### 锁升级
	锁分为4钟状态：无锁状态，偏向锁状态，轻量级锁状态，重量级锁状态。它们会竞争激烈而逐渐升级，锁可以升级，不可降级，这种策略是为了
	提高获得锁和释放锁的效率
#### 重量级锁
	重量级锁是通过对象内部的监视器（Monitor）实现的，而Monitor的本质是依赖于底层操作系统的Mutrx Lock实现。
	操作系统实现线程间的切换，需要从用户态切换到内核态，成本非常高。
#### 轻量级锁
	轻量锁并不是代替重量级锁的，目的是在没有多线程竞争的前提下，减少传统重量级锁使用操作系统互斥量产生的性能消耗
	加锁：
	1.线程在执行同步块前，JVM会在当前线程栈帧中创建用于存储锁记录的空间（LockRecord）,并将对象头的MarkWord信息复制到线程的锁记录中。
	2.然后线程尝试使用CAS将对象头的MarkWord替换为指向线程锁记录的指针，如果成功，当前线程获取到锁，并将对象的锁标志位变成"00",如果失败，表示
	其他线程在竞争锁，当前线程尝试自旋获取锁。
	3.如果有2个以上的线程竞争同一个锁。轻量锁不再有效。膨胀为重量级锁,锁标记变为"10",Markword中存储的是
	指向重量级锁的指针，后面等待线程进入阻塞状态
	
	解锁：
	轻量级锁解锁时，同样通过CAS操作将对象头换回来，如果成功，表示没有竞争。失败的话，说明有其他线程尝试获取该锁，锁
	膨胀为重量级锁。释放锁时，唤醒被挂起的线程。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/qingliangjisuo.jpg)
	
	轻量级锁的依据是：绝大部分的锁，在整个生命周期中不会存在竞争。如果打破这个依据，除了互斥的开销外，还有额外CAS操作。因此多线程
	竞争的情况下，轻量级锁比重量级锁更慢。

#### 偏向锁
	目的是减少数据在无竞争情况下性能消耗，核心思想是锁会偏向第一个获取它的线程，在接下来的执行过程中该锁没有被其他线程获取的话
	持有偏向锁的线程不需要再进行同步。
	加锁：
	1.当一个线程访问同步代码块获取锁时，会在对象头和线程栈帧的锁记录里存储锁偏向的线程id。以后该线程在进入和退出同步块时不需要进行CAS操作
	来加锁和解锁。只需要检查当前MarkWork中是否是当前线程id。如果是，说明获取到了锁，继续执行同步代码块
	2.如果不是，通过CAS竞争锁，竞争成功的话，将MarkWork的线程id替换为当前线程id,继续执行同步代码块
	3.如果竞争失败，说明存在多线程竞争，达到安全点时，偏向锁升级为轻量锁。
	撤销：
	偏向锁使用等待竞争出现才释放锁的机制， 当其他线程尝试获得锁时，才会释放锁。偏向锁的撤销需要等待安全点。
	1.首先暂停拥有偏向锁的线程，然后检查持有偏向锁的线程是否活着，如果不处于活动状态，将对象头置为无锁状态。
	2.如果线程是活动状态，拥有偏向锁的线程栈被执行，遍历偏向对象的锁记录，对栈中的锁记录和对象头的MarkWord进行重置。
	要么重新偏向其他线程，即将偏向锁交给其他线程，相当于释放锁。要么恢复到无锁或升级为轻量锁。
	3.最后唤醒暂停的线程。被阻塞在安全点的线程继续执行同步代码块。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/pianxiangsuo.jpg)

### 三种锁的对比
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/duibi.jpg)	
### 三种锁的转换
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/synchronizedoptimize/image/zhuanhuan.jpg)
 



### 阻塞锁
	只有获取锁的线程才能继续，竞争失败的线程由running状态进入blocking状态，被防止到与目标锁相关的等待队列。
	解锁：当持有锁的线程推出临界区，释放锁后，将等待队列中的一个阻塞线程唤醒，重新参与锁竞争。
### 公平锁
	公平锁是获取锁的顺序按照先到先得原则。实现上是某个线程竞争某个对象锁时，只要锁的等待队列非空，必须把这个线程阻塞插入队尾（CAS操作）
### 非公平锁
	每个线程要先竞争锁，在竞争失败或已被加锁的前提下才会插入等待队列。实现上，后到的线程无需进入等待队列，可直接竞争锁
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	














