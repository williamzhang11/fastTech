package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalTest {

	public static Thread getThreadByName(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
		if (t.getName().equals(threadName)) return t;
		}
		return null;
	}
	
	public static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
		
		@Override
		protected Integer initialValue() {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	public Integer addThreadLocal() {
		threadLocal.set(threadLocal.get()+1);
		return threadLocal.get();
	}
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	public static void main(String[] args) {
		
		ThreadLocalTest threadLocalTest = new ThreadLocalTest();
		for(int i=0;i<4;i++) {
			Thread userthread = new Thread(new UserThread(threadLocalTest)); 
			executorService.submit(userthread);
		}
		executorService.shutdown();
		
		System.out.println("main"+threadLocal.get());
		
		
	}
}
