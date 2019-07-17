# 触发JVM进行FullGC的情况以及应对策略

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/fullgc/image/heap.jpg)	
	
	堆内存分为 Eden,Survivor和Tenured/Old空间
	年轻代空间的内存回收成功，MinorGC，老年代GC成为MajorGC，FullGC是对整个堆来说的。而出现FullGC时经常伴随至少一次的 MinorGC。MajorGC速度会比
	MinorGC慢10倍以上。
	Major GC通常是跟full GC是等价的

## 1.System.gc()方法调用
	此方法是建议JVM进行FullGC，只是建议而非一定，很多情况下会触发FullGC，只会增加FullGC的频率，也就增加了间歇性停顿次数，因此最好不要使用该方法
	让虚拟机自己去管理它的内存。可通过通过-XX:+ DisableExplicitGC来禁止RMI调用System.gc。
## 2.老年代空间不足
	老年代空间只有在新生代对象转入以及创建大对象，大数组时才会出现不足的现象，当执行FullGC后空间仍然不足，则抛出
	Java.lang.OutOfMemoryError:java heap space
	为避免以上2种状态引起的FullGC，调优时尽量做到让对象在MinorGC阶段被回收，让对象在新生代多存活一段时间，以及不要创建过大对象和数组
## 3.永久代空间不足
	JVM规范中运行数组区域的方法区，在HotSpot虚拟机中又被称为永久代，永久代中存放一些class信息，常量，静态变量等数据。当系统
	要加载的类，反射类和调用的方法较多时，永久代可能占满，在未配置为采用CMS GC的情况下也会执行FullGC。如果FullGC仍然回收不了，
	JVM会抛出如下错误：
	java.lang.OutOfMemoryError:PerGen space
	为避免Perm Gen占满造成FullGC现象，可采用增大PermGen空间或使用CMSGC
## 4.CMS GC时出现promotion failed和concurrent mode failure
	对于采用CMS进入老年代GC的程序而言，注意GC日志中是否有promotionfailed和concurrent mode failure两种情况。当出现这2种情况时
	可能触发FullGC
	
	promotion failed是在进行MinorGC时，survivor space 放不下，对象只能放入老年代，而此时老年代也放不下造成的
	concurrent mode faulure是在执行CMS GC过程中同时有对象要放入老年代，此时老年代空间不足造成的。
	
	措施：增大survivor space，老年代空间
	
	
## 5.堆中分配很大的对象
	所谓大对象，是指需要大量连续内存空间的java对象，如很长的数组，此种对象会直接进入老年代，而老年代虽然有很大剩余空间，但无法找到足够大的连续空间
	分配给当前对象，此种情况会触发JVM进行FullGC
	为了解决这个问题，CMS垃圾收集器提供一个可配置参数，即：-XX:+UseCMSCompactAtFullCollection开关参数。
	即FullGC后，有一个碎片整理的过程，但停顿时间也增长了， -XX:CMSFullGCsBeforeCompaction这个参数用于设置
	在执行多少次不压缩的FullGC后，跟着来一次带压缩的
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	