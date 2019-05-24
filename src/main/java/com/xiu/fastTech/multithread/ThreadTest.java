package com.xiu.fastTech.multithread;

public class ThreadTest {

	public static void main(String[] args) {
		
		Runnable runnable =	new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				System.out.println(11111);
			}
		};
		
		Thread thread = new Thread(runnable);
		
		thread.setDaemon(true);
		thread.start();
		System.out.println("end");
		
	}
}
