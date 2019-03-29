package com.xiu.fastTech.volatiletest;



public class VolatileTest2 {

	private static volatile int count=0;
	public static void main(String[] args) {
		
		for(int i =0;i<100;i++) {
			new Thread(new VolatileThread()).start();
		}
		
	}
	
	static class VolatileThread implements Runnable{

		public VolatileThread() {
		}
		
		public void run() {
			
			for(int i =0;i<1000;i++) {
				synchronized (VolatileThread.class) {
					
					System.out.println(Thread.currentThread().getName()+"=" + (++count));
				}
			}
		}
		
		
	}
}
