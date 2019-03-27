package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

public class ThreadPoolInheritableThreadLocals {

	private static TransmittableThreadLocal<Integer> threadLocal = new TransmittableThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};
	
	private static ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newCachedThreadPool());
	
	
	public static void main(String[] args) {
		int i=100;
		while(i>0) {
			
			executorService.submit(new ChildThread());
			i--;
		}
	}
	
	static class ChildThread implements Runnable{

		public void run() {
			threadLocal.set(threadLocal.get()+1);
			System.out.println("childThread value:"+threadLocal.get());
			new Thread(new ChildChildThread()).start();
		}
		
		
	}
	
	static class ChildChildThread implements Runnable{

		public void run() {
			System.out.println("childchildThread value:"+threadLocal.get());
		}
		
	}
}
