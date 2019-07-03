package com.xiu.fastTech.executorfuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//Callable+Future获取执行结果
public class FutureTest {
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Task task = new FutureTest().new Task();
		Future<Integer> result = executorService.submit(task);
		executorService.shutdown();
		
		 try {
			System.out.println("task运行结果"+result.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	class Task implements Callable<Integer>{

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
