package com.xiu.fastTech.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		final Semaphore semaphore = new Semaphore(5);
		//模拟50个客户端同时访问，但一次性仅允许5个并发
		for (int index =0;index<50;index++) {
			final int NO = index;
			Runnable runnable = new Runnable() {
				
				public void run() {
					try {
						semaphore.acquire();
						
						System.out.println("Accessing:"+NO);
						Thread.sleep((long)(Math.random()*6000));
						semaphore.release();
						System.out.println("-----------------"+semaphore.availablePermits());
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			executorService.execute(runnable);
		}
		executorService.shutdown();
	}
	
}
