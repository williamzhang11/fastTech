package com.xiu.fastTech.createthread;
/*
 * 线程池创建
 * 内部类调用有
 * 2.声明成静态的
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FourthCreateThread {

	private static ExecutorService executorService = Executors.newFixedThreadPool(5);
	
	
	public static void main(String[] args) {
		
		FourthCreateThread.RunnableThread RunnableThread1 =new FourthCreateThread.RunnableThread();
		
		executorService.execute(RunnableThread1);
	}
	
	public static class RunnableThread implements  Runnable {
		public void run() {
			
			System.out.println("...RunnableThread...");
		}
	}
	
	public  class RunnableThread1 implements  Runnable {
		public void run() {
			
			System.out.println("...RunnableThread...");
		}
	}
	
	
	public static class TestThread implements  Runnable {
		public void run() {
			
			System.out.println("...TestThread...");
		}
	}
}
