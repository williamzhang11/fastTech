package com.xiu.fastTech.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 
 *带Runnable的CyclicBarrier构造函数，当多个线程都到达屏障点后，开始执行runnable  
 *
 */
public class CyclicBarrierTest1 {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final CyclicBarrier cbBarrier = new CyclicBarrier(3,new Runnable() {
			
			public void run() {
				
				int i = 1/0;
			}
		});
		
		for(int i=0;i<3;i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					
					try {
						Thread.sleep((long)(Math.random()*10000));
						System.out.println("线程"+Thread.currentThread().getName()+"即将到达集合点1，当前已有"+cbBarrier.getNumberWaiting()+"个已经到达"
								+ "正在等候");
						try {
							cbBarrier.await();
						} catch (BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Thread.sleep((long)(Math.random()*10000));    
                        System.out.println("线程" + Thread.currentThread().getName() + 
                                "即将到达集合地点2，当前已有" + cbBarrier.getNumberWaiting() + "个已经到达，正在等候");                        
                        cbBarrier.await();    
                        Thread.sleep((long)(Math.random()*10000));    
                        System.out.println("线程" + Thread.currentThread().getName() + 
                                 "即将到达集合地点3，当前已有" + cbBarrier.getNumberWaiting() + "个已经到达，正在等候");                        
                        cbBarrier.await();  
					
						
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			service.submit(runnable);
		}
		
		
		service.shutdown();
		
		
	}
}
