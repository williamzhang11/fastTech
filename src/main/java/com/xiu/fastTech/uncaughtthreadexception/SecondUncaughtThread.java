package com.xiu.fastTech.uncaughtthreadexception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class SecondUncaughtThread {

	public static void main(String[] args) {
		/*//第一种：线程方式
		Thread thread = new Thread(new Task());
		thread.setUncaughtExceptionHandler(new UncaughtThread());
		thread.start();*/
		
		/*//第二种：线程池execute方式
		Thread thread = new Thread(new Task1());
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(thread);
		
		executorService.shutdown();*/
		
		//第三种：线程池submit方式,FutureTask.get中重新抛出
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		FutureTask<?> futureTask = (FutureTask<?>) executorService.submit(new Task());
		
		try {
			futureTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static class Task1 implements Runnable{

		public void run() {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtThread());
			System.out.println("begin");
			System.out.println(3/0);
			System.out.println("end");
		}
		
	}
	
	
	public static class Task implements Runnable{

		public void run() {
			Thread.currentThread().setUncaughtExceptionHandler(new UncaughtThread());
			System.out.println("begin");
			System.out.println(3/0);
			System.out.println("end");
		}
		
	}
	
	public static class UncaughtThread implements UncaughtExceptionHandler{

		public void uncaughtException(Thread t, Throwable e) {
			System.out.println(t.getName()+"="+e.getMessage());
		}
		
	}
	
}
