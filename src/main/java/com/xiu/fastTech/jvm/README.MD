# Jvm

## jvm内存区域
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/jvmmemorymodel.jpg)

## 程序计数器
	虚拟机中没有使用CPU的程序计数器，而且在内存中模拟CPU的程序计数器。每一条线程都有一个独立的程序计数器。虚拟机中的程序计数器指向
	正在执行的字节码地址。
## java虚拟机栈
	java虚拟机栈也是线程私有的，每个方法执行时都会创建一个栈帧，用于存放局部变量表，操作数栈，动态链接，方法出口等信息。每一个方法从调用直到执行完成
	都对应着一个栈帧在虚拟机中入栈到出栈过程。
## 本地方法栈
	本地方法栈与虚拟机栈区别在于，虚拟机栈为执行java方法服务，本地方法栈为虚拟机使用Native方法服务。
## java堆内存
	java堆是被所有线程共享的一块区域，在虚拟机启动时创建。用来存放对象实例，几乎所有对象实例都会在这里分配内存。堆是垃圾收集器管理的主要区域。
	垃圾收集器实现对象的自动销毁。java堆物理上是不连续的内存空间，逻辑上是连续的。
## 方法区
	是所有线程共享。主要用于存储类的信息，常量池，方法数据，方法代码。
	
## 方法区和永久代

	虚拟机规范中只规定了有方法区这个概念和它的作用，并没有规定如何去实现它。HotSpot jvm是用永久代实现方法区。
		jdk1.7以前可用
	 		-XX:PermSize 调整方法区初始大小
	 		-XX:MaxPermSize 调整方法区最大大小
	 	jdk1.7中存储在永久代的部分数据转移到了Java Heap或NativeHeap，并没有完全移除
		jdk1.8中移除了永久代，用元空间代替
			-XX: MetaspaceSize 元空间初始大小
			-XX: MaxMetaspaceSize元空间最大大小
	元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过最大的区别是：元空间不在虚拟机中
	而是使用本地内存
## 元空间替换永久代的原因：
	1.字符串存在永久代中，容易出现性能问题和内存溢出
	2.类及方法的信息等比较难确定其大小，因此对永久代的大小指定比较困难。太小容易出现永久代溢出，太大容易导致老年代溢出
	3.永久代会为GC带来不必要的复杂度，回收效率偏低
## 堆内存

	堆内存=新生代+老年代
	新生代分为3个区域，一个Eden区和2个Survivor区，它们之间的默认比例为（8：1：1），可修改。
	对象创建时主要分配在新生代的Eden区，少数情况下可直接分配在老年代（新生代空间不够时）。
	JVM每次使用新生代中的Eden和其中一块Survivor.在经过一次MinorGC后，将Eden和Survivor中还存活
	的对象一次性复制到另一Survivor空间（复制算法进行GC）最后清理掉Eden和刚才用到的Survivor空间
	此时在Survivor空间存活下来的对象的年龄设置为1，以后这些对象每在Survivor区熬过一次GC,它们的年龄加1
	，当对象达到某个年龄（默认15）时，把它们移到老年代。
   	新生代区与老年代比例为1：2
   	
## 堆内存垃圾回收方式

	java堆是GC垃圾回收的主要区域，采用分代垃圾回收方式，GC分为2种：MinorGC，FullGC
	
### Minor GC（新生代垃圾回收方式）
	触发Minor GC(新生代GC)的情况：
		1.Eden区域满了
		2.新创建的对象大小大于Eden区域所剩余大小
	触发Full GC的情况：
		1.直接调用System.gc
		2.老年代空间不足时（可能是大对象创建，新生代存活下来对象转入引入的）
			策略：1.尽量做到让对象在Minor GC阶段回收
				2.不要创建大对象
		3.方法区空间不足（系统中要加载的类，反射类，调用方法多导致）
			策略：增大方法区
				转为使用CMS GC
		4.Minor GC时，survivor放不下，对象只能放入老年代，而老年代也放不下
			策略：增加survivor space，老年代空间
			
### 垃圾回收时的Stop The World

	java中Stop The World机制简称STW，是在执行垃圾收集算法时，java应用程序中其他所有线程都被挂起（除了垃圾收集帮助器之外）。它是java中
	一种全局暂停现象，全局停顿，所有java代码停止，native代码可以执行，但不能与JVM交互。不管是新生代gc还是full gc都会全局暂停执行
	STW总会发生不管是新生代还是老年代 就算是CMS也有STW的时候重点是 时间长短,新生代gc暂停非常短，full gc时间较长，因此要避免频繁full gc。
	
### java垃圾收集器历史：

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/garbagecollect.jpg)

	上面共7中收集器，上面为新生代收集器，下面是老年代收集器。如果2个收集器之间存在连线，说明它们可以搭配使用。
	新生代收集器使用复制算法
	老年代使用并发标记清除（CMS）或标记-整理算法
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/garbagecollectdetail.jpg)
	并行（Parallel）:多条垃圾收集线程并行工作，而用户线程仍处于等待状态
	并发（Concurrent）:垃圾收集线程与用户线程一段时间内同时工作（交替执行）
#### 第一阶段，串行收集器（Serial）
	在jdk1.3.1之前，java虚拟机仅仅能使用Serial收集器。 Serial收集器是一个单线程的收集器，但它的“单线程”的意义并不仅仅是说明它只会使用一个CPU或一条收集线程去完成垃圾	收集工作，更重要的是在它进行垃圾收集时，必须暂停其他所有的工作线程，直到它收集结束。
	开启Serial收集器的方式
    -XX:+UseSerialGC
##### Serial(串行GC)收集器
	Serial收集器是一个新生代收集器，单线程执行，使用复制算法。它在进行垃圾收集时必须暂停其他所有的工作线程（用户线程）。对于单个CPU的环境来说，Serial收集器
	由于没有线程交互的开销，专心做垃圾收集自然可以获得最高的单线程收集效率。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/serialgc.jpg)
#### 第二阶段：并行收集器（parallel）

	Parallel收集器也称吞吐量收集器，相比Serial收集器，Parallel最主要的优势在于使用多线程去完成垃圾清理工作，这样可以充分利用多核的特性，大幅降低gc时间。
	
	PS:开启Parallel收集器的方式
	
	-XX:+UseParallelGC -XX:+UseParallelOldGC

#### ParNew(并行gc)收集器
	ParNew收集器是serial收集器的多线程版本，除了使用多条线程进行垃圾收集之外，其余行为与Serial收集器一样
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/parnewgc.jpg)
##### Parallel Scavenge（并行回收GC）收集器
	Parallel Scavenge收集器是一个新生代收集器，使用复制算法收集器，并行多线程收集器。该收集器目标是达到一个可控制的吞吐量。吞吐量=程序运行时间/（程序运行时间+垃圾收集时间）
	虚拟机总共运行100分钟，其中垃圾收集花掉1分钟，那么吞吐量是99%。
##### Serial old（串行GC）收集器
	Serial old是Serial收集器的老年代版本，它同样使用一个单线程执行收集，使用标记-整理算法。
##### Parallel old（并行GC）收集器
	Parallel Old是Parallel Scavenge收集器的老年代版本，使用多线程和标记整理算法

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvm/image/paralleloldgc.jpg)

#### 第三阶段，CMS（并发）收集器
	只有在初始标记阶段、重新标记阶段会暂停应用线程，其余阶段都是和应用程序同时工作的。
	PS:开启CMS收集器的方式
	
	-XX:+UseParNewGC -XX:+UseConcMarkSweepGC

##### CMS（并发GC）收集器
	CMS收集器是一种获取最短回收停顿时间为目标的收集器
	优点：是并发收集，低停顿
	缺点：1.对CPU资源非常敏感
		2.无法处理浮动垃圾
		3.基于标记-清除实现的收集器，使用后产生大量碎片。碎片太多时，会给对象分配带来很大麻烦，比如大对象，内存空间
		找不到连续空间来分配不得不提前触发一次Full GC。
		
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/jvmgc.png)	

	优秀文章：https://www.cnblogs.com/aspirant/p/8662690.html
####第四阶段，G1（并发）收集器

	G1收集器（或者垃圾优先收集器）的设计初衷是为了尽量缩短处理超大堆（大于4GB）时产生的停顿。相对于CMS的优势而言是内存碎片的产生率大大降低。
	
	PS:开启G1收集器的方式
	
	-XX:+UseG1GC
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/gca.jpg)	
	
	
	
	https://crowhawk.github.io/2017/08/15/jvm_3/
























	
	
	
	
	
	
	
	
