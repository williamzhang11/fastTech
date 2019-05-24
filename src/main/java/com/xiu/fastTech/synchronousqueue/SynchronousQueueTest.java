package com.xiu.fastTech.synchronousqueue;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 *SynchronousQueue是没有容量的，是无缓存等待队列，是一个不存储元素的阻塞队列，会直接将任务交给消费者，必须等队列中的添加元素被消费后，才能继续添加新元素。 
 *
 */
public class SynchronousQueueTest {

	public static void main(String[] args) {
		
		SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();
		Consumer consumer= new Consumer(queue);
		Product product = new Product(queue);
		
		new Thread(consumer).start();
		new Thread(product).start();
	}
	
	
	static class Product implements Runnable{
		SynchronousQueue<Integer> queue = null;
		
		public Product() {
		}
		
		public Product(SynchronousQueue<Integer> queue) {
			this.queue = queue;
		}
		
		public void run() {
			while(true) {
				int rand = new Random().nextInt(1000);
				System.out.println(String.format("生产：%d", rand));
				
				try {
					
					queue.put(rand);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	static class Consumer implements Runnable{
		SynchronousQueue<Integer>queue = null;
		
		public Consumer(SynchronousQueue<Integer> queue) {
			this.queue = queue;
		}
		
		
		public void run() {
			
			while(true) {
				try {
					System.out.println(String.format("消费：%d", queue.take()));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("-------------------------------------");
			}
		}
		
	}
}
