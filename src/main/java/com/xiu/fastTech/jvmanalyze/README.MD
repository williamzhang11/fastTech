# jvm分析
## 高CPU占用
	一个应用占用cpu很高，有2种情况,1种是计算密集型应用，另一种是出现死循环
	步骤如下：
	第一步定位到进程：
	（1）可通过top命令查看高cpu进程的PID。
	（2）通过ps aux |grep PID,确认进程
	 第二步定位到线程：
	 （3）显示进程内容所有的线程
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/showthread.jpg)
		找到了耗时最高的线程28802，占用CPU时间快两个小时了！
	（4）将线程ID转换成16进制
		printf "%x\n" tid
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/transsex.jpg)
	（5）打印线程堆栈
		jstack pid |grep tid -A 30
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/mapstack.jpg)
	
	总结：1.top命令：linux命令，可以查看实时CPU使用情况，也可以查看最近一段时间的cpu使用情况
		2.ps命令：linux命令，强大的进程状态监控命令。可以查看进程以及进行中线程的当前CPU使用情况，是当前状态的采样数据
		3.jstack:java命令。可以查看某个进程的当前线程栈运行情况，根据这个命令的输出可以定位某个进程的所有线程的运行状态，运行代码，是否死锁。
		
## 高内存占用
	java中经常出现以下2种内存异常
	1.java.lang.OutOfMemoryError:PermGen space
	2.java.lang.OutOfMemoryError:java heap space
	java内存模型是描述java程序中各变量（实例域，静态域，数组元素）之间的关系，以及在实际计算机系统中将变量存储到内存和从内存取出变量这样的底层细节
	
	java虚拟机中堆内存分为3个代：新生代（new）,老年代（old）永久代（perm）
	(1)新生代new：新建的对象存放这里
	(2)老年代old:存放从新生代new中迁移过来的生命周期教久的对象，新生代new与老年代old共同组成了堆内存
	(3)永久代Perm：是非堆内存的组成部分。主要存放加载的class类对象如class本身，method,field
	
	如果出现java.lang.OutOfMemoryError:java heap space异常，说明java虚拟机的堆内存不够，原因有：
	（1）java虚拟机的堆内存设置不够，可以通过参数-Xms,-Xmx调整
	（2）代码中创建了大量大对象，并且长时间不能被垃圾收集器收集（存在引用）
	如果出现java.lang.OutOfMemoryError:PermGen space说明java虚拟机对永久代Perm内存设置不够
		一般这种情况，是程序启动需要加载大量的第三方jar包。如：一个tomcat下部署太多应用
		从代码的角度，软件开发人员只要关注java.lang.OutOfMemoryError:java heap space异常，减少不必要对象创建，同时避免内存泄露
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/memtop.jpg)
	步骤如下：
	（1）top命令，发现PID为9004的Java进程一直占用比较高的内存不释放（24.7%），出现高内存占用的故障。
	（2）ps -mp 9004 -o THREAD,tid,time,rss,size,%mem
		ps可以查到进程的的cpu占用情况，但是查不到一个进程下具体线程的内存占用情况
	（3）可以使用java的内存监控工具：jmap命令
	 常用的用法：
	jmap [pid]
	jmap -histo:live[pid]>a.log
	jmap -dump:live,format=b,file=xxx.xxx[pid]
	后2种是用的最多的。其中
	jmap heap [pid]打印head的概要信息，GC算法，heap的配置和使用情况，以及当前的垃圾回收情况
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/jmapheap.JPG)
	jmap -histo:live [pid] 可以查看当前java进程创建的活跃对象数目和占用内存大小
	jmap -dump:live,format=b,file=xxx.xxx [pid] 可以将当前java进程的内存占用情况导出来，方便用专门的内存分析工具分析（如：MAT）
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/jmapoperator.jpg)

	总结：
	1.top命令，linux命令，可以实时查看内存使用情况
	2.jmap -histo:live [pid]，然后分析具体对象数目和占用内存大小，从而定位代码
	3.jmap -dump:live,format=b,file=xxx.xxx [pid]，然后利用MAT工具分析是否存在内存泄漏等
	
## 频繁GC
	jvm查看gc情况
	jstat -gcutil pid interval(ms)
	
	jstat -gcutil 8721 1000
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/jstat.JPG)

	参数说明如下：
	
	S0: 新生代中Survivor space 0区已使用空间的百分比
	
	S1: 新生代中Survivor space 1区已使用空间的百分比
	E: 新生代已使用空间的百分比
	O: 老年代已使用空间的百分比
	P: 永久带已使用空间的百分比
	
	
	YGC: 从应用程序启动到当前，发生Yang GC 的次数
	
	YGCT: 从应用程序启动到当前，Yang GC所用的时间【单位秒】
	FGC: 从应用程序启动到当前，发生Full GC的次数
	FGCT: 从应用程序启动到当前，Full GC所用的时间
	GCT: 从应用程序启动到当前，用于垃圾回收的总时间【单位秒】
	
	配置输出gc日志（配置位置catalina.sh）
	CATALINA_OPTS=-XX:ParallelGCThreads=4 -XX:+PrintGCDetails  -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseConcMarkSweepGC 	-XX:MaxTenuringThreshold=15 -Xloggc:../logs/tomcat_gc.log
	
	-XX:+PrintGC 输出GC日志
	-XX:+PrintGCDetails 输出GC的详细日志
	-XX:+PrintGCTimeStamps 输出GC的时间戳（以基准时间的形式）
	-XX:+PrintGCDateStamps 输出GC的时间戳（以日期的形式，如 2013-05-04T21:53:59.234+0800）
	-XX:+PrintHeapAtGC 在进行GC的前后打印出堆的信息
	-Xloggc:../logs/gc.log 日志文件的输出路径
	使用GCViewer工具对gc日志进行可视化分析：
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/gc.JPG)
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/gcpause.JPG)
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/gcsummary.JPG)

	发现24小时内触发的fullGC竟然高达25次，每次FullGC的平均时长近16s,YGC 1937次，这已经严重影响到线上代码的运行了。
	解决方式：
	第一步：
		依据高内存占用解决方式，打印出堆内存中所有的大对象列表，找出是否存在内存泄露的情况，参照代码依次排查每个对象的嫌疑，发现
	输出日志时是通过先输入到无界队列，然后消费者从无界队列中另一端去消费，消费者是kafka，由于网络原因，kakfa与broker连接超时
	导致没有被消费，该无界队列中的对象越来越多。又存在强引用，所以fullGC也无法GC掉，导致老年代越来越大。fullGC清理掉的仅仅是
	不被GC root关联的对象。通过把网络打通，缓解了fullGC发生的频率，但是还是出现的异常
	第二步：
		 发现新生代的大小为600M,老年代大小为19G，这样的比例是明显不合适的，一般推荐的比例为1：2.因为新生代太小，很容易触发YGC，当到达15次，依然存活的对象进入老年代
	并且当新生代还没有被填满了时，如果有大对象进来，会直接放在老年代，当大量的大对象都进入老年代很容易触发fullgc，因为这些大对象本来应该在新生代被清理的。
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/jvmanalyze/image/jvmgc.png)	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
