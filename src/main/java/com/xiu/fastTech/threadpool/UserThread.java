package com.xiu.fastTech.threadpool;

public class UserThread implements Runnable{

	public void run() {
		Thread.currentThread().setName("1111");
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("user Thread");
	}

}
