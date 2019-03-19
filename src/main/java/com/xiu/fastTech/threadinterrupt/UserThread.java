package com.xiu.fastTech.threadinterrupt;

public class UserThread implements Runnable{

	public void run() {
		
		System.out.println("user Thread start");
		
		for(int i=0 ;i<100000000;i++) {
			if(i%1000000 == 0) {
				System.out.println(i);
			}
			
		}
		
		/*System.out.println(Thread.currentThread().interrupted());
		System.out.println(Thread.currentThread().interrupted());*/
		System.out.println(Thread.currentThread().isInterrupted());
		System.out.println(Thread.currentThread().isInterrupted());
		
		if(Thread.currentThread().isInterrupted()) {
			
			System.out.println("interrupt");
			return;
		}
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(Thread.currentThread().isInterrupted());
			return;
		}
		
		System.out.println("user Thread end");
		
		
	}

}
