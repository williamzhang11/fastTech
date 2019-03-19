package com.xiu.fastTech.createthread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 通过Callable和FutureTask创建线程
 * 1.创建Callable接口实现类，实现call()方法
 * 2.创建Callable实现类的实例，使用FutureTask类包装Callable对象，该FutureTask对象封装了该Callback对象的Call方法的返回值
 * 3.使用FutureTask对象作为Thread对象的target创建并启动新线程
 * 4.调用FutureTask对象get方法获取子线程执行结束后的返回值
 */
public class ThirdCreateThread implements Callable<Integer>{

	public Integer call() throws Exception {
		
		int i=100,sum=0;
		while(i>0) {
			sum +=i;
			i--;
		}
		System.out.println(Thread.currentThread().getName()+":sum= "+sum);
		return sum;
	}

	public static void main(String[] args) {
		
		ThirdCreateThread thirdCreateThread = new ThirdCreateThread();
		FutureTask<Integer>futureTask = new FutureTask<Integer>(thirdCreateThread);
		new Thread(futureTask).start();
		
		try {
			System.out.println("end: "+futureTask.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
