package com.xiu.fastTech.threadinterrupt;

public class UserThread2 implements Runnable{

	public void run() {
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("user Thread start");
		
	}
	
	
	public static void main(String[] args) {
		
		Thread trThread = new Thread(new UserThread2());
		
		trThread.setName("111");
		
		Thread trThread1 = new Thread(new UserThread2());
		
		trThread1.setName("111");
		
		trThread.start();
		trThread1.start();
		
		
	}

}
