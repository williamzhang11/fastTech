package com.xiu.fastTech.executorfuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadPoolTest {

	Executor executor = Executors.newCachedThreadPool();
	public static void main(String[] args) {
		
		
		//Runnable thread = new UserThread();
		Thread thread = new UserThread2();
		thread.start();
		//thread.setName("2222");
		new ThreadPoolTest().executor.execute(thread);
		
	}
}
