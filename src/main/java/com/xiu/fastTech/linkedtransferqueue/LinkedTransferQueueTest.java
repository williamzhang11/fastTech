package com.xiu.fastTech.linkedtransferqueue;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class LinkedTransferQueueTest {
	
	public static void main(String[] args) throws InterruptedException {
		TransferQueue<String> queue = new LinkedTransferQueue<String>();
		Thread producer = new Thread(new Producter(queue));
		producer.setDaemon(true);
		producer.start();
		
		for(int i=0;i<10;i++) {
			Thread consumer = new Thread(new Consumer(queue));
			consumer.setDaemon(true);
			consumer.start();
			
			TimeUnit.SECONDS.sleep(1);
		}
		
	}

	 static class Producter implements Runnable{

		private final TransferQueue <String> queue;
		
		public Producter(TransferQueue<String> queue) {
			this.queue = queue;
		}
		
		private String produce() {
			return "your lucky number"+(new Random().nextInt(100));
		}
		
		public void run() {
			try {
			
				while (true) {
					if(queue.hasWaitingConsumer()) {
							queue.transfer(produce());
							
							TimeUnit.SECONDS.sleep(1);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	 
	  static class Consumer implements Runnable{
		 
		 private final TransferQueue<String> queue;
		 
		 public Consumer(TransferQueue<String> queue) {
			 this.queue = queue;
		 }
		 
		public void run() {
			try {
				System.out.println("Consumer"+Thread.currentThread().getName()+queue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
	 }
}
