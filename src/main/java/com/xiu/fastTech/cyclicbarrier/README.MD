# 并发工具CyclicBarrier

## 介绍
	它允许线程相互等待，直到到达某个公共屏障点，因为该屏障在释放等待后可重用，因此称为循环(Cyclic)屏障(Barrier)。
	让一组线程到达一个屏障时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程继续执行。
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/cyclicbarrier/image/cyclicbarrier.png)

创建CyclicBarrier时

```
	public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        //parties为拦截线程的总数量
        this.parties = parties;
        //拦截线程的剩余需要数量
        this.count = parties;
        //线程到达屏障时执行的业务逻辑
        this.barrierCommand = barrierAction;
    }

```

## 源码
### await

如果该线程不是最后一个到达的线程，则一直处于等待状态，除非发生以下情况

1.最后一个线程到达
2.超出指定时间
3.其他某个线程中断当前线程
4.其他某个线程中断另一个等待线程
5.其他某个线程在等待barrier超时
6.其他某个线程在此barrier调用reset方法。

```

    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return dowait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen;
        }
    }
    
        private int dowait(boolean timed, long nanos)
        throws InterruptedException, BrokenBarrierException,
               TimeoutException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        	//分代，到达屏障的同一批线程是同一代
            final Generation g = generation;
			//当前代是否损坏，
            if (g.broken)
                throw new BrokenBarrierException();
			//线程中断则终止CyclicBarrier
            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }
			//进来一个线程则减1
           int index = --count;
           //==0则表示所有线程到位，触发Runnable任务
           if (index == 0) {  // tripped
               boolean ranAction = false;
               try {
                   final Runnable command = barrierCommand;
                   //触发任务
                   if (command != null)
                       command.run();
                       
                   ranAction = true;
                   //唤醒所有线程，并重置设置
                   nextGeneration();
                   return 0;
               } finally {
               
                   if (!ranAction)
                   //ranAction=true未执行，说明barrierCommand执行报错
                       breakBarrier();
               }
           }

            // loop until tripped, broken, interrupted, or timed out
            for (;;) {
                try {
                //不超时，则等待
                    if (!timed)
                        trip.await();
                    else if (nanos > 0L)
                    //超时等待
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        // We're about to finish waiting even if we had not
                        // been interrupted, so this interrupt is deemed to
                        // "belong" to subsequent execution.
                        Thread.currentThread().interrupt();
                    }
                }

                if (g.broken)
                    throw new BrokenBarrierException();
				//generation已经更新，返回index
                if (g != generation)
                    return index;
				//超时，终止CyclicBarrier，抛出异常
                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }
    //唤醒线程并重置设置
    private void nextGeneration() {
        // signal completion of last generation
        trip.signalAll();
        // set up next generation
        count = parties;
        generation = new Generation();
    }
    
    private void breakBarrier() {
    	//中止所有方法，并唤醒阻塞线程
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }
    
```


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	