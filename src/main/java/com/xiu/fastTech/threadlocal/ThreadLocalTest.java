package com.xiu.fastTech.threadlocal;

public class ThreadLocalTest {

	public static Thread getThreadByName(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
		if (t.getName().equals(threadName)) return t;
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		
		Thread userthread = new Thread(new UserThread()); 
		userthread.setName("userthread");
		userthread.start();
		
		Thread thread = getThreadByName("userthread");
		
		
		ThreadLocal<String> threadLocal = new ThreadLocal<String>();
		
		threadLocal.set("111");
		
		System.out.println(threadLocal.get());
	}
}
