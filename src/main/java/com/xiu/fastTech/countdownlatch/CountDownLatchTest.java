package com.xiu.fastTech.countdownlatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;


public class CountDownLatchTest {

	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		
		Worker worker1 = new Worker("zhangsan", 5000, latch);
		Worker worker2 = new Worker("lisi", 8000, latch);
		worker1.start();
		worker2.start();
		latch.await();
		System.out.println("all work done at "+sdf.format(new Date()));  
		
	}
	
	static class Worker extends Thread{
		
		String workName;
		int workTime;
		CountDownLatch latch;
		
		public Worker(String workerName, int workTime,CountDownLatch latch) {
			this.workName = workerName;
			this.workTime = workTime;
			this.latch = latch;
		}
		
		public void run() {
			
			System.out.println("Worker"+workName+"do work begin at"+sdf.format(new Date()));
			doWork();
			System.out.println("Worker "+workName+" do work complete at "+sdf.format(new Date()));  
			latch.countDown();
		}
		
		public void doWork() {
			try {
				Thread.sleep(workTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
