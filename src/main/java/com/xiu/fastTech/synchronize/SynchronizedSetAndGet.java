package com.xiu.fastTech.synchronize;

//一个线程访问同步代码块时，另一个线程可以同时访问非同步代码块
public class SynchronizedSetAndGet {

	public static void main(String[] args) {
		
		Counter counter= new Counter();
		Thread thread1 = new Thread(counter, "A");
		Thread thread2 = new Thread(counter, "B");
		thread1.start();
		thread2.start();
	}
	
	static class Counter implements Runnable{

		private int count;
		
		public Counter() {
			count=0;
		}
		
		public void countAdd() {
			synchronized(this) {
		         for (int i = 0; i < 5; i ++) {
		            try {
		               System.out.println(Thread.currentThread().getName() + ":" + (count++));
		               Thread.sleep(100);
		            } catch (InterruptedException e) {
		               e.printStackTrace();
		            }
		         }
		      }
		}
		
		public void printCount() {
			
			for(int i=0;i<5;i++) {
				System.out.println(Thread.currentThread().getName() + " count:" + count);
	            try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		public void run() {
			
			String threadName = Thread.currentThread().getName();
			if(threadName.equals("A")) {
				countAdd();
			}else {
				printCount();
			}
		}
		
	}
}
