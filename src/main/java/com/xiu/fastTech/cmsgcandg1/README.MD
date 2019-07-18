#CMS垃圾收集器与G1收集器

## 1.CMS收集器
	CMS收集器是一种以获取最短回收停顿时间为目标的收集器基于“标记-清除”算法实现。CMS只要是对老年代的回收，
	尽量减少应用的暂停时间，减少fullgc发生的机率。在启动JVM参数加上-XX:+UseConcMarkSweepGC
	
	运作过程如下：
	1)初始标记
	2)并发标记
	3)重新标记
	4)并发清除
	优点：并发收集，低停顿
	缺点：
	1)CMS收集器对CPU资源非常敏感。在并发阶段，它虽然不会导致用户线程停顿，但因为占用一部分
	线程而导致应用程序变慢，总吞吐量降低。
	2)CMS收集器无法处理浮动垃圾，可能会出现“Concurrent Mode Failure”(并发模式故障)，失败而导致
	FullGC产生
		浮动垃圾：由于CMS并发清理阶段用户线程在运行着，伴随着程序运行自然会有新的垃圾不断产生，这部分
	垃圾的标记过程，CMS无法在当此收集中处理掉它们，只好留待下一次GC中再清理。这些垃圾就是浮动垃圾
	3)	CMS是一款“标记-清除”算法实现的收集器，容易出现大量空间碎片。当空间碎片过多，会给大对象
	分配带来很大麻烦，往往会出现老年代还有很大空间剩余，但无法找到足够大的连续空间分配当前对象，
	不得不提前触发一次Full GC
	
	如果应用程序对停顿比较敏感，并且在应用程序运行的时候，可以提供更大的内存和更多的CPU,可以使用CMS收集
	
### 参数
	1.启动CMS:-XX:+UseConcMarkSweepGC
	2.CMS默认启动的回收线程数目,可以通过-XX:ParallelCMSThreads=X来设定
	3.CMS是不会整理碎片的，因此为了防止堆碎片引起full gc，可通过开启CMS阶段进行合并碎片选项：
	XX:+UseCMSCompactAtFullCollection,开启这个选项一定程度上会影响性能
	4.为了减少第二次暂停时间，开启并行remark:-XX:+CMSParallelRemarkEnabled。如果remark还是过长的话，可以
	开启XX:+CMSScavengeBeforeRemark选项，强制remark之前开始一次minor gc，减少remark的暂停时间，但是
	在remark后也将立即开始又一次minorgc
	5.为了避免perm区满引起fullgc，建议开启CMS回收Perm区选项：+CMSPermGenSweepingEnabled -XX:	+CMSClassUnloadingEnabled
	6.默认CMS是在tenured generation占满68%的时候开始进行CMS收集，如果你的老年代增长不是那么快，
	并且希望降低CMS次数的话，可以适当调高此值：-XX:CMSInitiatingOccuoancyFraction=80
	这里修改成80%占满的时候才进行CMS回收
	7.年轻代的并行收集线程数默认是（CPU<=8）?cpu：3 + ((cpu * 5) / 8)如果你希望降低这个线程数，可以通过-XX:ParallelGCThreads= N 来调整。
	8.进入重点，在初步设置了一些参数后
```
-server -Xms1536m -Xmx1536m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=64m  
-XX:MaxPermSize=64m -XX:-UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection  
-XX:CMSInitiatingOccupancyFraction=80 -XX:+CMSParallelRemarkEnabled  
-XX:SoftRefLRUPolicyMSPerMB=0  
```
	需要打开GC日志时再加上
```
-verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -Xloggc:/home/test/logs/gc.log
```
## G1收集器
	G1具备以下特点：
	1.并行与并发：G1能充分利用CPU,多核环境下的硬件优势，使用多个CPU来缩短STOP-THE-WORLD停顿时间
	部分其他收集器原本需要停顿java线程执行的GC动作，G1收集器任然可以通过并发方式让java程序继续执行
	2.分发收集：虽然G1可以不需要其他收集器配合就能独立管理整个GC堆，但还是保留了分代概念。它能够采用不同
	的方式去处理新创建的对象和已经存活一段时间，熬过多次GC的旧对象，以获取更好的收集效果。
	3.空间整合：与CMS的“标记-清理”算法不同，G1从整体来看是基于“标记整理”算法实现的收集器，从局部看是基于复制算法
	实现的
	
	
	
	
	
	
	
	
	
	
	
	
	
		