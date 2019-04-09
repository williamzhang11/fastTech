package com.xiu.fastTech.aqs;

import java.util.concurrent.locks.ReentrantLock;

public class AQSTest {

	
	public static void main(String[] args) {
		
		ReentrantLock lock = new ReentrantLock();
		
		lock.newCondition();
		new Thread(new ThreadTest(lock)).start();
		//new Thread(new ThreadTest(lock)).start();
		
	}
	
	static class ThreadTest implements Runnable{
		
		ReentrantLock lock;
		public ThreadTest(ReentrantLock lock) {
			this.lock = lock;
		}
		
		public void run() {
		 lock.lock();
		try {
			Thread.sleep(1000);
			exec();
			System.out.println("========1===========");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
			
		}
		
		public void exec() {
			
			lock.lock();
			System.out.println("=======2============");
			lock.unlock();
		}
		
		
	}
}
