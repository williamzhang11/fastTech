package com.xiu.fastTech.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ReentrantLockTest {
	
	private static volatile Boolean isReady = false;
	private static volatile int value;
	
	
	public static void main(String[] args) throws InterruptedException  {
		ReentrantLock reentrantLock = new ReentrantLock();
		Condition condition = reentrantLock.newCondition();
		new Thread(new ConsumerThread(reentrantLock, condition)).start();
		new Thread( new ProducerThread(reentrantLock, condition)).start();
		
		
		
	}
	
	
	static class ProducerThread implements Runnable{
		
		Condition condition;
		ReentrantLock reentrantLock;
		
		public ProducerThread(ReentrantLock reentrantLock,Condition condition) {
			this.reentrantLock = reentrantLock;
			this.condition = condition;
			
		}
		

		public void run() {
			try {
				reentrantLock.tryLock(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				
				value = Operator.count(100);
				isReady =true;
				condition.signal();
				
			}finally {
				
				reentrantLock.unlock();
			}
		}
	}
	
	static class ConsumerThread implements Runnable{
		
		Condition condition;
		ReentrantLock reentrantLock;

		public ConsumerThread(ReentrantLock reentrantLock,Condition condition) {
			this.reentrantLock = reentrantLock;
			this.condition = condition;
		}
		
		public void run() {
			reentrantLock.lock();
			try {
				
				while (!isReady)
					condition.await();
				
				
				System.out.println("value:"+value);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				reentrantLock.unlock();
			}
			
			
			
		}
		
	}
	
	
	static class Operator{
		
		public static int count(int num) {
			int sum =0;
			
			if(num<=0) {
				return sum;
			}
			
			for(int i=0;i<=num;i++) {
				sum +=i;
			}
			return sum;
		}
	}
	
}
