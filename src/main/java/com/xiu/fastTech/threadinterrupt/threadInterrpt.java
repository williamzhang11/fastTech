package com.xiu.fastTech.threadinterrupt;

public class threadInterrpt {

	public static void main(String[] args) {
		
		
		Thread thread = new Thread(new UserThread());
		
		thread.start();
		thread.interrupt();
		
		
		
	}
}
