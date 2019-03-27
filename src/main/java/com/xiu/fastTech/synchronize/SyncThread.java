package com.xiu.fastTech.synchronize;

public class SyncThread {

	public static void main(String[] args) {
		
		ThreadTest threadTest1 = new ThreadTest();
		ThreadTest threadTest2 = new ThreadTest();
		Thread thread1= new Thread(threadTest1,"threadTest1");
		Thread thread2= new Thread(threadTest2,"threadTest2");
		thread1.start();
		thread2.start();
		
	}
	
	
	static class ThreadTest implements Runnable{

		private  static int count;
		
		public ThreadTest() {
			count=0;
		}
		public void run() {
			
			synchronized (ThreadTest.class) {
				
				for(int i=0;i<100;i++) {
					try {
						
						System.out.println(Thread.currentThread().getName()+":"+(count++));
					}catch (Exception e) {
						
					}
				}
			}
		}
		
		public int getCount() {
		      return count;
		   }
		
		
	}
	
}
