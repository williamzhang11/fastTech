package com.xiu.fastTech.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadPoolTest {

	Executor executor = Executors.newCachedThreadPool();
	public static void main(String[] args) {
		
		
		//Runnable thread = new UserThread();
		Thread thread = new UserThread2();
		//thread.setName("2222");
		new ThreadPoolTest().executor.execute(thread);
	}
}
