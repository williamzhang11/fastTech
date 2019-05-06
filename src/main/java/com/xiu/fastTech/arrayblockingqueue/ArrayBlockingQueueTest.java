package com.xiu.fastTech.arrayblockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueTest {

	static BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(10);
	
	public void add() throws InterruptedException {
		
		int i=20;
		while(i>0) {
			System.out.println("入队列："+i);
			
			blockingQueue.put(String.valueOf(i));
			i--;
		}
	}
	
	
	public void get() throws InterruptedException {
		String i=null;
		while(( i= blockingQueue.take())!=null) {
			System.out.println("出队列："+i);
		}
		
		System.out.println("size="+blockingQueue.size());
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		new ProductThread(blockingQueue, new ArrayBlockingQueueTest()).start();
		new ConsumerThread(blockingQueue, new ArrayBlockingQueueTest()).start();
		
		
	}
	
	static class ProductThread extends Thread{
		BlockingQueue<String> blockingQueue;
		ArrayBlockingQueueTest arrayBlockingQueueTest;
		
		public ProductThread(BlockingQueue<String> blockingQueue,ArrayBlockingQueueTest arrayBlockingQueueTest) {
			this.blockingQueue = blockingQueue;
			this.arrayBlockingQueueTest = arrayBlockingQueueTest;
		}
		
		public void run() {
			
				try {
					arrayBlockingQueueTest.add();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	static class ConsumerThread extends Thread{
		BlockingQueue<String> blockingQueue;
		ArrayBlockingQueueTest arrayBlockingQueueTest;
		public ConsumerThread(BlockingQueue<String> blockingQueue,ArrayBlockingQueueTest arrayBlockingQueueTest) {
			this.blockingQueue = blockingQueue;
			this.arrayBlockingQueueTest = arrayBlockingQueueTest;
		}
		public void run() {
			
			try {
				arrayBlockingQueueTest.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
}
