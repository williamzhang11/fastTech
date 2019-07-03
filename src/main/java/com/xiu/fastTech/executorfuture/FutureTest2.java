package com.xiu.fastTech.executorfuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureTest2 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		Task task = new Task();
		
		FutureTask<Integer> futureTask =new FutureTask<Integer>(task);
		executorService.submit(futureTask);
		executorService.shutdown();
		System.out.println("task运行结果"+futureTask.get());
		
	}
	
	static class Task implements Callable<Integer>{

		public Integer call() throws Exception {
			 System.out.println("子线程在进行计算");
		     Thread.sleep(3000);
		     int sum = 0;
		     for(int i=0;i<100;i++)
		         sum += i;
		     return sum;
		}
	}
}
