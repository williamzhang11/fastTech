package com.xiu.fastTech.threadsynchronized;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class BlockingQueueTest {
	
	private static BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<Integer>();
	private static volatile Boolean productRunning = true;
	private static volatile Boolean consumerRunning = true;
	private static AtomicInteger count = new AtomicInteger(0);
	public static void main(String[] args) {
		
		new Thread(new Consumer()).start();
		new Thread(new Consumer()).start();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		new Thread(new Product()).start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		productRunning = false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		System.out.println("max count:"+count);
		
	}
	
	static class Product implements Runnable{

		
		public void run() {
			while(productRunning) {
				
				try {
					Integer data = count.addAndGet(1);
					System.out.println("product:"+data);
					blockingQueue.offer(data,2,TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("product close");
			
		}
		
		public void stop() {
			productRunning =false;
		}
		
	}
	
	
	static class Consumer implements Runnable{
		
		public void run() {
			
			while(consumerRunning) {
				
				Integer date = null;
				try {
					date = blockingQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " get from blockqueue:"+date);
			}
			System.out.println("Consumer close");
			
		}
		
		public void stop() {
			consumerRunning=false;
		}
	}
}
