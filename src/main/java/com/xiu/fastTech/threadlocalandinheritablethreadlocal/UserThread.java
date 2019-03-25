package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

public class UserThread implements Runnable{
	
	private ThreadLocalTest threadLocalTest;
	
	public UserThread(ThreadLocalTest threadLocalTest) {
		this.threadLocalTest = threadLocalTest;
	}
	
	public void run() {
		
		
		for(int i=0; i<3;i++) {
			System.out.println(Thread.currentThread().getName()+"="+threadLocalTest.addThreadLocal());
		}
	}

}
